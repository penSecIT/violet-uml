package com.horstmann.violet.product.diagram.sequence;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.plugin.IDiagramPlugin;
import com.horstmann.violet.framework.plugin.extensionpoint.Violet016FileFilterExtensionPoint;

/**
 * Describes sequence diagram graph type
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class SequenceDiagramPlugin implements IDiagramPlugin, Violet016FileFilterExtensionPoint
{

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.plugin.AbstractPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Sequence UML diagram";
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
     * @see com.horstmann.violet.framework.diagram.GraphType#getName()
     */
    public String getName()
    {
        return this.rs.getString("menu.sequence_diagram.text");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.GraphType#getFileExtension()
     */
    public String getFileExtension()
    {
        return this.rs.getString("files.seq.extension");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.GraphType#getFileExtensionName()
     */
    public String getFileExtensionName()
    {
        return this.rs.getString("files.seq.name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.GraphType#getGraphClass()
     */
    public Class<? extends IGraph> getGraphClass()
    {
        return SequenceDiagramGraph.class;
    }

    public Map<String, String> getMappingToKeepViolet016Compatibility()
    {
        Map<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.put("com.horstmann.violet.CallEdge", CallEdge.class.getName());
        replaceMap.put("com.horstmann.violet.CallNode", ActivationBarNode.class.getName());
        replaceMap.put("com.horstmann.violet.ImplicitParameterNode", LifelineNode.class.getName());
        replaceMap.put("com.horstmann.violet.ReturnEdge", ReturnEdge.class.getName());
        replaceMap.put("com.horstmann.violet.SequenceDiagramGraph", SequenceDiagramGraph.class.getName());
        return replaceMap;
    }
    
    private ResourceBundle rs = ResourceBundle.getBundle(SequenceDiagramConstant.SEQUENCE_DIAGRAM_STRINGS, Locale.getDefault());
    

}
