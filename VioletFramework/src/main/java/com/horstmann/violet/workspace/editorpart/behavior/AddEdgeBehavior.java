package com.horstmann.violet.workspace.editorpart.behavior;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.Id;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.edge.SegmentedLineEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.diagram.abstracts.property.BentStyle;
import com.horstmann.violet.workspace.editorpart.IEditorPart;
import com.horstmann.violet.workspace.editorpart.IEditorPartBehaviorManager;
import com.horstmann.violet.workspace.editorpart.IEditorPartSelectionHandler;
import com.horstmann.violet.workspace.editorpart.IGrid;
import com.horstmann.violet.workspace.sidebar.graphtools.GraphTool;
import com.horstmann.violet.workspace.sidebar.graphtools.IGraphToolsBar;

public class AddEdgeBehavior extends AbstractEditorPartBehavior
{

    public AddEdgeBehavior(IEditorPart editorPart, IGraphToolsBar graphToolsBar)
    {
        this.editorPart = editorPart;
        this.graph = editorPart.getGraph();
        this.grid = editorPart.getGrid();
        this.selectionHandler = editorPart.getSelectionHandler();
        this.behaviorManager = editorPart.getBehaviorManager();
    }

    private void resetAttributes()
    {
        this.startEdgeLocation = null;
        this.freePathPoints.clear();
        this.endEdgeLocation = null;
        this.isEligibleToFreePath = false;
    }

    @Override
    public void onMouseClicked(MouseEvent event)
    {
        GraphTool selectedTool = this.selectionHandler.getSelectedTool();
        boolean isNoEdgeSelected = !IEdge.class.isInstance(selectedTool.getNodeOrEdge());
        boolean isDoubleClick = event.getClickCount() > 1;
        boolean isWrongButton = event.getButton() != MouseEvent.BUTTON1;
        if (isDoubleClick || isWrongButton || isNoEdgeSelected)
        {
            resetAttributes();
            return;
        }

        this.isEligibleToFreePath = isEligibleToFreePath((IEdge) selectedTool.getNodeOrEdge());

        double zoom = editorPart.getZoomFactor();
        final Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        boolean isClickOnNode = (graph.findNode(mousePoint) != null);
        if (isClickOnNode)
        {
            boolean isStartLocationSet = (startEdgeLocation != null);
            boolean isEndLocationSet = (endEdgeLocation != null);
            if (!isStartLocationSet)
            {
                startEdgeLocation = mousePoint;
            }
            if (isStartLocationSet && !isEndLocationSet)
            {
                endEdgeLocation = mousePoint;
            }
        }
        if (!isClickOnNode)
        {
            boolean isStartLocationSet = (startEdgeLocation != null);
            if (!isStartLocationSet)
            {
                return;
            }
            if (this.isEligibleToFreePath)
            {
                Point2D snapPoint = this.grid.snap(mousePoint);
                this.freePathPoints.add(snapPoint);
            }
        }
        boolean isReadyToConnectEdge = (startEdgeLocation != null && endEdgeLocation != null);
        if (isReadyToConnectEdge)
        {
            IEdge prototype = (IEdge) selectedTool.getNodeOrEdge();
            IEdge newEdge = (IEdge) prototype.clone();
            newEdge.setId(new Id());
            boolean added = addEdgeAtPoints(newEdge, startEdgeLocation, endEdgeLocation);
            if (added)
            {
                selectionHandler.setSelectedElement(newEdge);
            }
            resetAttributes();
        }
    }

    @Override
    public void onMouseMoved(MouseEvent event)
    {
        double zoom = editorPart.getZoomFactor();
        Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
        Point2D snapPoint = this.grid.snap(mousePoint);
        if (this.currentMouseLocation == null) {
            this.currentMouseLocation = snapPoint;
            editorPart.getSwingComponent().repaint();
        }
        if (!snapPoint.equals(this.currentMouseLocation)) {
            this.currentMouseLocation = snapPoint;
            editorPart.getSwingComponent().repaint();
        }
    }

    /**
     * Adds an edge at a specific location
     * 
     * @param newEdge
     * @param startPoint
     * @param endPoint
     * @return true id the edge has been added
     */
    public boolean addEdgeAtPoints(IEdge newEdge, Point2D startPoint, Point2D endPoint)
    {
        boolean isAdded = false;
        if (startPoint.distance(endPoint) > CONNECT_THRESHOLD)
        {
            this.behaviorManager.fireBeforeAddingEdgeAtPoints(newEdge, startPoint, endPoint);
            try
            {
                INode startNode = graph.findNode(startPoint);
                INode endNode = graph.findNode(endPoint);
                Point2D relativeStartPoint = null;
                Point2D relativeEndPoint = null;
                if (startNode != null)
                {
                    Point2D startNodeLocationOnGraph = startNode.getLocationOnGraph();
                    double relativeStartX = startPoint.getX() - startNodeLocationOnGraph.getX();
                    double relativeStartY = startPoint.getY() - startNodeLocationOnGraph.getY();
                    relativeStartPoint = new Point2D.Double(relativeStartX, relativeStartY);
                }
                if (endNode != null)
                {
                    Point2D endNodeLocationOnGraph = endNode.getLocationOnGraph();
                    double relativeEndX = endPoint.getX() - endNodeLocationOnGraph.getX();
                    double relativeEndY = endPoint.getY() - endNodeLocationOnGraph.getY();
                    relativeEndPoint = new Point2D.Double(relativeEndX, relativeEndY);
                }
                if (graph.connect(newEdge, startNode, relativeStartPoint, endNode, relativeEndPoint))
                ;
                {
                    newEdge.incrementRevision();
                    isAdded = true;
                }
                if (this.isEligibleToFreePath)
                {
                    SegmentedLineEdge segmentedLineEdge = (SegmentedLineEdge) newEdge;
                    segmentedLineEdge.setFreePathPoints(this.freePathPoints);
                }
            }
            finally
            {
                this.behaviorManager.fireAfterAddingEdgeAtPoints(newEdge, startPoint, endPoint);
            }
        }
        return isAdded;
    }

    /**
     * Checks if the edge could support free path. The two conditions are to be a subclass of SegmentedLineEdge and to have a
     * BentStyle property
     * 
     * @param edge to check
     * @return true if all the conditions are verified
     */
    private boolean isEligibleToFreePath(IEdge edge)
    {
        boolean isSegmentLineEdge = SegmentedLineEdge.class.isInstance(edge);
        if (!isSegmentLineEdge)
        {
            return false;
        }
        try
        {
            Class<? extends IEdge> edgeClass = edge.getClass();
            BeanInfo info = Introspector.getBeanInfo(edgeClass);
            PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor aPropDesc : propertyDescriptors)
            {
                Class<?> propertyType = aPropDesc.getPropertyType();
                boolean isBentStyleFound = propertyType.isAssignableFrom(BentStyle.class);
                if (isBentStyleFound)
                {
                    return true;
                }
            }
        }
        catch (IntrospectionException e)
        {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void onPaint(Graphics2D g2)
    {
        if (this.startEdgeLocation == null)
        {
            return;
        }
        List<Point2D> edgePoints = new ArrayList<Point2D>();
        edgePoints.add(startEdgeLocation);
        if (this.isEligibleToFreePath)
        {
            edgePoints.addAll(freePathPoints);
        }
        edgePoints.add(currentMouseLocation);
        Color oldColor = g2.getColor();
        g2.setColor(PURPLE);
        for (int i = 1; i < edgePoints.size(); i++)
        {
            Point2D p1 = edgePoints.get(i - 1);
            Point2D p2 = edgePoints.get(i);
            g2.draw(new Line2D.Double(p1, p2));
        }
        g2.setColor(oldColor);
    }

    private static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);
    private static final int CONNECT_THRESHOLD = 8;

    private Point2D startEdgeLocation = null;
    private Point2D currentMouseLocation = null;
    private List<Point2D> freePathPoints = new ArrayList<Point2D>();
    private Point2D endEdgeLocation = null;
    private boolean isEligibleToFreePath = false;

    private IEditorPart editorPart;
    private IGraph graph;
    private IGrid grid; 
    private IEditorPartSelectionHandler selectionHandler;
    private IEditorPartBehaviorManager behaviorManager;

}
