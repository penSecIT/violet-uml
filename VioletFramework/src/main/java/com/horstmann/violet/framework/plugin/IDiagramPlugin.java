package com.horstmann.violet.framework.plugin;

import com.horstmann.violet.product.diagram.abstracts.IGraph;

/**
 * Describes a Violet's plugin embedding a new kind of diagram.
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public interface IDiagramPlugin extends AbstractPlugin
{

    /**
     * @return graph type name (ex : Class Diagram)
     */
    public abstract String getName();

    /**
     * @return file extension associated to this graph (ex : .class.violet)
     */
    public abstract String getFileExtension();

    /**
     * @return file extension textual name (ex : Class Diagram Files)
     */
    public abstract String getFileExtensionName();

    /**
     * @return corresponding graph class
     */
    public abstract Class<? extends IGraph> getGraphClass();

}
