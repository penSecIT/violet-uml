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

package com.horstmann.violet.product.diagram.abstracts.node;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import com.horstmann.violet.product.diagram.abstracts.AbstractGraph;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.Id;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;

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
        this.bgColor = BACKGROUND_COLOR;
        // Empty graph used to avoid null pointer while drawing elements not attached
        // to a graph such as diagram tools
        this.graph = new AbstractGraph()
        {
            @Override
            public INode[] getNodePrototypes()
            {
                return new INode[0];
            }

            @Override
            public IEdge[] getEdgePrototypes()
            {
                return new IEdge[0];
            }
        };
    }

    @Override
    public Point2D getLocation()
    {
        return this.location;
    }

    @Override
    public Point2D getLocationOnGraph()
    {
        INode parentNode = getParent();
        if (parentNode == null)
        {
            return getLocation();
        }
        Point2D parentLocationOnGraph = parentNode.getLocationOnGraph();
        Point2D relativeLocation = getLocation();
        Point2D result = new Point2D.Double(parentLocationOnGraph.getX() + relativeLocation.getX(), parentLocationOnGraph.getY()
                + relativeLocation.getY());
        return result;
    }

    @Override
    public void setLocation(Point2D aPoint)
    {
        this.location = aPoint;
    }

    @Override
    public Id getId()
    {
        return this.id;
    }

    @Override
    public void setId(Id id)
    {
        this.id = id;
    }

    @Override
    public Integer getRevision()
    {
        return this.revision;
    }

    @Override
    public void setRevision(Integer newRevisionNumber)
    {
        this.revision = newRevisionNumber;
    }

    @Override
    public void incrementRevision()
    {
        int i = this.revision.intValue();
        i++;
        this.revision = new Integer(i);
    }

    @Override
    public void translate(double dx, double dy)
    {
        Point2D newLocation = new Point2D.Double(location.getX() + dx, location.getY() + dy);
        setLocation(newLocation);
    }

    @Override
    public boolean addConnection(IEdge e)
    {
        return e.getEnd() != null;
    }

    @Override
    public void removeConnection(IEdge e)
    {
    }

    @Override
    public void removeChild(INode node)
    {
        if (node.getParent() != this) return;
        children.remove(node);
    }

    @Override
    public boolean addChild(INode n, Point2D p)
    {
        return false;
    }

    @Override
    public INode getParent()
    {
        return parent;
    }

    @Override
    public void setParent(INode node)
    {
        parent = node;
    }

    @Override
    public List<INode> getChildren()
    {
        return children;
    }

    @Override
    public boolean addChild(INode node, int index)
    {
        INode oldParent = node.getParent();
        if (oldParent != null) oldParent.removeChild(node);
        children.add(index, node);
        node.setParent(this);
        node.setGraph(getGraph());
        return true;
    }

    @Override
    public void draw(Graphics2D g2)
    {
        Shape shape = getShape();
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
        return new Rectangle2D.Double(0, 0, 0, 0);
    }

    @Override
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

    @Override
    public void setGraph(IGraph g)
    {
        graph = g;
    }

    @Override
    public IGraph getGraph()
    {
        return graph;
    }

    @Override
    public int getZ()
    {
        return z;
    }

    @Override
    public void setZ(int z)
    {
        this.z = z;
    }

    /**
     * Sets node tool tip
     * 
     * @param label
     */
    public void setToolTip(String s)
    {
        this.toolTip = s;
    }

    @Override
    public String getToolTip()
    {
        return this.toolTip;
    }
    
    /**
     * Gets the value of the color property.
     * 
     * @return the background color of the note
     */
    public Color getBgColor()
    {
        return bgColor;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param newValue the background color of the note
     */
    public void setBgColor(Color newValue)
    {
        bgColor = newValue;
    }

    private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;
    protected static Color BACKGROUND_COLOR = UIManager.getColor("TextPane.background");
    public static final double SHADOW_GAP = 4;

    private ArrayList<INode> children;
    private INode parent;
    private IGraph graph;
    private Point2D location = new Point2D.Double(0, 0);
    private transient String toolTip;
    private transient int z;

    /** Node's current id (unique in all the graph) */
    protected Id id;

    /** Node's current revision */
    protected Integer revision = new Integer(0);
    
    /** Background color */
    private Color bgColor;
}
