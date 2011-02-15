/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.product.diagram.abstracts;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.AbstractNode;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.common.NoteNode;
import com.horstmann.violet.product.workspace.editorpart.EmptyGrid;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * A graph consisting of selectable nodes and edges.
 */
public abstract class AbstractGraph implements Serializable, Cloneable, IGraph
{
    /**
     * Constructs a graph with no nodes or edges.
     */
    public AbstractGraph()
    {
        nodes = new ArrayList<INode>();
        edges = new ArrayList<IEdge>();
        nodesToBeRemoved = new ArrayList<INode>();
        edgesToBeRemoved = new ArrayList<IEdge>();
        grid = new EmptyGrid();
    }


    @Override
    public void moveNode(INode existingNode, Point2D dest)
    {
        existingNode.translate(dest.getX(), dest.getY());
    }



    @Override
    public INode findNode(Point2D p)
    {
        for (INode n : getAllNodes()) {
            Point2D locationOnGraph = n.getLocationOnGraph();
            Rectangle2D bounds = n.getBounds();
            Rectangle2D boundsToCheck = new Rectangle2D.Double(locationOnGraph.getX(), locationOnGraph.getY(), bounds.getWidth(), bounds.getHeight());
            if (boundsToCheck.contains(p)) {
                return n;
            }
        }
        return null;
    }
    
    private List<INode> getAllNodes() {
        List<INode> fifo = new ArrayList<INode>();
        List<INode> allNodes = new ArrayList<INode>();
        fifo.addAll(nodes);
        allNodes.addAll(nodes);
        while (!fifo.isEmpty()) {
            INode nodeToInspect = fifo.remove(0);
            List<INode> children = nodeToInspect.getChildren();
            fifo.addAll(children);
            allNodes.addAll(children);
        }
        // Let's have children first 
        Collections.reverse(allNodes);
        return allNodes;
    }

    @Override
    public INode findNode(Id id)
    {
        for (INode n : getAllNodes())
        {
            if (n.getId().equals(id)) return n;
        }
        return null;
    }

    @Override
    public IEdge findEdge(Point2D p)
    {
        for (IEdge e : edges)
        {
            if (e.contains(p)) return e;
        }
        return null;
    }

    @Override
    public IEdge findEdge(Id id)
    {
        for (IEdge e : edges)
        {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    @Override
    public void draw(Graphics2D g2)
    {
        List<INode> specialNodes = new ArrayList<INode>();
        
        int count = 0;
        int z = 0;
        Collection<INode> nodes = getNodes();
        while (count < nodes.size())
        {
            for (INode n : nodes)
            {
                
                if (n.getZ() == z)
                {
                    if (n instanceof NoteNode) {
                        specialNodes.add(n);
                    } else {
                        n.draw(g2);
                    }
                    count++;
                }
            }
            z++;
        }

        for (int i = 0; i < edges.size(); i++)
        {
            IEdge e = (IEdge) edges.get(i);
            e.draw(g2);
        }
        // Special nodes are always drawn upon other elements
        for (INode n : specialNodes) {
            // Translate g2 if node has parent
            INode p = n.getParent();
            Point2D nodeLocationOnGraph = n.getLocationOnGraph();
            Point2D nodeLocation = n.getLocation();
            Point2D g2Location = new Point2D.Double(nodeLocationOnGraph.getX() - nodeLocation.getX(), nodeLocationOnGraph.getY() - nodeLocation.getY());
            g2.translate(g2Location.getX(), g2Location.getY());
            n.draw(g2);
            // Restore g2 original location
            g2.translate(-g2Location.getX(), -g2Location.getY());
        }
        
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.IGraph#getBounds()
     */
    public Rectangle2D getClipBounds()
    {
        Rectangle2D r = minBounds;
        for (INode n : getAllNodes())
        {
            Rectangle2D b = n.getBounds();
            if (r == null) r = b;
            else r.add(b);
        }
        for (IEdge e : edges)
        {
            r.add(e.getBounds());
        }
        return r == null ? new Rectangle2D.Double() : new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth()
                + AbstractNode.SHADOW_GAP, r.getHeight() + AbstractNode.SHADOW_GAP);
    }

    @Override
    public void setBounds(Rectangle2D newValue)
    {
        minBounds = newValue;
    }

    @Override
    public abstract INode[] getNodePrototypes();

    @Override
    public abstract IEdge[] getEdgePrototypes();


    @Override
    public Collection<INode> getNodes()
    {
        return Collections.unmodifiableCollection(getAllNodes());
    }

    @Override
    public Collection<IEdge> getEdges()
    {
        return Collections.unmodifiableCollection(edges);
    }

    @Override
    public boolean addNode(INode newNode, Point2D p)
    {
        // Case 1 : Note node always attached to the graph
        if (newNode instanceof NoteNode) {
            newNode.setGraph(this);
            newNode.setLocation(p);
            nodes.add(newNode);
            return true;
        }
        // Case 2 : attached to an existing node
        INode potentialParentNode = findNode(p);
        if (potentialParentNode != null) {
            Point2D parentLocationOnGraph = potentialParentNode.getLocationOnGraph();
            Point2D relativeLocation = new Point2D.Double(p.getX() - parentLocationOnGraph.getX(), p.getY() - parentLocationOnGraph.getY());
            return potentialParentNode.addChildNode(newNode, relativeLocation);
        }
        // Case 3 : attached directly to the graph
        newNode.setGraph(this);
        newNode.setLocation(p);
        nodes.add(newNode);
        return true;
    }


    @Override
    public void removeNode(INode... nodesToRemove)
    {
        // Notify all nodes of removals. This might trigger recursive invocations.
        if (nodesToRemove != null)
        {
            for (INode n : nodesToRemove)
            {
                if (!nodesToBeRemoved.contains(n))
                {
                    for (INode n2 : getAllNodes())
                    {
                        n2.checkRemoveNode(n);
                    }
                    nodesToBeRemoved.add(n);
                }
            }
        }
        

        // Traverse all nodes other than the ones to be removed and make sure that none
        // of their node-valued properties fall into the set of removed nodes. (Null out if necessary.)

        for (INode n : getAllNodes())
        {
            if (!nodesToBeRemoved.contains(n))
            {
                try
                {
                    for (PropertyDescriptor descriptor : Introspector.getBeanInfo(n.getClass()).getPropertyDescriptors())
                    {
                        if (INode.class.isAssignableFrom(descriptor.getPropertyType()))
                        {
                            INode value = (INode) descriptor.getReadMethod().invoke(n);
                            if (nodesToBeRemoved.contains(value)) descriptor.getWriteMethod().invoke(n, (Object) null);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        for (INode n : nodesToBeRemoved)
        {
            for (int i = n.getChildren().size() - 1; i >= 0; i--)
                n.checkRemoveNode(n.getChildren().get(i));
        }
        for (INode n : nodesToBeRemoved)
        {
            List<IEdge> edgesToRemove = new ArrayList<IEdge>();
            for (IEdge edge : getEdges()) {
                if (n == edge.getStart() || n == edge.getEnd()) {
                    edgesToRemove.add(edge);
                }
            }
            removeEdge(edgesToRemove.toArray(new IEdge[edgesToRemove.size()]));
        }
        for (INode n : nodesToBeRemoved)
        {
            if (n.getParent() != null) n.getParent().checkRemoveNode(n);
            n.setGraph(null);
        }
        nodes.removeAll(nodesToBeRemoved);
        nodesToBeRemoved.clear();
        
        
    }

    
    @Override
    public boolean connect(IEdge e, INode start, Point2D startLocation, INode end, Point2D endLocation)
    {
        // Step 1 : find if nodes exist
        if (start != null && !nodes.contains(start)) addNode(start, start.getLocation());
        if (end != null && !nodes.contains(end)) addNode(end, end.getLocation());
        e.setStart(start);
        e.setStartLocation(startLocation);
        e.setEnd(end);
        e.setEndlocation(endLocation);
        if (start.checkAddEdge(e))
        {
            edges.add(e);
            return true;
        }
        return false;
    }


    @Override
    public void removeEdge(IEdge... edgesToRemove)
    {
        if (edgesToRemove != null)
        {
            for (IEdge e : edgesToRemove)
            {
                if (!edgesToBeRemoved.contains(e))
                {
                    for (INode n1 : getAllNodes())
                    {
                        n1.checkRemoveEdge(e);
                    }
                    edgesToBeRemoved.add(e);
                }
            }
        }

        for (IEdge e : edges)
        {
            if (!edgesToBeRemoved.contains(e) && (nodesToBeRemoved.contains(e.getStart()) || nodesToBeRemoved.contains(e.getEnd()))) edgesToBeRemoved
                    .add(e);
        }
        edges.removeAll(edgesToBeRemoved);
        edgesToBeRemoved.clear();
    }

    
    @Override
    public IGrid getGrid()
    {
        return grid;
    }


    private ArrayList<INode> nodes;
    private ArrayList<IEdge> edges;
    private transient ArrayList<INode> nodesToBeRemoved;
    private transient ArrayList<IEdge> edgesToBeRemoved;
    private transient Rectangle2D minBounds;
    private transient IGrid grid;
}
