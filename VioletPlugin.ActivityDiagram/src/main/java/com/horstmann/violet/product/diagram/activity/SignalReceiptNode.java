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

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.RectangularNode;
import com.horstmann.violet.framework.diagram.property.MultiLineString;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;


/**
 * An receive event node in an activity diagram.
 */
public class SignalReceiptNode extends RectangularNode
{
    /**
     * Construct an receive event node with a default size
     */
    public SignalReceiptNode()
    {
        signal = new MultiLineString();
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
        if (e.getEnd() != null && this != e.getEnd())
        {
            return true;
        }
        return false;
    }

    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        g2.draw(getShape());
        signal.draw(g2, getTextBounds());
    }

    public Shape getShape()
    {
        Rectangle2D b = getBounds();
        float x1 = (float) b.getX();
        float y1 = (float) b.getY();
        float x2 = x1 + (float) b.getWidth();
        float y2 = y1;
        float x3 = x1 + (float) b.getWidth() - EDGE_WIDTH;
        float y3 = y1 + (float) b.getHeight() / 2;
        float x4 = x2;
        float y4 = y1 + (float) b.getHeight();
        float x5 = x1;
        float y5 = y4;
        GeneralPath path = new GeneralPath();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.lineTo(x5, y5);
        path.lineTo(x1, y1);
        return path;
    }

    private Rectangle2D getTextBounds()
    {
        Rectangle2D b = getBounds();
        return new Rectangle2D.Double(b.getX(), b.getY(), b.getWidth() - EDGE_WIDTH, b.getHeight());
    }

    public void layout(Graphics2D g2, IGrid grid)
    {
        Rectangle2D textBounds = signal.getBounds(g2);
        snapBounds(grid, Math.max(textBounds.getWidth(), DEFAULT_WIDTH), Math.max(textBounds
                .getHeight(), DEFAULT_HEIGHT));        
    }

    /**
     * Sets the signal property value.
     * 
     * @param newValue the new signal description
     */
    public void setSignal(MultiLineString newValue)
    {
        signal = newValue;
    }

    /**
     * Gets the signal property value.
     * 
     * @param the signal description
     */
    public MultiLineString getSignal()
    {
        return signal;
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public SignalReceiptNode clone()
    {
        SignalReceiptNode cloned = (SignalReceiptNode) super.clone();
        cloned.signal = (MultiLineString) signal.clone();
        return cloned;        
    }
    
    private MultiLineString signal;

    private static int DEFAULT_WIDTH = 80;
    private static int DEFAULT_HEIGHT = 40;
    private static int EDGE_WIDTH = 20;
}
