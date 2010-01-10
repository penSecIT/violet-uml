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

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.EllipticalNode;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;


/**
 * A final node (bull's eye) in an activity diagram.
 */
public class ScenarioEndNode extends EllipticalNode
{
    /**
     * Construct a node with a default size
     * 
     */
    public ScenarioEndNode()
    {
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_DIAMETER + 2 * DEFAULT_GAP, DEFAULT_DIAMETER + 2 * DEFAULT_GAP));
    }
    
    @Override
    public void layout(Graphics2D g2, IGrid grid)
    {
        snapBounds(grid, DEFAULT_DIAMETER + 2 * DEFAULT_GAP, DEFAULT_DIAMETER + 2 * DEFAULT_GAP);
    }    

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.AbstractNode#addEdge(com.horstmann.violet.framework.diagram.Edge,
     *      java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    public boolean checkAddEdge(IEdge e, Point2D p1, Point2D p2)
    {
        return e.getEnd() != null && this != e.getEnd();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        Ellipse2D circle = new Ellipse2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds()
                .getHeight());

        Rectangle2D bounds = getBounds();
        Ellipse2D inside = new Ellipse2D.Double(bounds.getX() + DEFAULT_GAP, bounds.getY() + DEFAULT_GAP, bounds.getWidth() - 2
                * DEFAULT_GAP, bounds.getHeight() - 2 * DEFAULT_GAP);
        g2.fill(inside);
        g2.draw(circle);
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public ScenarioEndNode clone()
    {
        return (ScenarioEndNode) super.clone();
    }

    /** default node diameter */
    private static int DEFAULT_DIAMETER = 14;

    /** default gap between the main circle and the ring for a final node */
    private static int DEFAULT_GAP = 3;
}
