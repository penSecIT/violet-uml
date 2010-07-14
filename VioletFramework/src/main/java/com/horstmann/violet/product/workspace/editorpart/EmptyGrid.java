package com.horstmann.violet.product.workspace.editorpart;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class EmptyGrid implements IGrid
{

    @Override
    public void changeGridSize(int steps)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public double getSnappingHeight()
    {
        return 0;
    }

    @Override
    public double getSnappingWidth()
    {
        return 0;
    }

    @Override
    public boolean isVisible()
    {
        return false;
    }

    @Override
    public void paint(Graphics2D g2)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setVisible(boolean isVisible)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public Point2D snap(Point2D p)
    {
        return p;
    }

    @Override
    public Rectangle2D snap(Rectangle2D r)
    {
        return r;
    }

}