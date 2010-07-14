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

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Edge#connect(com.horstmann.violet.framework.Node, com.horstmann.violet.framework.Node)
     */
    public final void connect(INode s, INode e)
    {
        start = s;
        end = e;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Edge#getStart()
     */
    public INode getStart()
    {
        return start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Edge#getEnd()
     */
    public INode getEnd()
    {
        return end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Edge#getBounds()
     */
    public Rectangle2D getBounds()
    {
        Line2D conn = getConnectionPoints();
        Rectangle2D r = new Rectangle2D.Double();
        r.setFrameFromDiagonal(conn.getX1(), conn.getY1(), conn.getX2(), conn.getY2());
        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Edge#getConnectionPoints()
     */
    public Line2D getConnectionPoints()
    {
        Rectangle2D startBounds = start.getBounds();
        Rectangle2D endBounds = end.getBounds();
        Point2D startCenter = new Point2D.Double(startBounds.getCenterX(), startBounds.getCenterY());
        Point2D endCenter = new Point2D.Double(endBounds.getCenterX(), endBounds.getCenterY());
        Direction toEnd = new Direction(startCenter, endCenter);
        return new Line2D.Double(start.getConnectionPoint(toEnd), end.getConnectionPoint(toEnd.turn(180)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.Edge#getId()
     */
    public Id getId()
    {
        return this.id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.Edge#setId(com.horstmann.violet.product.diagram.abstracts.Id)
     */
    public void setId(Id id)
    {
        this.id = id;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.Edge#getRevision()
     */
    public Integer getRevision()
    {
        return this.revision;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.Edge#setRevision(java.lang.Integer)
     */
    public void setRevision(Integer newRevisionNumber)
    {
        this.revision = newRevisionNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.Edge#incrementRevision()
     */
    public void incrementRevision()
    {
        int i = this.revision.intValue();
        i++;
        this.revision = new Integer(i);
    }

    /**
     * Sets edge tool tip
     * @param s
     */
    public void setToolTip(String s) {
        this.toolTip = s;
    }
    


    /* (non-Javadoc)
     * @see com.horstmann.violet.product.diagram.abstracts.Edge#getToolTip()
     */
    public String getToolTip()
    {
        return this.toolTip;
    }
    
    /** The node where the edge starts */
    private INode start;

    /** The node where the edge ends */
    private INode end;

    /** Edge's current id (unique in all the graph) */
    private Id id;

    /** Edge's current revision */
    private Integer revision = new Integer(0);
    
    /** Edge tool tip */
    private transient String toolTip; 
}
