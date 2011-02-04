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
 * A field node in an object diagram.
 */
public class FieldNode extends RectangularNode
{
    /**
     * Default constructor
     */
    public FieldNode()
    {
        name = new MultiLineString();
        name.setJustification(MultiLineString.RIGHT);
        value = new MultiLineString();
        equalSeparator = new MultiLineString();
        equalSeparator.setText(" = ");
        setZ(1);
    }

    @Override
    public Point2D getLocation()
    {
        Point2D location = new Point2D.Double(this.horizontalLocation, this.verticalLocation);
        Point2D snappedLocation = getGraph().getGrid().snap(location);
        return snappedLocation;
    }
    
    private void adjustVerticalLocation() {
        this.verticalLocation = 0;
        INode parent = getParent();
        if (parent == null) {
            return;
        }
        List<INode> children = parent.getChildren();
        ObjectNode parentNode = (ObjectNode) parent;
        Rectangle2D topRectangle = parentNode.getTopRectangle();
        this.verticalLocation = topRectangle.getHeight();
        for (INode node : children) {
            if (node == this) {
                return;
            }
            Rectangle2D bounds = node.getBounds();
            double nodeHeight = bounds.getHeight();
            this.verticalLocation = this.verticalLocation + nodeHeight + YGAP;
        }
    }
    
    private void adjustHorizontalLocation() {
        this.horizontalLocation = 0;
        double maxWidth = 0;
        INode parent = getParent();
        if (parent == null) {
            return;
        }
        for (INode node : parent.getChildren()) {
            if (node == this) {
                continue;
            }
            if (!node.getClass().isAssignableFrom(FieldNode.class)) {
                continue;
            }
            Rectangle2D bounds = node.getBounds();
            double nodeWidth = bounds.getWidth();
            maxWidth = Math.max(maxWidth, nodeWidth);
        }
        Rectangle2D currentBounds = getBounds();
        double currentWidth = currentBounds.getWidth();
        if (currentWidth < maxWidth) {
            this.horizontalLocation = (maxWidth - currentWidth) / 2;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.INode#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        adjustHorizontalLocation();
        adjustVerticalLocation();
        // Translate g2 if node has parent
        Point2D nodeLocationOnGraph = getLocationOnGraph();
        Point2D nodeLocation = getLocation();
        Point2D g2Location = new Point2D.Double(nodeLocationOnGraph.getX() - nodeLocation.getX(), nodeLocationOnGraph.getY() - nodeLocation.getY());
        g2.translate(g2Location.getX(), g2Location.getY());
        // Perform drawing
        Rectangle2D b = getBounds();
        name.draw(g2, getNameBounds());
        equalSeparator.draw(g2, getEqualSeparatorBounds());
        value.draw(g2, getValueBounds());
        // Restore g2 original location
        g2.translate(-g2Location.getX(), -g2Location.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.INode#addIEdge(com.horstmann.violet.framework.IEdge, java.awt.geom.Point2D,
     *      java.awt.geom.Point2D)
     */
    @Override
    public boolean checkAddEdge(IEdge e, Point2D p1, Point2D p2)
    {
        INode endingINode = e.getEnd();
        if (e instanceof ObjectReferenceEdge && endingINode instanceof ObjectNode)
        {
            Object oldValue = value.clone();
            value.setText("");
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.INode#addINode(com.horstmann.violet.framework.INode, java.awt.geom.Point2D)
     */
    @Override
    public boolean addChildNode(INode n, Point2D p)
    {
        if (n instanceof PointNode)
        {
            return true;
        }
        return false;
    }


    @Override
    public Point2D getConnectionPoint(Direction d)
    {
        Rectangle2D b = getBounds();
        return new Point2D.Double((b.getMaxX() + b.getX() + getAxisX()) / 2, b.getCenterY());
    }
    
    private Rectangle2D getNameBounds() {
        Rectangle2D nameBounds = name.getBounds();
        Point2D currentLocation = getLocation();
        double x = currentLocation.getX();
        double y = currentLocation.getY();
        double w = nameBounds.getWidth();
        double h = nameBounds.getHeight();
        nameBounds = new Rectangle2D.Double(x, y, w, h);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(nameBounds);
        return snappedBounds;
    }
    
    private Rectangle2D getEqualSeparatorBounds() {
        Rectangle2D equalsSeparatorBounds = equalSeparator.getBounds();
        Rectangle2D nameBounds = getNameBounds();
        double x = nameBounds.getMaxX();
        double y = nameBounds.getY();
        double w = equalsSeparatorBounds.getWidth();
        double h = equalsSeparatorBounds.getHeight();
        equalsSeparatorBounds = new Rectangle2D.Double(x, y, w, h);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(equalsSeparatorBounds);
        return snappedBounds;
    }
    
    private Rectangle2D getValueBounds() {
        Rectangle2D valueBounds = value.getBounds();
        Rectangle2D equalSeparatorBounds = getEqualSeparatorBounds();
        double x = equalSeparatorBounds.getMaxX();
        double y = equalSeparatorBounds.getY();
        double w = valueBounds.getWidth();
        double h = valueBounds.getHeight();
        valueBounds = new Rectangle2D.Double(x, y, w, h);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(valueBounds);
        return snappedBounds;
    }
    
    @Override
    public Rectangle2D getBounds()
    {
        Rectangle2D nameBounds = getNameBounds();
        Rectangle2D valueBounds = getValueBounds();
        Rectangle2D equalSeparatorBounds = getEqualSeparatorBounds();
        nameBounds.add(equalSeparatorBounds);
        nameBounds.add(valueBounds);
        double x = nameBounds.getX();
        double y = nameBounds.getY();
        double w = Math.max(nameBounds.getWidth(), DEFAULT_WIDTH);
        double h = Math.max(nameBounds.getHeight(), DEFAULT_HEIGHT);
        Rectangle2D globalBounds = new Rectangle2D.Double(x, y, w, h);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(globalBounds);
        return snappedBounds;
    }

    
    /**
     * Sets the name property value.
     * 
     * @param newValue the field name
     */
    public void setName(MultiLineString newValue)
    {
        name = newValue;
    }

    /**
     * Gets the name property value.
     * 
     * @return the field name
     */
    public MultiLineString getName()
    {
        return name;
    }

    /**
     * Sets the value property value.
     * 
     * @param newValue the field value
     */
    public void setValue(MultiLineString newValue)
    {
        value = newValue;
    }

    /**
     * Gets the value property value.
     * 
     * @return the field value
     */
    public MultiLineString getValue()
    {
        return value;
    }

    /**
     * Gets the x-offset of the axis (the location of the = sign) from the left corner of the bounding rectangle.
     * 
     * @return the x-offset of the axis
     */
    public double getAxisX()
    {
        Rectangle2D nameBounds = getNameBounds();
        Rectangle2D equalSeparatorBounds = getEqualSeparatorBounds();
        double leftWidth = nameBounds.getWidth();
        double middleWidth = equalSeparatorBounds.getWidth();
        return leftWidth + middleWidth / 2;
    }

    @Override
    public FieldNode clone()
    {
        FieldNode cloned = (FieldNode) super.clone();
        cloned.name = (MultiLineString) name.clone();
        cloned.value = (MultiLineString) value.clone();
        return cloned;
    }

    private MultiLineString name;
    private MultiLineString value;
    private MultiLineString equalSeparator;

    private static int DEFAULT_WIDTH = 60;
    private static int DEFAULT_HEIGHT = 20;
    private static int XGAP = 5;
    private static int YGAP = 5;
    
    private transient double verticalLocation = 0; 
    private transient double horizontalLocation = 0; 
    
    
}
