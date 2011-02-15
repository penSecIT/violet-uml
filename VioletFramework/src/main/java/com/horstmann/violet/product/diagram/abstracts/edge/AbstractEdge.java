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

package com.horstmann.violet.product.diagram.abstracts.edge;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.Id;
import com.horstmann.violet.product.diagram.abstracts.node.INode;

/**
 * A class that supplies convenience implementations for a number of methods in the Edge interface
 */
public abstract class AbstractEdge implements IEdge
{
    public AbstractEdge()
    {
        this.id = new Id();
    }

    @Override
    public void setStart(INode startingNode)
    {
        this.start = startingNode;
    }

    @Override
    public INode getStart()
    {
        return start;
    }

    @Override
    public void setEnd(INode endingNode)
    {
        this.end = endingNode;
    }

    @Override
    public INode getEnd()
    {
        return end;
    }

    @Override
    public void setStartLocation(Point2D startLocation)
    {
        this.startLocation = startLocation;
    }

    @Override
    public Point2D getStartLocation()
    {
        return startLocation;
    }

    @Override
    public void setEndlocation(Point2D endLocation)
    {
        this.endLocation = endLocation;
    }

    @Override
    public Point2D getEndLocation()
    {
        return this.endLocation;
    }

    @Override
    public Rectangle2D getBounds()
    {
        Line2D conn = getConnectionPoints();
        Rectangle2D r = new Rectangle2D.Double();
        r.setFrameFromDiagonal(conn.getX1(), conn.getY1(), conn.getX2(), conn.getY2());
        return r;
    }

    @Override
    public Line2D getConnectionPoints()
    {
        Rectangle2D startBounds = start.getBounds();
        Rectangle2D endBounds = end.getBounds();
        Point2D startCenter = new Point2D.Double(startBounds.getCenterX(), startBounds.getCenterY());
        Point2D endCenter = new Point2D.Double(endBounds.getCenterX(), endBounds.getCenterY());
        Direction toEnd = new Direction(startCenter, endCenter);
        return new Line2D.Double(start.getConnectionPoint(toEnd), end.getConnectionPoint(toEnd.turn(180)));
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
    public AbstractEdge clone()
    {
        try
        {
            AbstractEdge cloned = (AbstractEdge) super.clone();
            cloned.id = new Id();
            return cloned;
        }
        catch (CloneNotSupportedException ex)
        {
            return null;
        }
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

    /**
     * Sets edge tool tip
     * 
     * @param s
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

    /** The node where the edge starts */
    private INode start;

    /** The node where the edge ends */
    private INode end;

    /** The point inside the starting node where this edge begins */
    private Point2D startLocation;

    /** The point inside the ending node where this edge ends */
    private Point2D endLocation;

    /** Edge's current id (unique in all the graph) */
    private Id id;

    /** Edge's current revision */
    private Integer revision = new Integer(0);

    /** Edge tool tip */
    private transient String toolTip;
}
