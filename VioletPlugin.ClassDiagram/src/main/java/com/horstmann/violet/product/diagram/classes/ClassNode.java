package com.horstmann.violet.product.diagram.classes;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.diagram.common.PointNode;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * A class node in a class diagram.
 */
public class ClassNode extends RectangularNode
{
    /**
     * Construct a class node with a default size
     */
    public ClassNode()
    {
        name = new MultiLineString();
        name.setSize(MultiLineString.LARGE);
        attributes = new MultiLineString();
        attributes.setJustification(MultiLineString.LEFT);
        methods = new MultiLineString();
        methods.setJustification(MultiLineString.LEFT);
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
        midHeight = DEFAULT_COMPARTMENT_HEIGHT;
        botHeight = DEFAULT_COMPARTMENT_HEIGHT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        super.draw(g2);
        Rectangle2D top = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), getBounds()
                .getHeight()
                - midHeight - botHeight);
        g2.draw(top);
        name.draw(g2, top);
        Rectangle2D mid = new Rectangle2D.Double(top.getX(), top.getMaxY(), top.getWidth(), midHeight);
        g2.draw(mid);
        attributes.draw(g2, mid);
        Rectangle2D bot = new Rectangle2D.Double(top.getX(), mid.getMaxY(), top.getWidth(), botHeight);
        g2.draw(bot);
        methods.draw(g2, bot);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#layout(com.horstmann.violet.framework.Graph, java.awt.Graphics2D,
     *      com.horstmann.violet.framework.Grid)
     */
    public void layout(Graphics2D g2, IGrid grid)
    {
        Rectangle2D min = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT);
        Rectangle2D top = name.getBounds(g2);
        top.add(min);
        Rectangle2D mid = attributes.getBounds(g2);
        Rectangle2D bot = methods.getBounds(g2);

        midHeight = mid.getHeight();
        botHeight = bot.getHeight();
        if (midHeight == 0 && botHeight == 0)
        {
            top.add(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
        }
        else
        {
            mid.add(min);
            bot.add(min);
            midHeight = mid.getHeight();
            botHeight = bot.getHeight();
        }

        snapBounds(grid, Math.max(top.getWidth(), Math.max(mid.getWidth(), bot.getWidth())), top.getHeight() + midHeight
                + botHeight);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addNode(com.horstmann.violet.framework.Node, java.awt.geom.Point2D)
     */
    public boolean checkAddNode(INode n, Point2D p)
    {
        // TODO : where is it added?
        if (n instanceof PointNode)
        {
            return true;
        }
        return false;
    }

    /**
     * Sets the name property value.
     * 
     * @param newValue the class name
     */
    public void setName(MultiLineString newValue)
    {
        name = newValue;
    }

    /**
     * Gets the name property value.
     * 
     * @return the class name
     */
    public MultiLineString getName()
    {
        return name;
    }

    /**
     * Sets the attributes property value.
     * 
     * @param newValue the attributes of this class
     */
    public void setAttributes(MultiLineString newValue)
    {
        attributes = newValue;
    }

    /**
     * Gets the attributes property value.
     * 
     * @return the attributes of this class
     */
    public MultiLineString getAttributes()
    {
        return attributes;
    }

    /**
     * Sets the methods property value.
     * 
     * @param newValue the methods of this class
     */
    public void setMethods(MultiLineString newValue)
    {
        methods = newValue;
    }

    /**
     * Gets the methods property value.
     * 
     * @return the methods of this class
     */
    public MultiLineString getMethods()
    {
        return methods;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.RectangularNode#clone()
     */
    public ClassNode clone()
    {
        ClassNode cloned = (ClassNode) super.clone();
        cloned.name = (MultiLineString) name.clone();
        cloned.methods = (MultiLineString) methods.clone();
        cloned.attributes = (MultiLineString) attributes.clone();
        return cloned;
    }

    private transient double midHeight;
    private transient double botHeight;
    private MultiLineString name;
    private MultiLineString attributes;
    private MultiLineString methods;

    private static int DEFAULT_COMPARTMENT_HEIGHT = 20;
    private static int DEFAULT_WIDTH = 100;
    private static int DEFAULT_HEIGHT = 60;

}
