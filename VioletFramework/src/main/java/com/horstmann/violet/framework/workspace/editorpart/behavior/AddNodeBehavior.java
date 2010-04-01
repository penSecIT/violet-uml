package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartBehaviorManager;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartSelectionHandler;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphTool;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.IGraphToolsBar;

public class AddNodeBehavior extends AbstractEditorPartBehavior
{

    public AddNodeBehavior(IEditorPart editorPart, IGraphToolsBar graphToolsBar)
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
        if (!INode.class.isInstance(selectedTool.getNodeOrEdge()))
        {
            return;
        }
        INode prototype = (INode) selectedTool.getNodeOrEdge();
        INode newNode = (INode) prototype.clone();
        double zoom = editorPart.getZoomFactor();
        final Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        boolean added = addNodeAtPoint(newNode, mousePoint);
        if (added)
        {
            selectionHandler.setSelectedElement(newNode);
        }
    }

    /**
     * Adds a new at a precise location
     * 
     * @param newNode to be added
     * @param location
     * @return true if the node has been added
     */
    private boolean addNodeAtPoint(INode newNode, Point2D location)
    {
        boolean isAdded = false;
        this.behaviorManager.fireBeforeAddingNodeAtPoint(newNode, location);
        try
        {
            if (graph.addNode(newNode, location))
            {
                newNode.incrementRevision();
                Graphics2D graphics = (Graphics2D) editorPart.getAWTComponent().getGraphics();
                IGrid grid = editorPart.getGrid();
                graph.layout(graphics, grid);
            }
        }
        finally
        {
            this.behaviorManager.fireAfterAddingNodeAtPoint(newNode, location);
        }
        return isAdded;
    }

    private IEditorPart editorPart;

    private IGraph graph;

    private IEditorPartSelectionHandler selectionHandler;

    private IEditorPartBehaviorManager behaviorManager;

    private IGraphToolsBar graphToolsBar;
}
