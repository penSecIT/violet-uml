package com.horstmann.violet.framework.workspace.sidebar;

import java.awt.Component;

import com.horstmann.violet.framework.workspace.IWorkspace;

/**
 * An element displayed on a side bar. Usually, this is a JPanel
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public interface ISideBarElement
{

    /**
     * Method invoked when this element is added to a sidebar
     * 
     * @param workspace
     */
    public void install(IWorkspace workspace);


    /**
     * Method called to change current element UI to its large size
     */
    public void setLargeUI();

    /**
     * Method called to change current element UI to its small size
     */
    public void setSmallUI();

    /**
     * @return the AWT component representing this side bar
     */
    public Component getAWTComponent();

}
