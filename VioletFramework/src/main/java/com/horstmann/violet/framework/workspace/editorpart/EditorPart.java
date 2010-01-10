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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.dialog.DialogFactory;
import com.horstmann.violet.framework.propertyeditor.CustomPropertyEditor;
import com.horstmann.violet.framework.propertyeditor.ICustomPropertyEditor;
import com.horstmann.violet.framework.resources.ResourceBundleConstant;
import com.horstmann.violet.framework.spring.SpringDependencyInjector;
import com.horstmann.violet.framework.spring.annotation.SpringBean;
import com.horstmann.violet.framework.util.GrabberUtils;
import com.horstmann.violet.framework.workspace.Workspace;
import com.horstmann.violet.framework.workspace.editorpart.behavior.IEditorPartBehavior;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphTool;
import com.horstmann.violet.product.diagram.common.DiagramLink;
import com.horstmann.violet.product.diagram.common.DiagramLinkNode;

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
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter()
        {

            public void mousePressed(MouseEvent event)
            {
                requestFocus();
                final Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
                boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                INode targetNode = graph.findNode(mousePoint);
                IEdge targetEdge = graph.findEdge(mousePoint);
                boolean isButton1Clicked = (event.getModifiers() & InputEvent.BUTTON1_MASK) == 0;
                if (event.getClickCount() > 1 || isButton1Clicked)
                {
                    if (targetEdge != null)
                    {
                        selectionHandler.setSelectedElement(targetEdge);
                        editSelected();
                    }
                    else if (targetNode != null)
                    {
                        selectionHandler.setSelectedElement(targetNode);
                        editSelected();
                    }
                }
                if (event.getClickCount() == 1 && GraphTool.SELECTION_TOOL.equals(selectedTool)) // select
                {
                    if (targetEdge != null)
                    {
                        selectionHandler.setSelectedElement(targetEdge);
                    }
                    else if (targetNode != null)
                    {
                        if (isCtrl) selectionHandler.addSelectedElement(targetNode);
                        else if (!selectionHandler.isElementAlreadySelected(targetNode)) selectionHandler.setSelectedElement(targetNode);
                        dragMode = EditorPartMouseDragModeEnum.DRAG_MOVE;
                    }
                    else
                    {
                        if (!isCtrl) selectionHandler.clearSelection();
                        dragMode = EditorPartMouseDragModeEnum.DRAG_LASSO;
                    }
                }
                if (event.getClickCount() == 1 && !GraphTool.SELECTION_TOOL.equals(selectedTool) && INode.class.isInstance(selectedTool.getNodeOrEdge()))
                {
                    INode prototype = (INode) selectedTool.getNodeOrEdge();
                    INode newNode = (INode) prototype.clone(); // FileExportService.cloneNode(prototype);
                    boolean added = addNodeAtPoint(newNode, mousePoint);
                    if (added)
                    {
                        selectionHandler.setSelectedElement(newNode);
                        dragMode = EditorPartMouseDragModeEnum.DRAG_MOVE;
                    }
                    else if (targetNode != null)
                    {
                        if (isCtrl) selectionHandler.addSelectedElement(targetNode);
                        else if (!selectionHandler.isElementAlreadySelected(targetNode)) selectionHandler.setSelectedElement(targetNode);
                        dragMode = EditorPartMouseDragModeEnum.DRAG_MOVE;
                    }
                }
                if (event.getClickCount() == 1 && !GraphTool.SELECTION_TOOL.equals(selectedTool) && IEdge.class.isInstance(selectedTool.getNodeOrEdge()))
                {
                    if (targetNode != null) dragMode = EditorPartMouseDragModeEnum.DRAG_RUBBERBAND;
                }

                lastMousePoint = mousePoint;
                mouseDownPoint = mousePoint;
                behaviorManager.fireOnMousePressed(event);
                repaint();
            }

            public void mouseReleased(MouseEvent event)
            {
                Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
                if (dragMode.equals(EditorPartMouseDragModeEnum.DRAG_RUBBERBAND))
                {
                    IEdge prototype = (IEdge) selectedTool.getNodeOrEdge();
                    IEdge newEdge = (IEdge) prototype.clone();
                    boolean added = addEdgeAtPoints(newEdge, mouseDownPoint, mousePoint);
                    if (added)
                    {
                        selectionHandler.setSelectedElement(newEdge);
                    }
                }
                if (dragMode.equals(EditorPartMouseDragModeEnum.DRAG_MOVE)) {
                    behaviorManager.fireOnElementsDropped(selectionHandler.getSelectedNodes(), selectionHandler.getSelectedEdges());
                }
                dragMode = EditorPartMouseDragModeEnum.DRAG_NONE;
                behaviorManager.fireOnMouseReleased(event);
                revalidate();
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseDragged(MouseEvent event)
            {
                Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
                if (dragMode.equals(EditorPartMouseDragModeEnum.DRAG_MOVE) && selectionHandler.isNodeSelectedAtLeast())
                {
                    behaviorManager.fireOnElementsDragged(selectionHandler.getSelectedNodes(), selectionHandler.getSelectedEdges());

                    INode lastNode = selectionHandler.getLastSelectedNode();
                    Rectangle2D bounds = lastNode.getBounds();
                    double dx = mousePoint.getX() - lastMousePoint.getX();
                    double dy = mousePoint.getY() - lastMousePoint.getY();

                    // we don't want to drag nodes into negative coordinates
                    // particularly with multiple selection, we might never be
                    // able to get them back.
                    List<INode> selectedNodes = selectionHandler.getSelectedNodes();
                    for (INode n : selectedNodes)
                        bounds.add(n.getBounds());
                    dx = Math.max(dx, -bounds.getX());
                    dy = Math.max(dy, -bounds.getY());

                    for (INode n : selectedNodes)
                    {
                        if (!selectedNodes.contains(n.getParent())) // parents are responsible for translating their children
                        n.translate(dx, dy);
                    }
                }
                else if (dragMode.equals(EditorPartMouseDragModeEnum.DRAG_LASSO))
                {
                    boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                    double x1 = mouseDownPoint.getX();
                    double y1 = mouseDownPoint.getY();
                    double x2 = mousePoint.getX();
                    double y2 = mousePoint.getY();
                    Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math
                            .abs(y1 - y2));
                    Iterator<INode> iter = graph.getNodes().iterator();
                    while (iter.hasNext())
                    {
                        INode n = (INode) iter.next();
                        Rectangle2D bounds = n.getBounds();
                        if (!isCtrl && !lasso.contains(bounds))
                        {
                            selectionHandler.removeElementFromSelection(n);
                        }
                        else if (lasso.contains(bounds))
                        {
                            selectionHandler.addSelectedElement(n);
                        }
                    }
                }
                behaviorManager.fireOnMouseDragged(event);
                lastMousePoint = mousePoint;
                repaint();
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
     * @see com.horstmann.violet.framework.gui.IEditorPart#setSelectedTool(com.horstmann.violet.framework.gui.sidebar.graphtools.GraphTool)
     */
    public void setSelectedTool(GraphTool tool)
    {
        this.selectedTool = tool;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.horstmann.violet.framework.gui.IEditorPart#editSelected()
     */
    public void editSelected()
    {
        final Object edited = selectionHandler.isNodeSelectedAtLeast() ? selectionHandler.getLastSelectedNode() : selectionHandler.getLastSelectedEdge();
        if (edited == null)
        {
            return;
        }
        final ICustomPropertyEditor sheet = new CustomPropertyEditor(edited);

        sheet.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(final PropertyChangeEvent event)
            {
                if (event.getSource() instanceof DiagramLinkNode)
                {
                    DiagramLinkNode ln = (DiagramLinkNode) event.getSource();
                    DiagramLink dl = ln.getDiagramLink();
                    if (dl != null && dl.getOpenFlag().booleanValue())
                    {
                        diagramPanel.fireMustOpenFile(dl.getFile());
                        dl.setOpenFlag(new Boolean(false));
                    }
                }

                graph.changeNodeOrEdgeProperty(event);
                graph.layout((Graphics2D) getGraphics(), grid);
                repaint();
            }
        });

        JOptionPane optionPane = new JOptionPane();
        optionPane.setOpaque(false);
        optionPane.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if ((event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && event.getNewValue() != null
                        && event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE)
                {
                    if (sheet.isEditable())
                    {
                        // This manages optionPane submits through a property listener because, as dialog display could be delegated
                        // (to Eclipse for example), host system can work in other threads
                        if (edited instanceof INode) {
                            behaviorManager.fireAfterEditingNode((INode) edited);
                        }
                        if (edited instanceof IEdge) {
                            behaviorManager.fireAfterEditingEdge((IEdge) edited);
                        }
                    }
                }
            }
        });

        if (sheet.isEditable())
        {
            if (edited instanceof INode) {
                this.behaviorManager.fireBeforeEditingNode((INode) edited);
            }
            if (edited instanceof IEdge) {
                this.behaviorManager.fireBeforeEditingEdge((IEdge) edited);
            }
            optionPane.setMessage(sheet.getAWTComponent());
        }
        if (!sheet.isEditable())
        {
            String message = this.resourceBundle.getString("dialog.properties.empty_bean_message");
            JLabel label = new JLabel(message);
            label.setFont(label.getFont().deriveFont(Font.PLAIN));
            optionPane.setMessage(label);
        }
        this.dialogFactory.showDialog(optionPane, this.resourceBundle.getString("dialog.properties.title"), true);

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
            graph.layout((Graphics2D) getGraphics(), grid);
        }
        finally
        {
            this.behaviorManager.fireAfterRemovingSelectedElements();
        }

        selectionHandler.clearSelection();
        repaint();
    }

    /* (non-Javadoc)
     * @see com.horstmann.violet.framework.gui.IEditorPart#selectAnotherGraphElement(int)
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

    /**
     * Adds a new at a precise location
     * @param newNode to be added
     * @param location 
     * @return true if the node has been added
     */
    private boolean addNodeAtPoint(INode newNode, Point2D location)
    {
        boolean isAdded = false;
        this.behaviorManager.fireBeforeAddingNodeAtPoint(newNode, location);
        try
        {
            if (graph.addNode(newNode, location))
            {
                newNode.incrementRevision();
                graph.layout((Graphics2D) getGraphics(), grid);
            }
        }
        finally
        {
            this.behaviorManager.fireAfterAddingNodeAtPoint(newNode, location);
        }
        return isAdded;
    }

    /**
     * Adds an edge at a specific location
     * @param newEdge
     * @param startPoint
     * @param endPoint
     * @return true id the edge has been added
     */
    private boolean addEdgeAtPoints(IEdge newEdge, Point2D startPoint, Point2D endPoint)
    {
        boolean isAdded = false;
        if (startPoint.distance(endPoint) > CONNECT_THRESHOLD)
        {
            this.behaviorManager.fireBeforeAddingEdgeAtPoints(newEdge, startPoint, endPoint);
            try
            {
                if (graph.addEdgeAtPoints(newEdge, startPoint, endPoint))
                {
                    newEdge.incrementRevision();
                    graph.layout((Graphics2D) getGraphics(), grid);
                }
            }
            finally
            {
                this.behaviorManager.fireAfterAddingEdgeAtPoints(newEdge, startPoint, endPoint);
            }
        }
        return isAdded;
    }

    public List<INode> getSelectedNodes() {
        return selectionHandler.getSelectedNodes();
    }

    public void clearSelection() {
        selectionHandler.clearSelection();
    }
    
    public void selectElement(INode node) {
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
        revalidate();
        repaint();
    }


    
    /* (non-Javadoc)
     * @see com.horstmann.violet.framework.workspace.editorpart.IEditorPart#getGrid()
     */
    public IGrid getGrid() {
        return this.grid;
    }
    
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.framework.workspace.editorpart.IEditorPart#growDrawingArea()
     */
    public void growDrawingArea() {
        IGraph g = getGraph();
        Rectangle2D bounds = g.getClipBounds();
        bounds.add(getBounds());
        g.setBounds(new Double(0, 0, GROW_SCALE_FACTOR * bounds.getWidth(), GROW_SCALE_FACTOR * bounds.getHeight()));
        repaint();
    }
    
    /* (non-Javadoc)
     * @see com.horstmann.violet.framework.workspace.editorpart.IEditorPart#clipDrawingArea()
     */
    public void clipDrawingArea() {
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
        graph.draw(g2, grid);
        List<INode> nodes = selectionHandler.getSelectedNodes();
        for (INode n : nodes)
        {
            if (graph.getNodes().contains(n))
            {
                Rectangle2D grabberBounds = n.getBounds();
                GrabberUtils.drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMinY());
                GrabberUtils.drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMaxY());
                GrabberUtils.drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMinY());
                GrabberUtils.drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMaxY());
            }
        }
        List<IEdge> edges = selectionHandler.getSelectedEdges();
        for (IEdge e : edges)
        {
            if (graph.getEdges().contains(e))
            {
                Line2D line = e.getConnectionPoints();
                GrabberUtils.drawGrabber(g2, line.getX1(), line.getY1());
                GrabberUtils.drawGrabber(g2, line.getX2(), line.getY2());
            }
        }

        if (dragMode.equals(EditorPartMouseDragModeEnum.DRAG_RUBBERBAND))
        {
            Color oldColor = g2.getColor();
            g2.setColor(PURPLE);
            g2.draw(new Line2D.Double(mouseDownPoint, lastMousePoint));
            g2.setColor(oldColor);
        }
        else if (dragMode.equals(EditorPartMouseDragModeEnum.DRAG_LASSO))
        {
            Color oldColor = g2.getColor();
            g2.setColor(PURPLE);
            double x1 = mouseDownPoint.getX();
            double y1 = mouseDownPoint.getY();
            double x2 = lastMousePoint.getX();
            double y2 = lastMousePoint.getY();
            Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1
                    - y2));
            g2.draw(lasso);
            g2.setColor(oldColor);
        }
    }
    

    

    public void addBehavior(IEditorPartBehavior newBehavior) {
        this.behaviorManager.addBehavior(newBehavior);
    }
    
    public List<IEditorPartBehavior> getBehaviors() {
        return this.behaviorManager.getBehaviors();
    }
    
    private ResourceBundle resourceBundle = ResourceBundle.getBundle(ResourceBundleConstant.OTHER_STRINGS, Locale.getDefault());

    private IGraph graph;

    private IGrid grid = new PlainGrid(this);

    private Workspace diagramPanel;

    private double zoom;

    private GraphTool selectedTool;

    private Point2D lastMousePoint;

    private Point2D mouseDownPoint;

    private EditorPartMouseDragModeEnum dragMode = EditorPartMouseDragModeEnum.DRAG_NONE;

    private EditorPartSelectionHandler selectionHandler = new EditorPartSelectionHandler();



    private static final int CONNECT_THRESHOLD = 8;

    private static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);
    
    /**
     * Scale factor used to grow drawing area
     */
    private static final double GROW_SCALE_FACTOR = Math.sqrt(2);

    
    @SpringBean(name = "dialogFactory")
    private DialogFactory dialogFactory;
    
    private EditorPartBehaviorManager behaviorManager = new EditorPartBehaviorManager();

}