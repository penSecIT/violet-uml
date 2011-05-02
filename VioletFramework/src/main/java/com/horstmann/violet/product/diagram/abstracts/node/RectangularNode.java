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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.UIManager;

import com.horstmann.violet.product.diagram.abstracts.Direction;

/**
 * A node that has a rectangular shape.
 */
public abstract class RectangularNode extends AbstractNode
{

    public RectangularNode()
    {
        super();
    }

    public boolean contains(Point2D p)
    {
        return getBounds().contains(p);
    }

    /**
     * Look for other edges with same kind of cardinal direction
     * 
     * @param d
     * @return
     */
    private List<Direction> getDirectionsOnSameCardinality(Direction d) {
        List<Direction> result = new ArrayList<Direction>();
        Direction cardinalDirectionToSearch = d.getNearestCardinalDirection();
        for (Direction aDirection :getEdgeDirections()) {
            Direction cardinalDirection = aDirection.getNearestCardinalDirection();
            if (cardinalDirection.equals(cardinalDirectionToSearch)) {
                result.add(aDirection);
            }
        }
        return result;
    }
    
    /**
     * Compare all directions to d and return the position of d
     * 
     * @param d
     * @param directionWithSameCardinality list of constant (Direction.NORTH, SOUTH, EAST or WEST only!!!) 
     * @return
     */
    private int getCardinalPosition(Direction d, List<Direction> directionWithSameCardinality) {
        Direction cardinalDirectionToSearch = d.getNearestCardinalDirection();
        if (Direction.NORTH.equals(cardinalDirectionToSearch) || Direction.SOUTH.equals(cardinalDirectionToSearch)) {
            Collections.sort(directionWithSameCardinality, new Comparator<Direction>() {
                @Override
                public int compare(Direction o1, Direction o2) {
                    double x1 = o1.getX();
                    double x2 = o2.getX();
                    return Double.compare(x1, x2);
                }
            });
        }
        if (Direction.EAST.equals(cardinalDirectionToSearch) || Direction.WEST.equals(cardinalDirectionToSearch)) {
            Collections.sort(directionWithSameCardinality, new Comparator<Direction>() {
                @Override
                public int compare(Direction o1, Direction o2) {
                    double y1 = o1.getY();
                    double y2 = o2.getY();
                    return Double.compare(y1, y2);
                }
            });
        }
        return directionWithSameCardinality.indexOf(d);
    }
    
    
    public Point2D getConnectionPoint(Direction d)
    {
        Rectangle2D b = getBounds();
        double slope = b.getHeight() / b.getWidth();
        double ex = d.getX();
        double ey = d.getY();
        double x = b.getCenterX();
        double y = b.getCenterY();

        
        
        Direction nearestCardinalDirection = d.getNearestCardinalDirection();
        if (Direction.NORTH.equals(nearestCardinalDirection)) {
            x = b.getX();
            y -= (b.getWidth() / 2) * ey / ex;
            System.out.println("north");
        }
        if (Direction.SOUTH.equals(nearestCardinalDirection)) {
            x = b.getMaxX();
            y += (b.getWidth() / 2) * ey / ex;
            System.out.println("south");
        }
        if (Direction.EAST.equals(nearestCardinalDirection)) {
            x += (b.getHeight() / 2) * ex / ey;
            y = b.getMaxY();
            System.out.println("east");
        }
        if (Direction.WEST.equals(nearestCardinalDirection)) {
            x -= (b.getHeight() / 2) * ex / ey;
            y = b.getY();
            System.out.println("west");
        }
        return new Point2D.Double(x, y);
        
        
        
//        if (ex != 0 && -slope <= ey / ex && ey / ex <= slope)
//        {
//            // intersects at left or right boundary
//            if (ex > 0)
//            {
//                x = b.getMaxX();
//                y += (b.getWidth() / 2) * ey / ex;
//            }
//            else
//            {
//                x = b.getX();
//                y -= (b.getWidth() / 2) * ey / ex;
//            }
//        }
//        else if (ey != 0)
//        {
//            // intersects at top or bottom
//            if (ey > 0)
//            {
//                x += (b.getHeight() / 2) * ex / ey;
//                y = b.getMaxY();
//            }
//            else
//            {
//                x -= (b.getHeight() / 2) * ex / ey;
//                y = b.getY();
//            }
//        }
//        return new Point2D.Double(x, y);
    }
    
    


    public Shape getShape()
    {
        return getBounds();
    }

    @Override
    public void draw(Graphics2D g2)
    {
        Shape shape = getShape();
        Color oldColor = g2.getColor();
        g2.translate(SHADOW_GAP, SHADOW_GAP);
        g2.setColor(SHADOW_COLOR);
        g2.fill(shape);
        g2.translate(-SHADOW_GAP, -SHADOW_GAP);
        g2.setColor(BACKGROUND_COLOR);
        g2.fill(shape);
        g2.setColor(oldColor);
    }

    
    
    




    private static final Color SHADOW_COLOR = new Color(210,210,210);
    protected static Color BACKGROUND_COLOR = UIManager.getColor("TextPane.background");
    public static final double SHADOW_GAP = 4;

}
