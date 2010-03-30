package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartSelectionHandler;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphTool;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.IGraphToolsBar;

public class DragSelectedBehavior extends AbstractEditorPartBehavior
{

    public DragSelectedBehavior(IEditorPart editorPart, IGraphToolsBar graphToolsBar)
    {
        this.editorPart = editorPart;
        this.graph = editorPart.getGraph();
        this.selectionHandler = editorPart.getSelectionHandler();
        this.graphToolsBar = graphToolsBar;
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        if (event.getClickCount() > 1)
        {
            return;
        }
        if (!GraphTool.SELECTION_TOOL.equals(this.graphToolsBar.getSelectedTool()))
        {
            return;
        }
        double zoom = editorPart.getZoomFactor();
        final Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        if (!isMouseOnNode(mousePoint))
        {
            isReadyForDragging = true;
            lastMousePoint = mousePoint;
        }
    }

    private boolean isMouseOnNode(Point2D mouseLocation)
    {
        INode node = this.graph.findNode(mouseLocation);
        if (node == null)
        {
            return true;
        }
        return false;
    }

    @Override
    public void onMouseDragged(MouseEvent event)
    {
        if (!isReadyForDragging)
        {
            return;
        }
        double zoom = editorPart.getZoomFactor();
        Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        // TODO : behaviorManager.fireOnElementsDragged(selectionHandler.getSelectedNodes(), selectionHandler.getSelectedEdges());
        INode lastNode = selectionHandler.getLastSelectedNode();
        Rectangle2D bounds = lastNode.getBounds();
        double dx = mousePoint.getX() - lastMousePoint.getX();
        double dy = mousePoint.getY() - lastMousePoint.getY();

        // we don't want to drag nodes into negative coordinates
        // particularly with multiple selection, we might never be
        // able to get them back.
        List<INode> selectedNodes = selectionHandler.getSelectedNodes();
        for (INode n : selectedNodes)
            bounds.add(n.getBounds());
        dx = Math.max(dx, -bounds.getX());
        dy = Math.max(dy, -bounds.getY());

        for (INode n : selectedNodes)
        {
            if (!selectedNodes.contains(n.getParent())) // parents are responsible for translating their children
            n.translate(dx, dy);
        }
        lastMousePoint = mousePoint;
    }

    @Override
    public void onMouseReleased(MouseEvent event)
    {
        lastMousePoint = null;
        isReadyForDragging = false;
    }

    private IGraph graph;

    private Point2D lastMousePoint = null;

    private IEditorPartSelectionHandler selectionHandler;

    private IEditorPart editorPart;

    private IGraphToolsBar graphToolsBar;

    private boolean isReadyForDragging = false;
}
