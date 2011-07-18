package com.horstmann.violet.framework.userpreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.framework.file.IFile;
import com.horstmann.violet.framework.injection.bean.annotation.InjectableBean;
import com.horstmann.violet.framework.injection.bean.annotation.InjectedBean;
import com.horstmann.violet.framework.display.theme.VistaBlueTheme;


/**
 * Manages all user preferences
 * 
 * @author alexandre de pellegrin
 *
 */
@InjectableBean
public class UserPreferencesService
{


    public void setPreferedLookAndFeel(String className)
    {
        IUserPreferencesDao pService = PreferencesServiceFactory.getInstance();
        pService.put(PreferencesConstant.LOOK_AND_FEEL, className);
    }

    public String getPreferedLookAndFeel()
    {
        IUserPreferencesDao pService = PreferencesServiceFactory.getInstance();
        String preferedLAF = pService.get(PreferencesConstant.LOOK_AND_FEEL, VistaBlueTheme.class.getName());
        return preferedLAF;
    }
    
    public Boolean isSmallSideBarPreferred() {
        return new Boolean(this.dao.get(PreferencesConstant.SMALL_SIDEBAR_PREFERRED, Boolean.FALSE.toString()));
    }
    
    public void setSmallSideBarPreferred(Boolean isSmallSideBarPreferred) {
        this.dao.put(PreferencesConstant.SMALL_SIDEBAR_PREFERRED, isSmallSideBarPreferred.toString());
    }
    
    /**
     * @return the list of lastest opened files (as path strings)
     */
    public List<IFile> getRecentFiles()
    {
        List<PreferredFile> recentFiles = new ArrayList<PreferredFile>();
        String recent = this.dao.get(PreferencesConstant.RECENT_FILES, "").trim();
        String[] strings = recent.split(PreferencesConstant.FILE_SEPARATOR.toString());
        for (String anEntry : strings)
        {
            try
            {
                PreferredFile aRecentFile  = new PreferredFile(anEntry);
                recentFiles.add(aRecentFile);
            }
            catch (IOException e)
            {
                // Nothing to do, will be automatically deleted on dao.put() action
            }
        }
        updateRecentFileList(recentFiles);
        return new ArrayList<IFile>(recentFiles);
    }
    
    
    /**
     * add recently opened file into user preferences
     * 
     * @param opened file
     */
    public void addRecentFile(IFile aFile)
    {
        PreferredFile newPreferredFile = new PreferredFile(aFile);
        List<PreferredFile> recentFileList = new ArrayList<PreferredFile>();
        for (IFile file : getRecentFiles()) {
            recentFileList.add(new PreferredFile(file));
        }
        recentFileList.add(newPreferredFile);
        while (recentFileList.size() > DEFAULT_MAX_RECENT_FILES) {
            recentFileList.remove(0);
        }
        updateRecentFileList(recentFileList);
    }
    
    /**
     * Update user preferences
     * @param recentFiles
     */
    private void updateRecentFileList(List<PreferredFile> recentFiles) {
        StringBuilder result = new StringBuilder("");
        for (IFile aFile : recentFiles) {
            PreferredFile aPreferredFile = new PreferredFile(aFile);
            result.append(aPreferredFile.toString()).append(PreferencesConstant.FILE_SEPARATOR.toString());
        }
        this.dao.put(PreferencesConstant.RECENT_FILES, result.toString());
    }
    
    /**
     * Gets opened files on last session. Used to restore workspace after restart
     * 
     * @return file list
     */
    public List<IFile> getOpenedFilesDuringLastSession()
    {
        String list = this.dao.get(PreferencesConstant.OPENED_FILES_ON_WORKSPACE, "");
        String[] strings = list.split(PreferencesConstant.FILE_SEPARATOR.toString());
        List<PreferredFile> result = new ArrayList<PreferredFile>();
        for (String anEntry : strings)
        {
            try {
				PreferredFile aFile = new PreferredFile(anEntry);
                result.add(aFile);
            } catch (IOException a) {
                // We should purge list from unparsable entries
            }
        }
        updateOpenedFileList(result);
        return new ArrayList<IFile>(result);
    }

    /**
     * Saves newly opened file path into user preferences
     * 
     * @param path file path (should be relative or absolute)
     */
    public void addOpenedFile(IFile aFile)
    {
        PreferredFile newPreferredFile = new PreferredFile(aFile);
        List<PreferredFile> openedFileList = new ArrayList<PreferredFile>();
        for (IFile file : getOpenedFilesDuringLastSession()) {
            openedFileList.add(new PreferredFile(file));
        }
        openedFileList.add(newPreferredFile);
        updateOpenedFileList(openedFileList);
    }
    
    
    /**
     * Removes newly closed file from user preferences
     * 
     * @param path file path (could be relative or absolute)
     */
    public void removeOpenedFile(IFile aFile)
    {
        PreferredFile newPreferredFile = new PreferredFile(aFile);
        List<PreferredFile> openedFileList = new ArrayList<PreferredFile>();
        for (IFile file : getOpenedFilesDuringLastSession()) {
            openedFileList.add(new PreferredFile(file));
        }
        openedFileList.remove(newPreferredFile);
        updateOpenedFileList(openedFileList);
    }

    
    /**
     * Update user preferences
     * @param recentFiles
     */
    private void updateOpenedFileList(List<PreferredFile> openedFiles) {
        StringBuilder result = new StringBuilder("");
        for (PreferredFile aPreferredFile : openedFiles) {
            result.append(aPreferredFile.toString()).append(PreferencesConstant.FILE_SEPARATOR.toString());
        }
        this.dao.put(PreferencesConstant.OPENED_FILES_ON_WORKSPACE, result.toString());
    }
    


    



    /**
     * Indicates which diagram is currently focused on workspace and saves it into user preferences
     * 
     * @param path file path (could be relative or absolute)
     */
    public void setActiveDiagramFile(IFile aFile)
    {
        if (aFile != null)
        {
            PreferredFile preferredFile = new PreferredFile(aFile);
            this.dao.put(PreferencesConstant.ACTIVE_FILE, preferredFile.toString());
        }
    }

    /**
     * Gets from user preferences which diagram was setted as focused
     * 
     * @return file path (could be relative or absolute). Returns null by default.
     */
    public IFile getActiveDiagramFile()
    {
        String entry = this.dao.get(PreferencesConstant.ACTIVE_FILE, "");
        IFile aFile = null;
        try
        {
            aFile = new PreferredFile(entry);
        }
        catch (IOException e)
        {
            // TODO : logger needed
        }
        return aFile;
    }
    
    /**
     * Clear user preferences
     */
    public void reset() {
        this.dao.reset();
    }
    
    /**
     * Allows to store and retrieve preferences
     */
    @InjectedBean
    private IUserPreferencesDao dao;
    
    /**
     * Recent opened files list capacity
     */
    private static final int DEFAULT_MAX_RECENT_FILES = 5;
}
