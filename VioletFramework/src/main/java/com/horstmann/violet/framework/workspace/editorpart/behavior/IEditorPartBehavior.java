package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphTool;

public interface IEditorPartBehavior
{

    public void onMousePressed(MouseEvent event);
    
    public void onMouseDragged(MouseEvent event);
    
    public void onMouseReleased(MouseEvent event);
    
    public void onToolSelected( GraphTool selectedTool);
    
    public void beforeEditingNode(INode node);
    
    public void afterEditingNode(INode node);
    
    public void beforeEditingEdge(IEdge edge);
    
    public void afterEditingEdge(IEdge edge);
    
    public void beforeRemovingSelectedElements();
    
    public void afterRemovingSelectedElements();
    
    public void beforeAddingNodeAtPoint(INode node, Point2D location);
    
    public void afterAddingNodeAtPoint(INode node, Point2D location);
    
    public void beforeAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint);
    
    public void afterAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint);
    
    public void onPaint(Graphics2D g2);
    
}
