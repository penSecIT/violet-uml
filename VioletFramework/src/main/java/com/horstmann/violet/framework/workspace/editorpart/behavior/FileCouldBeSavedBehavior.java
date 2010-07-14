package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.file.IGraphFile;

public class FileCouldBeSavedBehavior extends AbstractEditorPartBehavior
{

    public FileCouldBeSavedBehavior(IGraphFile graphFile)
    {
        this.graphFile = graphFile;
    }

    @Override
    public void afterAddingEdgeAtPoints(IEdge edge, Point2D startPoint, Point2D endPoint)
    {
        graphFile.setSaveRequired();
    }

    @Override
    public void afterAddingNodeAtPoint(INode node, Point2D location)
    {
        graphFile.setSaveRequired();
    }

    @Override
    public void afterEditingEdge(IEdge edge)
    {
        graphFile.setSaveRequired();
    }

    @Override
    public void afterEditingNode(INode node)
    {
        graphFile.setSaveRequired();
    }

    @Override
    public void afterRemovingSelectedElements()
    {
        graphFile.setSaveRequired();
    }


    @Override
    public void onMouseDragged(MouseEvent event)
    {
        graphFile.setSaveRequired();
        // FIXME : when behaviorHandler will manage new events such as onNodeDragged
    }

    private IGraphFile graphFile;
    
}
