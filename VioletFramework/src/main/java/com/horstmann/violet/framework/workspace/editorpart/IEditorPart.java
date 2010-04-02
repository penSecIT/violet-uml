package com.horstmann.violet.framework.workspace.editorpart;

import java.awt.Component;
import java.util.List;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.node.INode;

/**
 * Defines the editor behaviour (an editor is something embedding an IGraph)
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public interface IEditorPart
{

    /**
     * Returns the graph handled by the editor
     */
    public abstract IGraph getGraph();

    /**
     * Removes the selected nodes or edges.
     */
    public abstract void removeSelected();


    /**
     * @return currently selected nodes
     */
    public abstract List<INode> getSelectedNodes();
    
    /**
     * Clears nodes and edges selection 
     */
    public void clearSelection();
    
    /**
     * Selects a node
     * @param node
     */
    public void selectElement(INode node);

    /**
     * Changes the zoom of this editor. The zoom is 1 by default and is multiplied by sqrt(2) for each positive stem or divided by
     * sqrt(2) for each negative step.
     * 
     * @param steps the number of steps by which to change the zoom. A positive value zooms in, a negative value zooms out.
     */
    public abstract void changeZoom(int steps);
    
    /**
     * @return current zoom factor
     */
    public double getZoomFactor();
    
    /**
     * @return the grid used to keep elements aligned
     */
    public IGrid getGrid();
    
    /**
     * Grows drawing area
     */
    public void growDrawingArea();
    
    /**
     * Clips drawing area
     */
    public void clipDrawingArea();

    /**
     * @return the awt object displaying this editor part
     */
    public Component getAWTComponent();
    
    /**
     * @return object that manages nodes and edges selection
     */
    public IEditorPartSelectionHandler getSelectionHandler();
    
    /**
     * @return manager used to declare new editor behaviors and how to send events between behaviors
     */
    public IEditorPartBehaviorManager getBehaviorManager();
    

}