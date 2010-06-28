package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphTool;

public class SwingRepaintingBehavior implements IEditorPartBehavior
{

    private IEditorPart editorPart;
    
    
    public SwingRepaintingBehavior(IEditorPart editorPart)
    {
        this.editorPart = editorPart;
    }

    @Override
    public void onToolSelected(GraphTool selectedTool)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void onMouseReleased(MouseEvent event)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void onMouseDragged(MouseEvent event)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void beforeRemovingSelectedElements()
    {
        // Nothing to do
    }

    @Override
    public void beforeEditingNode(INode node)
    {
        // nothing to do
    }

    @Override
    public void beforeEditingEdge(IEdge edge)
    {
        // nothing to do
    }

    @Override
    public void beforeAddingNodeAtPoint(INode node, Point2D location)
    {
        // nothing to do
    }

    @Override
    public void beforeAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint)
    {
        // nothing to do
    }

    @Override
    public void afterRemovingSelectedElements()
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void afterEditingNode(INode node)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void afterEditingEdge(IEdge edge)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void afterAddingNodeAtPoint(INode node, Point2D location)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void afterAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }
    
    @Override
    public void onPaint(Graphics2D g2)
    {
        // nothing to do
        
    }

    @Override
    public void onEdgeSelected(IEdge edge)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void onNodeSelected(INode node)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void whileEditingEdge(IEdge edge, PropertyChangeEvent event)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

    @Override
    public void whileEditingNode(INode node, PropertyChangeEvent event)
    {
        this.editorPart.getAWTComponent().doLayout();
        this.editorPart.getAWTComponent().repaint();
    }

   
}
