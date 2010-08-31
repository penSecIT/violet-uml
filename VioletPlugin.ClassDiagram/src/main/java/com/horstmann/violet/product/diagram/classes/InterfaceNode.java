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
 * An interface node in a class diagram.
 */
public class InterfaceNode extends RectangularNode
{
    /**
     * Construct an interface node with a default size and the text <<interface>>.
     */
    public InterfaceNode()
    {
        name = new MultiLineString();
        name.setSize(MultiLineString.LARGE);
        name.setText("\u00ABinterface\u00BB");
        methods = new MultiLineString();
        methods.setJustification(MultiLineString.LEFT);
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
        midHeight = DEFAULT_COMPARTMENT_HEIGHT;
        botHeight = DEFAULT_COMPARTMENT_HEIGHT;
    }

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
        Rectangle2D bot = new Rectangle2D.Double(top.getX(), mid.getMaxY(), top.getWidth(), botHeight);
        g2.draw(bot);
        methods.draw(g2, bot);
    }

    public void layout(Graphics2D g2, IGrid grid)
    {
        Rectangle2D min = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT);
        Rectangle2D top = name.getBounds(g2);
        top.add(min);
        Rectangle2D bot = methods.getBounds(g2);

        botHeight = bot.getHeight();
        if (botHeight == 0)
        {
            top.add(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
            midHeight = 0;
        }
        else
        {
            bot.add(min);
            midHeight = DEFAULT_COMPARTMENT_HEIGHT;
            botHeight = bot.getHeight();
        }

        snapBounds(grid, Math.max(top.getWidth(), bot.getWidth()),
                top.getHeight() + midHeight + botHeight);
    }

    public boolean addChildNode(INode n, Point2D p)
    {
        if (n  instanceof PointNode)
        {
            return true;
        }
        return false;
    }

    /**
     * Sets the name property value.
     * 
     * @param newValue the interface name
     */
    public void setName(MultiLineString newValue)
    {
        name = newValue;
    }

    /**
     * Gets the name property value.
     * 
     * @return the interface name
     */
    public MultiLineString getName()
    {
        return name;
    }

    /**
     * Sets the methods property value.
     * 
     * @param newValue the methods of this interface
     */
    public void setMethods(MultiLineString newValue)
    {
        methods = newValue;
    }

    /**
     * Gets the methods property value.
     * 
     * @return the methods of this interface
     */
    public MultiLineString getMethods()
    {
        return methods;
    }

    public InterfaceNode clone()
    {
       InterfaceNode cloned = (InterfaceNode)super.clone();
       cloned.name = name.clone();
       cloned.methods = methods.clone();
       return cloned;
    }
    
    private transient double midHeight;
    private transient double botHeight;
    private MultiLineString name;
    private MultiLineString methods;

    private static int DEFAULT_COMPARTMENT_HEIGHT = 20;
    private static int DEFAULT_WIDTH = 100;
    private static int DEFAULT_HEIGHT = 60;
}
