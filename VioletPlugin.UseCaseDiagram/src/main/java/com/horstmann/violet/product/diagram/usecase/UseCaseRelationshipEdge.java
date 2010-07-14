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

package com.horstmann.violet.product.diagram.usecase;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.SegmentedLineEdge;
import com.horstmann.violet.product.diagram.abstracts.property.BentStyle;

/**
 * An edge that is shaped like a line with up to three segments with an arrowhead
 */
public class UseCaseRelationshipEdge extends SegmentedLineEdge
{
    /**
     * Constructs a straight edge.
     */
    public UseCaseRelationshipEdge()
    {
        bentStyle = BentStyle.STRAIGHT;
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

    public ArrayList<Point2D> getPoints()
    {

        Line2D connectionPoints = getConnectionPoints();
        Point2D p = connectionPoints.getP1();
        Point2D q = connectionPoints.getP2();
        Rectangle startingRect = new Rectangle(new Point((int) p.getX(), (int) p.getY()), new Dimension());
        Rectangle endingRect = new Rectangle(new Point((int) q.getX(), (int) q.getY()), new Dimension());
        return bentStyle.getPath(startingRect, endingRect);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.SegmentedLineEdge#getConnectionPoints()
     */
    public Line2D getConnectionPoints()
    {
        Direction d1;
        Direction d2;

        Rectangle2D start = getStart().getBounds();
        Rectangle2D end = getEnd().getBounds();
        Point2D startCenter = new Point2D.Double(start.getCenterX(), start.getCenterY());
        Point2D endCenter = new Point2D.Double(end.getCenterX(), end.getCenterY());
        d1 = new Direction(startCenter, endCenter);
        d2 = new Direction(endCenter, startCenter);

        // Forces to connect use case node to cadinal points if it is attached to an actor node
        if (getStart() instanceof ActorNode && getEnd() instanceof UseCaseNode)
        {
            ArrayList<Point2D> points = bentStyle.getPath(getStart().getBounds(), getEnd().getBounds());
            Point2D p = getStart().getConnectionPoint(d1);
            Point2D q = (Point2D) points.get(points.size() - 1);
            return new Line2D.Double(p, q);
        }

        // Forces to connect use case node to cadinal points if it is attached to an actor node
        if (getStart() instanceof ActorNode && getEnd() instanceof ActorNode)
        {
            ArrayList<Point2D> points = bentStyle.getPath(getStart().getBounds(), getEnd().getBounds());
            Point2D p = (Point2D) points.get(0);
            Point2D q = getEnd().getConnectionPoint(d2);
            return new Line2D.Double(p, q);
        }

        // Default connection points
        Point2D p = getStart().getConnectionPoint(d1);
        Point2D q = getEnd().getConnectionPoint(d2);
        return new Line2D.Double(p, q);
    }

    private BentStyle bentStyle;
}
