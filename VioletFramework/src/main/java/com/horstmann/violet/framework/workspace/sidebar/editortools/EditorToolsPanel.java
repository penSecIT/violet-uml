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

package com.horstmann.violet.framework.workspace.sidebar.editortools;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.horstmann.violet.framework.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.workspace.IWorkspace;
import com.horstmann.violet.framework.workspace.editorpart.behavior.CutCopyPasteBehavior;
import com.horstmann.violet.framework.workspace.editorpart.behavior.UndoRedoBehavior;
import com.horstmann.violet.framework.workspace.sidebar.ISideBarElement;
import com.horstmann.violet.framework.workspace.sidebar.SideBar;

@ResourceBundleBean(resourceReference = SideBar.class)
public class EditorToolsPanel extends JPanel implements ISideBarElement
{

    public EditorToolsPanel()
    {
        super();
        ResourceBundleInjector.getInjector().inject(this);
        this.bZoomIn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                workspace.getEditorPart().changeZoom(1);
            }
        });
        this.bZoomOut.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                workspace.getEditorPart().changeZoom(-1);
            }
        });
        this.bUndo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                undoRedoBehavior.undo();
            }
        });
        this.bRedo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                undoRedoBehavior.redo();
            }
        });
        this.bDelete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                workspace.getEditorPart().removeSelected();
            }
        });
        this.bCut.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cutCopyPasteBehavior.cut();
            }
        });
        this.bCopy.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cutCopyPasteBehavior.copy();
            }
        });
        this.bPaste.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cutCopyPasteBehavior.paste();
            }
        });        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.sidebar.ISideBarElement#getTitle()
     */
    public String getTitle()
    {
        return this.title;
    }


    /* (non-Javadoc)
     * @see com.horstmann.violet.framework.workspace.sidebar.ISideBarElement#install(com.horstmann.violet.framework.workspace.IWorkspace)
     */
    public void install(IWorkspace workspace)
    {
        this.workspace = workspace;
        this.undoRedoBehavior = new UndoRedoBehavior(workspace.getEditorPart());
        this.cutCopyPasteBehavior = new CutCopyPasteBehavior(workspace.getEditorPart());
        workspace.getEditorPart().addBehavior(undoRedoBehavior);
        workspace.getEditorPart().addBehavior(cutCopyPasteBehavior);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.sidebar.ISideBarElement#getAWTComponent()
     */
    public Component getAWTComponent()
    {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.sidebar.ISideBarElement#setLargeUI()
     */
    public void setLargeUI()
    {
        this.setUI(new EditorToolsPanelLargeUI(this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.sidebar.ISideBarElement#setSmallUI()
     */
    public void setSmallUI()
    {
        this.setUI(new EditorToolsPanelSmallUI(this));
    }

    /**
     * @return zoom in button
     */
    JButton getZoomInButton()
    {
        return this.bZoomIn;
    }

    /**
     * @return zoom out button
     */
    JButton getZoomOutButton()
    {
        return this.bZoomOut;
    }

    /**
     * @return undo button
     */
    JButton getUndoButton()
    {
        return this.bUndo;
    }

    /**
     * @return redo button
     */
    JButton getRedoButton()
    {
        return this.bRedo;
    }

    /**
     * @return delete button
     */
    JButton getDeleteButton()
    {
        return this.bDelete;
    }

    /**
     * @return cut button
     */
    JButton getCutButton()
    {
        return this.bCut;
    }

    /**
     * @return copy button
     */
    JButton getCopyButton()
    {
        return this.bCopy;
    }

    /**
     * @return paste button
     */
    JButton getPasteButton()
    {
        return this.bPaste;
    }


    /** current workspace */
    private IWorkspace workspace;

    @ResourceBundleBean(key = "zoomin")
    private JButton bZoomIn;
    @ResourceBundleBean(key = "zoomout")
    private JButton bZoomOut;
    @ResourceBundleBean(key = "undo")
    private JButton bUndo;
    @ResourceBundleBean(key = "redo")
    private JButton bRedo;
    @ResourceBundleBean(key = "delete")
    private JButton bDelete;
    @ResourceBundleBean(key = "cut")
    private JButton bCut;
    @ResourceBundleBean(key = "copy")
    private JButton bCopy;
    @ResourceBundleBean(key = "paste")
    private JButton bPaste;
    @ResourceBundleBean(key = "title.standardbuttons.text")
    private String title;
    
    private UndoRedoBehavior undoRedoBehavior;
    private CutCopyPasteBehavior cutCopyPasteBehavior;

}
