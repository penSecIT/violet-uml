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

package com.horstmann.violet.framework.workspace.sidebar;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.horstmann.violet.framework.preference.UserPreferencesService;
import com.horstmann.violet.framework.spring.SpringDependencyInjector;
import com.horstmann.violet.framework.spring.annotation.SpringBean;
import com.horstmann.violet.framework.workspace.IWorkspace;
import com.horstmann.violet.framework.workspace.sidebar.editortools.EditorToolsPanel;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphToolsBar;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.IGraphToolsBar;
import com.horstmann.violet.framework.workspace.sidebar.optionaltools.OptionalToolsPanel;

public class SideBar extends JPanel implements ISideBar
{

    public SideBar(IWorkspace diagramPanel)
    {
        SpringDependencyInjector.getInjector().inject(this);
        this.diagramPanel = diagramPanel;
        this.isSmallSize = this.userPreferencesService.isSmallSideBarPreferred();
        setupUI();
    }
    
    private void setupUI() {
        if (this.isSmallSize)
        {
            setUI(new SideBarSmallUI(this));
        }
        else
        {
            setUI(new SideBarLargeUI(this));
        }
    }

    /* (non-Javadoc)
     * @see com.horstmann.violet.framework.gui.sidebar.ISideBar#reduceOrMaximizeSize()
     */
    public void reduceOrMaximizeSize()
    {
        if (this.isSmallSize)
        {
            this.isSmallSize = false;
            this.userPreferencesService.setSmallSideBarPreferred(Boolean.FALSE);
            setupUI();
            return;
        }
        if (!this.isSmallSize)
        {
            this.isSmallSize = true;
            this.userPreferencesService.setSmallSideBarPreferred(Boolean.TRUE);
            setupUI();
            return;
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.sidebar.ISideBar#addElement(com.horstmann.violet.framework.gui.sidebar.ISideBarElement,
     *      java.lang.String)
     */
    public void addElement(ISideBarElement element, String title)
    {
        element.install(this.diagramPanel);
        this.externalContributionElements.put(element, title);
        if (this.isSmallSize)
        {
            setUI(new SideBarSmallUI(this));
        }
        else
        {
            setUI(new SideBarLargeUI(this));
        }
    }

    public IGraphToolsBar getGraphToolsBar()
    {
        if (this.graphToolsBar == null)
        {
            this.graphToolsBar = new GraphToolsBar();
            this.graphToolsBar.install(this.diagramPanel);
        }
        return this.graphToolsBar;
    }

    protected ISideBarElement getEditorToolsBar()
    {
        if (this.editorToolsBar == null)
        {
            this.editorToolsBar = new EditorToolsPanel();
            this.editorToolsBar.install(this.diagramPanel);
        }
        return this.editorToolsBar;
    }

    protected ISideBarElement getOptionalToolsBar()
    {
        if (this.optionalToolsBar == null)
        {
            this.optionalToolsBar = new OptionalToolsPanel();
            this.optionalToolsBar.install(this.diagramPanel);
        }
        return this.optionalToolsBar;
    }

    protected Map<ISideBarElement, String> getExternalContributionElements()
    {
        return this.externalContributionElements;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.sidebar.ISideBar#getAWTComponent()
     */
    public Component getAWTComponent()
    {
        return this;
    }

    private IWorkspace diagramPanel;
    private IGraphToolsBar graphToolsBar;
    private ISideBarElement editorToolsBar;
    private ISideBarElement optionalToolsBar;
    private Map<ISideBarElement, String> externalContributionElements = new HashMap<ISideBarElement, String>();
    private boolean isSmallSize;
    
    @SpringBean(name = "userPreferencesService")
    private UserPreferencesService userPreferencesService;
    
}
