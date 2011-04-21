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

package com.horstmann.violet.eclipseplugin.wizards;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

import com.horstmann.violet.framework.file.persistence.IFilePersistenceService;
import com.horstmann.violet.framework.injection.bean.SpringDependencyInjector;
import com.horstmann.violet.framework.injection.bean.annotation.SpringBean;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.classes.ClassDiagramGraph;

/**
 * Generic diagram creation wizard
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public abstract class NewWizard extends Wizard implements INewWizard
{

    private IStructuredSelection selection = null;

    private WizardNewFileCreationPage creationPage = null;

    private String pageTitle;

    private IGraph UMLGraph;

    /**
     * Called by eclipse when wizard ends
     */
    public boolean performFinish()
    {
        String fname = creationPage.getFileName();
        if (!fname.toLowerCase().endsWith(getFileExtension()))
        {
            creationPage.setFileName(fname + getFileExtension());
        }

        if (creationPage.getErrorMessage() != null) return false;

        final IFile file = creationPage.createNewFile();

        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run(IProgressMonitor monitor) throws InvocationTargetException
            {
                try
                {
                    doFinish(file, monitor);
                }
                catch (CoreException e)
                {
                    throw new InvocationTargetException(e);
                }
                finally
                {
                    monitor.done();
                }
            }
        };
        try
        {
            getContainer().run(true, false, op);
        }
        catch (InterruptedException e)
        {
            return false;
        }
        catch (InvocationTargetException e)
        {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }

        return true;

    }

    /**
     * Called by Eclipse at wizard init
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.selection = selection;
        SpringDependencyInjector.getInjector().inject(this);
    }

    /**
     * Construct wizard page (use standard WizardNewFileCreationPage)
     */
    public void addPages()
    {
        creationPage = new WizardNewFileCreationPage(this.getPageTitle(), selection);
        creationPage.setTitle(this.getPageTitle());
        creationPage.setDescription("Enter file name.");
        addPage(creationPage);
    }

    /**
     * Get wizard page title
     * 
     * @return
     */
    private String getPageTitle()
    {
        if (this.pageTitle == null)
        {
            // Return class diagram by default
            this.pageTitle = "New UML Class Diagram";
        }
        return this.pageTitle;
    }

    /**
     * Set wizard page title
     * 
     * @param title
     */
    public void setPageTitle(String title)
    {
        this.pageTitle = title;
    }

    /**
     * Return UML graph diagram model to create
     * 
     * @return class diagram by default if not alredy set
     */
    private IGraph getUMLGraph()
    {
        if (this.UMLGraph == null)
        {
            this.UMLGraph = new ClassDiagramGraph();
        }
        return this.UMLGraph;
    }

    /**
     * Set UML graph diagram model to create
     * 
     * @param graph
     */
    public void setUMLGraph(IGraph graph)
    {
        this.UMLGraph = graph;
    }

    /**
     * Called by performFinish(). Create new file wirh default content and then open editor
     * 
     * @param file
     * @param monitor
     * @throws CoreException
     */
    private void doFinish(final IFile file, IProgressMonitor monitor) throws CoreException
    {
        try
        {
            IGraph graph = this.getUMLGraph();
            PipedOutputStream pos = new PipedOutputStream();
            PipedInputStream pis = new PipedInputStream(pos);
            this.filePersistenceService.write(graph, pos);
            file.setContents(pis, true, true, monitor);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        monitor.worked(1);
        monitor.setTaskName("Opening file for editing...");
        getShell().getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try
                {
                    IDE.openEditor(page, file, "com.horstmann.violet.eclipseplugin.editors.UMLEditor", true);
                }
                catch (PartInitException e)
                {
                }
            }
        });
        monitor.worked(1);
    }

    /**
     * @return file extension (.class.violet for example)
     */
    public abstract String getFileExtension();
    
    @SpringBean
    private IFilePersistenceService filePersistenceService;

}
