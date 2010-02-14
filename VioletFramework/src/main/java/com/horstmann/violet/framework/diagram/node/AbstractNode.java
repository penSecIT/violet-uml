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

package com.horstmann.violet.framework.diagram.node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.UIManager;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.Id;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;

/**
 * A class that supplies convenience implementations for a number of methods in the Node interface
 * 
 * @author Cay Horstmann
 */
public abstract class AbstractNode implements INode
{
    /**
     * Constructs a node with no parents or children at location (0, 0).
     */
    public AbstractNode()
    {
        children = new ArrayList<INode>();
        parent = null;
        this.id = new Id();
    }



    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#getLocation()
     */
    public Point2D getLocation()
    {
        // Legacy grief--some versions of the XML encoder wrote calls to setBounds
        // We use the location set by setBounds until the first call to translate.
        if (location == null) return new Point2D.Double(getBounds().getX(), getBounds().getY());
        return location;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#getId()
     */
    public Id getId()
    {
        return this.id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#setId(com.horstmann.violet.framework.diagram.Id)
     */
    public void setId(Id id)
    {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#getRevision()
     */
    public Integer getRevision()
    {
        return this.revision;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#setRevision(java.lang.Integer)
     */
    public void setRevision(Integer newRevisionNumber)
    {
        this.revision = newRevisionNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#incrementRevision()
     */
    public void incrementRevision()
    {
        int i = this.revision.intValue();
        i++;
        this.revision = new Integer(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#translate(double, double) Note that we don't translate the children since that is
     *      not always appropriate and hard to un-inherit.
     */
    public void translate(double dx, double dy)
    {
        // Legacy grief--some versions of the XML encoder wrote calls to setBounds
        // We use the location set by setBounds until the first call to translate.
        if (location == null) location = (Point2D.Double) getLocation();

        location.x += dx;
        location.y += dy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addEdge(com.horstmann.violet.framework.Edge, java.awt.geom.Point2D,
     *      java.awt.geom.Point2D)
     */
    public boolean checkAddEdge(IEdge e, Point2D p1, Point2D p2)
    {
        return e.getEnd() != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#removeEdge(com.horstmann.violet.framework.Graph,
     *      com.horstmann.violet.framework.Edge)
     */
    public void checkRemoveEdge(IEdge e)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#removeNode(com.horstmann.violet.framework.Graph,
     *      com.horstmann.violet.framework.Node)
     */
    public void checkRemoveNode(INode e)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#layout(com.horstmann.violet.framework.Graph, java.awt.Graphics2D,
     *      com.horstmann.violet.framework.Grid)
     */
    public void layout(Graphics2D g2, IGrid grid)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addNode(com.horstmann.violet.framework.Node, java.awt.geom.Point2D)
     */
    public boolean checkAddNode(INode n, Point2D p)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#getAncestors()
     */
    public List<INode> getAncestors()
    {
        List<INode> result = new ArrayList<INode>();
        INode parent = this.getParent();
        while (parent != null)
        {
            result.add(parent);
            parent = parent.getParent();
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#getParent()
     */
    public INode getParent()
    {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#setParent(com.horstmann.violet.framework.Node)
     */
    public void setParent(INode node)
    {
        parent = node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#getChildren()
     */
    public List<INode> getChildren()
    {
        return children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#setChildren(java.util.List)
     */
    public void setChildren(List<INode> children)
    {
        this.children = new ArrayList<INode>(children);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addChild(int, com.horstmann.violet.framework.Node)
     */
    public void addChild(int index, INode node)
    {
        INode oldParent = node.getParent();
        if (oldParent != null) oldParent.removeChild(node);
        children.add(index, node);
        if (node instanceof AbstractNode) ((AbstractNode) node).setParent(this);
        if (graph != null) graph.fireChildAttached(index, this, node);
    }

    /**
     * Called from decoder
     * 
     * @param node
     */
    public void addChild(INode node)
    {
        addChild(children.size(), node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#removeChild(com.horstmann.violet.framework.Node)
     */
    public void removeChild(INode node)
    {
        if (node.getParent() != this) return;
        int i = children.indexOf(node);
        if (i >= 0)
        {
            children.remove(i);
            if (node instanceof AbstractNode) ((AbstractNode) node).setParent(null);
            if (graph != null) graph.fireChildDetached(i, this, node);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        Shape shape = getShape();
        if (shape == null) return;
        /*
         * Area shadow = new Area(shape); shadow.transform(AffineTransform.getTranslateInstance(SHADOW_GAP, SHADOW_GAP));
         * shadow.subtract(new Area(shape));
         */
        Color oldColor = g2.getColor();
        g2.translate(SHADOW_GAP, SHADOW_GAP);
        g2.setColor(SHADOW_COLOR);
        g2.fill(shape);
        g2.translate(-SHADOW_GAP, -SHADOW_GAP);
        g2.setColor(BACKGROUND_COLOR);
        g2.fill(shape);
        g2.setColor(oldColor);
    }

    /**
     * @return the shape to be used for computing the drop shadow
     */
    public Shape getShape()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#checkPasteChildren(java.util.Collection)
     */
    public boolean checkPasteChildren(Collection<INode> children)
    {
        return false;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    public AbstractNode clone()
    {
        try
        {
            AbstractNode cloned = (AbstractNode) super.clone();
            cloned.id = new Id();
            cloned.children = new ArrayList<INode>();
            cloned.location = (Point2D.Double) getLocation().clone();
            /*
             * for (Node child : children) { Node clonedChild = child.clone(); cloned.children.add(clonedChild);
             * clonedChild.setParent(cloned); }
             */
            cloned.graph = null;
            return cloned;
        }
        catch (CloneNotSupportedException exception)
        {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#setGraph(com.horstmann.violet.framework.diagram.Graph)
     */
    public void setGraph(IGraph g)
    {
        graph = g;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#getGraph()
     */
    public IGraph getGraph()
    {
        return graph;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#getZ()
     */
    public int getZ()
    {
        return z;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.Node#setZ(int)
     */
    public void setZ(int z)
    {
        this.z = z;
    }

    /**
     * Sets node tool tip
     * @param label
     */
    public void setToolTip(String s) {
        this.toolTip = s;
    }
    

    /* (non-Javadoc)
     * @see com.horstmann.violet.framework.diagram.Node#getToolTip()
     */
    public String getToolTip()
    {
        return this.toolTip;
    }

    
    private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;
    protected static Color BACKGROUND_COLOR = UIManager.getColor("TextPane.background");
    public static final int SHADOW_GAP = 4;

    private ArrayList<INode> children;
    private INode parent;
    private IGraph graph;
    private transient String toolTip;
    private Point2D.Double location;
    private transient int z;

    /** Node's current id (unique in all the graph) */
    protected Id id;

    /** Node's current revision */
    protected Integer revision = new Integer(0);
}
