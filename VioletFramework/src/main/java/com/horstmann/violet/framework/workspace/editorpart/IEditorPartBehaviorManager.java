package com.horstmann.violet.framework.workspace.editorpart;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.editorpart.behavior.IEditorPartBehavior;

public interface IEditorPartBehaviorManager
{

    public abstract void addBehavior(IEditorPartBehavior newBehavior);

    public abstract List<IEditorPartBehavior> getBehaviors();

    public abstract void fireOnMousePressed(MouseEvent event);

    public abstract void fireOnMouseDragged(MouseEvent event);

    public abstract void fireOnMouseReleased(MouseEvent event);

    public abstract void fireBeforeEditingNode(INode node);

    public abstract void fireAfterEditingNode(INode node);

    public abstract void fireBeforeEditingEdge(IEdge edge);

    public abstract void fireAfterEditingEdge(IEdge edge);

    public abstract void fireBeforeRemovingSelectedElements();

    public abstract void fireAfterRemovingSelectedElements();

    public abstract void fireBeforeAddingNodeAtPoint(INode node, Point2D location);

    public abstract void fireAfterAddingNodeAtPoint(INode node, Point2D location);

    public abstract void fireBeforeAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint);

    public abstract void fireAfterAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint);

}