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

package com.horstmann.violet.application.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.horstmann.violet.application.gui.MainFrame;
import com.horstmann.violet.framework.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;
import com.horstmann.violet.framework.workspace.editorpart.behavior.CutCopyPasteBehavior;
import com.horstmann.violet.framework.workspace.editorpart.behavior.IEditorPartBehavior;
import com.horstmann.violet.framework.workspace.editorpart.behavior.UndoRedoBehavior;

/**
 * Edit menu
 * 
 * @author Alexandre de Pellegrin
 * 
 */
@ResourceBundleBean(resourceReference = MenuFactory.class)
public class EditMenu extends JMenu
{

    /**
     * Default constructor
     * 
     * @param mainFrame where is attached this menu
     * @param factory for accessing to external resources
     */
    @ResourceBundleBean(key = "edit")
    public EditMenu(final MainFrame mainFrame)
    {
        ResourceBundleInjector.getInjector().inject(this);
        this.mainFrame = mainFrame;
        this.createMenu();
    }

    /**
     * Initializes menu
     */
    private void createMenu()
    {
        undo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                UndoRedoBehavior undoRedoBehavior = getUndoRedoBehavior(getActiveEditorPart());
                if (undoRedoBehavior != null) undoRedoBehavior.undo();
            }
        });
        this.add(undo);

        redo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                UndoRedoBehavior undoRedoBehavior = getUndoRedoBehavior(getActiveEditorPart());
                if (undoRedoBehavior != null) undoRedoBehavior.redo();
            }
        });
        this.add(redo);

        cut.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                CutCopyPasteBehavior cutCopyPasteBehavior = getCutCopyPasteBehavior(getActiveEditorPart());
                if (cutCopyPasteBehavior != null) cutCopyPasteBehavior.cut();
            }
        });
        this.add(cut);

        copy.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                CutCopyPasteBehavior cutCopyPasteBehavior = getCutCopyPasteBehavior(getActiveEditorPart());
                if (cutCopyPasteBehavior != null) cutCopyPasteBehavior.copy();
            }
        });
        this.add(copy);

        paste.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                CutCopyPasteBehavior cutCopyPasteBehavior = getCutCopyPasteBehavior(getActiveEditorPart());
                if (cutCopyPasteBehavior != null) cutCopyPasteBehavior.paste();
            }
        });
        this.add(paste);

        delete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (isThereAnyWorkspaceDisplayed()) getActiveEditorPart().removeSelected();
            }
        });
        this.add(delete);

        selectNext.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (isThereAnyWorkspaceDisplayed()) getActiveEditorPart().selectAnotherGraphElement(1);
            }
        });
        this.add(selectNext);

        selectPrevious.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (isThereAnyWorkspaceDisplayed()) getActiveEditorPart().selectAnotherGraphElement(-1);
            }
        });
        this.add(selectPrevious);

    }

    /**
     * @return current editor
     */
    private IEditorPart getActiveEditorPart()
    {
        return this.mainFrame.getActiveWorkspace().getEditorPart();
    }

    /**
     * @return true id at least one workspace is reachable
     */
    private boolean isThereAnyWorkspaceDisplayed()
    {
        return mainFrame.getWorkspaceList().size() > 0;
    }
    
    /**
     * @param activeEditorPart
     * @return current editor undo/redo behavior or null if not found
     */
    private UndoRedoBehavior getUndoRedoBehavior(IEditorPart activeEditorPart) {
        if (!isThereAnyWorkspaceDisplayed()) {
            return null;
        }
        for (IEditorPartBehavior behavior : activeEditorPart.getBehaviorManager().getBehaviors()) {
            if (behavior instanceof UndoRedoBehavior) {
                return (UndoRedoBehavior) behavior;
            }
        }
        return null;
    }
    
    /**
     * @param activeEditorPart
     * @return current editor cut/copy/paste behavior or null if not found
     */
    private CutCopyPasteBehavior getCutCopyPasteBehavior(IEditorPart activeEditorPart) {
        if (!isThereAnyWorkspaceDisplayed()) {
            return null;
        }
        for (IEditorPartBehavior behavior : getActiveEditorPart().getBehaviorManager().getBehaviors()) {
            if (behavior instanceof CutCopyPasteBehavior) {
                return (CutCopyPasteBehavior) behavior;
            }
        }
        return null;
    }
    

    /** Application frame */
    private MainFrame mainFrame;

    @ResourceBundleBean(key = "edit.undo")
    private JMenuItem undo;

    @ResourceBundleBean(key = "edit.redo")
    private JMenuItem redo;

    @ResourceBundleBean(key = "edit.cut")
    private JMenuItem cut;

    @ResourceBundleBean(key = "edit.copy")
    private JMenuItem copy;

    @ResourceBundleBean(key = "edit.paste")
    private JMenuItem paste;

    @ResourceBundleBean(key = "edit.delete")
    private JMenuItem delete;

    @ResourceBundleBean(key = "edit.select_next")
    private JMenuItem selectNext;

    @ResourceBundleBean(key = "edit.select_previous")
    private JMenuItem selectPrevious;
}
