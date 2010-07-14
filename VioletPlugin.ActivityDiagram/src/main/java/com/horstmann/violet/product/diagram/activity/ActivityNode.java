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
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.workspace.editorpart.IGrid;


/**
 * An activity node in an activity diagram.
 */
public class ActivityNode extends RectangularNode
{
    /**
     * Construct an action node with a default size
     */
    public ActivityNode()
    {
        name = new MultiLineString();
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        g2.draw(getShape());
        name.draw(g2, getBounds());
    }

    public Shape getShape()
    {
        return new RoundRectangle2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds().getHeight(),
                ARC_SIZE, ARC_SIZE);
    }

    public void layout(Graphics2D g2, IGrid grid)
    {
        Rectangle2D b = name.getBounds(g2);
        snapBounds(grid, Math.max(b.getWidth(), DEFAULT_WIDTH), Math.max(b
                .getHeight(), DEFAULT_HEIGHT));
    }

    /**
     * Sets the name property value.
     * 
     * @param newValue the new action name
     */
    public void setName(MultiLineString newValue)
    {
        name = newValue;
    }

    /**
     * Gets the name property value.
     * 
     * @param the action name
     */
    public MultiLineString getName()
    {
        return name;
    }

    @Override
    public ActivityNode clone()
    {
        ActivityNode cloned = (ActivityNode) super.clone();
        cloned.name = name.clone();
        return cloned;
    }

    private MultiLineString name;
    
    private static int ARC_SIZE = 20;
    private static int DEFAULT_WIDTH = 80;
    private static int DEFAULT_HEIGHT = 60;
}
