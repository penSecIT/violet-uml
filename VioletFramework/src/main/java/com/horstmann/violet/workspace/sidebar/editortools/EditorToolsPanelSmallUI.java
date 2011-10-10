package com.horstmann.violet.workspace.sidebar.editortools;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.PanelUI;

import com.horstmann.violet.framework.swingextension.IconButtonUI;
import com.horstmann.violet.framework.theme.ThemeManager;

/**
 * UI for displaying a small EditorToolsPanel
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class EditorToolsPanelSmallUI extends PanelUI
{

    /**
     * Default constructor
     * 
     * @param editorToolsPanel
     */
    public EditorToolsPanelSmallUI(EditorToolsPanel editorToolsPanel)
    {
        this.editorToolsPanel = editorToolsPanel;
    }

    @Override
    public void installUI(JComponent c)
    {
        c.removeAll();
        c.setBackground(ThemeManager.getInstance().getTheme().getSidebarElementBackgroundColor());

        this.editorToolsPanel.getZoomInButton().setUI(new IconButtonUI(SMALLSIZE_SCALING_FACTOR));
        this.editorToolsPanel.getZoomOutButton().setUI(new IconButtonUI(SMALLSIZE_SCALING_FACTOR));
        this.editorToolsPanel.getUndoButton().setUI(new IconButtonUI(SMALLSIZE_SCALING_FACTOR));
        this.editorToolsPanel.getRedoButton().setUI(new IconButtonUI(SMALLSIZE_SCALING_FACTOR));
        this.editorToolsPanel.getDeleteButton().setUI(new IconButtonUI(SMALLSIZE_SCALING_FACTOR));

        c.setLayout(new FlowLayout(FlowLayout.CENTER));
        c.add(getToolsPanel());
    }

    /**
     * @return the main panel
     */
    private JPanel getToolsPanel()
    {
        if (this.toolsPanel == null)
        {
            this.toolsPanel = new JPanel();
            this.toolsPanel.setOpaque(false);
            this.toolsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
            this.toolsPanel.add(this.editorToolsPanel.getUndoButton());
            this.toolsPanel.add(this.editorToolsPanel.getZoomInButton());
            this.toolsPanel.add(this.editorToolsPanel.getZoomOutButton());
            this.toolsPanel.add(this.editorToolsPanel.getDeleteButton());
            this.toolsPanel.add(this.editorToolsPanel.getRedoButton());

            this.editorToolsPanel.getUndoButton().setBorder(new EmptyBorder(5, 0, 5, 0));
            this.editorToolsPanel.getRedoButton().setBorder(new EmptyBorder(5, 0, 5, 0));
            this.editorToolsPanel.getZoomInButton().setBorder(new EmptyBorder(5, 0, 5, 0));
            this.editorToolsPanel.getZoomOutButton().setBorder(new EmptyBorder(5, 0, 5, 0));
            this.editorToolsPanel.getDeleteButton().setBorder(new EmptyBorder(5, 0, 5, 0));

            BoxLayout boxLayout = new BoxLayout(this.toolsPanel, BoxLayout.Y_AXIS);
            this.toolsPanel.setLayout(boxLayout);
        }
        return this.toolsPanel;
    }

    /**
     * Small size icon scaling factor
     */
    private static final double SMALLSIZE_SCALING_FACTOR = 0.8;

    /**
     * Panel containing tools
     */
    private JPanel toolsPanel;

    /**
     * Main panel
     */
    private EditorToolsPanel editorToolsPanel;

}
