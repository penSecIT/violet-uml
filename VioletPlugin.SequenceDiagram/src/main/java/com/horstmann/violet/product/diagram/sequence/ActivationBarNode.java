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

import javax.lang.model.type.NullType;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;

/**
 * An activation bar in a sequence diagram. This activation bar is hang on a lifeline (implicit parameter)
 */
public class ActivationBarNode extends RectangularNode
{

    @Override
    public boolean addChild(INode n, Point2D p)
    {
        boolean isActivationBarNode = n instanceof ActivationBarNode;
        if (isActivationBarNode)
        {
            ActivationBarNode newChildNode = (ActivationBarNode) n;
            newChildNode.setParent(this);
            newChildNode.setGraph(getGraph());
            newChildNode.setImplicitParameter(this.getImplicitParameter());
            return getChildren().add(newChildNode);
        }
        return false;
    }

    @Override
    public boolean addChild(INode n, int index)
    {
        boolean isActivationBarNode = n instanceof ActivationBarNode;
        if (isActivationBarNode)
        {
            ActivationBarNode newChildNode = (ActivationBarNode) n;
            newChildNode.setParent(this);
            newChildNode.setGraph(getGraph());
            newChildNode.setImplicitParameter(this.getImplicitParameter());
            getChildren().add(index, newChildNode);
            return true;
        }
        return false;
    }

    @Override
    public boolean addConnection(IEdge edge)
    {
        INode endingNode = edge.getEnd();
        INode startingNode = edge.getStart();
        if (startingNode == endingNode)
        {
            return false;
        }
        if (edge instanceof CallEdge)
        {
            return isCallEdgeAcceptable((CallEdge) edge);

        }
        else if (edge instanceof ReturnEdge)
        {
            return isReturnEdgeAcceptable((ReturnEdge) edge);
        }

        return false;
    }

    @Override
    public void removeConnection(IEdge e)
    {
        if (e.getStart() == this) removeChild(e.getEnd());
    }

    @Override
    public Point2D getConnectionPoint(IEdge e)
    {
        boolean isCallEdge = e.getClass().isAssignableFrom(CallEdge.class);
        boolean isReturnEdge = e.getClass().isAssignableFrom(ReturnEdge.class);
        boolean isActivationBarNodeOnStart = e.getStart() != null && e.getStart().getClass().isAssignableFrom(ActivationBarNode.class);
        boolean isActivationBarNodeOnEnd = e.getEnd() != null && e.getEnd().getClass().isAssignableFrom(ActivationBarNode.class);
        boolean isLifelineNodeOnEnd = e.getEnd() != null && e.getEnd().getClass().isAssignableFrom(LifelineNode.class);
        if (isCallEdge) {
            if (isActivationBarNodeOnStart && isActivationBarNodeOnEnd) {
                ActivationBarNode startingNode = (ActivationBarNode) e.getStart();
                ActivationBarNode endingNode = (ActivationBarNode) e.getEnd();
                LifelineNode startingLifelineNode = startingNode.getImplicitParameter();
                LifelineNode endingLifelineNode = endingNode.getImplicitParameter();
                boolean isSameLifelineNode = startingLifelineNode != null && endingLifelineNode != null && startingLifelineNode.equals(endingLifelineNode);
                boolean isDifferentLifelineNodes = startingLifelineNode != null && endingLifelineNode != null && !startingLifelineNode.equals(endingLifelineNode);
                // Case 1 : two activation bars connected on differents LifeLines
                if (isDifferentLifelineNodes && isActivationBarNodeOnStart && isActivationBarNodeOnEnd) {
                    boolean isStartingNode = this.equals(e.getStart());
                    boolean isEndingNode = this.equals(e.getEnd());
                    if (isStartingNode) {
                        Point2D startingNodeLocation = getLocationOnGraph();
                        Point2D endingNodeLocation = e.getEnd().getLocationOnGraph();
                        Direction d = e.getDirection(this);
                        if (d.getX() > 0)
                        {
                            double x = startingNodeLocation.getX(); 
                            double y = endingNodeLocation.getY();
                            return new Point2D.Double(x, y);
                        }
                        else
                        {
                            double x = startingNodeLocation.getX() + DEFAULT_WIDTH;
                            double y = endingNodeLocation.getY();
                            return new Point2D.Double(x, y);
                        }
                    }
                    if (isEndingNode) {
                        Point2D endingNodeLocation = getLocationOnGraph();
                        Direction d = e.getDirection(this);
                        if (d.getX() > 0)
                        {
                            double x = endingNodeLocation.getX(); 
                            double y = endingNodeLocation.getY();
                            return new Point2D.Double(x, y);
                        }
                        else
                        {
                            double x = endingNodeLocation.getX() + DEFAULT_WIDTH;
                            double y = endingNodeLocation.getY();
                            return new Point2D.Double(x, y);
                        }
                    }
                }
                // Case 2 : two activation bars connected on same lifeline (self call)
                if (isSameLifelineNode && isActivationBarNodeOnStart && isActivationBarNodeOnEnd) {
                    boolean isStartingNode = this.equals(e.getStart());
                    boolean isEndingNode = this.equals(e.getEnd());
                    if (isStartingNode) {
                        Point2D startingNodeLocation = getLocationOnGraph();
                        Point2D endingNodeLocation = e.getEnd().getLocation();
                        double x = startingNodeLocation.getX() + DEFAULT_WIDTH;
                        double y = startingNodeLocation.getY() + endingNodeLocation.getY() - CALL_YGAP / 2;
                        return new Point2D.Double(x, y);
                    }
                    if (isEndingNode) {
                        Point2D endingNodeLocation = getLocationOnGraph();
                        double x = endingNodeLocation.getX() + DEFAULT_WIDTH;
                        double y = endingNodeLocation.getY();
                        return new Point2D.Double(x, y);
                    }
                }
            }
            if (isActivationBarNodeOnStart && isLifelineNodeOnEnd) {
                Direction d = e.getDirection(this);
                Point2D startingNodeLocation = getLocationOnGraph();
                if (d.getX() > 0)
                {
                    double x = startingNodeLocation.getX(); 
                    double y = startingNodeLocation.getY() + CALL_YGAP / 2;
                    return new Point2D.Double(x, y);
                }
                else
                {
                    double x = startingNodeLocation.getX() + DEFAULT_WIDTH;
                    double y = startingNodeLocation.getY() + CALL_YGAP / 2;
                    return new Point2D.Double(x, y);
                }
            }
        }
        if (isReturnEdge) {
            if (isActivationBarNodeOnStart && isActivationBarNodeOnEnd) {
                ActivationBarNode startingNode = (ActivationBarNode) e.getStart();
                ActivationBarNode endingNode = (ActivationBarNode) e.getEnd();
                LifelineNode startingLifelineNode = startingNode.getImplicitParameter();
                LifelineNode endingLifelineNode = endingNode.getImplicitParameter();
                boolean isDifferentLifelineNodes = startingLifelineNode != null && endingLifelineNode != null && !startingLifelineNode.equals(endingLifelineNode);
                // Case 1 : two activation bars connected on differents LifeLines
                if (isDifferentLifelineNodes && isActivationBarNodeOnStart && isActivationBarNodeOnEnd) {
                    boolean isStartingNode = this.equals(e.getStart());
                    boolean isEndingNode = this.equals(e.getEnd());
                    if (isStartingNode) {
                        Point2D startingNodeLocation = getLocationOnGraph();
                        Rectangle2D startingNodeBounds = getBounds();
                        Direction d = e.getDirection(this);
                        if (d.getX() > 0)
                        {
                            double x = startingNodeLocation.getX(); 
                            double y = startingNodeLocation.getY() + startingNodeBounds.getHeight();
                            return new Point2D.Double(x, y);
                        }
                        else
                        {
                            double x = startingNodeLocation.getX() + DEFAULT_WIDTH;
                            double y = startingNodeLocation.getY() + startingNodeBounds.getHeight();
                            return new Point2D.Double(x, y);
                        }
                    }
                    if (isEndingNode) {
                        Point2D startingNodeLocation = e.getStart().getLocationOnGraph();
                        Rectangle2D startingNodeBounds = e.getStart().getBounds();
                        Point2D endingNodeLocation = getLocationOnGraph();
                        Direction d = e.getDirection(this);
                        if (d.getX() > 0)
                        {
                            double x = endingNodeLocation.getX() ; 
                            double y = startingNodeLocation.getY() + startingNodeBounds.getHeight();
                            return new Point2D.Double(x, y);
                        }
                        else
                        {
                            double x = endingNodeLocation.getX() + DEFAULT_WIDTH;
                            double y = startingNodeLocation.getY() + startingNodeBounds.getHeight();
                            return new Point2D.Double(x, y);
                        }
                    }
                }
            }
        }
        // Default case
        Direction d = e.getDirection(this);
        if (d.getX() > 0)
        {
            double y = getBounds().getMinY();
            double x = getBounds().getMaxX();
            return new Point2D.Double(x, y);
        }
        else
        {
            double y = getBounds().getMinY();
            double x = getBounds().getX();
            return new Point2D.Double(x, y);
        }

    }
    
    
   

    @Override
    public Point2D getLocation()
    {
        double x = getHorizontalLocation();
        double y = getVerticalLocation();
        return new Point2D.Double(x, y);
    }

    /**
     * 
     * @return true if this activation bar is connected to another one from another lifeline with a CallEdge AND if this activation
     *         bar is the STARTING node of this edge
     */
    private boolean isCallingNode()
    {
        LifelineNode currentLifelineNode = getImplicitParameter();
        for (IEdge edge : getGraph().getAllEdges())
        {
            if (edge.getStart() != this)
            {
                continue;
            }
            if (!edge.getClass().isAssignableFrom(CallEdge.class))
            {
                continue;
            }
            INode endingNode = edge.getEnd();
            if (!endingNode.getClass().isAssignableFrom(ActivationBarNode.class))
            {
                continue;
            }
            if (((ActivationBarNode) endingNode).getImplicitParameter() == currentLifelineNode)
            {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * 
     * @return true if this activation bar has been called by another activation bar
     */
    private boolean isCalledNode()
    {
        LifelineNode currentLifelineNode = getImplicitParameter();
        for (IEdge edge : getGraph().getAllEdges())
        {
            if (edge.getEnd() != this)
            {
                continue;
            }
            if (!edge.getClass().isAssignableFrom(CallEdge.class))
            {
                continue;
            }
            INode startingNode = edge.getStart();
            if (!startingNode.getClass().isAssignableFrom(ActivationBarNode.class))
            {
                continue;
            }
            if (((ActivationBarNode) startingNode).getImplicitParameter() == currentLifelineNode)
            {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    public Rectangle2D getBounds()
    {
        Point2D nodeLocation = getLocation();
        // Height
        double height = DEFAULT_HEIGHT;
        boolean isCallingNode = isCallingNode();
        if (isCallingNode)
        {
            height = getHeightWhenLinked();
        }
        if (!isCallingNode)
        {
            height = getHeightWhenHasChildren();
        }
        // TODO : manage openbottom
        Rectangle2D currentBounds = new Rectangle2D.Double(nodeLocation.getX(), nodeLocation.getY(), DEFAULT_WIDTH, height);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(currentBounds);
        return snappedBounds;
    }

    /**
     * If this activation bar calls another activation bar on another life line, its height must be greater than the activation bar
     * which is called
     * 
     * @return h
     */
    private double getHeightWhenLinked()
    {
        for (IEdge edge : getGraph().getAllEdges())
        {
            if (!edge.getClass().isAssignableFrom(CallEdge.class))
            {
                continue;
            }
            if (edge.getStart() == this)
            {
                INode endingNode = edge.getEnd();
                Rectangle2D endingNodeBounds = endingNode.getBounds();
                return CALL_YGAP / 2 + endingNodeBounds.getHeight() + CALL_YGAP / 2;

            }
        }
        return DEFAULT_HEIGHT;
    }

    /**
     * 
     * @return
     */
    private double getHeightWhenHasChildren()
    {
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
        return height;
    }

    @Override
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
            for (INode aChildNode : getChildren()) {
                if (ActivationBarNode.class.isInstance(aChildNode)) {
                    ActivationBarNode aChildActivationBarNode = (ActivationBarNode) aChildNode;
                    aChildActivationBarNode.setImplicitParameter(newValue);
                }
            }
        }
    }

    private boolean isReturnEdgeAcceptable(ReturnEdge edge)
    {
        INode endingNode = edge.getEnd();
        INode startingNode = edge.getStart();
        Class<? extends INode> startingNodeClass = startingNode.getClass();
        Class<? extends INode> endingNodeClass = endingNode.getClass();
        if (!startingNodeClass.isAssignableFrom(ActivationBarNode.class) || !endingNodeClass.isAssignableFrom(ActivationBarNode.class))
        {
            return false;
        }
        ActivationBarNode startingActivationBarNode = (ActivationBarNode) startingNode;
        ActivationBarNode endingActivationBarNode = (ActivationBarNode) endingNode;
        if (startingActivationBarNode.getImplicitParameter() == endingActivationBarNode.getImplicitParameter())
        {
            return false;
        }
        if (!isCalledNode())
        {
            return false;
        }
        return true;
    }

    private boolean isCallEdgeAcceptable(CallEdge edge)
    {
        INode endingNode = edge.getEnd();
        INode startingNode = edge.getStart();
        Point2D endingNodePoint = edge.getEndLocation();
        Class<?> startingNodeClass = (startingNode != null ? startingNode.getClass() : NullType.class);
        Class<?> endingNodeClass = (endingNode != null ? endingNode.getClass() : NullType.class);
        // Case 1 : classic connection between activation bars
        if (startingNodeClass.isAssignableFrom(ActivationBarNode.class) && endingNodeClass.isAssignableFrom(ActivationBarNode.class))
        {
            return true;
        }
        // Case 2 : an activation bar creates a new class instance
        if (startingNodeClass.isAssignableFrom(ActivationBarNode.class) && endingNodeClass.isAssignableFrom(LifelineNode.class))
        {
            ActivationBarNode startingActivationBarNode = (ActivationBarNode) startingNode;
            LifelineNode startingLifeLineNode = startingActivationBarNode.getImplicitParameter();
            LifelineNode endingLifeLineNode = (LifelineNode) endingNode;
            Rectangle2D topRectangle = endingLifeLineNode.getTopRectangle();
            if (startingLifeLineNode != endingLifeLineNode && topRectangle.contains(endingNodePoint))
            {
                return true;
            }
        }
        // Case 3 : classic connection between activation bars but the ending bar doesn't exist and need to be automatically created
        if (startingNodeClass.isAssignableFrom(ActivationBarNode.class) && endingNodeClass.isAssignableFrom(LifelineNode.class))
        {
            ActivationBarNode startingActivationBarNode = (ActivationBarNode) startingNode;
            LifelineNode startingLifeLineNode = startingActivationBarNode.getImplicitParameter();
            LifelineNode endingLifeLineNode = (LifelineNode) endingNode;
            Rectangle2D topRectangle = endingLifeLineNode.getTopRectangle();
            if (startingLifeLineNode != endingLifeLineNode && !topRectangle.contains(endingNodePoint))
            {
                ActivationBarNode newActivationBar = new ActivationBarNode();
                int lastNodePos = endingNode.getChildren().size();
                endingNode.addChild(newActivationBar, lastNodePos);
                edge.setEnd(newActivationBar);
                return true;
            }
        }
        // Case 4 : self call on an activation bar
        if (startingNodeClass.isAssignableFrom(ActivationBarNode.class) && endingNodeClass.isAssignableFrom(LifelineNode.class))
        {
            ActivationBarNode startingActivationBarNode = (ActivationBarNode) startingNode;
            LifelineNode startingLifeLineNode = startingActivationBarNode.getImplicitParameter();
            LifelineNode endingLifeLineNode = (LifelineNode) endingNode;
            Rectangle2D topRectangle = endingLifeLineNode.getTopRectangle();
            if (startingLifeLineNode == endingLifeLineNode && !topRectangle.contains(endingNodePoint))
            {
                ActivationBarNode newActivationBar = new ActivationBarNode();
                int lastNodePos = startingNode.getChildren().size();
                startingNode.addChild(newActivationBar, lastNodePos);
                edge.setEnd(newActivationBar);
                return true;
            }
        }
        // Case 5 : self call on an activation bar but its child which is called doesn"t exist and need to be created automatically
        if (startingNodeClass.isAssignableFrom(ActivationBarNode.class) && endingNodeClass.isAssignableFrom(NullType.class))
        {
            ActivationBarNode newActivationBar = new ActivationBarNode();
            int lastNodePos = startingNode.getChildren().size();
            startingNode.addChild(newActivationBar, lastNodePos);
            edge.setEnd(newActivationBar);
            return true;
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
        for (IEdge e : getGraph().getAllEdges())
        {
            if (e.getStart() == start && e.getEnd() == end) return e;
        }
        return null;
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
            // Case 1 : the activation bar is adjusted with all the others top level activation bars on the same lifeline
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
            
            // Case 2 : the activation bar can be connected to another bar which IS NOT on the same lifeline
            List<IEdge> connectedEdges = getConnectedEdges();
            for (IEdge anEdge : connectedEdges) {
                if (!anEdge.getClass().isAssignableFrom(CallEdge.class)) {
                    continue;
                }
                INode startingNode = anEdge.getStart();
                INode endingNode = anEdge.getEnd();
                if (endingNode == this) {
                    double minY = startingNode.getLocationOnGraph().getY() + CALL_YGAP / 2;
                    y = Math.max(y, minY) - this.lifeline.getLocationOnGraph().getY();
                }
            }
            return y;
        }
        
        
        return 0;
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
