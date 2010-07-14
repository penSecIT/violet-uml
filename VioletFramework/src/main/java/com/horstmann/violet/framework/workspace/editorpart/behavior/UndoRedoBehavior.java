package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.diagram.property.MultiLineString;
import com.horstmann.violet.framework.util.PropertyUtils;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartSelectionHandler;

public class UndoRedoBehavior extends AbstractEditorPartBehavior
{

    /**
     * The concerned workspace
     */
    private IEditorPart editorPart;

    /**
     * To retreive selected elements
     */
    private IEditorPartSelectionHandler selectionHandler;

    /**
     * Used on node's drag'n drop
     */
    private Point2D lastMouseLocation = null;

    /**
     * Used on node's drag'n drop
     */
    private boolean isReadyForDragging = false;

    /**
     * Current composed undoable edit
     */
    private CompoundEdit currentCapturedEdit;

    /**
     * Undo/redo manager
     */
    private UndoManager undoManager = new UndoManager();

    /**
     * History size limit
     */
    private static final int HISTORY_SIZE = 50;

    public UndoRedoBehavior(IEditorPart editorPart)
    {
        this.editorPart = editorPart;
        this.selectionHandler = editorPart.getSelectionHandler();
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        if (event.getClickCount() > 1)
        {
            return;
        }
        double zoom = editorPart.getZoomFactor();
        final Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        if (isMouseOnNode(mousePoint))
        {
            isReadyForDragging = true;
            this.lastMouseLocation = mousePoint;
        }
    }

    private boolean isMouseOnNode(Point2D mouseLocation)
    {
        IGraph graph = this.editorPart.getGraph();
        INode node = graph.findNode(mouseLocation);
        if (node == null)
        {
            return false;
        }
        return true;
    }

    @Override
    public void onMouseDragged(MouseEvent event)
    {

        if (!this.isReadyForDragging)
        {
            return;
        }
        startHistoryCapture();
        List<INode> selectedNodes = this.selectionHandler.getSelectedNodes();
        CompoundEdit capturedEdit = getCurrentCapturedEdit();
        Point newMouseLocation = event.getPoint();
        double zoom = editorPart.getZoomFactor();
        Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        INode lastNode = selectionHandler.getLastSelectedNode();
        if (lastNode == null)
        {
            return;
        }
        final double dx = mousePoint.getX() - this.lastMouseLocation.getX();
        final double dy = mousePoint.getY() - this.lastMouseLocation.getY();
        this.lastMouseLocation = newMouseLocation;
        for (final INode aSelectedNode : selectedNodes)
        {
            UndoableEdit edit = new AbstractUndoableEdit()
            {
                @Override
                public void undo() throws CannotUndoException
                {
                    aSelectedNode.translate(-dx, -dy);
                    super.undo();
                }

                @Override
                public void redo() throws CannotRedoException
                {
                    super.redo();
                    aSelectedNode.translate(dx, dy);
                }
            };
            capturedEdit.addEdit(edit);
        }
    }

    @Override
    public void onMouseReleased(MouseEvent event)
    {
        stopHistoryCapture();
        this.lastMouseLocation = null;
        this.isReadyForDragging = false;
    }

    @Override
    public void beforeRemovingSelectedElements()
    {
        startHistoryCapture();
        CompoundEdit capturedEdit = getCurrentCapturedEdit();
        List<INode> selectedNodes = this.selectionHandler.getSelectedNodes();
        List<IEdge> selectedEdges = this.selectionHandler.getSelectedEdges();
        for (final IEdge aSelectedEdge : selectedEdges)
        {
            UndoableEdit edit = new AbstractUndoableEdit()
            {
                @Override
                public void undo() throws CannotUndoException
                {
                    IGraph graph = editorPart.getGraph();
                    graph.connect(aSelectedEdge, aSelectedEdge.getStart(), aSelectedEdge.getEnd());
                    super.undo();
                }

                @Override
                public void redo() throws CannotRedoException
                {
                    super.redo();
                    IGraph graph = editorPart.getGraph();
                    graph.removeEdge(aSelectedEdge);
                }
            };
            capturedEdit.addEdit(edit);
        }
        for (final INode aSelectedNode : selectedNodes)
        {
            UndoableEdit edit = new AbstractUndoableEdit()
            {
                @Override
                public void undo() throws CannotUndoException
                {
                    IGraph graph = editorPart.getGraph();
                    graph.addNode(aSelectedNode, aSelectedNode.getLocation());
                    super.undo();
                }

                @Override
                public void redo() throws CannotRedoException
                {
                    super.redo();
                    IGraph graph = editorPart.getGraph();
                    graph.removeNode(aSelectedNode);
                }
            };
            capturedEdit.addEdit(edit);
        }
        stopHistoryCapture();
    }

    @Override
    public void afterAddingNodeAtPoint(final INode node, final Point2D location)
    {
        startHistoryCapture();
        CompoundEdit capturedEdit = getCurrentCapturedEdit();
        UndoableEdit edit = new AbstractUndoableEdit()
        {
            @Override
            public void undo() throws CannotUndoException
            {
                IGraph graph = editorPart.getGraph();
                graph.removeNode(node);
                super.undo();
            }

            @Override
            public void redo() throws CannotRedoException
            {
                super.redo();
                IGraph graph = editorPart.getGraph();
                graph.addNode(node, location);
            }
        };
        capturedEdit.addEdit(edit);
        stopHistoryCapture();
    }

    @Override
    public void afterAddingEdgeAtPoints(final IEdge edge, final Point2D startPoint, final Point2D endPoint)
    {
        startHistoryCapture();
        CompoundEdit capturedEdit = getCurrentCapturedEdit();
        UndoableEdit edit = new AbstractUndoableEdit()
        {
            @Override
            public void undo() throws CannotUndoException
            {
                IGraph graph = editorPart.getGraph();
                graph.removeEdge(edge);
                super.undo();
            }

            @Override
            public void redo() throws CannotRedoException
            {
                super.redo();
                IGraph graph = editorPart.getGraph();
                graph.addEdgeAtPoints(edge, startPoint, endPoint);
            }
        };
        capturedEdit.addEdit(edit);
        stopHistoryCapture();
    }

    @Override
    public void beforeEditingNode(INode node)
    {
        startHistoryCapture();
    }

    @Override
    public void whileEditingNode(INode node, PropertyChangeEvent event)
    {
        capturePropertyChanges(event);
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
    public void whileEditingEdge(IEdge edge, final PropertyChangeEvent event)
    {
        capturePropertyChanges(event);
    }

    @Override
    public void afterEditingEdge(IEdge edge)
    {
        stopHistoryCapture();
    }

    private void capturePropertyChanges(final PropertyChangeEvent event)
    {
        CompoundEdit capturedEdit = getCurrentCapturedEdit();
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
                PropertyChangeEvent invertedEvent = new PropertyChangeEvent(event.getSource(), event.getPropertyName(), event.getNewValue(), event.getOldValue());
                IGraph graph = editorPart.getGraph();
                changeNodeOrEdgeProperty(invertedEvent);
            }

            @Override
            public void redo() throws CannotRedoException
            {
                IGraph graph = editorPart.getGraph();
                changeNodeOrEdgeProperty(event);
            }
            
            private void changeNodeOrEdgeProperty(PropertyChangeEvent e)
            {
                PropertyUtils.setProperty(e.getSource(), e.getPropertyName(), e.getNewValue());
            }
        };
        capturedEdit.addEdit(edit);
    }

    /**
     * Restores previous graph action from the history cursor location
     */
    public void undo()
    {
        if (undoManager.canUndo())
        {
            undoManager.undo();
            editorPart.getAWTComponent().repaint();
        }
    }

    /**
     * Restores next graph action from the history cursor location
     */
    public void redo()
    {
        if (undoManager.canRedo())
        {
            undoManager.redo();
            editorPart.getAWTComponent().repaint();
        }
    }

    /**
     * Starts capturing actions on graph
     */
    private void startHistoryCapture()
    {
        if (this.currentCapturedEdit == null)
        {
            this.currentCapturedEdit = new CompoundEdit();
        }
    }

    /**
     * @return current composed undoable edit
     */
    private CompoundEdit getCurrentCapturedEdit()
    {
        return this.currentCapturedEdit;
    }

    /**
     * Stops capturing actions on graph and adds an entry to history
     */
    private void stopHistoryCapture()
    {
        if (this.currentCapturedEdit == null) return;
        this.currentCapturedEdit.end();
        this.undoManager.addEdit(this.currentCapturedEdit);
        this.currentCapturedEdit = null;
    }

}
