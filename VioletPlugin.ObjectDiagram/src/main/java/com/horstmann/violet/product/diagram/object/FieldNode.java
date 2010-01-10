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
import java.beans.PropertyChangeEvent;

import com.horstmann.violet.framework.diagram.Direction;
import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.diagram.node.RectangularNode;
import com.horstmann.violet.framework.diagram.property.MultiLineString;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;
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
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
        name = new MultiLineString();
        name.setJustification(MultiLineString.RIGHT);
        value = new MultiLineString();
        equalSeparator = new MultiLineString();
        equalSeparator.setText(" = ");
        setZ(1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.INode#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        // super.draw(g2);
        Rectangle2D b = getBounds();

        double leftWidth = name.getBounds(g2).getWidth();
        double middleWidth = equalSeparator.getBounds(g2).getWidth();
        double rightWidth = value.getBounds(g2).getWidth();

        nameBounds = new Rectangle2D.Double(b.getX(), b.getY(), leftWidth, b.getHeight());
        equalBounds = new Rectangle2D.Double(b.getX() + leftWidth, b.getY(), middleWidth, b.getHeight());
        valueBounds = new Rectangle2D.Double(b.getX() + leftWidth + middleWidth, b.getY(), rightWidth, b.getHeight());

        name.draw(g2, nameBounds);
        equalSeparator.draw(g2, equalBounds);
        value.draw(g2, valueBounds);
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
            IGraph g = getGraph();
            if (g != null)
            {
                PropertyChangeEvent event = new PropertyChangeEvent(this, "value", oldValue, value);
                g.firePropertyChangeOnNodeOrEdge(event);
            }
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
    public boolean checkAddNode(INode n, Point2D p)
    {
        if (n instanceof PointNode)
        {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.INode#getConnectionPoint(com.horstmann.violet.framework.Direction)
     */
    @Override
    public Point2D getConnectionPoint(Direction d)
    {
        Rectangle2D b = getBounds();
        return new Point2D.Double((b.getMaxX() + b.getX() + axisX) / 2, b.getCenterY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.INode#layout(com.horstmann.violet.framework.IGraph, java.awt.IGraphics2D,
     *      com.horstmann.violet.framework.IGrid)
     */
    @Override
    public void layout(Graphics2D g2, IGrid grid)
    {
        nameBounds = name.getBounds(g2);
        valueBounds = value.getBounds(g2);
        equalBounds = equalSeparator.getBounds(g2);

        double leftWidth = nameBounds.getWidth();
        double middleWidth = equalBounds.getWidth();
        double rightWidth = valueBounds.getWidth();
        double totalWidth = leftWidth + middleWidth + rightWidth;

        double leftHeight = nameBounds.getHeight();
        double middleHeight = equalBounds.getHeight();
        double rightHeight = valueBounds.getHeight();

        double globalHeight = Math.max(leftHeight, rightHeight);
        globalHeight = Math.max(middleHeight, globalHeight);

        if (getParent() == null) snapBounds(grid, totalWidth, globalHeight);
        else setBounds(new Rectangle2D.Double(getLocation().getX(), getLocation().getY(), totalWidth, globalHeight));

        axisX = leftWidth + middleWidth / 2;
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
        return axisX;
    }

    @Override
    public FieldNode clone()
    {
        FieldNode cloned = (FieldNode) super.clone();
        cloned.name = (MultiLineString) name.clone();
        cloned.value = (MultiLineString) value.clone();
        return cloned;
    }

    private transient double axisX;
    private MultiLineString name;
    private MultiLineString value;
    private MultiLineString equalSeparator;
    private transient Rectangle2D nameBounds;
    private transient Rectangle2D valueBounds;
    private transient Rectangle2D equalBounds;

    public static int DEFAULT_WIDTH = 60;
    public static int DEFAULT_HEIGHT = 20;
}
