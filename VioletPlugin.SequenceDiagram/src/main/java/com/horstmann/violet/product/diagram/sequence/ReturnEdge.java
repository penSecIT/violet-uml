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

import com.horstmann.violet.product.diagram.abstracts.edge.SegmentedLineEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.property.ArrowHead;
import com.horstmann.violet.product.diagram.abstracts.property.LineStyle;
import com.horstmann.violet.product.diagram.common.PointNode;

/**
 * An edge that joins two call nodes.
 */
public class ReturnEdge extends SegmentedLineEdge
{
    public ReturnEdge()
    {
        setEndArrowHead(ArrowHead.V);
        setLineStyle(LineStyle.DOTTED);
    }

    public ArrayList<Point2D> getPoints()
    {
        ArrayList<Point2D> a = new ArrayList<Point2D>();
        INode n = getEnd();
        Rectangle2D start = getStart().getBounds();
        Rectangle2D end = getEnd().getBounds();
        if (n instanceof PointNode) // show nicely in tool bar
        {
            a.add(new Point2D.Double(end.getX(), end.getY()));
            a.add(new Point2D.Double(start.getMaxX(), end.getY()));
        }
        else if (start.getCenterX() < end.getCenterX())
        {
            a.add(new Point2D.Double(start.getMaxX(), start.getMaxY()));
            a.add(new Point2D.Double(end.getX(), start.getMaxY()));
        }
        else
        {
            a.add(new Point2D.Double(start.getX(), start.getMaxY()));
            a.add(new Point2D.Double(end.getMaxX(), start.getMaxY()));
        }
        return a;
    }
}
