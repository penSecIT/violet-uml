package com.horstmann.violet.product.workspace;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.horstmann.violet.framework.display.swingextension.TinyScrollBarUI;
import com.horstmann.violet.framework.display.theme.ThemeManager;
import com.horstmann.violet.product.workspace.editorpart.IEditorPart;
import com.horstmann.violet.product.workspace.sidebar.ISideBar;
import com.horstmann.violet.product.workspace.statusbar.IStatusBar;

public class WorkspacePanel extends JPanel
{

    public WorkspacePanel(IWorkspace workspace)
    {
        this.workspace = workspace;
    }

    public void prepareLayout()
    {
        LayoutManager layout = new BorderLayout();
        setLayout(layout);

        JScrollPane scrollGPanel = getScrollableEditorPart();
        add(scrollGPanel, BorderLayout.CENTER);
        JScrollPane scrollSideBarPanel = getScrollableSideBar();
        add(scrollSideBarPanel, BorderLayout.EAST);
        JScrollPane scrollStatusBarPanel = getScrollableStatusBar();
        add(scrollStatusBarPanel, BorderLayout.SOUTH);

        refreshDisplay();
    }

    
    

    /**
     * @return the scrollable panel containing the editor
     */
    public JScrollPane getScrollableEditorPart()
    {
        if (this.scrollableEditorPart == null)
        {
            IEditorPart editorPart = this.workspace.getEditorPart();
            Component panel = editorPart.getAWTComponent();
            panel.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    if (e.getButton() == MouseEvent.BUTTON3)
                    {
                        WorkspacePanel.this.workspace.getSideBar().getGraphToolsBar().reset();
                    }
                }
            });
            panel.addMouseWheelListener(new MouseWheelListener()
            {
                public void mouseWheelMoved(MouseWheelEvent e)
                {
                    int scroll = e.getUnitsToScroll();
                    if (scroll > 0)
                    {
                        workspace.getSideBar().getGraphToolsBar().selectNextTool();
                    }
                    if (scroll < 0)
                    {
                        workspace.getSideBar().getGraphToolsBar().selectPreviousTool();
                    }
                }
            });
            panel.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent e)
                {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    {
                        workspace.getSideBar().getGraphToolsBar().reset();
                    }
                }
            });

            this.scrollableEditorPart = new JScrollPane(panel);
            this.scrollableEditorPart.setBackground(ThemeManager.getInstance().getTheme().getWhiteColor());
            this.scrollableEditorPart.setBorder(new EmptyBorder(0, 0, 0, 0));
        }
        return this.scrollableEditorPart;
    }

    /**
     * @param workspace TODO
     * @return scrollpane containing sidebar
     */
    public JScrollPane getScrollableSideBar()
    {
        if (this.scrollableSideBar == null)
        {
            ISideBar sideBar = this.workspace.getSideBar();
            this.scrollableSideBar = new JScrollPane(sideBar.getAWTComponent());
            this.scrollableSideBar.setAlignmentY(Component.TOP_ALIGNMENT);
            this.scrollableSideBar.getHorizontalScrollBar().setUI(new TinyScrollBarUI());
            this.scrollableSideBar.getVerticalScrollBar().setUI(new TinyScrollBarUI());
            this.scrollableSideBar.setBorder(new MatteBorder(0, 1, 0, 0, ThemeManager.getInstance().getTheme()
                    .getSidebarBorderColor()));
            this.scrollableSideBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            this.scrollableSideBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return this.scrollableSideBar;
    }

    /**
     * @return scrollpane containing status bar
     */
    JScrollPane getScrollableStatusBar()
    {
        if (this.scrollableStatusBar == null)
        {
            IStatusBar statusBar = this.workspace.getStatusBar();
            this.scrollableStatusBar = new JScrollPane(statusBar.getAWTComponent());
            this.scrollableStatusBar.getHorizontalScrollBar().setUI(new TinyScrollBarUI());
            this.scrollableStatusBar.getVerticalScrollBar().setUI(new TinyScrollBarUI());
            this.scrollableStatusBar.setBorder(new MatteBorder(1, 0, 0, 0, ThemeManager.getInstance().getTheme()
                    .getStatusbarBorderColor()));
            this.scrollableStatusBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.scrollableStatusBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        }
        return this.scrollableStatusBar;
    }


    public void refreshDisplay()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                WorkspacePanel.this.revalidate();
                WorkspacePanel.this.doLayout();
                WorkspacePanel.this.repaint();
            }
        });
    }



    private IWorkspace workspace;
    private JScrollPane scrollableSideBar;
    private JScrollPane scrollableEditorPart;
    private JScrollPane scrollableStatusBar;


}
