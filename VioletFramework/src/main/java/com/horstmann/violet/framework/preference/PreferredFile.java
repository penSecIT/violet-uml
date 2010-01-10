package com.horstmann.violet.framework.preference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class allows to wrap a file definition (composed by <br/>
 * a filename and its location which is called directory) <br/>
 * into a string to be stored into user local preferences.
 * 
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class PreferredFile implements IFile
{

    /**
     * Creates a wrapper from a filename and its directory
     * 
     * @param filename
     * @param directory
     */
    public PreferredFile(String directory, String filename)
    {
        this.filename = filename;
        this.directory = directory;
    }

    /**
     * Constructs an instance from a generic IFile. Allows to wrap an IFile into a PreferredFile instance
     * 
     * @param aFile
     */
    public PreferredFile(IFile aFile)
    {
        this.filename = aFile.getFilename();
        this.directory = aFile.getDirectory();
    }

    /**
     * Creates a wrapper from a string from user preferences
     * 
     * @param userPreferenceString
     * @throws IOException if unable to parse input String
     */
    public PreferredFile(String userPreferenceString) throws IOException
    {
        StringTokenizer tokenizer = new StringTokenizer(userPreferenceString, PreferencesConstant.PATH_SEPARATOR.toString());
        if (tokenizer.countTokens() != 2)
        {
            throw new IOException("Unable to parse file path from user preferences");
        }
        List<String> result = new ArrayList<String>();
        while (tokenizer.hasMoreTokens())
        {
            result.add(tokenizer.nextToken());
        }
        this.directory = result.get(0);
        this.filename = result.get(1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.preference.IFile#getFilename()
     */
    public String getFilename()
    {
        return filename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.preference.IFile#getDirectory()
     */
    public String getDirectory()
    {
        return directory;
    }

    @Override
    public String toString()
    {
        return this.directory + PreferencesConstant.PATH_SEPARATOR.toString() + this.filename;
    }

    
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((directory == null) ? 0 : directory.hashCode());
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PreferredFile other = (PreferredFile) obj;
        if (directory == null)
        {
            if (other.directory != null) return false;
        }
        else if (!directory.equals(other.directory)) return false;
        if (filename == null)
        {
            if (other.filename != null) return false;
        }
        else if (!filename.equals(other.filename)) return false;
        return true;
    }



    /**
     * The file name
     */
    private String filename;

    /**
     * Its location
     */
    private String directory;

}
