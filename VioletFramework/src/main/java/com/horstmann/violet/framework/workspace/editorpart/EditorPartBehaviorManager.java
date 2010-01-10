package com.horstmann.violet.framework.workspace.editorpart;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.editorpart.behavior.IEditorPartBehavior;

public class EditorPartBehaviorManager
{

    
    private List<IEditorPartBehavior> behaviors = new ArrayList<IEditorPartBehavior>();
    
    public void addBehavior(IEditorPartBehavior newBehavior) {
        this.behaviors.add(newBehavior);
    }
    
    public List<IEditorPartBehavior> getBehaviors() {
        return  this.behaviors;
    }
    
    public void fireOnMousePressed(MouseEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onMousePressed(event);
    }
    
    public void fireOnMouseDragged(MouseEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onMouseDragged(event);
    }
    
    public void fireOnMouseReleased(MouseEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onMouseReleased(event);
    }
    
    public void fireOnElementsDragged(List<INode> concernedNodes, List<IEdge> concernedEdges) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onElementsDragged(concernedNodes, concernedEdges);
    }
    
    public void fireOnElementsDropped(List<INode> concernedNodes, List<IEdge> concernedEdges) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onElementsDropped(concernedNodes, concernedEdges);
    }
    
    public void fireBeforeEditingNode(INode node) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeEditingNode(node);
    }
    
    public void fireAfterEditingNode(INode node) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterEditingNode(node);
    }
    public void fireBeforeEditingEdge(IEdge edge) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeEditingEdge(edge);
    }
    
    public void fireAfterEditingEdge(IEdge edge) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterEditingEdge(edge);
    }
    
    public void fireBeforeRemovingSelectedElements() {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeRemovingSelectedElements();
    }
    
    public void fireAfterRemovingSelectedElements() {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterRemovingSelectedElements();
    }
    
    public void fireBeforeAddingNodeAtPoint(INode node, Point2D location) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeAddingNodeAtPoint(node, location);
    }
    
    public void fireAfterAddingNodeAtPoint(INode node, Point2D location) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterAddingNodeAtPoint(node, location);
    }
    
    public void fireBeforeAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeAddingEdgeAtPoints(edge, startPoint, endPoint);
    }
    
    public void fireAfterAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterAddingEdgeAtPoints(edge, startPoint, endPoint);
    }
    
}
