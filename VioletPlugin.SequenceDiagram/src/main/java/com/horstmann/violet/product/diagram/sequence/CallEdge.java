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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.SegmentedLineEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.property.ArrowHead;

/**
 * An edge that joins two call nodes. Typically, call edges are used in sequence diagram to represent calls between entities (call
 * nodes).
 */
public class CallEdge extends SegmentedLineEdge
{
    /**
     * Default constructor
     */
    public CallEdge()
    {
        setSignal(false);
    }

    /**
     * Gets the signal property.
     * 
     * @return true if this is an edge that represents an asynchronus signal
     */
    public boolean isSignal()
    {
        return signal;
    }

    /**
     * Sets the signal property.
     * 
     * @param newValue true if this is an edge that represents an asynchronus signal
     */
    public void setSignal(boolean newValue)
    {
        signal = newValue;
        if (signal) setEndArrowHead(ArrowHead.V);
        else setEndArrowHead(ArrowHead.BLACK_TRIANGLE);
    }

    @Override
    public ArrayList<Point2D> getPoints()
    {
        INode endingNode = getEnd();
        INode startingNode = getStart();
        // Case 1 : classic connection between activation bars on different lifelines
        if (startingNode.getClass().isAssignableFrom(ActivationBarNode.class) && endingNode.getClass().isAssignableFrom(ActivationBarNode.class))
        {
            ActivationBarNode startingActivationBarNode = (ActivationBarNode) startingNode;
            ActivationBarNode endingActivationBarNode = (ActivationBarNode) endingNode;
            if (startingActivationBarNode.getImplicitParameter() != endingActivationBarNode.getImplicitParameter())
            {
                return getPointsForActivationBarsOnDifferentLifeLines(startingActivationBarNode, endingActivationBarNode);
            }
        }
        // Case 2 : self call on an activation bar
        if (startingNode.getClass().isAssignableFrom(ActivationBarNode.class) && endingNode.getClass().isAssignableFrom(ActivationBarNode.class))
        {
            ActivationBarNode startingActivationBarNode = (ActivationBarNode) startingNode;
            ActivationBarNode endingActivationBarNode = (ActivationBarNode) endingNode;
            if (startingActivationBarNode.getImplicitParameter() == endingActivationBarNode.getImplicitParameter())
            {
                return getPointsForLoopOnActivationBarNode(startingActivationBarNode, endingActivationBarNode);
            }
        }
        // Case 3 : activation bar connected to a lifeline by its top rectangle
        if (startingNode.getClass().isAssignableFrom(ActivationBarNode.class) && endingNode.getClass().isAssignableFrom(LifelineNode.class)) {
            ActivationBarNode startingActivationBarNode = (ActivationBarNode) startingNode;
            LifelineNode endingLifelineNode = (LifelineNode) endingNode;
            return getPointsForActivationBarConnectedToLifeLineTopRectangle(startingActivationBarNode, endingLifelineNode);
        }
        // Case 4 : default case
        Point2D startingPoint = getStart().getLocation();
        Point2D endingPoint = getEnd().getLocation();
        ArrayList<Point2D> result = new ArrayList<Point2D>();
        result.add(startingPoint);
        result.add(endingPoint);
        return result;
    }
    
    
    private ArrayList<Point2D> getPointsForActivationBarConnectedToLifeLineTopRectangle(ActivationBarNode startingNode, LifelineNode endingNode) {
        ArrayList<Point2D> a = new ArrayList<Point2D>();
        Rectangle2D startingNodeBounds = startingNode.getBounds();
        Rectangle2D endingNodeBounds = endingNode.getBounds();
        Point2D startingNodeLocationOnGraph = startingNode.getLocationOnGraph();
        Point2D endingNodeLocationOnGraph = endingNode.getLocationOnGraph();
        Rectangle2D startNodeBoundsOnGraph = new Rectangle2D.Double(startingNodeLocationOnGraph.getX(), startingNodeLocationOnGraph.getY(), startingNodeBounds.getWidth(), startingNodeBounds.getHeight());
        Rectangle2D endNodeBoundsOnGraph = new Rectangle2D.Double(endingNodeLocationOnGraph.getX(), endingNodeLocationOnGraph.getY(), endingNodeBounds.getWidth(), endingNodeBounds.getHeight());
        Direction d = new Direction(startNodeBoundsOnGraph.getX() - endNodeBoundsOnGraph.getX(), 0);
        double topMiddleHeight = endingNode.getTopRectangle().getHeight() / 2;
        Point2D endPoint = getEnd().getConnectionPoint(d);
        Point2D endPointOnGraph = new Point2D.Double(endPoint.getX() + endingNodeLocationOnGraph.getX() - endingNodeBounds.getX(), endingNodeLocationOnGraph.getY() + topMiddleHeight);
        if (startNodeBoundsOnGraph.getCenterX() < endPointOnGraph.getX()) {
            Point2D.Double startPointOnGraph = new Point2D.Double(startNodeBoundsOnGraph.getMaxX(), endPointOnGraph.getY());
            a.add(startPointOnGraph);
        }
        else {
            Point2D.Double startPointOnGraph = new Point2D.Double(startNodeBoundsOnGraph.getX(), endPointOnGraph.getY());
            a.add(startPointOnGraph);
        }
        a.add(endPointOnGraph);
        return a;
    }
    

    private ArrayList<Point2D> getPointsForActivationBarsOnDifferentLifeLines(ActivationBarNode startingNode, ActivationBarNode endingNode) {
        ArrayList<Point2D> a = new ArrayList<Point2D>();
        Rectangle2D startingNodeBounds = startingNode.getBounds();
        Rectangle2D endingNodeBounds = endingNode.getBounds();
        Point2D startingNodeLocationOnGraph = startingNode.getLocationOnGraph();
        Point2D endingNodeLocationOnGraph = endingNode.getLocationOnGraph();
        Rectangle2D startNodeBoundsOnGraph = new Rectangle2D.Double(startingNodeLocationOnGraph.getX(), startingNodeLocationOnGraph.getY(), startingNodeBounds.getWidth(), startingNodeBounds.getHeight());
        Rectangle2D endNodeBoundsOnGraph = new Rectangle2D.Double(endingNodeLocationOnGraph.getX(), endingNodeLocationOnGraph.getY(), endingNodeBounds.getWidth(), endingNodeBounds.getHeight());
        Direction d = new Direction(startNodeBoundsOnGraph.getX() - endNodeBoundsOnGraph.getX(), 0);
        Point2D endPoint = getEnd().getConnectionPoint(d);
        Point2D endPointOnGraph = new Point2D.Double(endPoint.getX() + endingNodeLocationOnGraph.getX() - endingNodeBounds.getX(), endingNodeLocationOnGraph.getY());
        if (startNodeBoundsOnGraph.getCenterX() < endPointOnGraph.getX()) {
            Point2D.Double startPointOnGraph = new Point2D.Double(startNodeBoundsOnGraph.getMaxX(), endPointOnGraph.getY());
            a.add(startPointOnGraph);
        }
        else {
            Point2D.Double startPointOnGraph = new Point2D.Double(startNodeBoundsOnGraph.getX(), endPointOnGraph.getY());
            a.add(startPointOnGraph);
        }
        a.add(endPointOnGraph);
        return a;
    }
    
    private ArrayList<Point2D> getPointsForLoopOnActivationBarNode(ActivationBarNode startingNode, ActivationBarNode endingNode)
    {
        ArrayList<Point2D> a = new ArrayList<Point2D>();
        Rectangle2D startingNodeBounds = startingNode.getBounds();
        Rectangle2D endingNodeBounds = endingNode.getBounds();
        Point2D startingNodeLocationOnGraph = startingNode.getLocationOnGraph();
        Point2D endingNodeLocationOnGraph = endingNode.getLocationOnGraph();
        Rectangle2D startNodeBoundsOnGraph = new Rectangle2D.Double(startingNodeLocationOnGraph.getX(), startingNodeLocationOnGraph.getY(), startingNodeBounds.getWidth(), startingNodeBounds.getHeight());
        Rectangle2D endNodeBoundsOnGraph = new Rectangle2D.Double(endingNodeLocationOnGraph.getX(), endingNodeLocationOnGraph.getY(), endingNodeBounds.getWidth(), endingNodeBounds.getHeight());
        Point2D p = new Point2D.Double(startNodeBoundsOnGraph.getMaxX(), endNodeBoundsOnGraph.getY() - ActivationBarNode.CALL_YGAP / 2);
        Point2D q = new Point2D.Double(endNodeBoundsOnGraph.getMaxX() + endNodeBoundsOnGraph.getWidth(), p.getY());
        Point2D r = new Point2D.Double(q.getX(), endNodeBoundsOnGraph.getY());
        Point2D s = new Point2D.Double(endNodeBoundsOnGraph.getMaxX(), r.getY());
        a.add(p);
        a.add(q);
        a.add(r);
        a.add(s);
        return a;
    }

    /** Indicate if the node represents an asynchonus signal */
    private boolean signal;
}
