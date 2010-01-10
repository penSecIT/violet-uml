package com.horstmann.violet.framework.diagram;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;


/**
 * Implement this interface and register it a Graph class instance to
 * be informed about modifications.
 * 
 * @author Cay Horstmann
 *
 */
public interface GraphModificationListener
{
    void nodeAdded(IGraph g, INode n, Point2D location);
    void nodeRemoved(IGraph g, INode n);
    void nodeMoved(IGraph g, INode n, double dx, double dy);
    void childAttached(IGraph g, int index, INode p, INode c);
    void childDetached(IGraph g, int index, INode p, INode c);
    void edgeAdded(IGraph g, IEdge e, Point2D startPoint, Point2D endPoint);
    void edgeRemoved(IGraph g, IEdge e);
    void propertyChangedOnNodeOrEdge(IGraph g, PropertyChangeEvent event);
    
}
