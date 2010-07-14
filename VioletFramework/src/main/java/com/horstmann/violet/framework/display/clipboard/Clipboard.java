package com.horstmann.violet.framework.display.clipboard;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.horstmann.violet.product.diagram.abstracts.AbstractGraph;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;

public class Clipboard extends AbstractGraph
{
    public void copyIn(IGraph g, List<INode> selectedNodes)
    {
        if (selectedNodes.size() == 0) return;
        
        Rectangle2D bounds = null;
        for (INode n : selectedNodes)
        {
            if (bounds == null) bounds = n.getBounds();
            else bounds.add(n.getBounds());
        }

        // form transitive closure over nodes, including children and node-valued properties        
        Set<INode> includedNodes = new HashSet<INode>();
        for (INode n : selectedNodes) addDependents(n, includedNodes);
        
        // empty old contents
        List<IEdge> edges = new ArrayList<IEdge>(getEdges());
        for (IEdge e : edges) removeEdge(e);
        List<INode> nodes = new ArrayList<INode>(getNodes());
        for (INode n : nodes) removeNode(n);
        
        copyStructure(g, includedNodes, this, null, -bounds.getX(), -bounds.getY());
    }
    
    private static void addDependents(INode n, Set<INode> dependents)
    {
        if (dependents.contains(n)) return;
        dependents.add(n);
        for (INode c : n.getChildren()) addDependents(c, dependents);
        try
        {
            /*
             * Enumerate all Node-valued properties
             */
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(n.getClass()).getPropertyDescriptors())
            {
                if (INode.class.isAssignableFrom(descriptor.getPropertyType()))
                {
                    INode value = (INode) descriptor.getReadMethod().invoke(n);
                    addDependents(value, dependents);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public Collection<INode> pasteOut(IGraph g, INode selectedNode)
    {
        return copyStructure(this, getNodes(), g, selectedNode, 0, 0);
    }

    private static Collection<INode> copyStructure(IGraph graphIn, Collection<INode> selectedIn, IGraph graphOut, INode selectedOut, double dx, double dy)
    {
        /*
         * Make sure target graph can receive node types.
         */
        INode[] nodeProtos = graphOut.getNodePrototypes();
        if (nodeProtos != null)
        {
            Set<Class<? extends INode>> nodeClasses = new HashSet<Class<? extends INode>>();
            for (INode n : nodeProtos) nodeClasses.add(n.getClass());
            for (INode n : selectedIn)
            {
                if (!nodeClasses.contains(n.getClass())) return null;
            }
        }                

        /*
         * Clone all nodes and remember the original-cloned correspondence
         */
        Map<INode, INode> originalAndClonedNodes = new LinkedHashMap<INode, INode>();
        for (INode n : selectedIn)
        {
            INode n2 = n.clone();
            originalAndClonedNodes.put(n, n2);
        }
                
        /*
         * Clone all edges that join copied nodes
         */
        List<IEdge> newEdges = new ArrayList<IEdge>();
        for (IEdge e : graphIn.getEdges())
        {
            INode start = null;
            INode end = null;            
            if ((start = originalAndClonedNodes.get(e.getStart())) != null && (end = originalAndClonedNodes.get(e.getEnd())) != null)
            {
                IEdge e2 = e.clone();
                e2.connect(start, end);
                newEdges.add(e2);
            }
        }
        
        /*
         * Make sure target graph can receive edge types.
         */
        IEdge[] edgeProtos = graphOut.getEdgePrototypes();
        if (edgeProtos != null)
        {
            Set<Class<? extends IEdge>> edgeClasses = new HashSet<Class<? extends IEdge>>();
            for (IEdge e : edgeProtos) edgeClasses.add(e.getClass());
            for (IEdge e : newEdges)
            {
                if (!edgeClasses.contains(e.getClass())) return null;
            }
        }        
        
        /*
         * Add nodes to target.
         */
        for (INode n : originalAndClonedNodes.values())
        {
            Point2D location = n.getLocation();
            Point2D p = new Point2D.Double(location.getX() + dx, location.getY() + dy);
            graphOut.addNode(n, p);
        }
        
        /*
         * Add edges to target.
         */
        for (IEdge e : newEdges)
            graphOut.connect(e, e.getStart(), e.getEnd());
        
        if (selectedOut != null)
            selectedOut.checkPasteChildren(originalAndClonedNodes.values());
        
        for (INode n : selectedIn)
        {
            for (INode child : n.getChildren())
            {
                INode child2 = originalAndClonedNodes.get(child);
                if (child2 != null)
                {
                    INode n2 = originalAndClonedNodes.get(n);
                    n2.addChild(n2.getChildren().size(), child2);
                }
            }
            try
            {
                /*
                 * Establish all Node-valued properties on clone 
                 */
                for (PropertyDescriptor descriptor : Introspector.getBeanInfo(n.getClass()).getPropertyDescriptors())
                {
                    if (INode.class.isAssignableFrom(descriptor.getPropertyType()))
                    {
                        INode value = (INode) descriptor.getReadMethod().invoke(n);
                        INode value2 =  originalAndClonedNodes.get(value);
                        if (value2 != null)
                        {
                            INode n2 = originalAndClonedNodes.get(n);
                            descriptor.getWriteMethod().invoke(n2, value2);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return originalAndClonedNodes.values();
    }

    @Override
    public INode[] getNodePrototypes()
    {
        return null;
    }
    
    @Override
    public IEdge[] getEdgePrototypes()
    {
        return null;
    }
}
