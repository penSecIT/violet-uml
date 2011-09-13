package com.horstmann.violet.product.workspace.editorpart.behavior;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.horstmann.violet.framework.util.PropertyUtils;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;
import com.horstmann.violet.product.workspace.editorpart.IEditorPart;
import com.horstmann.violet.product.workspace.editorpart.IEditorPartSelectionHandler;

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
     * Keeps node locations before dragging event
     */
    private Map<INode, Point2D> selectNodesLocationsBeforeDrag = new HashMap<INode, Point2D>();
    
    /**
     * Keeps all the nodes attached to the graph before the remove action
     */
    private List<INode> nodesOnGraphBeforeRemove = new ArrayList<INode>();
    
    /**
     * Keeps all the edges attached to the graph before the remove action
     */
    private List<IEdge> edgesOnGraphBeforeRemove = new ArrayList<IEdge>();
    
    /**
     * Used on node's drag'n drop
     */
    private boolean isDragInProgress = false;

    /**
     * Current composed undoable edit
     */
    private CompoundEdit currentCapturedEdit;

    /**
     * Undo/redo manager
     */
    private UndoManager undoManager = new UndoManager();


    public UndoRedoBehavior(IEditorPart editorPart)
    {
        this.editorPart = editorPart;
        this.selectionHandler = editorPart.getSelectionHandler();
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        double zoom = editorPart.getZoomFactor();
        final Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        this.isDragInProgress = false;
        if (isMouseOnNode(mousePoint))
        {
            saveSelectedNodesLocationsBeforeDrag();
        }
    }



    @Override
    public void onMouseDragged(MouseEvent event)
    {
    	this.isDragInProgress = true;
    }

    @Override
    public void onMouseReleased(MouseEvent event)
    {
    	if (!this.isDragInProgress) {
    		return;
    	}
        List<INode> selectedNodes = this.selectionHandler.getSelectedNodes();
        List<UndoableEdit> editList = new ArrayList<UndoableEdit>();
        for (final INode aSelectedNode : selectedNodes)
        {
        	Point2D lastNodeLocation = this.selectNodesLocationsBeforeDrag.get(aSelectedNode);
        	Point2D currentNodeLocation = aSelectedNode.getLocation();
        	if (currentNodeLocation.equals(lastNodeLocation)) {
        		continue;
        	}
        	final double dx = currentNodeLocation.getX() - lastNodeLocation.getX();
        	final double dy = currentNodeLocation.getY() - lastNodeLocation.getY();
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
            editList.add(edit);
        }
        if (editList.size() > 0) {
        	startHistoryCapture();
        	CompoundEdit capturedEdit = getCurrentCapturedEdit();
        	for (UndoableEdit edit : editList) {
        		capturedEdit.addEdit(edit);
        	}
        	stopHistoryCapture();
        }
        this.selectNodesLocationsBeforeDrag.clear();
        this.isDragInProgress = false;
    }

    @Override
    public void beforeRemovingSelectedElements()
    {
        this.nodesOnGraphBeforeRemove.clear();
        this.edgesOnGraphBeforeRemove.clear();
        this.nodesOnGraphBeforeRemove.addAll(this.editorPart.getGraph().getAllNodes());
        this.edgesOnGraphBeforeRemove.addAll(this.editorPart.getGraph().getAllEdges());
    }
    
    @Override
    public void afterRemovingSelectedElements() {
        List<INode> nodesOnGraphAfterAction = new ArrayList<INode>(this.editorPart.getGraph().getAllNodes());
        List<IEdge> edgesOnGraphAfterAction = new ArrayList<IEdge>(this.editorPart.getGraph().getAllEdges());

        List<INode> nodesReallyRemoved = new ArrayList<INode>();
        nodesReallyRemoved.addAll(this.nodesOnGraphBeforeRemove);
        nodesReallyRemoved.removeAll(nodesOnGraphAfterAction);
        
        List<IEdge> edgesReallyRemoved = new ArrayList<IEdge>();
        edgesReallyRemoved.addAll(this.edgesOnGraphBeforeRemove);
        edgesReallyRemoved.removeAll(edgesOnGraphAfterAction);
        
        startHistoryCapture();
        CompoundEdit capturedEdit = getCurrentCapturedEdit();

        for (final IEdge aSelectedEdge : edgesReallyRemoved)
        {
            UndoableEdit edit = new AbstractUndoableEdit()
            {
                @Override
                public void undo() throws CannotUndoException
                {
                    IGraph graph = editorPart.getGraph();
                    graph.connect(aSelectedEdge, aSelectedEdge.getStart(), aSelectedEdge.getStartLocation(), aSelectedEdge.getEnd(), aSelectedEdge.getEndLocation());
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

        List<INode> filteredNodes = removeChildren(nodesReallyRemoved);
        for (final INode aSelectedNode : filteredNodes)
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
        this.nodesOnGraphBeforeRemove.clear();
        this.edgesOnGraphBeforeRemove.clear();
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
                graph.connect(edge, edge.getStart(), edge.getStartLocation(), edge.getEnd(), edge.getEndLocation());
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
    
    /**
     * Saves currently selected node locations
     */
    private void saveSelectedNodesLocationsBeforeDrag() {
    	this.selectNodesLocationsBeforeDrag.clear();
    	List<INode> selectedNodes = this.selectionHandler.getSelectedNodes();
    	for (INode aSelectedNode : selectedNodes) {
    		Point2D location = aSelectedNode.getLocation();
    		this.selectNodesLocationsBeforeDrag.put(aSelectedNode, location);
    	}
    }
    
    /**
     * Checks if ancestorNode is a parent node of child node
     * 
     * @param childNode
     * @param ancestorNode
     * @return b
     */
    private boolean isAncestorRelationship(INode childNode, INode ancestorNode) {
    	INode parent = childNode.getParent();
    	if (parent == null) {
    	    return false;
    	}
    	List<INode> fifo = new ArrayList<INode>();
    	fifo.add(parent);
    	while (!fifo.isEmpty()) {
    		INode aParentNode = fifo.get(0);
    		fifo.remove(0);
    		if (aParentNode.equals(ancestorNode)) {
    			return true;
    		}
    		INode aGranParent = aParentNode.getParent();
    		if (aGranParent != null) {
    			fifo.add(aGranParent);
    		}
    	}
    	return false;
    }
    
    /**
     * Takes a list of nodes and removes from this list all nodes which have ancestors node in this list.<br/>
     * 
     * @param nodes the list to filter
     * @return the filtered list
     */
    private List<INode> removeChildren(List<INode> nodes) {
    	List<INode> result = new ArrayList<INode>();
    	for (INode aNode : nodes) {
    		boolean isOrphelin = true;
    		for (INode aParent : nodes) {
    			boolean isAncestorRelationship = isAncestorRelationship(aNode, aParent);
    			if (isAncestorRelationship) {
    				isOrphelin = false;
    			}
    		}
    		if (isOrphelin) {
    			result.add(aNode);
    		}
    	}
    	return result;
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
   

}
