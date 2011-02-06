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
import java.util.Collection;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;

/**
 * An activation bar in a sequence diagram. This activation bar is hang on a lifeline (implicit parameter)
 */
public class ActivationBarNode extends RectangularNode
{

    @Override
    public boolean addChildNode(INode n, Point2D p)
    {
        boolean isActivationBarNode = n instanceof ActivationBarNode;
        if (isActivationBarNode)
        {
            ActivationBarNode newChildNode = (ActivationBarNode) n;
            newChildNode.setParent(this);
            newChildNode.setGraph(getGraph());
            newChildNode.setLocation(p);
            newChildNode.setImplicitParameter(this.getImplicitParameter());
            return getChildren().add(newChildNode);
        }
        return false;
    }

    @Override
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

    @Override
    public void checkRemoveNode(INode n)
    {
        getChildren().remove(n);
    }

    @Override
    public void checkRemoveEdge(IEdge e)
    {
        if (e.getStart() == this) removeChild(e.getEnd());
    }

    @Override
    public Point2D getConnectionPoint(Direction d)
    {
        if (d.getX() > 0) {
            double y = getBounds().getMinY() + CALL_YGAP / 2;
            double x = getBounds().getMaxX();
            return new Point2D.Double(x, y);
        } else {
            double y = getBounds().getMinY();
            double x = getBounds().getX();
            return new Point2D.Double(x, y);
        }
    }

    @Override
    public Point2D getLocation()
    {
        double x = getHorizontalLocation();
        double y = getVerticalLocation() + this.verticalLocationAdjustment;
        return new Point2D.Double(x, y);
    }
    
    private boolean isEndingNode() {
        for (IEdge edge : getGraph().getEdges()) {
            if (!edge.getClass().isAssignableFrom(CallEdge.class)) {
                continue;
            }
            if (edge.getEnd() == this) {
                return true;
            }
        }
        return false;
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
        // TODO : manage openbottom
        Rectangle2D currentBounds = new Rectangle2D.Double(nodeLocation.getX(), nodeLocation.getY(), DEFAULT_WIDTH, height);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(currentBounds);
        return snappedBounds;
    }

    @Override
    public void draw(Graphics2D g2)
    {
        adjustedVerticalLocation();
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

    /**
     * Adjust vertical location to be able to align ActivationBarNodes that are connected
     * This method is called when nodes are painted. 
     */
    private void adjustedVerticalLocation()
    {
        IGraph currentGraph = getGraph();
        if (currentGraph == null)
        {
            return;
        }
        Collection<IEdge> edges = currentGraph.getEdges();
        for (IEdge edge : edges)
        {
            if (edge.getClass().isAssignableFrom(CallEdge.class) && edge.getStart().getClass().isAssignableFrom(ActivationBarNode.class) && edge.getEnd().getClass().isAssignableFrom(ActivationBarNode.class))
            {
                ActivationBarNode startingNode = (ActivationBarNode) edge.getStart();
                ActivationBarNode endingNode = (ActivationBarNode) edge.getEnd();
                LifelineNode startingLifeLineNode = startingNode.getImplicitParameter();
                LifelineNode endingLifeLineNode = endingNode.getImplicitParameter();
                boolean isSameLifeLine = (startingLifeLineNode == endingLifeLineNode);
                boolean isThisInvolved = (startingNode == this || endingNode == this);
                if (isThisInvolved && !isSameLifeLine)
                {
                    Point2D startingNodeLocationOnGraph = startingNode.getLocationOnGraph();
                    Point2D endingNodeLocationOnGraph = endingNode.getLocationOnGraph();
                    double startingY = startingNodeLocationOnGraph.getY();
                    double endingY = endingNodeLocationOnGraph.getY() - CALL_YGAP / 2;
                    if (startingNode == this && endingY > startingY) {
                        this.verticalLocationAdjustment = endingY - startingY;
                    }
                    if (endingNode == this && startingY > endingY) {
                        this.verticalLocationAdjustment = startingY - endingY;
                    }
                    return;
                }
            }
        }
        // If there's no edge connected
        this.verticalLocationAdjustment = 0;
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
                    y = aNode.getBounds().getMaxY() + CALL_YGAP;
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
    Point2D currentLocation = getLocation();

    /** The lifeline that embeds this activation bar in the sequence diagram */
    private LifelineNode lifeline;

    /** Adjust y to align connected nodes */
    private transient double verticalLocationAdjustment = 0; 
    
    /** Default with */
    private static int DEFAULT_WIDTH = 16;

    /** Default height */
    private static int DEFAULT_HEIGHT = 30;

    /** Default vertical gap between two call nodes and a call node and an implicit node */
    public static int CALL_YGAP = 20;
}
