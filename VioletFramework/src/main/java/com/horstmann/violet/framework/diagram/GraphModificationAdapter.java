package com.horstmann.violet.framework.diagram;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;

public class GraphModificationAdapter implements GraphModificationListener
{

    public void childAttached(IGraph g, int index, INode p, INode c)
    {
        
    }

    public void childDetached(IGraph g, int index, INode p, INode c)
    {

    }

    public void edgeAdded(IGraph g, IEdge e, Point2D startPoint, Point2D endPoint)
    {

    }

    public void edgeRemoved(IGraph g, IEdge e)
    {

    }

    public void nodeAdded(IGraph g, INode n, Point2D location)
    {

    }

    public void nodeMoved(IGraph g, INode n, double dx, double dy)
    {

    }

    public void nodeRemoved(IGraph g, INode n)
    {

    }

    public void propertyChangedOnNodeOrEdge(IGraph g, PropertyChangeEvent event)
    {

    }
    

}
