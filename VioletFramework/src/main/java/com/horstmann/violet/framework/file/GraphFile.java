package com.horstmann.violet.framework.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.horstmann.violet.framework.display.dialog.DialogFactory;
import com.horstmann.violet.framework.file.chooser.IFileChooserService;
import com.horstmann.violet.framework.file.export.FileExportService;
import com.horstmann.violet.framework.file.naming.ExtensionFilter;
import com.horstmann.violet.framework.file.naming.FileNamingService;
import com.horstmann.violet.framework.file.persistence.IFilePersistenceService;
import com.horstmann.violet.framework.file.persistence.IFileReader;
import com.horstmann.violet.framework.file.persistence.IFileWriter;
import com.horstmann.violet.framework.injection.bean.SpringDependencyInjector;
import com.horstmann.violet.framework.injection.bean.annotation.SpringBean;
import com.horstmann.violet.framework.injection.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.injection.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.printer.PrintEngine;
import com.horstmann.violet.product.diagram.abstracts.IGraph;

public class GraphFile implements IGraphFile
{
    /**
     * Creates a new graph file with a new graph instance
     * 
     * @param graphClass
     */
    public GraphFile(Class<? extends IGraph> graphClass)
    {
        ResourceBundleInjector.getInjector().inject(this);
        SpringDependencyInjector.getInjector().inject(this);
        try
        {
            this.graph = graphClass.newInstance();
        }
        catch (Exception e)
        {
            DialogFactory.getInstance().showErrorDialog(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs a graph file from an existing file
     * 
     * @param file
     * 
     */
    public GraphFile(IFile file) throws IOException
    {
        ResourceBundleInjector.getInjector().inject(this);
        SpringDependencyInjector.getInjector().inject(this);
        IFileReader fileOpener = fileChooserService.getFileReader(file);
        if (fileOpener == null)
        {
            throw new IOException("Open file action cancelled by user");
        }
        InputStream in = fileOpener.getInputStream();
        if (in != null)
        {
            this.graph = this.filePersistenceService.read(in);
            this.currentFilename = fileOpener.getFileDefinition().getFilename();
            this.currentDirectory = fileOpener.getFileDefinition().getDirectory();
        }
        else
        {
            throw new IOException("Unable to read file " + fileOpener.getFileDefinition().getFilename() + " from location " + fileOpener.getFileDefinition().getDirectory());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#getGraph()
     */
    public IGraph getGraph()
    {
        return this.graph;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#getFilename()
     */
    public String getFilename()
    {
        return this.currentFilename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#getDirectory()
     */
    public String getDirectory()
    {
        return this.currentDirectory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#setSaveRequired()
     */
    public void setSaveRequired()
    {
        this.isSaveRequired = true;
        fireGraphModified();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#save()
     */
    public void save()
    {
        try
        {
            IFileWriter fileSaver = getFileSaver(false);
            OutputStream outputStream = fileSaver.getOutputStream();
            this.filePersistenceService.write(this.graph, outputStream);
            this.isSaveRequired = false;
            fireGraphSaved();
            this.currentFilename = fileSaver.getFileDefinition().getFilename();
            this.currentDirectory = fileSaver.getFileDefinition().getDirectory();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#saveToNewLocation()
     */
    public void saveToNewLocation()
    {
        try
        {
            IFileWriter fileSaver = getFileSaver(true);
            OutputStream outputStream = fileSaver.getOutputStream();
            this.filePersistenceService.write(this.graph, outputStream);
            this.isSaveRequired = false;
            fireGraphSaved();
            this.currentFilename = fileSaver.getFileDefinition().getFilename();
            this.currentDirectory = fileSaver.getFileDefinition().getDirectory();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a IFileSaver instance. Then, this object allows to save graph content. If the graph has never been saved, the
     * FileChooserService<br/>
     * will open a dialog box to select a location. If not, the returned IFileSaver will automatically be bound to the last saving
     * location.<br/>
     * You can also force the FileChooserService to open the dialog box with the given argument.<br/>
     * 
     * @param isAskedForNewLocation if true, then the FileChooser will open a dialog box to allow to choice a new location
     * @return f
     */
    private IFileWriter getFileSaver(boolean isAskedForNewLocation)
    {
        try
        {
            if (isAskedForNewLocation)
            {
                ExtensionFilter extensionFilter = this.fileNamingService.getExtensionFilter(this.graph);
                ExtensionFilter[] array =
                {
                    extensionFilter
                };
                return this.fileChooserService.chooseAndGetFileWriter(array);
            }
            if (!isAskedForNewLocation)
            {

            }
            return this.fileChooserService.getFileWriter(this);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#addListener(com.horstmann.violet.framework.file.IGraphFileListener)
     */
    public void addListener(IGraphFileListener listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#removeListener(com.horstmann.violet.framework.file.IGraphFileListener)
     */
    public void removeListener(IGraphFileListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }

    /**
     * Sends an event to listeners each time the graph is modified
     */
    private void fireGraphModified()
    {
        synchronized (listeners)
        {
            for (IGraphFileListener listener : listeners)
                listener.onFileModified();
        }
    }

    /**
     * Sends an event to listeners when the graph has been saved
     */
    private void fireGraphSaved()
    {
        synchronized (listeners)
        {
            for (IGraphFileListener listener : listeners)
                listener.onFileSaved();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#exportToClipboard()
     */
    public void exportToClipboard()
    {
        FileExportService.exportToclipBoard(this.graph);
        JOptionPane optionPane = new JOptionPane();
        optionPane.setIcon(this.clipBoardDialogIcon);
        optionPane.setMessage(this.clipBoardDialogMessage);
        optionPane.setName(this.clipBoardDialogTitle);
        DialogFactory.getInstance().showDialog(optionPane, this.clipBoardDialogTitle, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#exportImage(java.io.OutputStream, java.lang.String)
     */
    public void exportImage(OutputStream out, String format)
    {
        if (!ImageIO.getImageWritersByFormatName(format).hasNext())
        {
            MessageFormat formatter = new MessageFormat(this.exportImageErrorMessage);
            String message = formatter.format(new Object[]
            {
                format
            });
            JOptionPane optionPane = new JOptionPane();
            optionPane.setMessage(message);
            this.dialogFactory.showDialog(optionPane, this.exportImageDialogTitle, true);
            return;
        }
        try
        {

            try
            {
                ImageIO.write(FileExportService.getImage(this.graph), format, out);
            }
            finally
            {
                out.close();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.file.IGraphFile#exportToPrinter()
     */
    public void exportToPrinter()
    {
        PrintEngine engine = new PrintEngine(this.graph);
        engine.start();
    }

    private IGraph graph;

    /**
     * Needed to identify the physical file used to save the graph
     */
    private String currentFilename;

    /**
     * Needed to identify the physical file used to save the graph
     */
    private String currentDirectory;

    private boolean isSaveRequired = false;

    @ResourceBundleBean(key = "dialog.export_to_clipboard.icon")
    private ImageIcon clipBoardDialogIcon;

    @ResourceBundleBean(key = "dialog.export_to_clipboard.title")
    private String clipBoardDialogTitle;

    @ResourceBundleBean(key = "dialog.export_to_clipboard.ok")
    private String clipBoardDialogMessage;

    @ResourceBundleBean(key = "dialog.error.unsupported_image")
    private String exportImageErrorMessage;

    @ResourceBundleBean(key = "dialog.error.title")
    private String exportImageDialogTitle;

    @SpringBean(name = "fileChooserService")
    private IFileChooserService fileChooserService;

    @SpringBean
    private FileNamingService fileNamingService;

    @SpringBean
    private IFilePersistenceService filePersistenceService;

    @SpringBean
    private DialogFactory dialogFactory;

    private List<IGraphFileListener> listeners = new ArrayList<IGraphFileListener>();

}
