package com.horstmann.violet.product.workspace.sidebar.graphtools;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.PanelUI;

import com.horstmann.violet.framework.display.theme.ThemeManager;

public class GraphToolsBarPanelSmallUI extends PanelUI
{

    @Override
    public void installUI(JComponent c)
    {
        GraphToolsBarPanel panel = (GraphToolsBarPanel) c;
        panel.removeAll();
        panel.setBackground(ThemeManager.getInstance().getTheme().getSidebarElementBackgroundColor());
        for (GraphToolsBarButton button : panel.getNodeButtons()) {
            button.setTextVisible(false);
        }
        for (GraphToolsBarButton button : panel.getEdgeButtons()) {
            button.setTextVisible(false);
        }
        JPanel nodeButtonsPanel = panel.getNodeButtonsPanel();
        JPanel edgeButtonsPanel = panel.getEdgeButtonsPanel();
        panel.setLayout(new BorderLayout());
        panel.add(nodeButtonsPanel, BorderLayout.NORTH);
        panel.add(edgeButtonsPanel, BorderLayout.SOUTH);
    }
    
}
