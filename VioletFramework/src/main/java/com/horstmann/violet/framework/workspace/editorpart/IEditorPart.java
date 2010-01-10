package com.horstmann.violet.framework.workspace.editorpart;

import java.awt.Component;
import java.util.List;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.workspace.editorpart.behavior.IEditorPartBehavior;
import com.horstmann.violet.framework.workspace.sidebar.graphtools.GraphTool;

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
     * Edits the properties of the selected graph element.
     */
    public abstract void editSelected();

    /**
     * Removes the selected nodes or edges.
     */
    public abstract void removeSelected();

    /**
     * Selects another graph element.
     * 
     * @param distanceFormCurrentElement distance from the currently selected element. For example : -1 for the previous element and
     *            +1 for the next one.
     */
    public abstract void selectAnotherGraphElement(int distanceFormCurrentElement);

    /**
     * Sets the currently selected tool
     * 
     * @param tool t
     */
    public abstract void setSelectedTool(GraphTool tool);

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
     * @return the grid used to keep elements aligned
     */
    public IGrid getGrid();
    
    /**
     * Grows drawing area
     */
    public abstract void growDrawingArea();
    
    /**
     * Clips drawing area
     */
    public abstract void clipDrawingArea();

    /**
     * @return the awt object displaying this editor part
     */
    public Component getAWTComponent();
    
    /**
     * Adds a new behavior to this editor
     * @param behavior
     */
    public void addBehavior(IEditorPartBehavior behavior);
    
    /**
     * @return installed behaviors
     */
    public List<IEditorPartBehavior> getBehaviors();

}