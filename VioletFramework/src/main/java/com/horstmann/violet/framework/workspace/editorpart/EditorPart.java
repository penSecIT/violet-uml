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

package com.horstmann.violet.framework.workspace.editorpart;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.spring.SpringDependencyInjector;
import com.horstmann.violet.framework.workspace.editorpart.behavior.IEditorPartBehavior;

/**
 * Graph editor
 */
public class EditorPart extends JPanel implements IEditorPart
{

    /**
     * Default constructor
     * 
     * @param aGraph graph which will be drawn in this editor part
     */
    public EditorPart(IGraph aGraph)
    {
        SpringDependencyInjector.getInjector().inject(this);
        this.graph = aGraph;
        this.zoom = 1;
        this.grid = new PlainGrid(this);
        

        addMouseListener(new MouseAdapter()
        {

            public void mousePressed(MouseEvent event)
            {
                behaviorManager.fireOnMousePressed(event);
            }

            public void mouseReleased(MouseEvent event)
            {
                behaviorManager.fireOnMouseReleased(event);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseDragged(MouseEvent event)
            {
                behaviorManager.fireOnMouseDragged(event);
            }
        });


        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.IEditorPart#getGraph()
     */
    public IGraph getGraph()
    {
        return this.graph;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.IEditorPart#removeSelected()
     */
    public void removeSelected()
    {
        this.behaviorManager.fireBeforeRemovingSelectedElements();
        try
        {
            graph.removeNodesAndEdges(selectionHandler.getSelectedNodes(), selectionHandler.getSelectedEdges());
        }
        finally
        {
            this.selectionHandler.clearSelection();
            this.behaviorManager.fireAfterRemovingSelectedElements();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.IEditorPart#selectAnotherGraphElement (int)
     */
    public void selectAnotherGraphElement(int distanceFromCurrentElement)
    {
        ArrayList<Object> selectables = new ArrayList<Object>();
        selectables.addAll(graph.getNodes());
        selectables.addAll(graph.getEdges());
        if (selectables.size() == 0) return;
        java.util.Collections.sort(selectables, new java.util.Comparator<Object>()
        {
            public int compare(Object obj1, Object obj2)
            {
                double x1;
                double y1;
                if (obj1 instanceof INode)
                {
                    Rectangle2D bounds = ((INode) obj1).getBounds();
                    x1 = bounds.getX();
                    y1 = bounds.getY();
                }
                else
                {
                    Point2D start = ((IEdge) obj1).getConnectionPoints().getP1();
                    x1 = start.getX();
                    y1 = start.getY();
                }
                double x2;
                double y2;
                if (obj2 instanceof INode)
                {
                    Rectangle2D bounds = ((INode) obj2).getBounds();
                    x2 = bounds.getX();
                    y2 = bounds.getY();
                }
                else
                {
                    Point2D start = ((IEdge) obj2).getConnectionPoints().getP1();
                    x2 = start.getX();
                    y2 = start.getY();
                }
                if (y1 < y2) return -1;
                if (y1 > y2) return 1;
                if (x1 < x2) return -1;
                if (x1 > x2) return 1;
                return 0;
            }
        });
        int index;
        Object lastSelected = null;
        if (selectionHandler.isNodeSelectedAtLeast())
        {
            lastSelected = selectionHandler.getLastSelectedNode();
        }
        if (selectionHandler.isEdgeSelectedAtLeast())
        {
            lastSelected = selectionHandler.getLastSelectedEdge();
        }
        if (lastSelected == null) index = 0;
        else index = selectables.indexOf(lastSelected) + distanceFromCurrentElement;
        while (index < 0)
            index += selectables.size();
        index %= selectables.size();
        Object toSelect = selectables.get(index);
        if (toSelect instanceof INode)
        {
            selectionHandler.setSelectedElement((INode) toSelect);
        }
        if (toSelect instanceof IEdge)
        {
            selectionHandler.setSelectedElement((IEdge) toSelect);
        }
        repaint();
    }



    public List<INode> getSelectedNodes()
    {
        return selectionHandler.getSelectedNodes();
    }

    public void clearSelection()
    {
        selectionHandler.clearSelection();
    }

    public void selectElement(INode node)
    {
        selectionHandler.addSelectedElement(node);
    }

    /*
     * Used by the scrollpane to evaluate if scrollbars are needed
     * 
     * @see javax.swing.JComponent#getPreferredSize()
     */
    public Dimension getPreferredSize()
    {
        Rectangle2D bounds = graph.getClipBounds();
        return new Dimension((int) (zoom * bounds.getMaxX()), (int) (zoom * bounds.getMaxY()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.IEditorPart#changeZoom(int)
     */
    public void changeZoom(int steps)
    {
        final double FACTOR = Math.sqrt(Math.sqrt(2));
        for (int i = 1; i <= steps; i++)
            zoom *= FACTOR;
        for (int i = 1; i <= -steps; i++)
            zoom /= FACTOR;
        repaint();
    }

    @Override
    public double getZoomFactor()
    {
        return this.zoom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.workspace.editorpart.IEditorPart#getGrid()
     */
    public IGrid getGrid()
    {
        return this.grid;
    }



    /*
     * (non-Javadoc)
     * 
     * @seecom.horstmann.violet.framework.workspace.editorpart.IEditorPart# growDrawingArea()
     */
    public void growDrawingArea()
    {
        IGraph g = getGraph();
        Rectangle2D bounds = g.getClipBounds();
        bounds.add(getBounds());
        g.setBounds(new Double(0, 0, GROW_SCALE_FACTOR * bounds.getWidth(), GROW_SCALE_FACTOR * bounds.getHeight()));
        repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.horstmann.violet.framework.workspace.editorpart.IEditorPart# clipDrawingArea()
     */
    public void clipDrawingArea()
    {
        IGraph g = getGraph();
        g.setBounds(null);
        repaint();
    }

    public Component getAWTComponent()
    {
        return this;
    }

    @Override
    public void doLayout()
    {
        if (graph != null && grid != null)
        {
            graph.layout((Graphics2D) getGraphics(), grid);
        }
        EditorPart.super.doLayout();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(zoom, zoom);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (grid.isVisible()) grid.paint(g2);
        graph.draw(g2);
        for (IEditorPartBehavior behavior : this.behaviorManager.getBehaviors()) {
            behavior.onPaint(g2);
        }
    }
    

    @Override
    public IEditorPartSelectionHandler getSelectionHandler()
    {
        return this.selectionHandler;
    }
    
    @Override
    public IEditorPartBehaviorManager getBehaviorManager()
    {
        return this.behaviorManager;
    }

    private IGraph graph;
    
    private IGrid grid;

    private double zoom;

    private IEditorPartSelectionHandler selectionHandler = new EditorPartSelectionHandler();

    /**
     * Scale factor used to grow drawing area
     */
    private static final double GROW_SCALE_FACTOR = Math.sqrt(2);


    private IEditorPartBehaviorManager behaviorManager = new EditorPartBehaviorManager();

}