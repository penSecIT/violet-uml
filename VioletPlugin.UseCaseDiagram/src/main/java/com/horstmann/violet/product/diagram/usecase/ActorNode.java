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
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.framework.diagram.node.RectangularNode;
import com.horstmann.violet.framework.diagram.property.MultiLineString;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;


/**
 * An actor node in a use case diagram.
 */
public class ActorNode extends RectangularNode
{

    /**
     * Construct an actor node with a default size and name
     */
    public ActorNode()
    {
        name = new MultiLineString();
        name.setText("Actor");
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    /**
     * Prepare the layout by setting the position and bounds
     */
    public void layout(Graphics2D g2, IGrid grid)
    {
        Rectangle2D top = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        Rectangle2D bot = name.getBounds(g2);
        snapBounds(grid, Math.max(top.getWidth(), bot.getWidth()),
                top.getHeight() + bot.getHeight());
    }

    /**
     * Draws the stick man
     */
    public void draw(Graphics2D g2)
    {
        Rectangle2D bounds = getBounds();

        // Draw stick person
        GeneralPath path = new GeneralPath();
        float neckX = (float) (bounds.getX() + bounds.getWidth() / 2);
        float neckY = (float) (bounds.getY() + HEAD_SIZE + GAP_ABOVE);
        // head
        path.moveTo(neckX, neckY);
        path.quadTo(neckX + HEAD_SIZE / 2, neckY, neckX + HEAD_SIZE / 2, neckY - HEAD_SIZE / 2);
        path.quadTo(neckX + HEAD_SIZE / 2, neckY - HEAD_SIZE, neckX, neckY - HEAD_SIZE);
        path.quadTo(neckX - HEAD_SIZE / 2, neckY - HEAD_SIZE, neckX - HEAD_SIZE / 2, neckY - HEAD_SIZE / 2);
        path.quadTo(neckX - HEAD_SIZE / 2, neckY, neckX, neckY);
        // body
        float hipX = neckX;
        float hipY = neckY + BODY_SIZE;
        path.lineTo(hipX, hipY);
        // arms
        path.moveTo(neckX - ARMS_SIZE / 2, neckY + BODY_SIZE / 3);
        path.lineTo(neckX + ARMS_SIZE / 2, neckY + BODY_SIZE / 3);
        // legs
        float dx = (float) (LEG_SIZE / Math.sqrt(2));
        float feetX1 = hipX - dx;
        float feetX2 = hipX + dx + 1;
        float feetY = hipY + dx + 1;
        path.moveTo(feetX1, feetY);
        path.lineTo(hipX, hipY);
        path.lineTo(feetX2, feetY);

        g2.draw(path);

        // Draw name
        Rectangle2D bot = name.getBounds(g2);
        Rectangle2D namebox = new Rectangle2D.Double(bounds.getX() + (bounds.getWidth() - bot.getWidth()) / 2, bounds.getY()
                + DEFAULT_HEIGHT, bot.getWidth(), bot.getHeight());
        name.draw(g2, namebox);
    }

    /**
     * Sets the name property value.
     * 
     * @param newValue the new actor name
     */
    public void setName(MultiLineString newValue)
    {
        name = newValue;
    }

    /**
     * Gets the name property value.
     * 
     * @param the actor name
     */
    public MultiLineString getName()
    {
        return name;
    }

    public ActorNode clone()
    {
       ActorNode cloned = (ActorNode) super.clone();
       cloned.name = (MultiLineString) name.clone();
       return cloned;
    }    

    /** Actor name */
    private MultiLineString name;
    /** Bounding rectangle width */
    private static int DEFAULT_WIDTH = 48;
    /** Bounding rectangle height */
    private static int DEFAULT_HEIGHT = 64;

    /** Stick man : neck size */
    private static int GAP_ABOVE = 4;
    /** Stick man : head size */
    private static int HEAD_SIZE = DEFAULT_WIDTH * 4 / 12;
    /** Stick man : body size */
    private static int BODY_SIZE = DEFAULT_WIDTH * 5 / 12;
    /** Stick man : leg size - Note : Height = HEAD_SIZE + BODY_SIZE + LEG_SIZE/sqrt(2) */
    private static int LEG_SIZE = DEFAULT_WIDTH * 5 / 12;
    /** Stick man : arm size */
    private static int ARMS_SIZE = DEFAULT_WIDTH * 6 / 12;
}
