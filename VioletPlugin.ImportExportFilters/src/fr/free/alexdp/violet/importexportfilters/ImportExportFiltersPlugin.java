/*
 * Projet     : 
 * Package    : fr.free.alexdp.violet.importexportfilters
 * Auteur     : a.depellegrin
 * Cr�� le    : 30 janv. 2008
 */
package fr.free.alexdp.violet.importexportfilters;

import java.io.OutputStream;

import com.horstmann.violet.framework.diagram.Graph;
import com.horstmann.violet.framework.diagram.GraphService;
import com.horstmann.violet.framework.file.ExtensionFilter;
import com.horstmann.violet.framework.file.FileChooserService;
import com.horstmann.violet.framework.file.FileSaverHandler;
import com.horstmann.violet.framework.file.FileService;
import com.horstmann.violet.framework.gui.DiagramPanel;
import com.horstmann.violet.framework.gui.DialogFactory;
import com.horstmann.violet.framework.gui.IDiagramPanel;
import com.horstmann.violet.framework.plugin.DiagramPlugin;

public class ImportExportFiltersPlugin implements DiagramPlugin {

    public void start() {
    }
    
    public IDiagramPanel diagramPanel;
    
    /**
     * Exports given diagram to xmi format.
     * 
     * @param diagramPanel
     * @param fileChooserService
     */
    public void exportToXMI(DiagramPanel diagramPanel, FileChooserService fileChooserService)
    {
        if (diagramPanel == null || diagramPanel.view.getGraphPanel(diagramPanel) == null || diagramPanel.view.getGraphPanel(diagramPanel).getGraph() == null) return;
        Graph graph = diagramPanel.view.getGraphPanel(diagramPanel).getGraph();
        if (graph instanceof ClassDiagramGraph)
        {
            DialogFactory.getInstance().showErrorDialog(this.menuResourceBundle.getString("dialog.export_to_xmi.error"));
            return;
        }
        try
        {
            String xmiExtension = FileService.getXMIFileExtension();
            ExtensionFilter extensionFilter = FileService.getExtensionFilter(diagramPanel.view.getGraphPanel(diagramPanel).getGraph());
            ExtensionFilter exportFilter = FileService.getXMIExtensionFilter();
            FileSaverHandler save = fileChooserService.save(null, diagramPanel.getFilePath(), exportFilter, extensionFilter
                    .getExtensions()[0], xmiExtension);
            OutputStream out = save.getOutputStream();
            if (out != null)
            {
                GraphService.exportToXMI(graph, out);
                out.close();
            }
        }
        catch (Exception e)
        {
            // Well, we tried...
            e.printStackTrace();
        }

    }

}
