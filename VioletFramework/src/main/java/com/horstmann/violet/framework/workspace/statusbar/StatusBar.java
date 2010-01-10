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

package com.horstmann.violet.framework.workspace.statusbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.horstmann.violet.framework.resources.ResourceBundleConstant;
import com.horstmann.violet.framework.resources.ResourceFactory;
import com.horstmann.violet.framework.swingextension.CustomToggleButton;
import com.horstmann.violet.framework.swingextension.LinkButtonUI;
import com.horstmann.violet.framework.theme.ITheme;
import com.horstmann.violet.framework.theme.ThemeManager;
import com.horstmann.violet.framework.workspace.IWorkspace;
import com.horstmann.violet.framework.workspace.sidebar.ISideBar;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphTool;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphToolsBar;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.IGraphToolsBar;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.IGraphToolsBarListener;

public class StatusBar extends JPanel implements IStatusBar
{

    /**
     * Default constructor
     * 
     * @param diagram panel embedding this status bar
     */
    public StatusBar(final IWorkspace workspace)
    {
        ISideBar sideBar = workspace.getSideBar();
        setLayout(new GridBagLayout());
        JPanel toolViewer = getDiagramToolViewer((GraphToolsBar) sideBar.getGraphToolsBar());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 4, 3, 5);
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        add(toolViewer, c);
        JButton sideBarLink = getSideBarLink(workspace);
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(2, 5, 3, 5);
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        add(sideBarLink, c);
        ITheme cLAF = ThemeManager.getInstance().getTheme();
        setBackground(cLAF.getStatusbarBackgroundColor());
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    /**
     * Create side bar link to show / hide it
     * 
     * @param diagram workspace
     * @return link label
     */
    private JButton getSideBarLink(final IWorkspace workspace)
    {
        JButton link = getResourceFactory().createButton("sidebarlink");
        link.setUI(new LinkButtonUI());
        link.setForeground(ThemeManager.getInstance().getTheme().getMenubarForegroundColor());
        link.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                workspace.getSideBar().reduceOrMaximizeSize();
            }
        });
        return link;
    }

    private JPanel getDiagramToolViewer(final IGraphToolsBar sideToolPanel)
    {
        GraphTool selectedTool = sideToolPanel.getSelectedTool();
        ITheme cLAF = ThemeManager.getInstance().getTheme();
        final CustomToggleButton button = new CustomToggleButton(selectedTool.getLabel(), selectedTool.getIcon(), cLAF
                .getToggleButtonSelectedColor(), cLAF.getToggleButtonSelectedBorderColor(), cLAF
                .getToggleButtonUnselectedColor());
        button.setSelected(true);
        button.setPreferredSize(new Dimension(GRAPH_TOOL_VIEWER_WIDTH, (int) button.getPreferredSize().getHeight()));
        sideToolPanel.addListener(new IGraphToolsBarListener()
        {
            public void toolSelectionChanged(GraphTool tool)
            {
                final String text = tool.getLabel();
                final Icon icon = tool.getIcon();
                button.setText(text);
                button.setIcon(icon);
                button.setToolTipText(text);
                button.repaint();
            }
        });
        return button;
    }

    /**
     * @return resource factory associated to statusString.properties file
     */
    private ResourceFactory getResourceFactory()
    {
        if (this.resourceFactory == null)
        {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(ResourceBundleConstant.STATUSBAR_STRINGS, Locale.getDefault());
            this.resourceFactory = new ResourceFactory(resourceBundle);
        }
        return this.resourceFactory;
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.framework.gui.IStatusBar#getAWTComponent()
     */
    public Component getAWTComponent() {
        return this;
    }

    /**
     * Resource factory instance
     */
    private ResourceFactory resourceFactory;

    private static final int GRAPH_TOOL_VIEWER_WIDTH = 350;

}
