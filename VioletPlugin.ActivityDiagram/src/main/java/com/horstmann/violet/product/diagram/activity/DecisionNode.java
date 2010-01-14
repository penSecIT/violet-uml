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
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.diagram.node.RectangularNode;
import com.horstmann.violet.framework.diagram.property.MultiLineString;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;
import com.horstmann.violet.product.diagram.common.PointNode;


/**
 * A decision node in an activity diagram.
 */
public class DecisionNode extends RectangularNode
{
    /**
     * Construct a decision node with a default size
     */
    public DecisionNode()
    {
        condition = new MultiLineString();
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
     * @see com.horstmann.violet.framClassework.Node#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        Rectangle2D shapeRect = getBounds();
        Rectangle2D textRect = condition.getBounds(g2);
        textRect.setRect(shapeRect.getCenterX() - textRect.getWidth() / 2, shapeRect.getCenterY() - textRect.getHeight() / 2,
                textRect.getWidth(), textRect.getHeight());

        g2.draw(getShape());
        condition.draw(g2, textRect);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.RectangularNode#getShape()
     */
    public Shape getShape()
    {
        Rectangle2D shapeRect = getBounds();
        GeneralPath diamond = new GeneralPath();
        float x1 = (float) shapeRect.getX();
        float y1 = (float) shapeRect.getCenterY();
        float x2 = (float) shapeRect.getCenterX();
        float y2 = (float) shapeRect.getY();
        float x3 = (float) (shapeRect.getX() + shapeRect.getWidth());
        float y3 = (float) shapeRect.getCenterY();
        float x4 = (float) shapeRect.getCenterX();
        float y4 = (float) (shapeRect.getY() + shapeRect.getHeight());
        diamond.moveTo(x1, y1);
        diamond.lineTo(x2, y2);
        diamond.lineTo(x3, y3);
        diamond.lineTo(x4, y4);
        diamond.lineTo(x1, y1);
        return diamond;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#layout(com.horstmann.violet.framework.Graph, java.awt.Graphics2D,
     *      com.horstmann.violet.framework.Grid)
     */
    public void layout(Graphics2D g2, IGrid grid)
    {
        Rectangle2D b = condition.getBounds(g2);
        Rectangle2D textRect = new Rectangle2D.Double(0, 0, Math.max(DEFAULT_WIDTH, b.getWidth()), Math.max(DEFAULT_HEIGHT, b
                .getHeight()));
        double w1 = textRect.getWidth() / 2;
        double h1 = textRect.getHeight() / 2;
        double w2 = Math.tan(Math.toRadians(60)) * h1;
        double h2 = Math.tan(Math.toRadians(30)) * w1;
        double shapeWidth = (w1 + w2) * 2;
        double shapeHeight = (h1 + h2) * 2;
        snapBounds(grid, shapeWidth, shapeHeight);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.AbstractNode#getAncestors()
     */
    public List<INode> getAncestors()
    {
        // TODO Auto-generated method stub
        return super.getAncestors();
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
     * Sets the condition property value.
     * 
     * @param newValue the branch condition
     */
    public void setCondition(MultiLineString newValue)
    {
        condition = newValue;
    }

    /**
     * Gets the condition property value.
     * 
     * @return the branch condition
     */
    public MultiLineString getCondition()
    {
        return condition;
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public DecisionNode clone()
    {
        DecisionNode cloned = (DecisionNode)super.clone();
        cloned.condition = (MultiLineString)condition.clone();
        return cloned;
    }
    
    private MultiLineString condition;

    private static int DEFAULT_WIDTH = 30;
    private static int DEFAULT_HEIGHT = 20;
}