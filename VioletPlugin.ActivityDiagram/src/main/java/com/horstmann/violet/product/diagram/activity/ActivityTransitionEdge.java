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

package com.horstmann.violet.product.diagram.activity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.SegmentedLineEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.property.BentStyle;


/**
 * An edge that is shaped like a line with up to three segments with an arrowhead
 */
public class ActivityTransitionEdge extends SegmentedLineEdge
{
    /**
     * Constructs a straight edge.
     */
    public ActivityTransitionEdge()
    {
        bentStyle = BentStyle.VHV;
    }

    /**
     * Sets the bentStyle property
     * 
     * @param newValue the bent style
     */
    public void setBentStyle(BentStyle newValue)
    {
        bentStyle = newValue;
    }

    /**
     * Gets the bentStyle property
     * 
     * @return the bent style
     */
    public BentStyle getBentStyle()
    {
        return bentStyle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.SegmentedLineEdge#getPoints()
     */
    public ArrayList<Point2D> getPoints()
    {
        Point2D startingPoint = getStart().getConnectionPoint(this);
        Point2D endingPoint = getEnd().getConnectionPoint(this);
        Direction startingCardinalDirection = getDirection(getStart()).getNearestCardinalDirection();
        Direction endingCardinalDirection = getDirection(getEnd()).getNearestCardinalDirection();
        
        if (BentStyle.AUTO.equals(getBentStyle())) {
            Rectangle2D startBounds = getStart().getBounds();
            Rectangle2D endBounds = getEnd().getBounds();
            ArrayList<Point2D> r = new ArrayList<Point2D>();

            if (getEnd() instanceof SynchronizationBarNode)
            {
                double startY = startBounds.getCenterY();
                double endY = endBounds.getCenterY();
                Point2D startPoint = getStart().getConnectionPoint(this); 
                r.add(startPoint);
                r.add(new Point2D.Double(startPoint.getX(), startY >= endY ? endBounds.getMaxY() : endBounds.getY()));
                return r;
            }
            if (getStart() instanceof SynchronizationBarNode)
            {
                double startY = startBounds.getCenterY();
                double endY = endBounds.getCenterY();
                Point2D endPoint = getEnd().getConnectionPoint(this); 
                r.add(new Point2D.Double(endPoint.getX(), startY >= endY ? startBounds.getY() : startBounds.getMaxY()));
                r.add(endPoint);
                return r;
            }
            if (getStart() instanceof DecisionNode) {
                return BentStyle.HV.getPath(getStart().getConnectionPoint(this), getEnd().getConnectionPoint(this));
            }
        }

        

        if ((Direction.NORTH.equals(startingCardinalDirection) || Direction.SOUTH.equals(startingCardinalDirection)) && (Direction.NORTH.equals(endingCardinalDirection) || Direction.SOUTH.equals(endingCardinalDirection))) {
            return BentStyle.VHV.getPath(startingPoint, endingPoint);
        }
        if ((Direction.NORTH.equals(startingCardinalDirection) || Direction.SOUTH.equals(startingCardinalDirection)) && (Direction.EAST.equals(endingCardinalDirection) || Direction.WEST.equals(endingCardinalDirection))) {
            return BentStyle.VH.getPath(startingPoint, endingPoint);
        }
        if ((Direction.EAST.equals(startingCardinalDirection) || Direction.WEST.equals(startingCardinalDirection)) && (Direction.NORTH.equals(endingCardinalDirection) || Direction.SOUTH.equals(endingCardinalDirection))) {
            return BentStyle.HV.getPath(startingPoint, endingPoint);
        }
        if ((Direction.EAST.equals(startingCardinalDirection) || Direction.WEST.equals(startingCardinalDirection)) && (Direction.EAST.equals(endingCardinalDirection) || Direction.WEST.equals(endingCardinalDirection))) {
            return BentStyle.HVH.getPath(startingPoint, endingPoint);
        }
        return BentStyle.STRAIGHT.getPath(startingPoint, endingPoint);
    }

    /**
     * Bent style
     */
    private BentStyle bentStyle;
}
