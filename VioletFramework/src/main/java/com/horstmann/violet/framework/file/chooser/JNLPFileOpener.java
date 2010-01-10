package com.horstmann.violet.framework.file.chooser;

import java.io.IOException;
import java.io.InputStream;

import javax.jnlp.FileContents;

import com.horstmann.violet.framework.preference.IFile;

public class JNLPFileOpener implements IFileOpener
{
    
    public JNLPFileOpener(FileContents contents) {
        this.contents = contents;
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return contents.getInputStream();
    }

    @Override
    public IFile getFileDefinition() throws IOException
    {
        final String name = contents.getName();
        return new IFile() {
            @Override
            public String getDirectory()
            {
                return null;
            }

            @Override
            public String getFilename()
            {
                return name;
            }
        };
    }
    
    
    private FileContents contents;




}
