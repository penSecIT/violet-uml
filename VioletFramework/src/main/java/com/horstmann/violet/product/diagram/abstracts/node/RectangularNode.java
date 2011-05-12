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
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;

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
     * List edges connected to the same side
     * 
     * @param edge
     * @return ordered list of edges 
     */
    private List<IEdge> getEdgesOnSameSide(IEdge edge) {
        // Step 1 : look for edges
        List<IEdge> result = new ArrayList<IEdge>();
        Direction d = edge.getDirection(this);
        if (d == null) return result;
        Direction cardinalDirectionToSearch = d.getNearestCardinalDirection();
        for (IEdge anEdge : getConnectedEdges()) {
            Direction edgeDirection = anEdge.getDirection(this);
            Direction nearestCardinalDirection = edgeDirection.getNearestCardinalDirection();
            if (cardinalDirectionToSearch.equals(nearestCardinalDirection)) {
                result.add(anEdge);
            }
        }
        // Step 2: sort them
        if (Direction.NORTH.equals(cardinalDirectionToSearch) || Direction.SOUTH.equals(cardinalDirectionToSearch)) {
            Collections.sort(result, new Comparator<IEdge>() {
                @Override
                public int compare(IEdge e1, IEdge e2) {
                    Direction d1 = e1.getDirection(RectangularNode.this);
                    Direction d2 = e2.getDirection(RectangularNode.this);
                    double x1 = d1.getX();
                    double x2 = d2.getX();
                    return Double.compare(x1, x2);
                }
            });
        }
        if (Direction.EAST.equals(cardinalDirectionToSearch) || Direction.WEST.equals(cardinalDirectionToSearch)) {
            Collections.sort(result, new Comparator<IEdge>() {
                @Override
                public int compare(IEdge e1, IEdge e2) {
                    Direction d1 = e1.getDirection(RectangularNode.this);
                    Direction d2 = e2.getDirection(RectangularNode.this);
                    double y1 = d1.getY();
                    double y2 = d2.getY();
                    return Double.compare(y1, y2);
                }
            });
        }
        return result;
    }
    
    public Point2D getConnectionPoint(IEdge e)
    {
        List<IEdge> edgesOnSameSide = getEdgesOnSameSide(e);
        int position = edgesOnSameSide.indexOf(e);
        int size = edgesOnSameSide.size();
        
        Rectangle2D b = getBounds();
        
        double x = b.getCenterX();
        double y = b.getCenterY();

        Direction d = e.getDirection(this);
        
        Direction nearestCardinalDirection = d.getNearestCardinalDirection();
        if (Direction.NORTH.equals(nearestCardinalDirection)) {
            x = b.getMaxX() - (b.getWidth() / (size + 1)) * (position + 1);
            y = b.getMaxY();
        }
        if (Direction.SOUTH.equals(nearestCardinalDirection)) {
            x = b.getMaxX() - (b.getWidth() / (size + 1)) * (position + 1);
            y = b.getMinY();
        }
        if (Direction.EAST.equals(nearestCardinalDirection)) {
            x = b.getMinX();
            y = b.getMaxY() - (b.getHeight() / (size + 1)) * (position + 1);
        }
        if (Direction.WEST.equals(nearestCardinalDirection)) {
            x = b.getMaxX();
            y = b.getMaxY() - (b.getHeight() / (size + 1)) * (position + 1);
        }
        return new Point2D.Double(x, y);
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
