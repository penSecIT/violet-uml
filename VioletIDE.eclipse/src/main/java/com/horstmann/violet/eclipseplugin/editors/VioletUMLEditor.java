/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.eclipseplugin.editors;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ResourceTransfer;

import com.horstmann.violet.eclipseplugin.tools.EclipseUtils;
import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.file.IGraphFile;
import com.horstmann.violet.framework.file.persistence.IFilePersistenceService;
import com.horstmann.violet.framework.spring.SpringDependencyInjector;
import com.horstmann.violet.framework.spring.annotation.SpringBean;
import com.horstmann.violet.framework.theme.ITheme;
import com.horstmann.violet.framework.theme.ThemeManager;
import com.horstmann.violet.framework.workspace.IWorkspace;
import com.horstmann.violet.framework.workspace.Workspace;
import com.horstmann.violet.framework.workspace.WorkspacePanel;
import com.horstmann.violet.product.diagram.classes.ClassDiagramGraph;

/**
 * Main Editor Part
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class VioletUMLEditor extends EditorPart
{



    /**
     * Performs saving
     */
    public void doSave(IProgressMonitor monitor)
    {
        if (this.UMLFile != null)
        {
            try
            {
                Graph g = this.getUMLDiagramPanel().view.getGraphPanel(this.getUMLDiagramPanel()).getGraph();
                ByteBuffer buffer = GraphService.serializeGraph(g);
                ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
                this.UMLFile.setContents(bis, true, true, monitor);
                this.getUMLDiagramPanel().setSaveNeeded(false);
                firePropertyChange(EditorPart.PROP_DIRTY);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Gestion des logs
                e.printStackTrace();
            }
            catch (CoreException e)
            {
                // TODO Gestion des logs
                e.printStackTrace();
            }
        }
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    public void doSaveAs()
    {
        // Nothing to do here. Files are always created with the wizard
    }

    /**
     * Initializes editor
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        SpringDependencyInjector.getInjector().inject(this);
        setInput(input);
        setSite(site);
        // Retreive file input
        if (input instanceof IFileEditorInput)
        {
            IFileEditorInput fe = (IFileEditorInput) input;
            this.UMLFile = fe.getFile();
            // Update part editor title
            this.setPartName(this.UMLFile.getName());
        }

    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    public boolean isDirty()
    {
        return this.getUMLDiagramPanel().isSaveNeeded();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        // Nothing to do here.
    }

    /**
     * 'Save As' is disabled
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    /**
     * Builds editor with embedded JPanels
     */
    public void createPartControl(Composite parent)
    {
        // Set parent layout
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        parent.setLayout(gridLayout);

        EclipseColorPicker eclipseColorPicker = new EclipseColorPicker(getSite().getShell().getDisplay());
        ITheme eclipseTheme = new EclipseTheme(eclipseColorPicker);
		ThemeManager.getInstance().switchToTheme(eclipseTheme);

        new DiagramComposite(parent, this.getUMLDiagramPanel());

        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
        Transfer[] types = new Transfer[]
        {
            ResourceTransfer.getInstance()
        };
        DropTarget target = new DropTarget(parent, operations);
        target.setTransfer(types);
        target.addDropListener(new FileDropTargetListener(this.getUMLDiagramPanel().view.getGraphPanel(this.getUMLDiagramPanel())));

    }

    /**
     * Return master Violet Frame. Usefull to retreive graph and toolbar
     * 
     * @return
     */
    public IWorkspace getUMLDiagramPanel()
    {
        if (this.UMLWorkspace == null)
        {
            IGraphFile aGraphFile = null;
            if (this.UMLFile != null)
            {
                // TODO : implement an Eclipse filePersistenceService
            	IGraph read = this.filePersistenceService.read(this.UMLFile.getContents());
            }
            if (this.UMLFile == null)
            {
                aGraph = new ClassDiagramGraph();
            }

            this.UMLWorkspace = new Workspace(aGraph);
            this.UMLWorkspace.addListener(new DiagramPanelListener()
            {
                public void mustOpenfile(URL url)
                {
                    IEditorSite site = getEditorSite();
                    Display d = site.getShell().getDisplay();
                    EclipseUtils.openUMLDiagram(url, d);
                }

                public void graphCouldBeSaved()
                {
                    IEditorSite site = getEditorSite();
                    if (site != null)
                    {
                        Display d = site.getShell().getDisplay();
                        d.asyncExec(new Runnable()
                        {
                            public void run()
                            {
                                firePropertyChange(EditorPart.PROP_DIRTY);
                            }
                        });
                    }
                }

                public void titleChanged(String newTitle)
                {
                    // Nothing to do in eclipse
                }

            });
        }
        return this.UMLWorkspace;
    }
    
    public static final String ID = "com.horstmann.violet.eclipseplugin.editors.VioletUMLEditor";

    /** UML diagram swing panel */
    private IWorkspace UMLWorkspace;
    
    @SpringBean
    private DialogManager dialogManager;
    
    @SpringBean
    private IFilePersistenceService filePersistenceService;


    /** UML file edited */
    private IFile UMLFile;

}
