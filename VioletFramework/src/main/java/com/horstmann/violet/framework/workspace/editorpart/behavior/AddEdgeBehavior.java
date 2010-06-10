package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartBehaviorManager;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartSelectionHandler;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphTool;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.IGraphToolsBar;

public class AddEdgeBehavior extends AbstractEditorPartBehavior
{

    public AddEdgeBehavior(IEditorPart editorPart, IGraphToolsBar graphToolsBar)
    {
        this.editorPart = editorPart;
        this.graph = editorPart.getGraph();
        this.selectionHandler = editorPart.getSelectionHandler();
        this.behaviorManager = editorPart.getBehaviorManager();
        this.graphToolsBar = graphToolsBar;
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        if (event.getClickCount() > 1)
        {
            return;
        }
        if (GraphTool.SELECTION_TOOL.equals(this.graphToolsBar.getSelectedTool()))
        {
            return;
        }
        GraphTool selectedTool = this.selectionHandler.getSelectedTool();
        if (!IEdge.class.isInstance(selectedTool.getNodeOrEdge()))
        {
            return;
        }
        double zoom = editorPart.getZoomFactor();
        final Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        INode targetNode = graph.findNode(mousePoint);
        this.isDraggingInProgress = (targetNode != null);
        mouseDownPoint = mousePoint;
        lastMousePoint = mousePoint;
    }

    @Override
    public void onMouseDragged(MouseEvent event)
    {
        if (!isDraggingInProgress)
        {
            return;
        }
        double zoom = editorPart.getZoomFactor();
        Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        lastMousePoint = mousePoint;
    }

    @Override
    public void onMouseReleased(MouseEvent event)
    {
        if (!isDraggingInProgress)
        {
            return;
        }
        GraphTool selectedTool = this.selectionHandler.getSelectedTool();
        if (!IEdge.class.isInstance(selectedTool.getNodeOrEdge()))
        {
            isDraggingInProgress = false;
            return;
        }
        IEdge prototype = (IEdge) selectedTool.getNodeOrEdge();
        IEdge newEdge = (IEdge) prototype.clone();
        boolean added = addEdgeAtPoints(newEdge, mouseDownPoint, lastMousePoint);
        if (added)
        {
            selectionHandler.setSelectedElement(newEdge);
        }
    }

    /**
     * Adds an edge at a specific location
     * 
     * @param newEdge
     * @param startPoint
     * @param endPoint
     * @return true id the edge has been added
     */
    private boolean addEdgeAtPoints(IEdge newEdge, Point2D startPoint, Point2D endPoint)
    {
        boolean isAdded = false;
        if (startPoint.distance(endPoint) > CONNECT_THRESHOLD)
        {
            this.behaviorManager.fireBeforeAddingEdgeAtPoints(newEdge, startPoint, endPoint);
            try
            {
                if (graph.addEdgeAtPoints(newEdge, startPoint, endPoint))
                {
                    newEdge.incrementRevision();
                    Graphics2D graphics = (Graphics2D) editorPart.getAWTComponent().getGraphics();
                    IGrid grid = editorPart.getGrid();
                    graph.layout(graphics, grid);
                    isAdded = true;
                }
            }
            finally
            {
                this.behaviorManager.fireAfterAddingEdgeAtPoints(newEdge, startPoint, endPoint);
            }
        }
        return isAdded;
    }

    @Override
    public void onPaint(Graphics2D g2)
    {
        if (!isDraggingInProgress)
        {
            return;
        }
        Color oldColor = g2.getColor();
        g2.setColor(PURPLE);
        g2.draw(new Line2D.Double(mouseDownPoint, lastMousePoint));
        g2.setColor(oldColor);
    }

    private static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);

    private static final int CONNECT_THRESHOLD = 8;

    private Point2D mouseDownPoint = null;

    private Point2D lastMousePoint = null;

    private IEditorPart editorPart;

    private IGraph graph;

    private IEditorPartSelectionHandler selectionHandler;

    private IEditorPartBehaviorManager behaviorManager;

    private IGraphToolsBar graphToolsBar;

    private boolean isDraggingInProgress = false;
}
