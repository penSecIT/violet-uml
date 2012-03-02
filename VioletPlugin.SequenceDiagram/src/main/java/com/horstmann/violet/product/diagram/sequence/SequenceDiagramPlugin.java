package com.horstmann.violet.product.diagram.sequence;

import java.awt.geom.Point2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.PersistenceDelegate;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.horstmann.violet.framework.plugin.IDiagramPlugin;
import com.horstmann.violet.framework.plugin.extensionpoint.IFilePersistenceServiceExtentionPoint;
import com.horstmann.violet.framework.plugin.extensionpoint.Violet016FileFilterExtensionPoint;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.Id;
import com.horstmann.violet.product.diagram.abstracts.node.INode;

/**
 * Describes sequence diagram graph type
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class SequenceDiagramPlugin implements IDiagramPlugin, Violet016FileFilterExtensionPoint, IFilePersistenceServiceExtentionPoint
{

    @Override
    public String getDescription()
    {
        return "Sequence UML diagram";
    }

    @Override
    public String getProvider()
    {
        return "Alexandre de Pellegrin / Cays S. Horstmann";
    }

    @Override
    public String getVersion()
    {
        return "1.0.0";
    }

    @Override
    public String getName()
    {
        return this.rs.getString("menu.sequence_diagram.text");
    }

    @Override
    public String getFileExtension()
    {
        return this.rs.getString("files.seq.extension");
    }

    @Override
    public String getFileExtensionName()
    {
        return this.rs.getString("files.seq.name");
    }

    @Override
    public Class<? extends IGraph> getGraphClass()
    {
        return SequenceDiagramGraph.class;
    }

    @Override
    public Map<Class<?>, PersistenceDelegate> getMapOfSpecificPersistenceDelegate() {
    	DefaultPersistenceDelegate customPersistenceDelegate = new DefaultPersistenceDelegate()
        {
            protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out)
            {
                super.initialize(type, oldInstance, newInstance, out);
                INode n = (INode) oldInstance;
                List<INode> children = new ArrayList<INode>(n.getChildren());
                for (int i = 0; i < children.size(); i++)
                {
                    INode c = (INode) children.get(i);
                    int indexOf = children.indexOf(c);
                    out.writeStatement(new Statement(oldInstance, "addChild", new Object[]
                    {
                        c,
                        indexOf
                    }));
                }
                boolean isWriteId = false; // Keep for further refinement
                if (isWriteId)
                {
                    Id id = n.getId();
                    out.writeStatement(new Statement(oldInstance, "setId", new Object[]
                    {
                        id
                    }));
                }
            }
        };
        Map<Class<?>, PersistenceDelegate> customMap = new HashMap<Class<?>, PersistenceDelegate>();
        customMap.put(LifelineNode.class, customPersistenceDelegate);
        customMap.put(ActivationBarNode.class, customPersistenceDelegate);
    	return customMap;
    }
    
    public Map<String, String> getMappingToKeepViolet016Compatibility()
    {
        Map<String, String> replaceMap = new HashMap<String, String>();
//        replaceMap.put("com.horstmann.violet.CallEdge", CallEdge.class.getName());
//        replaceMap.put("com.horstmann.violet.CallNode", ActivationBarNode.class.getName());
//        replaceMap.put("com.horstmann.violet.ImplicitParameterNode", LifelineNode.class.getName());
//        replaceMap.put("com.horstmann.violet.ReturnEdge", ReturnEdge.class.getName());
//        replaceMap.put("com.horstmann.violet.SequenceDiagramGraph", SequenceDiagramGraph.class.getName());
        return replaceMap;
    }
    
    private ResourceBundle rs = ResourceBundle.getBundle(SequenceDiagramConstant.SEQUENCE_DIAGRAM_STRINGS, Locale.getDefault());
    

}
