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
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.diagram.common.PointNode;

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
        if (d.getX() > 0)
        {
            return new Point2D.Double(bounds.getMaxX(), topMinY + ActivationBarNode.CALL_YGAP / 2);
        }
        return new Point2D.Double(bounds.getX(), topMinY + ActivationBarNode.CALL_YGAP / 2);
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
        Rectangle2D topRectangle = new Rectangle2D.Double(topX, topY, topWidth, topHeight);
        Rectangle2D snappedRectangle = getGraph().getGrid().snap(topRectangle);
        return snappedRectangle;
    }

    @Override
    public Rectangle2D getBounds()
    {
        double topRectHeight = getTopRectangle().getHeight();
        double topRectWidth = getTopRectangle().getWidth();
        double height = topRectHeight; // default initial height 
        List<INode> children = getChildren();
        for (INode n : children)
        {
            if (n.getClass().isAssignableFrom(ActivationBarNode.class)) {
                // We are looking for the last activation bar node to get the total height needed
                height = Math.max(height, n.getBounds().getMaxY());
            }
        }
        height = height + ActivationBarNode.CALL_YGAP * 2;
        Point2D nodeLocation = getLocation();
        Rectangle2D bounds = new Rectangle2D.Double(nodeLocation.getX(), nodeLocation.getY(), topRectWidth, height);
        Rectangle2D scaledBounds = getScaledBounds(bounds);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(scaledBounds);
        return snappedBounds;
    }

    private Rectangle2D getScaledBounds(Rectangle2D bounds)
    {
        double x = bounds.getX();
        double y = bounds.getY();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        double diffY = this.maxYOverAllLifeLineNodes - bounds.getMaxY();
        if (diffY > 0)
        {
            h = h + diffY;
        }
        return new Rectangle2D.Double(x, y, w, h);
    }

    public Shape getShape()
    {
        return getTopRectangle();
    }


    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        Rectangle2D top = getTopRectangle();
        g2.draw(top);
        name.draw(g2, top);
        double xmid = top.getCenterX();
        Line2D line = new Line2D.Double(xmid, top.getMaxY(), xmid, getMaxYOverAllLifeLineNodes());
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]
        {
                5.0f,
                5.0f
        }, 0.0f));
        g2.draw(line);
        g2.setStroke(oldStroke);
        // Draw its children
        for (INode node : getChildren())
        {
            node.draw(g2);
        }
    }

    private double getMaxYOverAllLifeLineNodes()
    {
        double maxY = this.getBounds().getMaxY();
        IGraph graph = getGraph();
        if (graph == null)
        {
            return maxY;
        }
        Collection<INode> nodes = graph.getNodes();
        for (INode node : nodes)
        {
            if (!node.getClass().isAssignableFrom(LifelineNode.class))
            {
                continue;
            }
            Rectangle2D aLifeLineBounds = node.getBounds();
            double currentMaxY = aLifeLineBounds.getMaxY();
            maxY = Math.max(maxY, currentMaxY);
        }
        this.maxYOverAllLifeLineNodes = maxY;
        return maxY;
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
    private double maxYOverAllLifeLineNodes = 0;
    private static int DEFAULT_TOP_HEIGHT = 60;
    private static int DEFAULT_WIDTH = 80;
    private static int DEFAULT_HEIGHT = 120;
}
