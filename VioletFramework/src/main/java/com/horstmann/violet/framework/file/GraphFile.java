package com.horstmann.violet.framework.file;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.horstmann.violet.framework.diagram.GraphModificationListener;
import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.dialog.DialogFactory;
import com.horstmann.violet.framework.file.chooser.IFileChooserService;
import com.horstmann.violet.framework.file.chooser.IFileOpener;
import com.horstmann.violet.framework.file.chooser.IFileSaver;
import com.horstmann.violet.framework.file.export.FileExportService;
import com.horstmann.violet.framework.file.naming.ExtensionFilter;
import com.horstmann.violet.framework.file.naming.FileNamingService;
import com.horstmann.violet.framework.file.persistence.IFilePersistenceService;
import com.horstmann.violet.framework.preference.IFile;
import com.horstmann.violet.framework.printer.PrintEngine;
import com.horstmann.violet.framework.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.spring.SpringDependencyInjector;
import com.horstmann.violet.framework.spring.annotation.SpringBean;

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
            addGraphModificationListener();
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
        IFileOpener fileOpener = fileChooserService.getFileOpener(file);
        if (fileOpener == null) {
            throw new IOException("Open file action cancelled by user");
        }
        InputStream in = fileOpener.getInputStream();
        if (in != null)
        {
            this.graph = this.filePersistenceService.read(in);
            this.currentFilename = fileOpener.getFileDefinition().getFilename();
            this.currentDirectory = fileOpener.getFileDefinition().getDirectory();
            addGraphModificationListener();
        }
        else
        {
            throw new IOException("Unable to read file " + fileOpener.getFileDefinition().getFilename() + " from location "
                    + fileOpener.getFileDefinition().getDirectory());
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
     * @see com.horstmann.violet.framework.file.IGraphFile#save()
     */
    public void save()
    {
        try
        {
            IFileSaver fileSaver = getFileSaver(false);
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
            IFileSaver fileSaver = getFileSaver(true);
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
    private IFileSaver getFileSaver(boolean isAskedForNewLocation)
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
                return this.fileChooserService.getFileSaver(array);
            }
            if (!isAskedForNewLocation) {
                
            }
            return this.fileChooserService.getFileSaver(this);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a graph listener to detect modifications and set flag isSaveRequired to true
     */
    private void addGraphModificationListener()
    {
        this.graph.addGraphModificationListener(new GraphModificationListener()
        {

            public void childAttached(IGraph g, int index, INode p, INode c)
            {
                isSaveRequired = true;
                fireGraphModified();
            }

            public void childDetached(IGraph g, int index, INode p, INode c)
            {
                isSaveRequired = true;
                fireGraphModified();
            }

            public void edgeAdded(IGraph g, IEdge e, Point2D startPoint, Point2D endPoint)
            {
                isSaveRequired = true;
                fireGraphModified();
            }

            public void edgeRemoved(IGraph g, IEdge e)
            {
                isSaveRequired = true;
                fireGraphModified();
            }

            public void nodeAdded(IGraph g, INode n, Point2D location)
            {
                isSaveRequired = true;
                fireGraphModified();
            }

            public void nodeMoved(IGraph g, INode n, double dx, double dy)
            {
                isSaveRequired = true;
                fireGraphModified();
            }

            public void nodeRemoved(IGraph g, INode n)
            {
                isSaveRequired = true;
                fireGraphModified();
            }

            public void propertyChangedOnNodeOrEdge(IGraph g, PropertyChangeEvent event)
            {
                isSaveRequired = true;
                fireGraphModified();
            }
        });
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
            DialogFactory.getInstance().showDialog(optionPane, this.exportImageDialogTitle, true);
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

    private List<IGraphFileListener> listeners = new ArrayList<IGraphFileListener>();

}
