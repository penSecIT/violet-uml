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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.framework.diagram.Direction;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.diagram.node.RectangularNode;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;
import com.horstmann.violet.product.diagram.common.PointNode;

/**
 * A synchronization bar node in an activity diagram.
 */
public class SynchronizationBarNode extends RectangularNode
{
    /**
     * Construct a join/fork node with a default size
     */
    public SynchronizationBarNode()
    {
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
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
     * @see com.horstmann.violet.framework.diagram.RectangularNode#layout(com.horstmann.violet.framework.diagram.Graph,
     *      java.awt.Graphics2D, com.horstmann.violet.framework.diagram.Grid)
     */
    public void layout(Graphics2D g2, IGrid grid)
    {
        List<INode> connectedNodes = new ArrayList<INode>();
        // needs to contain all incoming and outgoing edges
        for (IEdge e : getGraph().getEdges())
        {
            if (e.getStart() == this) connectedNodes.add(e.getEnd());
            if (e.getEnd() == this) connectedNodes.add(e.getStart());
        }

        Rectangle2D b = getBounds();
        if (connectedNodes.size() > 0)
        {
            double centerY = b.getCenterY();
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            for (INode n : connectedNodes)
            {
                double y = n.getBounds().getCenterY();
                Direction d = y <= centerY ? Direction.NORTH : Direction.SOUTH;
                Point2D c = n.getConnectionPoint(d);
                minX = Math.min(minX, c.getX());
                maxX = Math.max(maxX, c.getX());
            }

            minX -= EXTRA_WIDTH;
            maxX += EXTRA_WIDTH;
            translate(minX - getLocation().getX(), b.getY() - getLocation().getY());
            b = new Rectangle2D.Double(minX, b.getY(), maxX - minX, DEFAULT_HEIGHT);
            setBounds(b);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        g2.fill(getShape());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addNode(com.horstmann.violet.framework.Node, java.awt.geom.Point2D)
     */
    public boolean checkAddNode(INode n, Point2D p)
    {
        if (n instanceof PointNode)
        {
            return true;
        }
        return false;
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public SynchronizationBarNode clone()
    {
        return (SynchronizationBarNode) super.clone();
    }

    private static int DEFAULT_WIDTH = 24;
    private static int DEFAULT_HEIGHT = 4;

    private static int EXTRA_WIDTH = 12;
}
