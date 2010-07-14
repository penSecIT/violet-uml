package com.horstmann.violet.application;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.horstmann.violet.application.gui.MainFrame;
import com.horstmann.violet.framework.display.dialog.DialogFactory;
import com.horstmann.violet.framework.injection.bean.SpringDependencyInjector;
import com.horstmann.violet.framework.injection.bean.annotation.SpringBean;
import com.horstmann.violet.framework.injection.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.injection.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.userpreferences.UserPreferencesService;
import com.horstmann.violet.product.workspace.IWorkspace;

public class ApplicationStopper
{

    public ApplicationStopper()
    {
        SpringDependencyInjector.getInjector().inject(this);
        ResourceBundleInjector.getInjector().inject(this);
    }

    /**
     * Exits the program if no graphs have been modified or if the user agrees to abandon modified graphs or save its.
     */
    public void exitProgram(MainFrame mainFrame)
    {
        boolean ok = isItReadyToExit(mainFrame);
        if (ok)
        {
            System.exit(0);
        }
    }

    /**
     * Asks user to save changes before exit.
     * 
     * @return true is all is saved either false
     */
    private boolean isItReadyToExit(MainFrame mainFrame)
    {
        List<IWorkspace> dirtyWorkspaceList = new ArrayList<IWorkspace>();
        List<IWorkspace> workspaceList = mainFrame.getWorkspaceList();
        if (workspaceList.size() == 0) return true;
        for (IWorkspace aWorkspacel : workspaceList)
        {
            if (aWorkspacel.isSaveNeeded())
            {
                dirtyWorkspaceList.add(aWorkspacel);
            }
        }
        int unsavedCount = dirtyWorkspaceList.size();
        IWorkspace activeWorkspace = mainFrame.getActiveWorkspace();
        if (unsavedCount > 0)
        {
            // ask user if it is ok to close
            String message = MessageFormat.format(this.dialogExitMessage, new Object[]
            {
                new Integer(unsavedCount)
            });
            JOptionPane optionPane = new JOptionPane(message, JOptionPane.CLOSED_OPTION, JOptionPane.YES_NO_CANCEL_OPTION,
                    this.dialogExitIcon);
            dialogFactory.showDialog(optionPane, this.dialogExitTitle, true);

            int result = JOptionPane.YES_OPTION;
            if (!JOptionPane.UNINITIALIZED_VALUE.equals(optionPane.getValue()))
            {
                result = ((Integer) optionPane.getValue()).intValue();
            }

            if (result == JOptionPane.CANCEL_OPTION)
            {
                return false;
            }
            if (result == JOptionPane.YES_OPTION)
            {
                for (IWorkspace aDirtyWorkspace : dirtyWorkspaceList)
                {
                    aDirtyWorkspace.getGraphFile().save();
                }
                this.userPreferencesService.setActiveDiagramFile(activeWorkspace.getGraphFile());
                return true;
            }
            if (result == JOptionPane.NO_OPTION)
            {
                this.userPreferencesService.setActiveDiagramFile(activeWorkspace.getGraphFile());
                return true;
            }
        }
        if (unsavedCount == 0)
        {
            if (activeWorkspace != null)
            {
                this.userPreferencesService.setActiveDiagramFile(activeWorkspace.getGraphFile());
            }
            return true;
        }
        return false;
    }

    @ResourceBundleBean(key = "dialog.exit.icon")
    private ImageIcon dialogExitIcon;

    @ResourceBundleBean(key = "dialog.exit.ok")
    private String dialogExitMessage;

    @ResourceBundleBean(key = "dialog.exit.title")
    private String dialogExitTitle;

    @SpringBean
    private DialogFactory dialogFactory;

    @SpringBean
    private UserPreferencesService userPreferencesService;

}
