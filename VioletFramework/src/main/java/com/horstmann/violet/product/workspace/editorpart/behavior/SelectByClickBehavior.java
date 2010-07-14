package com.horstmann.violet.product.workspace.editorpart.behavior;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.horstmann.violet.framework.util.GrabberUtils;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.workspace.editorpart.IEditorPart;
import com.horstmann.violet.product.workspace.editorpart.IEditorPartSelectionHandler;
import com.horstmann.violet.product.workspace.sidebar.graphtools.GraphTool;
import com.horstmann.violet.product.workspace.sidebar.graphtools.IGraphToolsBar;

public class SelectByClickBehavior extends AbstractEditorPartBehavior
{

    public SelectByClickBehavior(IEditorPart editorPart, IGraphToolsBar graphToolsBar)
    {
        this.editorPart = editorPart;
        this.graph = editorPart.getGraph();
        this.selectionHandler = editorPart.getSelectionHandler();
        this.graphToolsBar = graphToolsBar;
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        if (event.getClickCount() > 1) {
            return;
        }
        if (!GraphTool.SELECTION_TOOL.equals(this.graphToolsBar.getSelectedTool())) {
            return;
        }
        double zoom = editorPart.getZoomFactor();
        Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
        boolean isOnNodeOrEdge = isMouseOnNodeOrEdge(mousePoint);
        if (!isOnNodeOrEdge && !isCtrl)
        {
            resetSelectedElements();
            return;
        }
        if (isOnNodeOrEdge && !isCtrl)
        {
            resetSelectedElements();
            addOrRemoveElementFromSelection(mousePoint);
            return;
        }
        if (isOnNodeOrEdge && isCtrl)
        {
            addOrRemoveElementFromSelection(mousePoint);
            return;
        }
    }

    private boolean isMouseOnNodeOrEdge(Point2D mouseLocation)
    {
        INode node = this.graph.findNode(mouseLocation);
        IEdge edge = this.graph.findEdge(mouseLocation);
        if (node == null && edge == null)
        {
            return false;
        }
        return true;
    }

    private void resetSelectedElements()
    {
        this.selectionHandler.clearSelection();
    }

    private void addOrRemoveElementFromSelection(Point2D mouseLocation)
    {
        INode node = this.graph.findNode(mouseLocation);
        IEdge edge = this.graph.findEdge(mouseLocation);
        if (node != null)
        {
            if (this.selectionHandler.isElementAlreadySelected(node))
            {
                this.selectionHandler.removeElementFromSelection(node);
            }
            else
            {
                this.selectionHandler.addSelectedElement(node);
            }
        }
        if (edge != null)
        {
            if (this.selectionHandler.isElementAlreadySelected(edge))
            {
                this.selectionHandler.removeElementFromSelection(edge);
            }
            else
            {
                this.selectionHandler.addSelectedElement(edge);
            }
        }
    }
    
    @Override
    public void onPaint(Graphics2D g2)
    {
        List<INode> nodes = selectionHandler.getSelectedNodes();
        for (INode n : nodes)
        {
            if (graph.getNodes().contains(n))
            {
                Rectangle2D grabberBounds = n.getBounds();
                GrabberUtils.drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMinY());
                GrabberUtils.drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMaxY());
                GrabberUtils.drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMinY());
                GrabberUtils.drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMaxY());
            }
        }
        List<IEdge> edges = selectionHandler.getSelectedEdges();
        for (IEdge e : edges)
        {
            if (graph.getEdges().contains(e))
            {
                Line2D line = e.getConnectionPoints();
                GrabberUtils.drawGrabber(g2, line.getX1(), line.getY1());
                GrabberUtils.drawGrabber(g2, line.getX2(), line.getY2());
            }
        }
    }


    private IEditorPart editorPart;

    private IGraph graph;

    private IEditorPartSelectionHandler selectionHandler;
    
    private IGraphToolsBar graphToolsBar;

}
