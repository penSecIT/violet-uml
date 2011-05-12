package com.horstmann.violet.product.diagram.classes;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.horstmann.violet.product.diagram.abstracts.Direction;
import com.horstmann.violet.product.diagram.abstracts.edge.SegmentedLineEdge;
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
        Point2D startingPoint = getStart().getConnectionPoint(this);
        Point2D endingPoint = getEnd().getConnectionPoint(this);
        if (!BentStyle.AUTO.equals(bentStyle)) {
            return bentStyle.getPath(startingPoint, endingPoint);
        }
        
        Direction startingCardinalDirection = getDirection(getStart()).getNearestCardinalDirection();
        Direction endingCardinalDirection = getDirection(getEnd()).getNearestCardinalDirection();
        if ((Direction.NORTH.equals(startingCardinalDirection) || Direction.SOUTH.equals(startingCardinalDirection)) && (Direction.NORTH.equals(endingCardinalDirection) || Direction.SOUTH.equals(endingCardinalDirection))) {
            return BentStyle.VHV.getPath(startingPoint, endingPoint);
        }
        if ((Direction.NORTH.equals(startingCardinalDirection) || Direction.SOUTH.equals(startingCardinalDirection)) && (Direction.EAST.equals(endingCardinalDirection) || Direction.WEST.equals(endingCardinalDirection))) {
            return BentStyle.VH.getPath(startingPoint, endingPoint);
        }
        if ((Direction.EAST.equals(startingCardinalDirection) || Direction.WEST.equals(startingCardinalDirection)) && (Direction.NORTH.equals(endingCardinalDirection) || Direction.SOUTH.equals(endingCardinalDirection))) {
            return BentStyle.HV.getPath(startingPoint, endingPoint);
        }
        if ((Direction.EAST.equals(startingCardinalDirection) || Direction.WEST.equals(startingCardinalDirection)) && (Direction.EAST.equals(endingCardinalDirection) || Direction.WEST.equals(endingCardinalDirection))) {
            return BentStyle.HVH.getPath(startingPoint, endingPoint);
        }
        return BentStyle.STRAIGHT.getPath(startingPoint, endingPoint);
    }
    

    private BentStyle bentStyle;
}
