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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.Id;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.workspace.editorpart.IGrid;

/**
 * A node in a graph. To be more precise, a node is an graphical entity that represents a class, a sequence, a state or all other
 * type of entities that can or not handle edges.
 * 
 * @author Cay Horstmann
 */
public interface INode extends Serializable, Cloneable
{
    /**
     * Draw the node.
     * 
     * @param g2 the graphics context
     */
    void draw(Graphics2D g2);
    
    /**
     * Translates the node by a given amount
     * 
     * @param dx the amount to translate in the x-direction
     * @param dy the amount to translate in the y-direction
     */
    void translate(double dx, double dy);
    
    /**
     * Gets the location of this node. The location determines how the node is laid out. The location is usually not the same as the 
     * top left corner of the bounds (which is adjusted visually when snapping to the grid).
     * @return the location
     */
    Point2D getLocation();     
    
    /**
     * Tests whether the node contains a point.
     * 
     * @param aPoint the point to test
     * @return true if this node contains aPoint
     */
    boolean contains(Point2D aPoint);

    /**
     * Get the best connection point to connect this node with another node. This should be a point on the boundary of the shape of
     * this node.
     * 
     * @param d the direction from the center of the bounding rectangle towards the boundary
     * @return the recommended connection point
     */
    Point2D getConnectionPoint(Direction d);

    /**
     * Get the visual bounding rectangle of the shape of this node
     * 
     * @return the bounding rectangle
     */
    Rectangle2D getBounds();

    /**
     * Checks whether to add an edge that originates at this node.
     * 
     * @param p1 a point in the starting node
     * @param p2 a point in the ending node
     * @param e the edge to add
     * @return true if the edge was added
     */
    boolean checkAddEdge(IEdge e, Point2D p1, Point2D p2);

    /**
     * Adds a node as a child node to this node.
     * 
     * @param n the child node
     * @param p the point at which the node is being added
     * @return true if this node accepts the given node as a child
     */
    boolean checkAddNode(INode n, Point2D p);

    /**
     * Notifies this node that an edge is being removed.
     * 
     * @param g the ambient graph
     * @param e the edge to be removed
     */
    void checkRemoveEdge(IEdge e);

    /**
     * Notifies this node that a node is being removed.
     * 
     * @param g the ambient graph
     * @param n the node to be removed
     */
    void checkRemoveNode(INode n);
    
    /**
     * Adds a set of nodes as child nodes to this node
     * @param children the children that are proposed to be pasted
     * @return true if the paste has been accepted
     */
    boolean checkPasteChildren(Collection<INode> children);

    /**
     * Lays out the node and its children.
     * 
     * @param g the ambient graph
     * @param g2 the graphics context
     * @param grid the grid to snap to
     */
    void layout(Graphics2D g2, IGrid grid);

    /**
     * Gets the parent of this node.
     * 
     * @return the parent node, or null if the node has no parent
     */
    INode getParent();
    
    /**
     * Sets node's parent (for decoder)
     * 
     * @param parentNode p
     */
    void setParent(INode parentNode);

    /**
     * Gets the children of this node.
     * 
     * @return an unmodifiable list of the children
     */
    List<INode> getChildren();
    
    /**
     * Sets the children of this node 
     * 
     * @param  an ordered list of children
     */
    void setChildren(List<INode> children);

    /**
     * Adds a child node and fires the graph modification event.
     * 
     * @param index the position at which to add the child
     * @param node the child node to add
     */
    void addChild(int index, INode node);

    /**
     * Removes a child node and fires the graph modification event.
     * 
     * @param node the child to remove.
     */
    void removeChild(INode node);
    
    /**
        
     * Gets the z-order. Nodes with higher z-order are drawn above those with lower z-order.
     * @return the z-order.
     */
    int getZ();
    
    /**
     * Sets the z-order.
     * @param z the desired z-order.
     */
    void setZ(int z);
    
    
    /**
     * Returns a unique id of this node to make it easier to identify
     * 
     * @return a unique id
     */
    Id getId();
    
    
    /**
     * Sets unique id to this node to make it easier to identify
     * 
     * @param id new unique id
     */
    void setId(Id id);
    
    /**
     * Returns current node revision
     */
    Integer getRevision();    
    
    /**
     * Updates current node revision number
     * @param newRevisionNumber n
     */
    void setRevision(Integer newRevisionNumber);
        
    /**
     * Increments revision number
     */
    void incrementRevision();
    

    /**
     * Sets the graph that contains this node.
     * @param g the graph
     */
    void setGraph(IGraph g);
    
    /**
     * Gets the graph that contains this node, or null if this node is not contained in any graph.
     * @return
     */
    IGraph getGraph();
    
    /**
     * Gets current node tool tip
     * @return
     */
    String getToolTip();
    
    INode clone();
}
