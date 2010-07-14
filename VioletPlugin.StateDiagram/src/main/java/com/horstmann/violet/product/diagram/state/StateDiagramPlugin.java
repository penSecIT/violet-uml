package com.horstmann.violet.product.diagram.state;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.horstmann.violet.framework.plugin.IDiagramPlugin;
import com.horstmann.violet.framework.plugin.extensionpoint.Violet016FileFilterExtensionPoint;
import com.horstmann.violet.product.diagram.abstracts.IGraph;

/**
 * Describes state diagram graph type
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class StateDiagramPlugin implements IDiagramPlugin, Violet016FileFilterExtensionPoint
{

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.plugin.AbstractPlugin#getDescription()
     */
    public String getDescription()
    {
        return "State UML diagram";
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
        return this.rs.getString("menu.state_diagram.text");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.GraphType#getFileExtension()
     */
    public String getFileExtension()
    {
        return this.rs.getString("files.state.extension");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.GraphType#getFileExtensionName()
     */
    public String getFileExtensionName()
    {
        return this.rs.getString("files.state.name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.GraphType#getGraphClass()
     */
    public Class<? extends IGraph> getGraphClass()
    {
        return StateDiagramGraph.class;
    }

    public Map<String, String> getMappingToKeepViolet016Compatibility()
    {
        Map<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.put("com.horstmann.violet.CircularStateNode", CircularInitialStateNode.class.getName());
        replaceMap.put("com.horstmann.violet.StateDiagramGraph", StateDiagramGraph.class.getName());
        replaceMap.put("com.horstmann.violet.StateNode", StateNode.class.getName());
        replaceMap.put("com.horstmann.violet.StateTransitionEdge", StateTransitionEdge.class.getName());
        return replaceMap;
    }

    private ResourceBundle rs = ResourceBundle.getBundle(StateDiagramConstant.STATE_DIAGRAM_STRINGS, Locale.getDefault());

}
