package com.horstmann.violet.product.diagram.activity;

import java.util.Locale;
import java.util.ResourceBundle;

import com.horstmann.violet.framework.plugin.IDiagramPlugin;
import com.horstmann.violet.product.diagram.abstracts.IGraph;

/**
 * Describes activity diagram graph type
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class ActivityDiagramPlugin implements IDiagramPlugin
{

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.plugin.AbstractPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Activity UML diagram";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.plugin.AbstractPlugin#getProvider()
     */
    public String getProvider()
    {
        return "Alexandre de Pellegrin / Cays S. Horstmann";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.plugin.AbstractPlugin#getVersion()
     */
    public String getVersion()
    {
        return "1.0.0";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.GraphType#getName()
     */
    public String getName()
    {
        return this.rs.getString("menu.activity_diagram.text");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.GraphType#getFileExtension()
     */
    public String getFileExtension()
    {
        return this.rs.getString("files.activity.extension");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.GraphType#getFileExtensionName()
     */
    public String getFileExtensionName()
    {
        return this.rs.getString("files.activity.name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.GraphType#getGraphClass()
     */
    public Class<? extends IGraph> getGraphClass()
    {
        return ActivityDiagramGraph.class;
    }
    
    
    private ResourceBundle rs = ResourceBundle.getBundle(ActivityDiagramConstant.ACTIVITY_DIAGRAM_STRINGS, Locale.getDefault());
    

}
