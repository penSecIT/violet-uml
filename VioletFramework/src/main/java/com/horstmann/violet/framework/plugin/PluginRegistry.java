package com.horstmann.violet.framework.plugin;

import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.framework.injection.bean.annotation.InjectableBean;

/**
 * Plugin registry
 * 
 * @author Alexandre de Pellegrin
 *
 */
@InjectableBean
public class PluginRegistry
{


    /**
     * Private constructor
     */
    public PluginRegistry()
    {
        // Singleton
    }

    /**
     * Registers a new diagram plugin
     * 
     * @param newDiagramPlugin
     */
    public void register(IDiagramPlugin newDiagramPlugin)
    {
        this.diagramPlugins.add(newDiagramPlugin);
    }
    
    /**
     * @return diagram plugin list
     */
    public List<IDiagramPlugin> getDiagramPlugins() {
        return this.diagramPlugins;
    }

    /** diagram plugins */
    private List<IDiagramPlugin> diagramPlugins = new ArrayList<IDiagramPlugin>();


}
