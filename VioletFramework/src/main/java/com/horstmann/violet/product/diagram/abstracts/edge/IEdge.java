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

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.horstmann.violet.product.diagram.abstracts.Id;
import com.horstmann.violet.product.diagram.abstracts.node.INode;

/**
 * An edge in a graph.
 */
public interface IEdge extends Serializable, Cloneable
{
    /**
     * Draw the edge.
     * 
     * @param g2 the graphics context
     */
    void draw(Graphics2D g2);

    /**
     * Tests whether the edge contains a point.
     * 
     * @param aPoint the point to test
     * @return true if this edge contains aPoint
     */
    boolean contains(Point2D aPoint);

    /**
     * Connect this edge to two nodes.
     * 
     * @param aStart the starting node
     * @param anEnd the ending node
     */
    void connect(INode aStart, INode anEnd);

    /**
     * Gets the starting node.
     * 
     * @return the starting node
     */
    INode getStart();

    /**
     * Gets the ending node.
     * 
     * @return the ending node
     */
    INode getEnd();

    /**
     * Gets the points at which this edge is connected to its nodes.
     * 
     * @return a line joining the two connection points
     */
    Line2D getConnectionPoints();

    /**
     * Gets the smallest rectangle that bounds this edge. The bounding rectangle contains all labels.
     * 
     * @return the bounding rectangle
     */
    Rectangle2D getBounds();

    /**
     * Returns a unique id of this edge to make it easier to identify
     * 
     * @return a unique id
     */
    Id getId();
    
    /**
     * Sets unique id to this edge to make it easier to identify
     * 
     * @param id new unique id
     */
    void setId(Id id);

    
    /**
     * Returns current edge revision
     */
    Integer getRevision();
    
    
    /**
     * Updates current edge revision number
     * @param newRevisionNumber n
     */
    void setRevision(Integer newRevisionNumber);    
    
    /**
     * Auto-increments revision number
     */
    void incrementRevision();    
    
    /**
     * Gets current edge tool tip
     * @return s
     */
    String getToolTip();

    IEdge clone();
    
}
