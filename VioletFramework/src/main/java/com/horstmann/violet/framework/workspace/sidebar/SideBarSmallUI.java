package com.horstmann.violet.framework.workspace.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.PanelUI;

import com.horstmann.violet.framework.theme.ITheme;
import com.horstmann.violet.framework.theme.ThemeManager;

public class SideBarSmallUI extends PanelUI
{

    
    
    public SideBarSmallUI(SideBar sideBar)
    {
        this.sideBar = sideBar;
    }

    @Override
    public void installUI(JComponent c)
    {
        c.removeAll();
        c.setLayout(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.anchor = GridBagConstraints.NORTH;
        c1.weighty = 0;
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.anchor = GridBagConstraints.NORTH;
        c2.weighty = 0;
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 2;
        c3.fill = GridBagConstraints.VERTICAL;
        c3.anchor = GridBagConstraints.SOUTH;
        c3.weighty = 1;
        
        this.sideBar.getEditorToolsBar().setSmallUI();
        this.sideBar.getGraphToolsBar().setSmallUI();
        
        c.add(this.sideBar.getEditorToolsBar().getAWTComponent(), c1);
        c.add(this.sideBar.getGraphToolsBar().getAWTComponent(), c2);
        c.add(new JLabel(), c3);
        
        ITheme cLAF = ThemeManager.getInstance().getTheme();
        c.setBackground(cLAF.getSidebarElementBackgroundColor());
        c.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.sideBar.doLayout();
        JRootPane rootPane = SwingUtilities.getRootPane(this.sideBar);
        if (rootPane != null) {
            rootPane.repaint();
        }
    }
    
    


    
    private SideBar sideBar;

}
