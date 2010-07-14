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

package com.horstmann.violet.product.diagram.abstracts.property;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.horstmann.violet.framework.util.SerializableEnumeration;

/**
 * A style for a segmented line that indicates the number and sequence of bends.
 */
public class BentStyle extends SerializableEnumeration
{

    /**
     * Default constructor
     */
    private BentStyle()
    {
    }

    /**
     * Gets the four connecting points at which a bent line connects to a rectangle.
     */
    private Point2D[] connectionPoints(Rectangle2D r)
    {
        Point2D[] a = new Point2D[4];
        a[0] = new Point2D.Double(r.getX(), r.getCenterY());
        a[1] = new Point2D.Double(r.getMaxX(), r.getCenterY());
        a[2] = new Point2D.Double(r.getCenterX(), r.getY());
        a[3] = new Point2D.Double(r.getCenterX(), r.getMaxY());
        return a;
    }

    /**
     * Gets the points at which a line joining two rectangles is bent according to a bent style.
     * 
     * @param startingRectangle the starting rectangle
     * @param endingRectangle the ending rectangle
     * @return an array list of points at which to bend the segmented line joining the two rectangles
     */
    public ArrayList<Point2D> getPath(Rectangle2D startingRectangle, Rectangle2D endingRectangle)
    {
        ArrayList<Point2D> r = null;

        // Try to get current path
        if (this == STRAIGHT) r = getStraightPath(startingRectangle, endingRectangle);
        else if (this == HV) r = getHVPath(startingRectangle, endingRectangle);
        else if (this == VH) r = getVHPath(startingRectangle, endingRectangle);
        else if (this == HVH) r = getHVHPath(startingRectangle, endingRectangle);
        else if (this == VHV) r = getVHVPath(startingRectangle, endingRectangle);
        if (r != null) return r;

        // Try to inverse path
        if (startingRectangle.equals(endingRectangle)) r = getSelfPath(startingRectangle);
        else if (this == HVH) r = getVHVPath(startingRectangle, endingRectangle);
        else if (this == VHV) r = getHVHPath(startingRectangle, endingRectangle);
        else if (this == HV) r = getVHPath(startingRectangle, endingRectangle);
        else if (this == VH) r = getHVPath(startingRectangle, endingRectangle);
        if (r != null) return r;

        // Return default path
        return getStraightPath(startingRectangle, endingRectangle);
    }

    /**
     * Gets an Vertical-Horizontal-Vertival path
     * 
     * @param startingRectangle
     * @param endingRectangle
     * @return an array list of points
     */
    private ArrayList<Point2D> getVHVPath(Rectangle2D startingRectangle, Rectangle2D endingRectangle)
    {

        ArrayList<Point2D> r = new ArrayList<Point2D>();
        double x1 = startingRectangle.getCenterX();
        double x2 = endingRectangle.getCenterX();
        double y1;
        double y2;
        if (startingRectangle.getMaxY() + 2 * MIN_SEGMENT <= endingRectangle.getY())
        {
            y1 = startingRectangle.getMaxY();
            y2 = endingRectangle.getY();
        }
        else if (endingRectangle.getMaxY() + 2 * MIN_SEGMENT <= startingRectangle.getY())
        {
            y1 = startingRectangle.getY();
            y2 = endingRectangle.getMaxY();

        }
        else return null;
        if (Math.abs(x1 - x2) <= MIN_SEGMENT)
        {
            r.add(new Point2D.Double(x2, y1));
            r.add(new Point2D.Double(x2, y2));
        }
        else
        {
            r.add(new Point2D.Double(x1, y1));
            r.add(new Point2D.Double(x1, (y1 + y2) / 2));
            r.add(new Point2D.Double(x2, (y1 + y2) / 2));
            r.add(new Point2D.Double(x2, y2));
        }
        return r;
    }

    /**
     * Gets an Horizontal-Vertical-Horizontal path
     * 
     * @param startingRectangle
     * @param endingRectangle
     * @return an array list of points
     */
    private ArrayList<Point2D> getHVHPath(Rectangle2D startingRectangle, Rectangle2D endingRectangle)
    {
        ArrayList<Point2D> r = new ArrayList<Point2D>();
        double x1;
        double x2;
        double y1 = startingRectangle.getCenterY();
        double y2 = endingRectangle.getCenterY();
        if (startingRectangle.getMaxX() + 2 * MIN_SEGMENT <= endingRectangle.getX())
        {
            x1 = startingRectangle.getMaxX();
            x2 = endingRectangle.getX();
        }
        else if (endingRectangle.getMaxX() + 2 * MIN_SEGMENT <= startingRectangle.getX())
        {
            x1 = startingRectangle.getX();
            x2 = endingRectangle.getMaxX();
        }
        else return null;
        if (Math.abs(y1 - y2) <= MIN_SEGMENT)
        {
            r.add(new Point2D.Double(x1, y2));
            r.add(new Point2D.Double(x2, y2));
        }
        else
        {
            r.add(new Point2D.Double(x1, y1));
            r.add(new Point2D.Double((x1 + x2) / 2, y1));
            r.add(new Point2D.Double((x1 + x2) / 2, y2));
            r.add(new Point2D.Double(x2, y2));
        }
        return r;
    }

    /**
     * Gets a Vertical-Horizontal path
     * 
     * @param startingRectangle
     * @param endingRectangle
     * @return an array list of points
     */
    private ArrayList<Point2D> getVHPath(Rectangle2D startingRectangle, Rectangle2D endingRectangle)
    {
        ArrayList<Point2D> r = new ArrayList<Point2D>();
        double x1 = startingRectangle.getCenterX();
        double x2;
        double y1;
        double y2 = endingRectangle.getCenterY();
        if (x1 + MIN_SEGMENT <= endingRectangle.getX()) x2 = endingRectangle.getX();
        else if (x1 - MIN_SEGMENT >= endingRectangle.getMaxX()) x2 = endingRectangle.getMaxX();
        else return null;
        if (y2 + MIN_SEGMENT <= startingRectangle.getY()) y1 = startingRectangle.getY();
        else if (y2 - MIN_SEGMENT >= startingRectangle.getMaxY()) y1 = startingRectangle.getMaxY();
        else return null;
        r.add(new Point2D.Double(x1, y1));
        r.add(new Point2D.Double(x1, y2));
        r.add(new Point2D.Double(x2, y2));
        return r;
    }

    /**
     * Gets an Horizontal-Vertical path
     * 
     * @param startingRectangle
     * @param endingRectangle
     * @return an array list of points
     */
    private ArrayList<Point2D> getHVPath(Rectangle2D startingRectangle, Rectangle2D endingRectangle)
    {
        ArrayList<Point2D> r = new ArrayList<Point2D>();
        double x1;
        double x2 = endingRectangle.getCenterX();
        double y1 = startingRectangle.getCenterY();
        double y2;
        if (x2 + MIN_SEGMENT <= startingRectangle.getX()) x1 = startingRectangle.getX();
        else if (x2 - MIN_SEGMENT >= startingRectangle.getMaxX()) x1 = startingRectangle.getMaxX();
        else return null;
        if (y1 + MIN_SEGMENT <= endingRectangle.getY()) y2 = endingRectangle.getY();
        else if (y1 - MIN_SEGMENT >= endingRectangle.getMaxY()) y2 = endingRectangle.getMaxY();
        else return null;
        r.add(new Point2D.Double(x1, y1));
        r.add(new Point2D.Double(x2, y1));
        r.add(new Point2D.Double(x2, y2));
        return r;
    }

    /**
     * Gets a straight path
     * 
     * @param startingRectangle
     * @param endingRectangle
     * @return an array list of points
     */
    private ArrayList<Point2D> getStraightPath(Rectangle2D startingRectangle, Rectangle2D endingRectangle)
    {
        ArrayList<Point2D> r = new ArrayList<Point2D>();
        Point2D[] a = connectionPoints(startingRectangle);
        Point2D[] b = connectionPoints(endingRectangle);
        Point2D p = a[0];
        Point2D q = b[0];
        double distance = p.distance(q);
        if (distance == 0) return null;
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
            {
                double d = a[i].distance(b[j]);
                if (d < distance)
                {
                    p = a[i];
                    q = b[j];
                    distance = d;
                }
            }
        r.add(p);
        r.add(q);
        return r;
    }

    /**
     * Gets the points at which a line joining two rectangles is bent according to a bent style.
     * 
     * @param s the starting and ending rectangle
     */
    private ArrayList<Point2D> getSelfPath(Rectangle2D s)
    {
        ArrayList<Point2D> r = new ArrayList<Point2D>();
        double x1 = s.getX() + s.getWidth() * 3 / 4;
        double y1 = s.getY();
        double y2 = s.getY() - SELF_HEIGHT;
        double x2 = s.getX() + s.getWidth() + SELF_WIDTH;
        double y3 = s.getY() + s.getHeight() / 4;
        double x3 = s.getX() + s.getWidth();
        r.add(new Point2D.Double(x1, y1));
        r.add(new Point2D.Double(x1, y2));
        r.add(new Point2D.Double(x2, y2));
        r.add(new Point2D.Double(x2, y3));
        r.add(new Point2D.Double(x3, y3));
        return r;
    }

    /** minimum segment size */
    private static final int MIN_SEGMENT = 10;
    /** width on self path */
    private static final int SELF_WIDTH = 30;
    /** height on self path */
    private static final int SELF_HEIGHT = 25;

    /** straight bent style */
    public static final BentStyle STRAIGHT = new BentStyle();
    /** Horizontal-Vertical bent style */
    public static final BentStyle HV = new BentStyle();
    /** Vertical-Horizontal bent style */
    public static final BentStyle VH = new BentStyle();
    /** Horizontal-Vertical-Horizontal bent style */
    public static final BentStyle HVH = new BentStyle();
    /** Vertical-Horizontal-Vertical bent style */
    public static final BentStyle VHV = new BentStyle();
}
