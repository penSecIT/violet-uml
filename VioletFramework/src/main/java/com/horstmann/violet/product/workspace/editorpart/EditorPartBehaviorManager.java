package com.horstmann.violet.product.workspace.editorpart;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.workspace.editorpart.behavior.IEditorPartBehavior;

public class EditorPartBehaviorManager implements IEditorPartBehaviorManager
{

    
    private List<IEditorPartBehavior> behaviors = new ArrayList<IEditorPartBehavior>();
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#addBehavior(com.horstmann.violet.product.workspace.editorpart.behavior.IEditorPartBehavior)
     */
    public void addBehavior(IEditorPartBehavior newBehavior) {
        this.behaviors.add(newBehavior);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#getBehaviors()
     */
    public List<IEditorPartBehavior> getBehaviors() {
        return  this.behaviors;
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#getBehaviors(java.lang.Class)
     */
    @Override
    public <T extends IEditorPartBehavior> List<T> getBehaviors(Class<T> type)
    {
        List<T> result = new ArrayList<T>();
        for (IEditorPartBehavior aBehavior : this.behaviors) {
            if (aBehavior.getClass().isAssignableFrom(type)) {
                result.add((T) aBehavior);
            }
        }
        return result;
    }
    
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireOnMousePressed(java.awt.event.MouseEvent)
     */
    public void fireOnMousePressed(MouseEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onMousePressed(event);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireOnMouseDragged(java.awt.event.MouseEvent)
     */
    public void fireOnMouseDragged(MouseEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onMouseDragged(event);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireOnMouseReleased(java.awt.event.MouseEvent)
     */
    public void fireOnMouseReleased(MouseEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onMouseReleased(event);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireOnMouseMoved(java.awt.event.MouseEvent)
     */
    public void fireOnMouseMoved(MouseEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onMouseMoved(event);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireBeforeEditingNode(com.horstmann.violet.product.diagram.abstracts.node.INode)
     */
    public void fireBeforeEditingNode(INode node) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeEditingNode(node);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireWhileEditingNode(com.horstmann.violet.product.diagram.abstracts.node.INode)
     */
    public void fireWhileEditingNode(INode node, PropertyChangeEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.whileEditingNode(node, event);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireAfterEditingNode(com.horstmann.violet.product.diagram.abstracts.node.INode)
     */
    public void fireAfterEditingNode(INode node) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterEditingNode(node);
    }
    
    public void fireBeforeEditingEdge(IEdge edge) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeEditingEdge(edge);
    }
    

    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireWhileEditingEdge(com.horstmann.violet.product.diagram.abstracts.edge.IEdge)
     */
    public void fireWhileEditingEdge(IEdge edge, PropertyChangeEvent event) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.whileEditingEdge(edge, event);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireAfterEditingEdge(com.horstmann.violet.product.diagram.abstracts.edge.IEdge)
     */
    public void fireAfterEditingEdge(IEdge edge) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterEditingEdge(edge);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireBeforeRemovingSelectedElements()
     */
    public void fireBeforeRemovingSelectedElements() {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeRemovingSelectedElements();
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireAfterRemovingSelectedElements()
     */
    public void fireAfterRemovingSelectedElements() {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterRemovingSelectedElements();
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireBeforeAddingNodeAtPoint(com.horstmann.violet.product.diagram.abstracts.node.INode, java.awt.geom.Point2D)
     */
    public void fireBeforeAddingNodeAtPoint(INode node, Point2D location) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeAddingNodeAtPoint(node, location);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireAfterAddingNodeAtPoint(com.horstmann.violet.product.diagram.abstracts.node.INode, java.awt.geom.Point2D)
     */
    public void fireAfterAddingNodeAtPoint(INode node, Point2D location) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterAddingNodeAtPoint(node, location);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireBeforeAddingEdgeAtPoints(com.horstmann.violet.product.diagram.abstracts.edge.IEdge, java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    public void fireBeforeAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.beforeAddingEdgeAtPoints(edge, startPoint, endPoint);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireAfterAddingEdgeAtPoints(com.horstmann.violet.product.diagram.abstracts.edge.IEdge, java.awt.geom.Point2D, java.awt.geom.Point2D)
     */
    public void fireAfterAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.afterAddingEdgeAtPoints(edge, startPoint, endPoint);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireOnEdgeSelected(com.horstmann.violet.product.diagram.abstracts.edge.IEdge)
     */
    public void fireOnEdgeSelected(IEdge edge) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onEdgeSelected(edge);
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager#fireOnNodeSelected(com.horstmann.violet.product.diagram.abstracts.node.INode)
     */
    public void fireOnNodeSelected(INode node) {
        for (IEditorPartBehavior aBehavior : this.behaviors) aBehavior.onNodeSelected(node);
    }
    
}
