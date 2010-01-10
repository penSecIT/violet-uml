package com.horstmann.violet.framework.file.chooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.horstmann.violet.framework.file.LocalFile;
import com.horstmann.violet.framework.preference.IFile;
/**
 * Standard Java FileSaver implementation
 * 
 * @author Alexandre de Pellegrin
 *
 */
public class JFileSaver implements IFileSaver
{

    public JFileSaver(File f) throws FileNotFoundException
    {
        this.f = f;
        this.out = new FileOutputStream(f);
    }

    
    @Override
    public OutputStream getOutputStream()
    {
        return out;
    }

    @Override
    public IFile getFileDefinition() throws IOException
    {
        return new LocalFile(this.f);
    }
    

    private File f;
    private OutputStream out;

}