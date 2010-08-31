package com.horstmann.violet.product.diagram.classes;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;

import javax.swing.JLabel;

import com.horstmann.violet.framework.util.GeometryUtils;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.diagram.common.NoteNode;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * A package node in a UML diagram.
 */
public class PackageNode extends RectangularNode {
    /**
     * Construct a package node with a default size
     */
    public PackageNode() {
        name = "";
        contents = new MultiLineString();
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
        top = new Rectangle2D.Double(0, 0, DEFAULT_TOP_WIDTH, DEFAULT_TOP_HEIGHT);
        bot = new Rectangle2D.Double(0, DEFAULT_TOP_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT);
    }

    @Override
    public void translate(double dx, double dy) {
        GeometryUtils.translate(top, dx, dy);
        GeometryUtils.translate(bot, dx, dy);
        if (getChildren().size() == 0)
            super.translate(dx, dy);
        else
            for (INode childNode : getChildren())
                childNode.translate(dx, dy);
    }

    public void draw(Graphics2D g2) {
        super.draw(g2);
        Rectangle2D bounds = getBounds();

        label.setText("<html>" + name + "</html>");
        label.setFont(g2.getFont());
        Dimension d = label.getPreferredSize();
        label.setBounds(0, 0, d.width, d.height);

        g2.draw(top);

        double textX = bounds.getX() + NAME_GAP;
        double textY = bounds.getY() + (top.getHeight() - d.getHeight()) / 2;

        g2.translate(textX, textY);
        label.paint(g2);
        g2.translate(-textX, -textY);

        g2.draw(bot);
        contents.draw(g2, bot);
    }

    public Shape getShape() {
        GeneralPath path = new GeneralPath();
        path.append(top, false);
        path.append(bot, false);
        return path;
    }

    public Point2D getLocation() {
        if (getChildren().size() > 0)
            return new Point2D.Double(getBounds().getX(), getBounds().getY());
        else
            return super.getLocation();
    }

    public void layout(Graphics2D g2, IGrid grid) {
        label.setText("<html>" + name + "</html>");
        label.setFont(g2.getFont());
        Dimension d = label.getPreferredSize();
        double topWidth = Math.max(d.getWidth() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH);
        double topHeight = Math.max(d.getHeight(), DEFAULT_TOP_HEIGHT);

        double xgap = Math.max(DEFAULT_XGAP, grid.getSnappingWidth());
        double ygap = Math.max(DEFAULT_YGAP, grid.getSnappingHeight());

        Rectangle2D childBounds = null;
        List<INode> children = getChildren();
        for (INode child : children) {
            child.setZ(getZ() + 1);
            child.layout(g2, grid);
            if (childBounds == null)
                childBounds = child.getBounds();
            else
                childBounds.add(child.getBounds());
        }

        Rectangle2D contentsBounds = contents.getBounds(g2);

        if (childBounds == null) // no children; leave (x,y) as is and place
                                 // default rect below
        {
            snapBounds(grid, Math.max(topWidth + DEFAULT_WIDTH - DEFAULT_TOP_WIDTH, Math.max(DEFAULT_WIDTH, contentsBounds.getWidth())), topHeight + Math.max(DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT, contentsBounds.getHeight()));
        } else {
            setBounds(new Rectangle2D.Double(childBounds.getX() - xgap, childBounds.getY() - topHeight - ygap, Math.max(topWidth, childBounds.getWidth() + 2 * xgap), topHeight + childBounds.getHeight() + 2 * ygap));
        }

        Rectangle2D b = getBounds();
        top = new Rectangle2D.Double(b.getX(), b.getY(), topWidth, topHeight);
        bot = new Rectangle2D.Double(b.getX(), b.getY() + topHeight, b.getWidth(), b.getHeight() - topHeight);
    }

    /**
     * Sets the name property value.
     * 
     * @param newValue
     *            the class name
     */
    public void setName(String newValue) {
        name = newValue;
    }

    /**
     * Gets the name property value.
     * 
     * @return the class name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the contents property value.
     * 
     * @param newValue
     *            the contents of this class
     */
    public void setContents(MultiLineString newValue) {
        contents = newValue;
    }

    /**
     * Gets the contents property value.
     * 
     * @return the contents of this class
     */
    public MultiLineString getContents() {
        return contents;
    }

    public boolean addChildNode(INode n, Point2D p) {
//        Old code from addChildNode(INode n)
//        if (!(n instanceof ClassNode || n instanceof InterfaceNode || n instanceof PackageNode))
//            return false;
//        final int GAP = 6;
//        n.translate(bot.getX() + GAP, bot.getY() + GAP);
//        addChildNode(n, getChildren().size());
//        return true;
        if (n instanceof ClassNode || n instanceof InterfaceNode || n instanceof PackageNode) {
            addChildNode(n, getChildren().size());
            return true;
        } else
            return n instanceof NoteNode;
    }


    public PackageNode clone() {
        PackageNode cloned = (PackageNode) super.clone();
        cloned.contents = contents.clone();
        top = (Rectangle2D) top.clone();
        bot = (Rectangle2D) bot.clone();
        return cloned;
    }

    private String name;
    private MultiLineString contents;

    private transient Rectangle2D top;
    private transient Rectangle2D bot;

    private static int DEFAULT_TOP_WIDTH = 60;
    private static int DEFAULT_TOP_HEIGHT = 20;
    private static int DEFAULT_WIDTH = 100;
    private static int DEFAULT_HEIGHT = 80;
    private static final int NAME_GAP = 3;
    private static final int DEFAULT_XGAP = 5;
    private static final int DEFAULT_YGAP = 5;

    private static JLabel label = new JLabel();
}
