package com.horstmann.violet.product.diagram.classes;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.horstmann.violet.product.diagram.abstracts.edge.SegmentedLineEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.property.BentStyle;

/**
 * An edge that is shaped like a line with up to three segments with an arrowhead
 */
public class ClassRelationshipEdge extends SegmentedLineEdge
{
    /**
     * Constructs a straight edge.
     */
    public ClassRelationshipEdge()
    {
        bentStyle = BentStyle.STRAIGHT;
    }

    /**
     * Sets the bentStyle property
     * 
     * @param newValue the bent style
     */
    public void setBentStyle(BentStyle newValue)
    {
        bentStyle = newValue;
    }

    /**
     * Gets the bentStyle property
     * 
     * @return the bent style
     */
    public BentStyle getBentStyle()
    {
        return bentStyle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.product.diagram.abstracts.SegmentedLineEdge#getPoints()
     */
    public ArrayList<Point2D> getPoints()
    {
        INode startingNode = getStart();
        Rectangle2D startingNodeBounds = startingNode.getBounds();
        Point2D startingNodeLocationOnGraph = startingNode.getLocationOnGraph();
        Rectangle2D startingNodeBoundsOnGraph = new Rectangle2D.Double(startingNodeLocationOnGraph.getX(),
                startingNodeLocationOnGraph.getY(), startingNodeBounds.getWidth(), startingNodeBounds.getHeight());
        INode endingNode = getEnd();
        Rectangle2D endingNodeBounds = endingNode.getBounds();
        Point2D endingNodeLocationOnGraph = endingNode.getLocationOnGraph();
        Rectangle2D endingNodeBoundsOnGraph = new Rectangle2D.Double(endingNodeLocationOnGraph.getX(),
                endingNodeLocationOnGraph.getY(), endingNodeBounds.getWidth(), endingNodeBounds.getHeight());
        return bentStyle.getPath(startingNodeBoundsOnGraph, endingNodeBoundsOnGraph);
    }

    private BentStyle bentStyle;
}
