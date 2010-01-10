/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.product.diagram.common;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.diagram.node.RectangularNode;
import com.horstmann.violet.framework.diagram.property.MultiLineString;
import com.horstmann.violet.framework.workspace.editorpart.IGrid;

/**
 * A node in a diagram represented by an image
 */
public class ImageNode extends RectangularNode
{
    /**
     * Default construct a note node with a default size and color
     */
    public ImageNode(Image img)
    {
        text = new MultiLineString();
        text.setJustification(MultiLineString.RIGHT);
        this.setImage(img);
    }

    /**
     * For internal use only.
     */
    public ImageNode()
    {
        text = new MultiLineString();
        text.setJustification(MultiLineString.RIGHT);
    }

    /**
     * Sets current image
     * 
     * @param img
     */
    public void setImage(Image img)
    {
        this.imageIcon = new ImageIcon(img);
        setBounds(new Rectangle2D.Double(0, 0, this.imageIcon.getIconWidth(), this.imageIcon.getIconHeight()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.AbstractNode#checkRemoveEdge(com.horstmann.violet.framework.diagram.Edge)
     */
    public void checkRemoveEdge(IEdge e)
    {
        if (e.getStart() == this) getGraph().removeNodesAndEdges(Arrays.asList(e.getEnd()), null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.RectangularNode#layout(java.awt.Graphics2D,
     *      com.horstmann.violet.framework.diagram.Grid)
     */
    @Override
    public void layout(Graphics2D g2, IGrid grid)
    {
        // Make sure that the end point has a high enough z to be selectable
        for (IEdge e : getGraph().getEdges())
        {
            if (e.getStart() == this)
            {
                INode end = e.getEnd();
                Point2D endPoint = end.getLocation();
                INode n = getGraph().findNode(endPoint);
                if (n != null && n != end) end.setZ(n.getZ() + 1);
            }
        }
        Rectangle2D b = text.getBounds(g2);
        snapBounds(grid, Math.max(b.getWidth(), this.imageIcon.getIconWidth()), b.getHeight() + this.imageIcon.getIconHeight());
    }

    /**
     * Gets the value of the text property.
     * 
     * @return the text inside the note
     */
    public MultiLineString getText()
    {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param newValue the text inside the note
     */
    public void setText(MultiLineString newValue)
    {
        text = newValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.AbstractNode#draw(java.awt.Graphics2D)
     */
    public void draw(Graphics2D g2)
    {
        Rectangle2D bounds = getBounds();
        g2.drawImage(this.imageIcon.getImage(), (int) bounds.getCenterX() - this.imageIcon.getIconWidth() / 2, (int) bounds.getY(),
                this.imageIcon.getImageObserver());
        Rectangle2D b = text.getBounds(g2);
        Rectangle2D textBounds = new Rectangle2D.Double(bounds.getX(), bounds.getY() + this.imageIcon.getIconHeight(),
                b.getWidth(), b.getHeight());
        text.draw(g2, textBounds);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.RectangularNode#getShape()
     */
    public Shape getShape()
    {
        Rectangle2D bounds = getBounds();
        GeneralPath path = new GeneralPath();
        path.moveTo((float) bounds.getX(), (float) bounds.getY());
        path.lineTo((float) bounds.getMaxX(), (float) bounds.getY());
        path.lineTo((float) bounds.getMaxX(), (float) bounds.getY());
        path.lineTo((float) bounds.getMaxX(), (float) bounds.getMaxY());
        path.lineTo((float) bounds.getX(), (float) bounds.getMaxY());
        path.closePath();
        return path;
    }

    /**
     * This method should be kept as private as long as it is used for serialization purpose
     * 
     * @return image content as an array
     * @throws InterruptedException
     */
    public String getImageContent() throws InterruptedException
    {
        Image img = this.imageIcon.getImage();
        int width = this.imageIcon.getIconWidth();
        int height = this.imageIcon.getIconHeight();
        int[] pixels = new int[width * height];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
        pg.grabPixels();
        StringBuilder result = new StringBuilder();
        for (int i : pixels)
        {
            result.append(i).append(PIXEL_SEPARATOR);
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }
    
    /**
     * @return current image width
     */
    public int getImageWidth() {
        return this.imageIcon.getIconWidth();
    }

    /**
     * @return current image height
     */
    public int getImageHeight() {
        return this.imageIcon.getIconHeight();
    }
    

    /**
     * This method should be kept as private as long as it is used for serialization purpose. Replaces current imageIcon by a new
     * one created with the image content guven is parameters
     * 
     * @param pixels image content
     * @param width image width
     * @param height image height
     */
    @SuppressWarnings("unused")
    public void setImageContent(String imageContent, int width, int height)
    {
        StringTokenizer tokenizer = new StringTokenizer(imageContent, PIXEL_SEPARATOR);
        int[] pixels = new int[tokenizer.countTokens()];
        int counter = 0;
        while (tokenizer.hasMoreTokens())
        {
            String aPixel = tokenizer.nextToken();
            pixels[counter] = Integer.parseInt(aPixel);
            counter++;
        }
        MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0, width);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = tk.createImage(mis);
        this.setImage(img);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.diagram.RectangularNode#clone()
     */
    public ImageNode clone()
    {
        ImageNode cloned = (ImageNode) super.clone();
        cloned.text = text.clone();
        cloned.imageIcon = imageIcon;
        return cloned;
    }

    

    private MultiLineString text;
    private ImageIcon imageIcon;
    private static final String PIXEL_SEPARATOR = ":";

}