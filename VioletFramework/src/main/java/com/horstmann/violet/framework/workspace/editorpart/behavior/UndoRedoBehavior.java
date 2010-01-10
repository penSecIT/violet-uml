package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.history.HistoryManager;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;

public class UndoRedoBehavior extends AbstractEditorPartBehavior
{

    private IEditorPart editorPart;
    private HistoryManager historyManager;

    public UndoRedoBehavior(IEditorPart editorPart)
    {
        this.editorPart = editorPart;
        this.historyManager = new HistoryManager(editorPart.getGraph());
    }
    
    @Override
    public void onMouseDragged(MouseEvent event)
    {
        startHistoryCapture();
    }
    
    @Override
    public void onMouseReleased(MouseEvent event)
    {
        stopHistoryCapture();
    }
    
    @Override
    public void beforeEditingNode(INode node)
    {
        startHistoryCapture();
    }
    
    @Override
    public void afterEditingNode(INode node)
    {
        stopHistoryCapture();
    }
    
    @Override
    public void beforeEditingEdge(IEdge edge)
    {
        startHistoryCapture();
    }
    
    @Override
    public void afterEditingEdge(IEdge edge)
    {
        stopHistoryCapture();
    }
    
    @Override
    public void beforeRemovingSelectedElements()
    {
        startHistoryCapture();
    }

    
    
    @Override
    public void afterRemovingSelectedElements()
    {
        stopHistoryCapture();
    }
    
    @Override
    public void beforeAddingNodeAtPoint(INode node, Point2D location)
    {
        startHistoryCapture();
    }
    
    @Override
    public void afterAddingNodeAtPoint(INode node, Point2D location)
    {
        stopHistoryCapture();
    }
    
    @Override
    public void beforeAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint)
    {
        startHistoryCapture();
    }
    
    @Override
    public void afterAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint)
    {
        stopHistoryCapture();
    }

    private void startHistoryCapture()
    {
        if (!historyManager.hasCaptureInProgress())
        {
            historyManager.startCaptureAction();
        }
    }
    
    private void stopHistoryCapture()
    {
        if (historyManager.hasCaptureInProgress())
        {
            historyManager.stopCaptureAction();
        }
    }
    
    public void undo() {
        historyManager.undo();
    }
    
    public void redo() {
        historyManager.redo();
    }
    
}
