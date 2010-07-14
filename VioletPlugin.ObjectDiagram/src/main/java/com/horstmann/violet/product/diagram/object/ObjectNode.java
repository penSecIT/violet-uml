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
import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.diagram.common.PointNode;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * An object node in an object diagram.
 */
public class ObjectNode extends RectangularNode
{
    /**
     * Construct an object node with a default size
     */
    public ObjectNode()
    {
        name = new MultiLineString();
        name.setUnderlined(true);
        name.setSize(MultiLineString.LARGE);
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        Rectangle2D top = getTopRectangle();
        g2.draw(top);
        g2.draw(getBounds());
        name.draw(g2, top);
        // for (Node n : getChildren()) n.draw(g2); // make sure they get drawn on top
    }

    @Override
    public void translate(double dx, double dy)
    {
        super.translate(dx, dy);
        for (INode childNode : getChildren())
            childNode.translate(dx, dy);
    }    
    
    /**
     * Returns the rectangle at the top of the object node.
     * 
     * @return the top rectangle
     */
    public Rectangle2D getTopRectangle()
    {
        return new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), topHeight);
    }

    public boolean checkAddEdge(IEdge e, Point2D p1, Point2D p2)
    {
        return e instanceof ObjectRelationshipEdge && e.getEnd() != null;
    }

    public Point2D getConnectionPoint(Direction d)
    {
        if (d.getX() > 0) return new Point2D.Double(getBounds().getMaxX(), getBounds().getMinY() + topHeight / 2);
        else return new Point2D.Double(getBounds().getX(), getBounds().getMinY() + topHeight / 2);
    }

    public void layout(Graphics2D g2, IGrid grid)
    {
        Rectangle2D b = name.getBounds(g2);
        b.add(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - YGAP));
        double leftWidth = 0;
        double rightWidth = 0;
        List<INode> fields = getChildren();
        double height = fields.size() == 0 ? 0 : YGAP;
        for (INode n : fields)
        {
            FieldNode f = (FieldNode) n;
            f.layout(g2, grid);
            Rectangle2D b2 = f.getBounds();
            height += b2.getHeight() + YGAP;
            double axis = f.getAxisX();
            leftWidth = Math.max(leftWidth, axis);
            rightWidth = Math.max(rightWidth, b2.getWidth() - axis);
        }
        double width = 2 * Math.max(leftWidth, rightWidth) + 2 * XGAP;
        width = Math.max(width, b.getWidth());
        width = Math.max(width, DEFAULT_WIDTH);
        snapBounds(grid, width, b.getHeight() + height);
        topHeight = getBounds().getHeight() - height;
        double ytop = getBounds().getY() + topHeight + YGAP;
        double xmid = getBounds().getCenterX();
        for (INode n : fields)
        {
            FieldNode f = (FieldNode) n;
            f.translate(xmid - f.getAxisX() - f.getLocation().getX(), ytop - f.getLocation().getY());
            ytop += f.getBounds().getHeight() + YGAP;
        }
    }

    /**
     * Sets the name property value.
     * 
     * @param newValue the new object name
     */
    public void setName(MultiLineString n)
    {
        name = n;
    }

    /**
     * Gets the name property value.
     * 
     * @param the object name
     */
    public MultiLineString getName()
    {
        return name;
    }

    public boolean checkAddNode(INode n, Point2D p)
    {
        List<INode> fields = getChildren();
        if (n instanceof PointNode) return true;
        if (!(n instanceof FieldNode)) return false;
        if (fields.contains(n)) return true;
        int i = 0;
        while (i < fields.size() && fields.get(i).getLocation().getY() < p.getY())
            i++;
        addChild(i, n);
        return true;
    }

    public void checkRemoveNode(INode n)
    {
        if (n == this)
        {
            List<INode> fields = new ArrayList<INode>(getChildren());
            for (int i = fields.size() - 1; i >= 0; i--)
            {
                INode field = fields.get(i);
                removeChild(field);
            }
            getGraph().removeNodesAndEdges(fields, null);
        }
    }    
    
    public ObjectNode clone()
    {
        ObjectNode cloned = (ObjectNode) super.clone();
        cloned.name = name.clone();
        return cloned;
    }

    private transient double topHeight;
    private MultiLineString name;

    private static int DEFAULT_WIDTH = 80;
    private static int DEFAULT_HEIGHT = 60;
    private static int XGAP = 5;
    private static int YGAP = 5;
}
