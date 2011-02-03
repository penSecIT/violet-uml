package com.horstmann.violet.product.diagram.classes;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.diagram.common.NoteNode;

/**
 * A package node in a UML diagram.
 */
public class PackageNode extends RectangularNode
{
    /**
     * Construct a package node with a default size
     */
    public PackageNode()
    {
        name = new MultiLineString();
        name.setSize(MultiLineString.LARGE);
        contents = new MultiLineString();
    }

    private Rectangle2D getTopRectangleBounds()
    {
        Rectangle2D globalBounds = new Rectangle2D.Double(0, 0, 0, 0);
        Rectangle2D nameBounds = name.getBounds();
        globalBounds.add(nameBounds);
        globalBounds.add(new Rectangle2D.Double(0, 0, DEFAULT_TOP_WIDTH, DEFAULT_TOP_HEIGHT));
        Point2D currentLocation = getLocation();
        double x = currentLocation.getX();
        double y = currentLocation.getY();
        double w = globalBounds.getWidth();
        double h = globalBounds.getHeight();
        globalBounds.setFrame(x, y, w, h);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(globalBounds);
        return snappedBounds;
    }

    private Rectangle2D getBottomRectangleBounds()
    {
        Rectangle2D globalBounds = new Rectangle2D.Double(0, 0, 0, 0);
        Rectangle2D contentsBounds = contents.getBounds();
        globalBounds.add(contentsBounds);
        globalBounds.add(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT));
        for (INode child : getChildren())
        {
            Rectangle2D childBounds = child.getBounds();
            globalBounds.add(childBounds);
        }
        Rectangle2D topBounds = getTopRectangleBounds();
        double x = topBounds.getX();
        double y = topBounds.getMaxY();
        double w = Math.max(globalBounds.getWidth(), topBounds.getWidth() + 2 * NAME_GAP);
        double h = globalBounds.getHeight();
        globalBounds.setFrame(x, y, w, h);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(globalBounds);
        return snappedBounds;
    }
    
    @Override
    public Rectangle2D getBounds()
    {
        Rectangle2D top = getTopRectangleBounds();
        Rectangle2D bot = getBottomRectangleBounds();
        top.add(bot);
        Rectangle2D snappedBounds = getGraph().getGrid().snap(top);
        return snappedBounds;
    }

    @Override
    public void draw(Graphics2D g2)
    {
        // Translate g2 if node has parent
        Point2D nodeLocationOnGraph = getLocationOnGraph();
        Point2D nodeLocation = getLocation();
        Point2D g2Location = new Point2D.Double(nodeLocationOnGraph.getX() - nodeLocation.getX(), nodeLocationOnGraph.getY() - nodeLocation.getY());
        g2.translate(g2Location.getX(), g2Location.getY());
        // Perform drawing
        super.draw(g2);
        Rectangle2D topBounds = getTopRectangleBounds();
        Rectangle2D bottomBounds = getBottomRectangleBounds();
        g2.draw(topBounds);
        g2.draw(bottomBounds);
        name.draw(g2, topBounds);
        contents.draw(g2, bottomBounds);
        // Draw its children
        for (INode node : getChildren())
        {
            node.draw(g2);
        }
        // Restore g2 original location
        g2.translate(-g2Location.getX(), -g2Location.getY());
    }

    @Override
    public Shape getShape()
    {
        GeneralPath path = new GeneralPath();
        path.append(getTopRectangleBounds(), false);
        path.append(getBottomRectangleBounds(), false);
        return path;
    }

    @Override
    public boolean addChildNode(INode n, Point2D p)
    {
        if (n instanceof ClassNode || n instanceof InterfaceNode || n instanceof PackageNode)
        {
            n.setParent(this);
            n.setGraph(this.getGraph());
            n.setLocation(p);
            addChildNode(n, getChildren().size());
            return true;
        }
        return false;
    }

    public PackageNode clone()
    {
        PackageNode cloned = (PackageNode) super.clone();
        cloned.name = name.clone();
        cloned.contents = contents.clone();
        return cloned;
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
     * Sets the contents property value.
     * 
     * @param newValue the contents of this class
     */
    public void setContents(MultiLineString newValue)
    {
        contents = newValue;
    }

    /**
     * Gets the contents property value.
     * 
     * @return the contents of this class
     */
    public MultiLineString getContents()
    {
        return contents;
    }

    private MultiLineString name;
    private MultiLineString contents;

    private static int DEFAULT_TOP_WIDTH = 60;
    private static int DEFAULT_TOP_HEIGHT = 20;
    private static int DEFAULT_WIDTH = 100;
    private static int DEFAULT_HEIGHT = 80;
    private static final int NAME_GAP = 3;

}
