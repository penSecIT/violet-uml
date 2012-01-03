package com.horstmann.violet.product.diagram.classes;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.xml.datatype.Duration;

import com.horstmann.violet.product.diagram.abstracts.Direction;
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

    @Override
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
    
    
    @Override
    public Direction getDirection(INode node)
    {
        Direction straightDirection = super.getDirection(node);
        double x = straightDirection.getX();
        double y = straightDirection.getY();
        if (node.equals(getStart())) {
            if (BentStyle.HV.equals(bentStyle) || BentStyle.HVH.equals(bentStyle)) {
                return (x >= 0) ? Direction.EAST : Direction.WEST;
            }
            if (BentStyle.VH.equals(bentStyle) || BentStyle.VHV.equals(bentStyle)) {
                return (y >= 0) ? Direction.SOUTH : Direction.NORTH;
            }
        }
        if (node.equals(getEnd())) {
            if (BentStyle.HV.equals(bentStyle) || BentStyle.VHV.equals(bentStyle)) {
                return (y >= 0) ? Direction.SOUTH : Direction.NORTH;
            }
            if (BentStyle.VH.equals(bentStyle) || BentStyle.HVH.equals(bentStyle)) {
                return (x >= 0) ? Direction.EAST : Direction.WEST;
            }
        }
        return straightDirection;
    }

    private BentStyle bentStyle;
}
