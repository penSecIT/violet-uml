package com.horstmann.violet.product.diagram.classes;


import java.awt.geom.Point2D;
import java.util.ArrayList;

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
        return bentStyle.getPath(getStart().getBounds(), getEnd().getBounds());
    }

    private BentStyle bentStyle;
}
