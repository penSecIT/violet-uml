package com.horstmann.violet.product.diagram.object;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.plugin.IDiagramPlugin;
import com.horstmann.violet.framework.plugin.extensionpoint.Violet016FileFilterExtensionPoint;

/**
 * Describes object diagram graph type
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class ObjectDiagramPlugin implements IDiagramPlugin, Violet016FileFilterExtensionPoint
{

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.plugin.AbstractPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Object UML diagram";
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
        return this.rs.getString("menu.object_diagram.text");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.GraphType#getFileExtension()
     */
    public String getFileExtension()
    {
        return this.rs.getString("files.object.extension");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.GraphType#getFileExtensionName()
     */
    public String getFileExtensionName()
    {
        return this.rs.getString("files.object.name");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.GraphType#getGraphClass()
     */
    public Class<? extends IGraph> getGraphClass()
    {
        return ObjectDiagramGraph.class;
    }

    public Map<String, String> getMappingToKeepViolet016Compatibility()
    {
        Map<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.put("com.horstmann.violet.ObjectDiagramGraph", ObjectDiagramGraph.class.getName());
        replaceMap.put("com.horstmann.violet.FieldNode", FieldNode.class.getName());
        replaceMap.put("com.horstmann.violet.ObjectNode", ObjectNode.class.getName());
        replaceMap.put("com.horstmann.violet.ObjectReferenceEdge", ObjectReferenceEdge.class.getName());
        replaceMap.put("com.horstmann.violet.ObjectRelationshipEdge", ObjectRelationshipEdge.class.getName());
        return replaceMap;
    }
    
    ResourceBundle rs = ResourceBundle.getBundle(ObjectDiagramConstant.OBJECT_DIAGRAM_STRINGS, Locale.getDefault());


}
