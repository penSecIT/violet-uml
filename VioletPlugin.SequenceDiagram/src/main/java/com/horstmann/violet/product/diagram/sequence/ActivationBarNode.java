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

package com.horstmann.violet.product.diagram.sequence;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.node.RectangularNode;
import com.horstmann.violet.product.diagram.common.PointNode;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * An activation bar in a sequence diagram. This activation bar is hang on a lifeline (implicit parameter)
 */
public class ActivationBarNode extends RectangularNode
{
    /**
     * Construct an activation bar with a default size
     */
    public ActivationBarNode()
    {
        setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        // Translate g2 if node has parent
        Point2D nodeLocationOnGraph = getLocationOnGraph();
        Point2D nodeLocation = getLocation();
        Point2D g2Location = new Point2D.Double(nodeLocationOnGraph.getX() - nodeLocation.getX(), nodeLocationOnGraph.getY() - nodeLocation.getY());
        g2.translate(g2Location.getX(), g2Location.getY());
        // Perform painting
        super.draw(g2);
        Color oldColor = g2.getColor();
        g2.setColor(Color.WHITE);
        Rectangle2D b = getBounds();
        g2.fill(b);
        g2.setColor(oldColor);
        g2.draw(b);
        // Restore g2 original location
        g2.translate(-g2Location.getX(), -g2Location.getY());
        // Draw its children
        for (INode node : getChildren())
        {
            node.draw(g2);
        }
    }

    /**
     * Gets the participant's life line of this call. Note : method's name is ot set to getLifeLine to keep compatibility with older
     * versions
     * 
     * @return the participant's life line
     */
    public LifelineNode getImplicitParameter()
    {
        return lifeline;
    }

    /**
     * Sets the participant's life line of this call. Note : method's name is ot set to setLifeLine to keep compatibility with older
     * versions
     * 
     * @param newValue the participant's lifeline
     */
    public void setImplicitParameter(LifelineNode newValue)
    {
        Object oldValue = lifeline;
        if (oldValue != newValue)
        {
            lifeline = newValue;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#getConnectionPoint(com.horstmann.violet.framework.Direction)
     */
    public Point2D getConnectionPoint(Direction d)
    {
        if (d.getX() > 0) return new Point2D.Double(getBounds().getMaxX(), getBounds().getMinY());
        else return new Point2D.Double(getBounds().getX(), getBounds().getMinY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addEdge(com.horstmann.violet.framework.Edge, java.awt.geom.Point2D,
     * java.awt.geom.Point2D)
     */
    public boolean checkAddEdge(IEdge edge, Point2D startingNodePoint, Point2D endingNodePoint)
    {

        if (edge instanceof CallEdge)
        {
            addCallEdge((CallEdge) edge, startingNodePoint, endingNodePoint);
            // INode endingNode = edge.getEnd();
            // if (endingNode == null)
            // {
            // // If ending node is null, is could be considered as an action
            // // to attach a child if the endingNodePoint is not to far from
            // // the starting node.
            // double x = this.getBounds().getX();
            // double y = this.getBounds().getY();
            // double w = this.getBounds().getWidth();
            // double h = this.getBounds().getHeight();
            // Rectangle2D acceptableBounds = new Rectangle2D.Double(x, y - h, w * 3, h * 3);
            // if (acceptableBounds.contains(endingNodePoint))
            // {
            // endingNode = this.getImplicitParameter();
            // }
            // }
            //
            // if (!(endingNode instanceof ActivationBarNode || endingNode instanceof LifelineNode)) return false;
            //
            // if (endingNode instanceof LifelineNode && ((LifelineNode) endingNode).getTopRectangle().contains(endingNodePoint))
            // {
            // if (endingNode == getImplicitParameter()) return false;
            // edge.connect(this, endingNode);
            // CallEdge callEdge = (CallEdge) edge;
            // callEdge.setMiddleLabel("\u00ABcreate\u00BB");
            // addChildNode(endingNode, getChildren().size());
            // return true;
            // }
            //
            // if (endingNode instanceof ActivationBarNode && (endingNode.getParent() != null ||
            // getAncestors().contains(endingNode))) endingNode = ((ActivationBarNode) endingNode)
            // .getImplicitParameter();
            //
            // if (endingNode == null) return false;
            //
            // if (endingNode instanceof LifelineNode)
            // {
            // ActivationBarNode newNode = new ActivationBarNode();
            // getGraph().addNode(newNode, endingNodePoint);
            // newNode.setImplicitParameter((LifelineNode) endingNode);
            // endingNode = newNode;
            // }
            //
            // edge.connect(this, endingNode);
            // List<INode> children = getChildren();
            // int i = 0;
            // while (i < children.size() && children.get(i).getLocation().getY() < startingNodePoint.getY())
            // i++;
            // addChildNode(endingNode, i);
            // return true;
        }
        else if (edge instanceof ReturnEdge)
        {
            INode endingNode = getParent();
            if (endingNode == null) return false;
            if (endingNode instanceof ActivationBarNode)
            {
                edge.connect(this, endingNode);
                return true;
            }
        }

        return false;
    }

    private boolean addCallEdge(CallEdge edge, Point2D startingNodePoint, Point2D endingNodePoint)
    {
        INode endingNode = edge.getEnd();
        if (endingNode == null)
        {
            return false;
        }
        if (endingNode instanceof ActivationBarNode)
        {
            ActivationBarNode castedEndingNode = (ActivationBarNode) endingNode;
            if (castedEndingNode == this)
            {
                return false;
            }
            if (castedEndingNode.getImplicitParameter() != this.getImplicitParameter())
            {
                List<INode> children = getChildren();
                int i = 0;
                while (i < children.size() && children.get(i).getLocation().getY() < startingNodePoint.getY())
                {
                    i++;
                }
                addChildNode(endingNode, i);
                edge.connect(this, endingNode);
                return true;
            }
        }
        if (endingNode instanceof LifelineNode)
        {
            LifelineNode castedEndingNode = (LifelineNode) endingNode;
            if (castedEndingNode == this.getImplicitParameter())
            {
                // If ending node is null, is could be considered as an action
                // to attach a child if the endingNodePoint is not to far from
                // the starting node.
                double x = this.getBounds().getX();
                double y = this.getBounds().getY();
                double w = this.getBounds().getWidth();
                double h = this.getBounds().getHeight();
                Rectangle2D acceptableBounds = new Rectangle2D.Double(x, y - h, w * 3, h * 3);
                if (acceptableBounds.contains(endingNodePoint))
                {
                    ActivationBarNode newEndingNode = new ActivationBarNode();
                    newEndingNode.setImplicitParameter(getImplicitParameter());
                    newEndingNode.setGraph(getGraph());
                    int lastPos = getChildren().size();
                    addChildNode(newEndingNode, lastPos);
                    edge.connect(this, newEndingNode);
                    return true;
                }
            }
            Rectangle2D topRectangle = castedEndingNode.getTopRectangle();
            if (topRectangle.contains(endingNodePoint))
            {
                edge.setMiddleLabel("\u00ABcreate\u00BB");
                addChildNode(endingNode, getChildren().size());
                edge.connect(this, endingNode);
                return true;
            }
            if (!topRectangle.contains(endingNodePoint))
            {
                ActivationBarNode newEndingNode = new ActivationBarNode();
                getGraph().addNode(newEndingNode, endingNodePoint);
                // newEndingNode.setImplicitParameter(castedEndingNode);
                List<INode> children = getChildren();
                int i = 0;
                while (i < children.size() && children.get(i).getLocation().getY() < startingNodePoint.getY())
                {
                    i++;
                }
                addChildNode(newEndingNode, i);
                edge.connect(this, newEndingNode);
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#removeEdge(com.horstmann.violet.framework.Graph,
     * com.horstmann.violet.framework.Edge)
     */
    public void checkRemoveEdge(IEdge e)
    {
        if (e.getStart() == this) removeChild(e.getEnd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#removeNode(com.horstmann.violet.framework.Graph,
     * com.horstmann.violet.framework.Node)
     */
    public void checkRemoveNode(INode n)
    {
        // FIXME : if (n == lifeline) getGraph().removeNodesAndEdges(Arrays.asList(this), null);
    }

    /**
     * Finds an edge in the graph connected to start and end nodes
     * 
     * @param g the graph
     * @param start the start node
     * @param end the end node
     * @return the edge or null if no one is found
     */
    private IEdge findEdge(INode start, INode end)
    {
        for (IEdge e : getGraph().getEdges())
        {
            if (e.getStart() == start && e.getEnd() == end) return e;
        }
        return null;
    }

    @Override
    public Point2D getLocation()
    {
        double x = getHorizontalLocation();
        double y = getVerticalLocation();
        return new Point2D.Double(x, y);
    }

    /**
     * @return x location relative to the parent
     */
    private double getHorizontalLocation()
    {
        INode parentNode = getParent();
        if (parentNode != null && parentNode.getClass().isAssignableFrom(ActivationBarNode.class))
        {
            return DEFAULT_WIDTH / 2;
        }
        if (parentNode != null && parentNode.getClass().isAssignableFrom(LifelineNode.class))
        {
            LifelineNode lifeLineNode = (LifelineNode) parentNode;
            Rectangle2D lifeLineTopRectangle = lifeLineNode.getTopRectangle();
            return lifeLineTopRectangle.getWidth() / 2 - DEFAULT_WIDTH / 2;
        }
        return 0;
    }

    /**
     * @return y location relative to the parent
     */
    private double getVerticalLocation()
    {
        INode parentNode = getParent();
        if (parentNode != null && parentNode.getClass().isAssignableFrom(ActivationBarNode.class))
        {
            List<INode> parentChildren = parentNode.getChildren();
            double y = CALL_YGAP;
            for (INode aNode : parentChildren)
            {
                if (aNode != this)
                {
                    y = y + aNode.getBounds().getHeight() + CALL_YGAP;
                }
                if (aNode == this)
                {
                    break;
                }
            }
            return y;
        }
        if (parentNode != null && parentNode.getClass().isAssignableFrom(LifelineNode.class))
        {
            LifelineNode lifeLineNode = (LifelineNode) parentNode;
            Rectangle2D lifeLineTopRectangle = lifeLineNode.getTopRectangle();
            return lifeLineTopRectangle.getHeight() + CALL_YGAP;
        }
        return 0;
    }

    @Override
    public Rectangle2D getBounds()
    {
        Point2D nodeLocation = getLocation();
        // Height
        double height = DEFAULT_HEIGHT;
        int childVisibleNodesCounter = 0;
        for (INode aNode : getChildren())
        {
            if (aNode instanceof ActivationBarNode)
            {
                childVisibleNodesCounter++;
            }
        }
        if (childVisibleNodesCounter > 0)
        {
            height = CALL_YGAP;
            for (INode aNode : getChildren())
            {
                if (aNode instanceof ActivationBarNode)
                {
                    Rectangle2D aNodeBounds = aNode.getBounds();
                    height = height + aNodeBounds.getHeight() + CALL_YGAP;
                }
            }
        }
        return new Rectangle2D.Double(nodeLocation.getX(), nodeLocation.getY(), DEFAULT_WIDTH, height);
        // TODO : manage openbottom
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#layout(com.horstmann.violet.framework.Graph, java.awt.Graphics2D,
     * com.horstmann.violet.framework.Grid)
     */
    public void layout(Graphics2D g2, IGrid grid)
    {

        // if (getImplicitParameter() == null)
        // {
        // return;
        // }
        //
        // // Horizontal translation
        // double xmid = lifeline.getBounds().getCenterX() - DEFAULT_WIDTH / 2;
        // for (ActivationBarNode n = (ActivationBarNode) getParent(); n != null; n = (ActivationBarNode) n.getParent())
        // {
        // if (n.lifeline == lifeline)
        // {
        // xmid += DEFAULT_WIDTH / 2;
        // }
        // }
        // translate(xmid - getLocation().getX(), 0);
        //
        // // Set height
        // double ytop = getLocation().getY() + CALL_YGAP;
        // List<INode> calls = getChildren();
        //
        // for (INode n : calls)
        // {
        // if (n instanceof ActivationBarNode) n.setZ(getZ() + 1);
        // else n.setZ(0);
        // IEdge callEdge = findEdge(this, n);
        // // compute height of call edge
        // if (callEdge != null) ytop += callEdge.getBounds().getHeight() - CALL_YGAP;
        //
        // if (n instanceof LifelineNode) n.translate(0, ytop - ((LifelineNode) n).getTopRectangle().getHeight() / 2
        // - n.getLocation().getY());
        // else n.translate(0, ytop - n.getLocation().getY());
        //
        // n.layout(g2, grid);
        // if (n instanceof ActivationBarNode && ((ActivationBarNode) n).signaled) ytop += CALL_YGAP;
        // else if (n instanceof ActivationBarNode) ytop += n.getBounds().getHeight() + CALL_YGAP;
        // else if (n instanceof LifelineNode) ytop += ((LifelineNode) n).getTopRectangle().getHeight() / 2 + CALL_YGAP;
        // }
        // if (openBottom) ytop += 2 * CALL_YGAP;
        //
        // double minHeight = DEFAULT_HEIGHT;
        // IEdge returnEdge = findEdge(this, getParent());
        // if (returnEdge != null) minHeight = Math.max(minHeight, returnEdge.getBounds().getHeight());
        //
        // setBounds(new Rectangle2D.Double(getLocation().getX(), getLocation().getY(), DEFAULT_WIDTH, Math.max(minHeight, ytop
        // - getLocation().getY())));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.Node#addNode(com.horstmann.violet.framework.Node, java.awt.geom.Point2D)
     */
    public boolean addChildNode(INode n, Point2D p)
    {
        if (n instanceof PointNode || n instanceof ActivationBarNode)
        {
            n.setParent(this);
            n.setLocation(p);
            return getChildren().add(n);
        }
        return false;
    }

    /** The lifeline that embeds this activation bar in the sequence diagram */
    private LifelineNode lifeline;

    /** Default with */
    private static int DEFAULT_WIDTH = 16;

    /** Default height */
    private static int DEFAULT_HEIGHT = 30;

    /** Default vertical gap between two call nodes and a call node and an implicit node */
    public static int CALL_YGAP = 20;
}
