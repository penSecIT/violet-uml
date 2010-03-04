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

package com.horstmann.violet.framework.diagram;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.AbstractNode;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.util.PropertyUtils;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;
import com.horstmann.violet.product.diagram.common.NoteNode;

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#addEdgeAtPoints(com.horstmann.violet.framework.diagram.Edge,
     *      java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    public boolean addEdgeAtPoints(IEdge e, Point2D p1, Point2D p2)
    {
        INode n1 = findNode(p1);
        INode n2 = findNode(p2);
        if (n1 != null)
        {
            e.connect(n1, n2);
            if (n1.checkAddEdge(e, p1, p2) && e.getEnd() != null)
            {
                if (!nodes.contains(e.getEnd()))
                {
                    addNode(e.getEnd(), e.getEnd().getLocation());
                }
                edges.add(e);
                fireEdgeAdded(e, p1, p2);
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Graph#moveNode(com.horstmann.violet.framework.diagram.Node,
     *      java.awt.geom.Point2D)
     */
    public void moveNode(INode existingNode, Point2D dest)
    {
        existingNode.translate(dest.getX(), dest.getY());
    }



    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#findNode(java.awt.geom.Point2D)
     */
    public INode findNode(Point2D p)
    {
        int maxZ = 0;
        for (INode n : nodes)
        {
            if (n.getZ() > maxZ) maxZ = n.getZ();
        }
        for (int z = maxZ; z >= 0; z--)
        {
            for (INode n : nodes)
            {
                if (n.getZ() == z && n.contains(p)) return n;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Graph#findNode(com.horstmann.violet.framework.diagram.Id)
     */
    public INode findNode(Id id)
    {
        for (INode n : nodes)
        {
            if (n.getId().equals(id)) return n;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#findEdge(java.awt.geom.Point2D)
     */
    public IEdge findEdge(Point2D p)
    {
        for (IEdge e : edges)
        {
            if (e.contains(p)) return e;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Graph#findEdge(com.horstmann.violet.framework.diagram.Id)
     */
    public IEdge findEdge(Id id)
    {
        for (IEdge e : edges)
        {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#draw(java.awt.Graphics2D, com.horstmann.violet.framework.diagram.Grid)
     */
    public void draw(Graphics2D g2)
    {
        List<INode> specialNodes = new ArrayList<INode>();
        
        int count = 0;
        int z = 0;
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
            n.draw(g2);
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#removeNodesAndEdges(java.util.Collection, java.util.Collection)
     */
    public void removeNodesAndEdges(Collection<? extends INode> nodesToRemove, Collection<? extends IEdge> edgesToRemove)
    {
        recursiveRemoves++;

        // Notify all nodes of removals. This might trigger recursive invocations.

        if (nodesToRemove != null)
        {
            for (INode n : nodesToRemove)
            {
                if (!nodesToBeRemoved.contains(n))
                {
                    for (INode n2 : nodes)
                    {
                        n2.checkRemoveNode(n);
                    }
                    nodesToBeRemoved.add(n);
                }
            }
        }
        if (edgesToRemove != null)
        {
            for (IEdge e : edgesToRemove)
            {
                if (!edgesToBeRemoved.contains(e))
                {
                    for (INode n1 : nodes)
                    {
                        n1.checkRemoveEdge(e);
                    }
                    edgesToBeRemoved.add(e);
                }
            }
        }

        recursiveRemoves--;
        if (recursiveRemoves > 0) return;

        for (IEdge e : edges)
        {
            if (!edgesToBeRemoved.contains(e) && (nodesToBeRemoved.contains(e.getStart()) || nodesToBeRemoved.contains(e.getEnd()))) edgesToBeRemoved
                    .add(e);
        }
        edges.removeAll(edgesToBeRemoved);
        for (IEdge e : edgesToBeRemoved)
            fireEdgeRemoved(e);
        edgesToBeRemoved.clear();

        // Traverse all nodes other than the ones to be removed and make sure that none
        // of their node-valued properties fall into the set of removed nodes. (Null out if necessary.)

        for (INode n : nodes)
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
                            PropertyChangeEvent event = new PropertyChangeEvent(n, descriptor.getName(), value, null);
                            firePropertyChangeOnNodeOrEdge(event);
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
                n.removeChild(n.getChildren().get(i));
        }
        for (INode n : nodesToBeRemoved)
        {
            if (n.getParent() != null) n.getParent().removeChild(n);
            n.setGraph(null);
        }
        nodes.removeAll(nodesToBeRemoved);
        for (INode n : nodesToBeRemoved)
        {
            fireNodeRemoved(n);
        }
        nodesToBeRemoved.clear();
    }


    /**
     * Prepare graph elements before painting
     * @param g2
     * @param gr
     */
    public void layout(Graphics2D g2, IGrid gr)
    {
        for (INode n : nodes)
        {
            if (n.getParent() == null) // parents lay out their children
            n.layout(g2, gr);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#getBounds()
     */
    public Rectangle2D getClipBounds()
    {
        Rectangle2D r = minBounds;
        for (INode n : nodes)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#setMinBounds(java.awt.geom.Rectangle2D)
     */
    public void setBounds(Rectangle2D newValue)
    {
        minBounds = newValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#getNodePrototypes()
     */
    public abstract INode[] getNodePrototypes();

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#getEdgePrototypes()
     */
    public abstract IEdge[] getEdgePrototypes();


    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#getNodes()
     */
    public Collection<INode> getNodes()
    {
        return Collections.unmodifiableCollection(nodes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#getEdges()
     */
    public Collection<IEdge> getEdges()
    {
        return Collections.unmodifiableCollection(edges);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#addNode(com.horstmann.violet.framework.diagram.Node,
     *      java.awt.geom.Point2D)
     */
    public boolean addNode(INode newNode, Point2D p)
    {
        newNode.setGraph(this);
        newNode.translate(p.getX() - newNode.getLocation().getX(), p.getY() - newNode.getLocation().getY());
        
        if (newNode instanceof NoteNode) {
            nodes.add(newNode);
            fireNodeAdded(newNode, p);
            return true;
        }
        
        
        boolean accepted = false;
        boolean insideANode = false;
        int maxZ = 0;
        for (INode n : nodes)
        {
            if (n.getZ() > maxZ) maxZ = n.getZ();
        }
        for (int z = maxZ; !accepted && z >= 0; z--)
        {
            for (int i = 0; !accepted && i < nodes.size(); i++)
            {
                INode n = nodes.get(i);
                if (!n.equals(newNode) && n.getZ() == z && n.contains(p))
                {
                    insideANode = true;
                    accepted = n.checkAddNode(newNode, p);
                }
            }
        }
        if (insideANode && !accepted) return false;
        nodes.add(newNode);
        fireNodeAdded(newNode, p);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#removeNode(com.horstmann.violet.framework.diagram.Node)
     */
    public void removeNode(INode n)
    {
        INode p = n.getParent();
        if (p != null) p.removeChild(n);
        nodes.remove(n);
        n.setGraph(null);
        fireNodeRemoved(n);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#connect(com.horstmann.violet.framework.diagram.Edge,
     *      com.horstmann.violet.framework.diagram.Node, com.horstmann.violet.framework.diagram.Node)
     */
    public void connect(IEdge e, INode start, INode end)
    {
        // Re-attaches nodes to list
        if (!nodes.contains(start)) addNode(start, start.getLocation());
        if (!nodes.contains(end)) addNode(end, start.getLocation());
        // Registers nodes to edge
        e.connect(start, end);
        edges.add(e);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#removeEdge(com.horstmann.violet.framework.diagram.Edge)
     */
    public void removeEdge(IEdge e)
    {
        edges.remove(e);
        fireEdgeRemoved(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#addGraphModificationListener(com.horstmann.violet.framework.diagram.GraphModificationListener)
     */
    public void addGraphModificationListener(GraphModificationListener listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Graph#addGraphModificationListener(java.util.List)
     */
    public void addGraphModificationListener(List<GraphModificationListener> l)
    {
        synchronized (listeners)
        {
            listeners.addAll(l);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Graph#getGraphModificationListener()
     */
    public List<GraphModificationListener> getGraphModificationListener()
    {
        synchronized (listeners)
        {
            return listeners;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#removeGraphModificationListener(com.horstmann.violet.framework.diagram.GraphModificationListener)
     */
    public synchronized void removeGraphModificationListener(GraphModificationListener listener)
    {
        synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#changeNodeOrEdgeProperty(java.beans.PropertyChangeEvent)
     */
    public void changeNodeOrEdgeProperty(PropertyChangeEvent e)
    {
        PropertyUtils.setProperty(e.getSource(), e.getPropertyName(), e.getNewValue());
        firePropertyChangeOnNodeOrEdge(e);
    }

    @SuppressWarnings("unchecked")
    private List<GraphModificationListener> cloneListeners()
    {
        synchronized (listeners)
        {
            return (List<GraphModificationListener>) listeners.clone();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#fireNodeAdded(com.horstmann.violet.framework.diagram.Node)
     */
    public void fireNodeAdded(INode n, Point2D location)
    {
        for (GraphModificationListener listener : cloneListeners())
            listener.nodeAdded(this, n, location);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#fireNodeRemoved(com.horstmann.violet.framework.diagram.Node)
     */
    public void fireNodeRemoved(INode n)
    {
        for (GraphModificationListener listener : cloneListeners())
            listener.nodeRemoved(this, n);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#fireChildAttached(int, com.horstmann.violet.framework.diagram.Node,
     *      com.horstmann.violet.framework.diagram.Node)
     */
    public void fireChildAttached(int index, INode p, INode c)
    {
        for (GraphModificationListener listener : cloneListeners())
            listener.childAttached(this, index, p, c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#fireChildDetached(int, com.horstmann.violet.framework.diagram.Node,
     *      com.horstmann.violet.framework.diagram.Node)
     */
    public void fireChildDetached(int index, INode p, INode c)
    {
        for (GraphModificationListener listener : cloneListeners())
            listener.childDetached(this, index, p, c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Graph#fireEdgeAdded(com.horstmann.violet.framework.diagram.Edge,
     *      java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    public void fireEdgeAdded(IEdge e, Point2D startPoint, Point2D endPoint)
    {
        for (GraphModificationListener listener : cloneListeners())
            listener.edgeAdded(this, e, startPoint, endPoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#fireEdgeRemoved(com.horstmann.violet.framework.diagram.Edge)
     */
    public void fireEdgeRemoved(IEdge e)
    {
        for (GraphModificationListener listener : cloneListeners())
            listener.edgeRemoved(this, e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#fireNodeMoved(com.horstmann.violet.framework.diagram.Node, double, double)
     */
    public void fireNodeMoved(INode node, double dx, double dy)
    {
        if (dx == 0 && dy == 0) return;
        for (GraphModificationListener listener : cloneListeners())
            listener.nodeMoved(this, node, dx, dy);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.IGraph#firePropertyChangeOnNodeOrEdge(java.beans.PropertyChangeEvent)
     */
    public void firePropertyChangeOnNodeOrEdge(PropertyChangeEvent event)
    {
        if (event.getOldValue() == event.getNewValue()) return;
        for (GraphModificationListener listener : cloneListeners())
            listener.propertyChangedOnNodeOrEdge(this, event);
    }

    private ArrayList<INode> nodes;
    private ArrayList<IEdge> edges;
    private transient ArrayList<INode> nodesToBeRemoved;
    private transient ArrayList<IEdge> edgesToBeRemoved;
    private transient int recursiveRemoves;
    private transient Rectangle2D minBounds;
    private transient ArrayList<GraphModificationListener> listeners = new ArrayList<GraphModificationListener>();
}
