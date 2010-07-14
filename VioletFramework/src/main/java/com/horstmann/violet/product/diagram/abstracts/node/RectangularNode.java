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

package com.horstmann.violet.product.diagram.abstracts.node;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * A node that has a rectangular shape.
 */
public abstract class RectangularNode extends AbstractNode
{
    public RectangularNode()
    {
        bounds = new Rectangle2D.Double();
    }

    public void translate(double dx, double dy)
    {
        super.translate(dx, dy);
        bounds.setFrame(bounds.getX() + dx, bounds.getY() + dy, bounds.getWidth(), bounds.getHeight());
    }

    public boolean contains(Point2D p)
    {
        return bounds.contains(p);
    }

    public Rectangle2D getBounds()
    {
        return (Rectangle2D) bounds.clone();
    }

    /**
     * Snaps the bounds of this rectangular node so that it has the node position as top left corner and the desired width and
     * height, snapped to the given grid.
     * 
     * @param g the grid to snap to
     * @param width the desired width
     * @param height the desired height
     */
    public void snapBounds(IGrid g, double width, double height)
    {
        bounds.setFrame(getLocation().getX(), getLocation().getY(), width, height);
        g.snap(bounds);
    }

    public void setBounds(Rectangle2D newBounds)
    {
        bounds.setFrame(newBounds);
    }

    public void layout(Graphics2D g2, IGrid grid)
    {
        snapBounds(grid, bounds.getWidth(), bounds.getHeight());
    }

    public Point2D getConnectionPoint(Direction d)
    {
        double slope = bounds.getHeight() / bounds.getWidth();
        double ex = d.getX();
        double ey = d.getY();
        double x = bounds.getCenterX();
        double y = bounds.getCenterY();

        if (ex != 0 && -slope <= ey / ex && ey / ex <= slope)
        {
            // intersects at left or right boundary
            if (ex > 0)
            {
                x = bounds.getMaxX();
                y += (bounds.getWidth() / 2) * ey / ex;
            }
            else
            {
                x = bounds.getX();
                y -= (bounds.getWidth() / 2) * ey / ex;
            }
        }
        else if (ey != 0)
        {
            // intersects at top or bottom
            if (ey > 0)
            {
                x += (bounds.getHeight() / 2) * ex / ey;
                y = bounds.getMaxY();
            }
            else
            {
                x -= (bounds.getHeight() / 2) * ex / ey;
                y = bounds.getY();
            }
        }
        return new Point2D.Double(x, y);
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
        writeRectangularShape(out, bounds);
    }

    /**
     * A helper method to overcome the problem that the 2D shapes aren't serializable. It writes x, y, width and height to the
     * stream.
     * 
     * @param out the stream
     * @param s the shape
     */
    private static void writeRectangularShape(ObjectOutputStream out, RectangularShape s) throws IOException
    {
        out.writeDouble(s.getX());
        out.writeDouble(s.getY());
        out.writeDouble(s.getWidth());
        out.writeDouble(s.getHeight());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        bounds = new Rectangle2D.Double();
        readRectangularShape(in, bounds);
    }

    /**
     * A helper method to overcome the problem that the 2D shapes aren't serializable. It reads x, y, width and height from the
     * stream.
     * 
     * @param in the stream
     * @param s the shape whose frame is set from the stream values
     */
    private static void readRectangularShape(ObjectInputStream in, RectangularShape s) throws IOException
    {
        double x = in.readDouble();
        double y = in.readDouble();
        double width = in.readDouble();
        double height = in.readDouble();
        s.setFrame(x, y, width, height);
    }

    public Shape getShape()
    {
        return bounds;
    }

    public RectangularNode clone()
    {
        RectangularNode cloned = (RectangularNode) super.clone();
        cloned.bounds = (Rectangle2D.Double) bounds.clone();
        return cloned;
    }

    private transient Rectangle2D.Double bounds;
}
