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

package com.horstmann.violet.product.diagram.sequence;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.common.PointNode;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * An activation bar in a sequence diagram. This activation bar is hang on a lifeline (implicit parameter)
 */
public class ActivationBarNode extends RectangularNode
{
    /**
     * Construct an activation bar with a default size
     */
    public ActivationBarNode()
    {
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        // Translate g2 if node has parent
        Point2D nodeLocationOnGraph = getLocationOnGraph();
        Point2D nodeLocation = getLocation();
        Point2D g2Location = new Point2D.Double(nodeLocationOnGraph.getX() - nodeLocation.getX(), nodeLocationOnGraph.getY() - nodeLocation.getY());
        g2.translate(g2Location.getX(), g2Location.getY());
        // Perform painting
        super.draw(g2);
        Color oldColor = g2.getColor();
        g2.setColor(Color.WHITE);
        Rectangle2D b = getBounds();
        g2.fill(b);
        g2.setColor(oldColor);
        g2.draw(b);
        // Restore g2 original location
        g2.translate(-g2Location.getX(), -g2Location.getY());
        // Draw its children
        for (INode node : getChildren())
        {
            node.draw(g2);
        }
    }

    /**
     * Gets the participant's life line of this call. Note : method's name is ot set to getLifeLine to keep compatibility with older
     * versions
     * 
     * @return the participant's life line
     */
    public LifelineNode getImplicitParameter()
    {
        return lifeline;
    }

    /**
     * Sets the participant's life line of this call. Note : method's name is ot set to setLifeLine to keep compatibility with older
     * versions
     * 
     * @param newValue the participant's lifeline
     */
    public void setImplicitParameter(LifelineNode newValue)
    {
        Object oldValue = lifeline;
        if (oldValue != newValue)
        {
            lifeline = newValue;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#getConnectionPoint(com.horstmann.violet.framework.Direction)
     */
    public Point2D getConnectionPoint(Direction d)
    {
        if (d.getX() > 0) return new Point2D.Double(getBounds().getMaxX(), getBounds().getMinY());
        else return new Point2D.Double(getBounds().getX(), getBounds().getMinY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addEdge(com.horstmann.violet.framework.Edge, java.awt.geom.Point2D,
     * java.awt.geom.Point2D)
     */
    public boolean checkAddEdge(IEdge edge, Point2D startingNodePoint, Point2D endingNodePoint)
    {
        INode endingNode = edge.getEnd();
        INode startingNode = edge.getStart();
        if (startingNode == null || endingNode == null)
        {
            return false;
        }
        if (startingNode == endingNode)
        {
            return false;
        }
        if (edge instanceof CallEdge)
        {
            return isCallEdgeAcceptable((CallEdge) edge, endingNodePoint);

        }
        else if (edge instanceof ReturnEdge)
        {
            return isReturnEdgeAcceptable((ReturnEdge) edge);
        }

        return false;
    }

    private boolean isReturnEdgeAcceptable(ReturnEdge edge)
    {
        INode endingNode = edge.getEnd();
        INode startingNode = edge.getStart();
        Class<? extends INode> startingNodeClass = startingNode.getClass();
        Class<? extends INode> endingNodeClass = endingNode.getClass();
        if (startingNodeClass.isAssignableFrom(ActivationBarNode.class) && endingNodeClass.isAssignableFrom(ActivationBarNode.class))
        {
            return true;
        }
        return false;
    }

    private boolean isCallEdgeAcceptable(CallEdge edge, Point2D endingNodePoint)
    {
        INode endingNode = edge.getEnd();
        INode startingNode = edge.getStart();
        Class<? extends INode> startingNodeClass = startingNode.getClass();
        Class<? extends INode> endingNodeClass = endingNode.getClass();
        if (startingNodeClass.isAssignableFrom(ActivationBarNode.class) && endingNodeClass.isAssignableFrom(ActivationBarNode.class))
        {
            return true;
        }
        if (startingNodeClass.isAssignableFrom(ActivationBarNode.class) && endingNodeClass.isAssignableFrom(LifelineNode.class))
        {
            LifelineNode endingLifeLineNode = (LifelineNode) endingNode;
            Rectangle2D topRectangle = endingLifeLineNode.getTopRectangle();
            if (topRectangle.contains(endingNodePoint))
            {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#removeEdge(com.horstmann.violet.framework.Graph,
     * com.horstmann.violet.framework.Edge)
     */
    public void checkRemoveEdge(IEdge e)
    {
        if (e.getStart() == this) removeChild(e.getEnd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#removeNode(com.horstmann.violet.framework.Graph,
     * com.horstmann.violet.framework.Node)
     */
    public void checkRemoveNode(INode n)
    {
        getChildren().remove(n);
    }

    /**
     * Finds an edge in the graph connected to start and end nodes
     * 
     * @param g the graph
     * @param start the start node
     * @param end the end node
     * @return the edge or null if no one is found
     */
    private IEdge findEdge(INode start, INode end)
    {
        for (IEdge e : getGraph().getEdges())
        {
            if (e.getStart() == start && e.getEnd() == end) return e;
        }
        return null;
    }

    @Override
    public Point2D getLocation()
    {
        double x = getHorizontalLocation();
        double y = getVerticalLocation();
        return new Point2D.Double(x, y);
    }

    /**
     * @return x location relative to the parent
     */
    private double getHorizontalLocation()
    {
        INode parentNode = getParent();
        if (parentNode != null && parentNode.getClass().isAssignableFrom(ActivationBarNode.class))
        {
            return DEFAULT_WIDTH / 2;
        }
        if (parentNode != null && parentNode.getClass().isAssignableFrom(LifelineNode.class))
        {
            LifelineNode lifeLineNode = (LifelineNode) parentNode;
            Rectangle2D lifeLineTopRectangle = lifeLineNode.getTopRectangle();
            return lifeLineTopRectangle.getWidth() / 2 - DEFAULT_WIDTH / 2;
        }
        return 0;
    }

    /**
     * @return y location relative to the parent
     */
    private double getVerticalLocation()
    {
        INode parentNode = getParent();
        if (parentNode != null && parentNode.getClass().isAssignableFrom(ActivationBarNode.class))
        {
            List<INode> brotherNodes = parentNode.getChildren();
            double y = CALL_YGAP;
            for (INode aNode : brotherNodes)
            {
                if (aNode != this && aNode.getClass().isAssignableFrom(ActivationBarNode.class))
                {
                    y = y + aNode.getBounds().getHeight() + CALL_YGAP;
                }
                if (aNode == this)
                {
                    break;
                }
            }
            return y;
        }
        if (parentNode != null && parentNode.getClass().isAssignableFrom(LifelineNode.class))
        {
            LifelineNode lifeLineNode = (LifelineNode) parentNode;
            Rectangle2D lifeLineTopRectangle = lifeLineNode.getTopRectangle();
            double y = lifeLineTopRectangle.getHeight() + CALL_YGAP;
            List<INode> brotherNodes = parentNode.getChildren();
            for (INode aNode : brotherNodes)
            {
                if (aNode != this && aNode.getClass().isAssignableFrom(ActivationBarNode.class))
                {
                    y = y + aNode.getBounds().getHeight() + CALL_YGAP;
                }
                if (aNode == this)
                {
                    break;
                }
            }
            return y;
        }
        return 0;
    }

    @Override
    public Rectangle2D getBounds()
    {
        Point2D nodeLocation = getLocation();
        // Height
        double height = DEFAULT_HEIGHT;
        int childVisibleNodesCounter = 0;
        for (INode aNode : getChildren())
        {
            if (aNode instanceof ActivationBarNode)
            {
                childVisibleNodesCounter++;
            }
        }
        if (childVisibleNodesCounter > 0)
        {
            height = CALL_YGAP;
            for (INode aNode : getChildren())
            {
                if (aNode instanceof ActivationBarNode)
                {
                    Rectangle2D aNodeBounds = aNode.getBounds();
                    height = height + aNodeBounds.getHeight() + CALL_YGAP;
                }
            }
        }
        return new Rectangle2D.Double(nodeLocation.getX(), nodeLocation.getY(), DEFAULT_WIDTH, height);
        // TODO : manage openbottom
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addNode(com.horstmann.violet.framework.Node, java.awt.geom.Point2D)
     */
    public boolean addChildNode(INode n, Point2D p)
    {
        if (n instanceof PointNode || n instanceof ActivationBarNode)
        {
            n.setParent(this);
            n.setLocation(p);
            return getChildren().add(n);
        }
        return false;
    }

    /** The lifeline that embeds this activation bar in the sequence diagram */
    private LifelineNode lifeline;

    /** Default with */
    private static int DEFAULT_WIDTH = 16;

    /** Default height */
    private static int DEFAULT_HEIGHT = 30;

    /** Default vertical gap between two call nodes and a call node and an implicit node */
    public static int CALL_YGAP = 20;
}
