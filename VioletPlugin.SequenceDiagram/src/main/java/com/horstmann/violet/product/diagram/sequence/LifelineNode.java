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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.diagram.common.PointNode;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * An object node in a scenario diagram.
 */
public class LifelineNode extends RectangularNode
{
    /**
     * Construct an object node with a default size
     */
    public LifelineNode()
    {
        name = new MultiLineString();
        name.setUnderlined(true);
    }

    /**
     * Sets the name property value.
     * 
     * @param newValue the name of this object
     */
    public void setName(MultiLineString n)
    {
        name = n;
    }

    /**
     * Gets the name property value.
     * 
     * @return the name of this object
     */
    public MultiLineString getName()
    {
        return name;
    }

    public boolean checkAddEdge(IEdge e, Point2D p1, Point2D p2)
    {
        return false;
    }

    public boolean addChildNode(INode n, Point2D p)
    {
        if (n instanceof ActivationBarNode || n instanceof PointNode)
        {
            if (n instanceof ActivationBarNode)
            {
                ((ActivationBarNode) n).setImplicitParameter(this);
            }
            int pos = getChildren().size();
            addChildNode(n, pos);
            return true;
        }
        return false;
    }

    public Point2D getConnectionPoint(Direction d)
    {
        Rectangle2D bounds = getBounds();
        Rectangle2D topRectangle = getTopRectangle();
        double topMinY = topRectangle.getMinY();
        double topHeight = topRectangle.getHeight();
        if (d.getX() > 0)
        {
            return new Point2D.Double(bounds.getMaxX(), topMinY + topHeight / 2);
        }
        return new Point2D.Double(bounds.getX(), topMinY + topHeight / 2);
    }

    @Override
    public Point2D getLocation()
    {
        IGraph currentGraph = getGraph();
        if (currentGraph == null)
        {
            return new Point2D.Double(0, 0);
        }
        Collection<IEdge> edges = currentGraph.getEdges();
        for (IEdge edge : edges)
        {
            if (edge instanceof CallEdge)
            {
                INode endingNode = edge.getEnd();
                if (endingNode == this)
                {
                    INode startingNode = edge.getStart();
                    Point2D locationOnGraph = startingNode.getLocationOnGraph();
                    Point2D realLocation = super.getLocation();
                    Point2D fixedLocation = new Point2D.Double(realLocation.getX(), locationOnGraph.getY());
                    return fixedLocation;
                }
            }
        }
        Point2D realLocation = super.getLocation();
        Point2D fixedLocation = new Point2D.Double(realLocation.getX(), 0);
        return fixedLocation;
    }

    /**
     * Returns the rectangle at the top of the object node.
     * 
     * @return the top rectangle
     */
    public Rectangle2D getTopRectangle()
    {
        Point2D nodeLocation = getLocation();
        Rectangle2D bounds = name.getBounds();
        double topX = nodeLocation.getX();
        double topY = nodeLocation.getY();
        double topWidth = Math.max(bounds.getWidth(), DEFAULT_WIDTH);
        double topHeight = Math.max(bounds.getHeight(), DEFAULT_TOP_HEIGHT);
        return new Rectangle2D.Double(topX, topY, topWidth, topHeight);
    }

    @Override
    public Rectangle2D getBounds()
    {
        double height = 0;
        double topRectHeight = getTopRectangle().getHeight();
        double topRectWidth = getTopRectangle().getWidth();
        height = topRectHeight + ActivationBarNode.CALL_YGAP;
        for (INode n : getChildren())
        {
            double childNodeHeight = n.getBounds().getHeight();
            if (childNodeHeight > 0)
            {
                height = height + childNodeHeight + ActivationBarNode.CALL_YGAP;
            }
        }
        Point2D nodeLocation = getLocation();
        return new Rectangle2D.Double(nodeLocation.getX(), nodeLocation.getY(), topRectWidth, height);
    }

    public Shape getShape()
    {
        return getTopRectangle();
    }

    public void layout(Graphics2D g2, IGrid grid)
    {
        Rectangle2D b = getBounds();
        grid.snap(b);
        setLocation(new Point2D.Double(b.getX(), b.getY()));
    }

    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        Rectangle2D top = getTopRectangle();
        g2.draw(top);
        name.draw(g2, top);
        double xmid = getBounds().getCenterX();
        Line2D line = new Line2D.Double(xmid, top.getMaxY(), xmid, getBounds().getMaxY());
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]
        {
                5.0f,
                5.0f
        }, 0.0f));
        g2.draw(line);
        g2.setStroke(oldStroke);
    }

    public boolean contains(Point2D p)
    {
        Rectangle2D bounds = getBounds();
        return bounds.getX() <= p.getX() && p.getX() <= bounds.getX() + bounds.getWidth();
    }

    public LifelineNode clone()
    {
        LifelineNode cloned = (LifelineNode) super.clone();
        cloned.name = name.clone();
        return cloned;
    }

    private MultiLineString name;
    private static int DEFAULT_TOP_HEIGHT = 60;
    private static int DEFAULT_WIDTH = 80;
    private static int DEFAULT_HEIGHT = 120;
}
