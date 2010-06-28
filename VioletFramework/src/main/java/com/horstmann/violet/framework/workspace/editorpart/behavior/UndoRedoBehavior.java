package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.diagram.property.MultiLineString;
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
    
    @Override
    public void whileEditingEdge(IEdge edge, final PropertyChangeEvent event)
    {
        capturePropertyChanges(event);
    }

    @Override
    public void whileEditingNode(INode node, PropertyChangeEvent event)
    {
        capturePropertyChanges(event);
    }
    
    private void capturePropertyChanges(final PropertyChangeEvent event)
    {
        CompoundEdit capturedEdit = historyManager.getCurrentCapturedEdit();
        if (capturedEdit == null) return;
        Object newValue = event.getNewValue();
        Object oldValue = event.getOldValue();
        if (oldValue == null && newValue == null) return;
        boolean isOldValueRecognized = oldValue != null && (String.class.isInstance(oldValue) || MultiLineString.class.isInstance(oldValue));
        boolean isNewValueRecognized = oldValue != null && (String.class.isInstance(newValue) || MultiLineString.class.isInstance(newValue));
        if (!isOldValueRecognized && !isNewValueRecognized) return;
        UndoableEdit edit = new AbstractUndoableEdit()
        {
            @Override
            public void undo() throws CannotUndoException
            {
                PropertyChangeEvent invertedEvent = new PropertyChangeEvent(event.getSource(), event.getPropertyName(),
                        event.getNewValue(), event.getOldValue());
                IGraph graph = editorPart.getGraph();
                graph.changeNodeOrEdgeProperty(invertedEvent);
            }

            @Override
            public void redo() throws CannotRedoException
            {
                IGraph graph = editorPart.getGraph();
                graph.changeNodeOrEdgeProperty(event);
            }
        };
        capturedEdit.addEdit(edit);
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
        editorPart.getAWTComponent().repaint();
    }
    
    public void redo() {
        historyManager.redo();
        editorPart.getAWTComponent().repaint();
    }
    
}
