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

package com.horstmann.violet.framework.file.chooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.horstmann.violet.framework.display.dialog.DialogFactory;
import com.horstmann.violet.framework.file.IFile;
import com.horstmann.violet.framework.file.LocalFile;
import com.horstmann.violet.framework.file.naming.ExtensionFilter;
import com.horstmann.violet.framework.file.naming.FileNamingService;
import com.horstmann.violet.framework.file.persistence.IFileReader;
import com.horstmann.violet.framework.file.persistence.IFileWriter;
import com.horstmann.violet.framework.file.persistence.JFileReader;
import com.horstmann.violet.framework.file.persistence.JFileWriter;
import com.horstmann.violet.framework.injection.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.injection.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.userpreferences.UserPreferencesService;

/**
 * This class implements a FileService with a JFileChooser
 */
public class JFileChooserService implements IFileChooserService
{

    public JFileChooserService(UserPreferencesService userPreferencesService, FileNamingService fileNamingService, DialogFactory dialogFactory)
    {
        ResourceBundleInjector.getInjector().inject(this);
        this.userPreferencesService = userPreferencesService;
        this.fileNamingService = fileNamingService;
        this.dialogFactory = dialogFactory;
        File initialDirectory = getLastOpenedDir();
        fileChooser.setCurrentDirectory(initialDirectory);
    }

    /**
     * @return the last opened file directory or the current directory if no one is found
     */
    private File getLastOpenedDir()
    {
        List<IFile> recentFiles = this.userPreferencesService.getRecentFiles();
        File lastDir = new File(".");
        if (recentFiles.size() > 0)
        {
            try
            {
                LocalFile localFile;
                localFile = new LocalFile(recentFiles.get(0));
                lastDir = new File(localFile.getDirectory());
            }
            catch (IOException e)
            {
                // Log here
            }
        }
        return lastDir;
    }

    @Override
    public boolean isWebStart()
    {
        return false;
    }

    @Override
    public IFileReader getFileReader(IFile file) throws FileNotFoundException
    {
        try
        {
            LocalFile localFile = new LocalFile(file);
            File physicalFile = localFile.toFile();
            if (physicalFile.exists() && physicalFile.isFile())
            {
                IFileReader foh = new JFileReader(physicalFile);
                return foh;
            }
            else
            {
                throw new FileNotFoundException("File " + file.getFilename() + " not reachable from " + file.getDirectory());
            }
        }
        catch (IOException e1)
        {
            throw new FileNotFoundException(e1.getLocalizedMessage());
        }
    }

    @Override
    public IFileReader chooseAndGetFileReader() throws FileNotFoundException
    {
        ExtensionFilter[] filters = fileNamingService.getFileFilters();
        fileChooser.resetChoosableFileFilters();
        for (int i = 0; i < filters.length; i++)
        {
            fileChooser.addChoosableFileFilter(filters[i]);
        }
        List<IFile> recentFiles = this.userPreferencesService.getRecentFiles();
        if (!recentFiles.isEmpty())
        {
            IFile lastEntryFile = recentFiles.get(recentFiles.size() - 1);
            try
            {
                LocalFile lastSavedFile = new LocalFile(lastEntryFile);
                File physicalFile = lastSavedFile.toFile();
                File directory = physicalFile.getParentFile();
                fileChooser.setCurrentDirectory(directory);
            }
            catch (IOException e)
            {
                // Nothing to do
            }
        }
        int response = fileChooser.showOpenDialog(null);
        File selectedFile = null;
        if (response == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = fileChooser.getSelectedFile();
        }
        if (selectedFile == null)
        {
            return null;
        }
        IFileReader foh = new JFileReader(selectedFile);
        return foh;
    }

    @Override
    public IFileWriter getFileWriter(IFile file) throws FileNotFoundException
    {
        try
        {
            LocalFile localFile = new LocalFile(file);
            IFileWriter fsh = new JFileWriter(localFile.toFile());
            return fsh;
        }
        catch (IOException e)
        {
            throw new FileNotFoundException(e.getLocalizedMessage());
        }
    }

    @Override
    public IFileWriter chooseAndGetFileWriter(ExtensionFilter... filters) throws FileNotFoundException
    {
        fileChooser.resetChoosableFileFilters();
        for (int i = 0; i < filters.length; i++)
        {
            fileChooser.addChoosableFileFilter(filters[i]);
        }
        List<IFile> recentFiles = this.userPreferencesService.getRecentFiles();
        if (!recentFiles.isEmpty())
        {
            IFile lastEntryFile = recentFiles.get(recentFiles.size() - 1);
            try
            {
                LocalFile lastSavedFile = new LocalFile(lastEntryFile);
                File physicalFile = lastSavedFile.toFile();
                File directory = physicalFile.getParentFile();
                fileChooser.setCurrentDirectory(directory);
            }
            catch (IOException e)
            {
                // Nothing to do
            }
        }
        int response = fileChooser.showSaveDialog(null);
        File selectedFile = null;
        if (response == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = fileChooser.getSelectedFile();
            ExtensionFilter selectedFilter = (ExtensionFilter) fileChooser.getFileFilter();
            String fullPath = selectedFile.getAbsolutePath();
            String extension = selectedFilter.getExtension();
            if (!fullPath.toLowerCase().endsWith(extension)) {
                fullPath = fullPath + extension;
                selectedFile = new File(fullPath);
            }
            if (selectedFile.exists())
            {
                JOptionPane optionPane = new JOptionPane();
                optionPane.setMessage(this.overwriteDialogBoxMessage);
                optionPane.setOptionType(JOptionPane.YES_NO_OPTION);
                optionPane.setIcon(this.overwriteDialogBoxIcon);
                this.dialogFactory.showDialog(optionPane, this.overwriteDialogBoxTitle, true);

                int result = JOptionPane.NO_OPTION;
                if (!JOptionPane.UNINITIALIZED_VALUE.equals(optionPane.getValue()))
                {
                    result = ((Integer) optionPane.getValue()).intValue();
                }

                if (result == JOptionPane.NO_OPTION)
                {
                    selectedFile = null;
                }
            }
        }
        if (selectedFile == null)
        {
            return null;
        }
        IFileWriter fsh = new JFileWriter(selectedFile);
        return fsh;
    }    
    
    
    private JFileChooser fileChooser = new JFileChooser();

    private UserPreferencesService userPreferencesService;
    
    private FileNamingService fileNamingService;
    
    private DialogFactory dialogFactory;

    @ResourceBundleBean(key="dialog.overwrite.ok")
    private String overwriteDialogBoxMessage;

    @ResourceBundleBean(key="dialog.overwrite.title")
    private String overwriteDialogBoxTitle;

    @ResourceBundleBean(key="dialog.overwrite.icon")
    private ImageIcon overwriteDialogBoxIcon;



}