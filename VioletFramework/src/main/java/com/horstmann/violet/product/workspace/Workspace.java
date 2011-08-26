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

package com.horstmann.violet.product.workspace;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.horstmann.violet.framework.file.IFile;
import com.horstmann.violet.framework.file.IGraphFile;
import com.horstmann.violet.framework.file.IGraphFileListener;
import com.horstmann.violet.framework.injection.bean.BeanInjector;
import com.horstmann.violet.framework.injection.bean.annotation.InjectedBean;
import com.horstmann.violet.framework.plugin.IDiagramPlugin;
import com.horstmann.violet.framework.plugin.PluginRegistry;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.Id;
import com.horstmann.violet.product.workspace.editorpart.EditorPart;
import com.horstmann.violet.product.workspace.editorpart.IEditorPart;
import com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager;
import com.horstmann.violet.product.workspace.editorpart.behavior.AddEdgeBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.AddNodeBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.ChangeToolByWeelBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.DragSelectedBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.EditSelectedBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.FileCouldBeSavedBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.ResizeNodeBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.SelectByClickBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.SelectByDistanceBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.SelectByLassoBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.SwingRepaintingBehavior;
import com.horstmann.violet.product.workspace.editorpart.behavior.ZoomByWheelBehavior;
import com.horstmann.violet.product.workspace.sidebar.ISideBar;
import com.horstmann.violet.product.workspace.sidebar.SideBar;
import com.horstmann.violet.product.workspace.sidebar.graphtools.GraphTool;
import com.horstmann.violet.product.workspace.sidebar.graphtools.IGraphToolsBarListener;
import com.horstmann.violet.product.workspace.statusbar.IStatusBar;
import com.horstmann.violet.product.workspace.statusbar.StatusBar;

/**
 * Diagram workspace. It is a kind of package composed by a diagram put in a scroll panel, a side bar for tools and a status bar.
 * This the class to use when you want to work with diagrams outside from Violet (in Eclipse or NetBeans for example)
 * 
 * @author Alexandre de Pellegrin
 */
public class Workspace implements IWorkspace
{
    /**
     * Constructs a diagram panel with the specified graph
     * 
     * @param graphFile
     */
    public Workspace(IGraphFile graphFile)
    {
        this.graphFile = graphFile;
        init();
    }

    /**
     * Constructs a diagram panel with the specified graph and a specified id
     * 
     * @param graphFile
     * @param id unique id
     */
    public Workspace(IGraphFile graphFile, Id id)
    {
        this.graphFile = graphFile;
        this.id = id;
        init();
    }

    private void init()
    {
        BeanInjector.getInjector().inject(this);
        setTitle(getGraphName());
        this.graphFile.addListener(new IGraphFileListener()
        {
            public void onFileModified()
            {
                updateTitle(true);
                fireSaveNeeded();
            }

            public void onFileSaved()
            {
                updateTitle(false);
            }
        });
        getAWTComponent().prepareLayout();
    }
    
    /**
     * @return graph filename or the corresponding diagram name if the graph <br/>
     * hasn't been saved yet.
     */
    private String getGraphName() {
       String filename = this.graphFile.getFilename();
       if (filename != null) {
           return filename;
       }
       List<IDiagramPlugin> diagramPlugins = this.pluginRegistry.getDiagramPlugins();
       Class<? extends IGraph> searchedClass = this.graphFile.getGraph().getClass();
       for (IDiagramPlugin aDiagramPlugin : diagramPlugins)
       {
           if (aDiagramPlugin.getGraphClass().equals(searchedClass))
           {
               return aDiagramPlugin.getName();
           }
       }
       return "Unknown";
    }
    
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.IWorkspace#getGraphFile()
     */
    public IGraphFile getGraphFile() {
        return this.graphFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IDiagramPanel#getGraph()
     */
    public IEditorPart getEditorPart()
    {
        if (this.graphEditor == null)
        {
            this.graphEditor = new EditorPart(this.graphFile.getGraph());
            IEditorPartBehaviorManager behaviorManager = this.graphEditor.getBehaviorManager();
            behaviorManager.addBehavior(new SelectByLassoBehavior(this.graphEditor, this.getSideBar().getGraphToolsBar()));
            behaviorManager.addBehavior(new SelectByClickBehavior(this.graphEditor, this.getSideBar().getGraphToolsBar()));
            behaviorManager.addBehavior(new SelectByDistanceBehavior(this.graphEditor));
            behaviorManager.addBehavior(new AddNodeBehavior(this.graphEditor, this.getSideBar().getGraphToolsBar()));
            behaviorManager.addBehavior(new AddEdgeBehavior(this.graphEditor, this.getSideBar().getGraphToolsBar()));
            behaviorManager.addBehavior(new DragSelectedBehavior(this.graphEditor, this.getSideBar().getGraphToolsBar()));
            behaviorManager.addBehavior(new SwingRepaintingBehavior(this.graphEditor));
            behaviorManager.addBehavior(new EditSelectedBehavior(this.graphEditor));
            behaviorManager.addBehavior(new FileCouldBeSavedBehavior(this.getGraphFile()));
            behaviorManager.addBehavior(new ResizeNodeBehavior(this.graphEditor, this.getSideBar().getGraphToolsBar()));
            behaviorManager.addBehavior(new ZoomByWheelBehavior(this.graphEditor));
            behaviorManager.addBehavior(new ChangeToolByWeelBehavior(this.getSideBar().getGraphToolsBar()));
        }
        return this.graphEditor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IDiagramPanel#getSideBar()
     */
    public ISideBar getSideBar()
    {
        if (this.sideBar == null)
        {

            this.sideBar = new SideBar(this);
            this.sideBar.getGraphToolsBar().addListener(new IGraphToolsBarListener()
            {
                public void toolSelectionChanged(GraphTool tool)
                {
                    getEditorPart().getSelectionHandler().setSelectedTool(tool);
                }
            });
        }
        return this.sideBar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IWorkspace#getStatusBar()
     */
    public IStatusBar getStatusBar()
    {
        if (this.statusBar == null)
        {
            this.statusBar = new StatusBar(this);
        }
        return this.statusBar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IDiagramPanel#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set graph title
     * 
     * @param newValue
     */
    private void setTitle(String newValue)
    {
        title = newValue;
        fireTitleChanged(newValue);
    }

    /**
     * Fires a event to indicate that the title has been changed
     * 
     * @param newTitle
     */
    private void fireTitleChanged(String newTitle)
    {
        Vector<IWorkspaceListener> tl = cloneListeners();
        int size = tl.size();
        if (size == 0) return;

        for (int i = 0; i < size; ++i)
        {
            IWorkspaceListener aListener = (IWorkspaceListener) tl.elementAt(i);
            aListener.titleChanged(newTitle);
        }
    }


    /**
     * Set a status indicating that the graph needs to be saved
     * 
     * @param isSaveNeeded
     */
    private void updateTitle(boolean isSaveNeeded)
    {
        this.isSaveNeeded = isSaveNeeded;
        String aTitle = getTitle();
        if (isSaveNeeded)
        {
            if (!aTitle.endsWith("*"))
            {
                setTitle(aTitle + "*");
            }
        }
        if (!isSaveNeeded)
        {
            if (aTitle.endsWith("*"))
            {
                setTitle(aTitle.substring(0, aTitle.length() - 1));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IDiagramPanel#getFilePath()
     */
    public String getFilePath()
    {
        return filePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IDiagramPanel#setFilePath(java.lang.String)
     */
    public void setFilePath(String path)
    {
        filePath = path;
        File file = new File(path);
        setTitle(file.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IDiagramPanel#isSaveNeeded()
     */
    public boolean isSaveNeeded()
    {
        return this.isSaveNeeded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IDiagramPanel#addListener(com.horstmann.violet.framework.display.clipboard.DiagramPanelListener)
     */
    public synchronized void addListener(IWorkspaceListener l)
    {
        if (!this.listeners.contains(l))
        {
            this.listeners.addElement(l);
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized Vector<IWorkspaceListener> cloneListeners()
    {
        return (Vector<IWorkspaceListener>) this.listeners.clone();
    }

    /**
     * Fire an event to all listeners by calling
     */
    public void fireMustOpenFile(IFile aFile)
    {
        Vector<IWorkspaceListener> tl = cloneListeners();
        int size = tl.size();
        if (size == 0) return;
        for (int i = 0; i < size; ++i)
        {
            IWorkspaceListener l = (IWorkspaceListener) tl.elementAt(i);
            l.mustOpenfile(aFile);
        }
    }

    /**
     * Fire an event to all listeners by calling
     */
    private void fireSaveNeeded()
    {
        Vector<IWorkspaceListener> tl = cloneListeners();
        int size = tl.size();
        if (size == 0) return;
        for (int i = 0; i < size; ++i)
        {
            IWorkspaceListener l = (IWorkspaceListener) tl.elementAt(i);
            l.graphCouldBeSaved();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IDiagramPanel#getId()
     */
    public Id getId()
    {
        if (this.id == null)
        {
            this.id = new Id();
        }
        return this.id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.display.clipboard.IWorkspace#getAWTComponent()
     */
    public WorkspacePanel getAWTComponent()
    {
        if (this.workspacePanel == null)
        {
            this.workspacePanel = new WorkspacePanel(this);
        }
        return this.workspacePanel;
    }

    public WorkspacePanel workspacePanel;
    private IGraphFile graphFile;
    private IEditorPart graphEditor;
    private ISideBar sideBar;
    private IStatusBar statusBar;
    private String filePath;
    private String title;
    private Vector<IWorkspaceListener> listeners = new Vector<IWorkspaceListener>();
    private Id id;
    private boolean isSaveNeeded = false;
    
    @InjectedBean
    private PluginRegistry pluginRegistry;

}
