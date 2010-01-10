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

package com.horstmann.violet.product.diagram.usecase;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.framework.diagram.node.EllipticalNode;
import com.horstmann.violet.framework.diagram.property.MultiLineString;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;


/**
 * A use case node in a use case diagram.
 */
public class UseCaseNode extends EllipticalNode
{
    /**
     * Construct a use case node with a default size
     */
    public UseCaseNode()
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

    /**
     * Sets the name property value.
     * 
     * @param newValue the new use case name
     */
    public void setName(MultiLineString newValue)
    {
        name = newValue;
    }

    /**
     * Gets the name property value.
     * 
     * @param the use case name
     */
    public MultiLineString getName()
    {
        return name;
    }

    public void layout(Graphics2D g2, IGrid grid)
    {
        double aspectRatio = DEFAULT_WIDTH / DEFAULT_HEIGHT;
        Rectangle2D b = name.getBounds(g2);
        double bw = b.getWidth();
        double bh = b.getHeight();
        double minWidth = Math.sqrt(bw * bw + aspectRatio * aspectRatio * bh * bh);
        double minHeight = minWidth / aspectRatio;

        snapBounds(grid, Math.max(minWidth, DEFAULT_WIDTH), Math.max(minHeight,
                DEFAULT_HEIGHT));
    }

    public UseCaseNode clone()
    {
        UseCaseNode cloned = (UseCaseNode) super.clone();
        cloned.name = name.clone();
        return cloned;
    }

    private MultiLineString name;

    private static int DEFAULT_WIDTH = 110;
    private static int DEFAULT_HEIGHT = 40;
}
