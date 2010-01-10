package com.horstmann.violet.framework.diagram;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.List;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;

public interface IGraph
{

    /**
     * Gets the node types of a particular graph type.
     * 
     * @return an array of node prototypes
     */
    public abstract INode[] getNodePrototypes();


    /**
     * Gets the edge types of a particular graph type.
     * 
     * @return an array of edge prototypes
     */
    public abstract IEdge[] getEdgePrototypes();


    /**
     * Gets the nodes of this graph.
     * 
     * @return an unmodifiable collection of the nodes
     */
    public abstract Collection<INode> getNodes();


    /**
     * Gets the edges of this graph.
     * 
     * @return an unmodifiable collection of the edges
     */
    public abstract Collection<IEdge> getEdges();


    /**
     * Removes the given nodes and edges from this graph
     * @param nodesToRemove a collection of nodes to remove, or null to remove no nodes
     * @param edgesToRemove a collection of edges to remove, or null to remove no edges
     */
    public abstract void removeNodesAndEdges(Collection<? extends INode> nodesToRemove, Collection<? extends IEdge> edgesToRemove);


    /**
     * Adds a node to the graph so that the top left corner of the bounding rectangle is at the given point.
     * This method is called by a decoder when reading a data file.
     * 
     * @param n the node to add
     * @param p the desired location
     */
    public abstract boolean addNode(INode n, Point2D p);


    /**
     * Moves a node to a new location
     * 
     * @param existingNode e
     * @param dest d
     */
    public abstract void moveNode(INode existingNode, Point2D dest);

    /**
     * Removes a node from this graph.
     * 
     * @param n
     */
    public abstract void removeNode(INode n);


    /**
     * Adds a child node
     * 
     * @param index the position at which to add the child
     * @param parentNode the parent node
     * @param childNode the child node to add
     */
    public abstract void attachChildNode(int index, INode parentNode, INode childNode);

    /**
     * Detachea a child node
     * 
     * @param index the position at which to add the child
     * @param parentNode the parent node
     * @param childNode the child node to add
     */
    public abstract void detachChildNode(int index, INode parentNode, INode childNode);

    /**
     * Adds an edge to the graph that joins the nodes containing the given points. If the points aren't both inside nodes, then no
     * edge is added.
     * 
     * @param e the edge to add
     * @param p1 a point in the starting node
     * @param p2 a point in the ending node
     */
    public abstract boolean addEdgeAtPoints(IEdge e, Point2D p1, Point2D p2);


    /**
     * Adds an edge to this graph. This method should only be called by a decoder when reading a data file.
     * 
     * @param e the edge to add
     * @param start the start node of the edge
     * @param end the end node of the edge
     * @return isOK as true if successfully connected
     */
    public abstract void connect(IEdge e, INode start, INode end);


    /**
     * Kept for compatibility.
     */
    public abstract void connect(IEdge e, INode aStart, Point2D sPoint, INode anEnd, Point2D ePoint);


    /**
     * Removes an edge from this graph. This method should only be called by an undoable edit.
     * @param n
     */
    public abstract void removeEdge(IEdge e);


    /**
     * Finds a node by its id. This internal method should only be used by network features (for the moment because
     * node ids are still generated automatically)
     * 
     * @param id 
     * @return the found node or null if no one found
     */
    public abstract INode findNode(Id id);


    /**
     * Finds a node containing the given point.
     * 
     * @param p a point
     * @return a node containing p or null if no nodes contain p
     */
    public abstract INode findNode(Point2D p);

    /**
     * Finds an adge by its id. This internal method should only be used by network features (for the moment because
     * edge ids are still generated automatically)
     * 
     * @param id
     * @return the found edge or null if no one found
     */
    public abstract IEdge findEdge(Id id);


    /**
     * Finds an edge containing the given point.
     * 
     * @param p a point
     * @return an edge containing p or null if no edges contain p
     */
    public abstract IEdge findEdge(Point2D p);

    /**
     * Draws the graph
     * 
     * @param g2 the graphics context
     */
    public abstract void draw(Graphics2D g2, IGrid g);

    /**
     * Computes the layout of the graph. If you override this method, you must first call <code>super.layout</code>.
     * 
     * @param g the graphics context
     * @param gr the grid to snap to
     */
    public abstract void layout(Graphics2D g2, IGrid gr);

    /**
     * Gets the smallest rectangle enclosing the graph
     * 
     * @return the bounding rectangle
     */
    public abstract Rectangle2D getClipBounds();

    /**
     * Sets desired bound
     * 
     * @param newValue
     */
    public abstract void setBounds(Rectangle2D newValue);

    public abstract void addGraphModificationListener(GraphModificationListener listener);

    public abstract void addGraphModificationListener(List<GraphModificationListener> listeners);
    
    public abstract List<GraphModificationListener> getGraphModificationListener();

    public abstract void removeGraphModificationListener(GraphModificationListener listener);

    public abstract void changeNodeOrEdgeProperty(PropertyChangeEvent e);

    public abstract void fireNodeAdded(INode n, Point2D location);

    public abstract void fireNodeRemoved(INode n);

    public abstract void fireChildAttached(int index, INode p, INode c);

    public abstract void fireChildDetached(int index, INode p, INode c);

    public abstract void fireEdgeAdded(IEdge e, Point2D startPoint, Point2D endPoint);

    public abstract void fireEdgeRemoved(IEdge e);

    public abstract void fireNodeMoved(INode node, double dx, double dy);

    public abstract void firePropertyChangeOnNodeOrEdge(PropertyChangeEvent event);

}