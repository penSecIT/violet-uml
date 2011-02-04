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

package com.horstmann.violet.product.diagram.object;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.diagram.common.PointNode;

/**
 * An object node in an object diagram.
 */
public class ObjectNode extends RectangularNode
{
    /**
     * Construct an object node with a default size
     */
    public ObjectNode()
    {
        name = new MultiLineString();
        name.setUnderlined(true);
        name.setSize(MultiLineString.LARGE);
    }

    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        Rectangle2D globalBounds = getBounds();
        Rectangle2D topBounds = getTopRectangle();
        g2.draw(globalBounds);
        name.draw(g2, topBounds);
        g2.drawLine((int) globalBounds.getX(),(int) topBounds.getMaxY(),(int) globalBounds.getMaxX(),(int) topBounds.getMaxY());
        for (INode n : getChildren()) {
            n.draw(g2); // make sure they get drawn on top
        }
    }

        
    /**
     * Returns the rectangle at the top of the object node.
     * 
     * @return the top rectangle
     */
    public Rectangle2D getTopRectangle()
    {
        Rectangle2D b = name.getBounds();
        double defaultHeight = DEFAULT_HEIGHT;
        boolean hasChildren = (getChildren().size() > 0);
        if (hasChildren) {
            defaultHeight = defaultHeight - YGAP;
        }
        Point2D currentLocation = getLocation();
        double x = currentLocation.getX();
        double y = currentLocation.getY();
        double w = Math.max(b.getWidth(), DEFAULT_WIDTH);
        double h = Math.max(b.getHeight(), DEFAULT_HEIGHT);
        Rectangle2D topBounds = new Rectangle2D.Double(x, y, w, h);
        topBounds = getGraph().getGrid().snap(topBounds);
        return topBounds;
    }
    
    private Rectangle2D getBottomRectangle() {
        Rectangle2D topBounds = getTopRectangle();
        double topHeight = topBounds.getHeight();
        Rectangle2D bottomBounds = new Rectangle2D.Double(0, topHeight, 0, 0);
        for (INode node : getChildren()) {
            Rectangle2D nodeBounds = node.getBounds();
            bottomBounds.add(nodeBounds);
        }
        double x = topBounds.getX();
        double y = topBounds.getMaxY();
        double w = bottomBounds.getWidth();
        double h = bottomBounds.getHeight();
        bottomBounds.setFrame(x, y, w, h);
        bottomBounds = getGraph().getGrid().snap(bottomBounds);
        return bottomBounds;
    }
    
    @Override
    public Rectangle2D getBounds()
    {
        Rectangle2D topBounds = getTopRectangle();
        Rectangle2D bottomBounds = getBottomRectangle();
        topBounds.add(bottomBounds);
        topBounds = getGraph().getGrid().snap(topBounds);
        return topBounds;
    }

    public boolean checkAddEdge(IEdge e, Point2D p1, Point2D p2)
    {
        if (!e.getClass().isAssignableFrom(ObjectRelationshipEdge.class)) {
            return false;
        }
        INode startingNode = e.getStart();
        INode endingNode = e.getEnd();
        if (startingNode.getClass().isAssignableFrom(FieldNode.class)) {
            startingNode = startingNode.getParent();
        }
        if (endingNode.getClass().isAssignableFrom(FieldNode.class)) {
            endingNode = endingNode.getParent();
        }
        e.connect(startingNode, endingNode);
        return true;
    }

    public Point2D getConnectionPoint(Direction d)
    {
        Rectangle2D topBounds = getTopRectangle();
        double topHeight = topBounds.getHeight();
        if (d.getX() > 0) {
            return new Point2D.Double(getBounds().getMaxX(), getBounds().getMinY() + topHeight / 2);
        }
        return new Point2D.Double(getBounds().getX(), getBounds().getMinY() + topHeight / 2);
    }

    
    /**
     * Sets the name property value.
     * 
     * @param newValue the new object name
     */
    public void setName(MultiLineString n)
    {
        name = n;
    }

    /**
     * Gets the name property value.
     * 
     * @param the object name
     */
    public MultiLineString getName()
    {
        return name;
    }

    @Override
    public boolean addChildNode(INode n, Point2D p)
    {
        List<INode> fields = getChildren();
        if (!(n instanceof FieldNode)) return false;
        if (fields.contains(n)) return true;
        int i = 0;
        while (i < fields.size() && fields.get(i).getLocation().getY() < p.getY())
            i++;
        addChildNode(n, i);
        n.setGraph(getGraph());
        n.setParent(this);
        return true;
    }

    
    public ObjectNode clone()
    {
        ObjectNode cloned = (ObjectNode) super.clone();
        cloned.name = name.clone();
        return cloned;
    }

    private MultiLineString name;

    private static int DEFAULT_WIDTH = 80;
    private static int DEFAULT_HEIGHT = 60;
    private static int YGAP = 5;
}
