package com.horstmann.violet.product.workspace;

import java.awt.Component;

import com.horstmann.violet.framework.file.IGraphFile;
import com.horstmann.violet.product.diagram.abstracts.Id;
import com.horstmann.violet.product.workspace.editorpart.IEditorPart;
import com.horstmann.violet.product.workspace.sidebar.ISideBar;
import com.horstmann.violet.product.workspace.statusbar.IStatusBar;

public interface IWorkspace
{

    /**
     * @return graph file
     */
    public IGraphFile getGraphFile();
    
    /**
     * @return graph editor
     */
    public IEditorPart getEditorPart();
    
    /**
     * @return current side bar
     */
    public ISideBar getSideBar();
    
    /**
     * @return current status bar
     */
    public IStatusBar getStatusBar();
    
    /**
     * @return current diagram's title
     */
    public String getTitle();

    /**
     * Gets the fileName property.
     * 
     * @return the file path
     */
    public String getFilePath();

    /**
     * Sets the fileName property.
     * 
     * @param path the file path
     */
    public void setFilePath(String path);

    /**
     * Registers a listener on this diagram panel to capture events
     * 
     * @param l
     */
    public void addListener(IWorkspaceListener l);

    /**
     * @return unique id
     */
    public Id getId();
    
   
    /**
     * @return the awt component representing this workspace
     */
    public Component getAWTComponent();

}