/*
 * Projet     : 
 * Package    : com.horstmann.violet.framework.gui
 * Auteur     : a.depellegrin
 * Cr�� le    : 8 mars 2007
 */
package com.horstmann.violet.framework.workspace.editorpart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;

public class EditorPartSelectionHandler
{

    public void setSelectedElement(INode node)
    {
        this.selectedNodes.clear();
        this.selectedEdges.clear();
        addSelectedElement(node);
    }

    public void setSelectedElement(IEdge edge)
    {
        this.selectedNodes.clear();
        this.selectedEdges.clear();
        addSelectedElement(edge);
    }
    
    public void updateSelectedElements(INode[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            if (isElementAlreadySelected(nodes[i])) {
                addSelectedElement(nodes[i]);
            }
        }
    }

    public void updateSelectedElements(IEdge[] edges) {
        for (int i = 0; i < edges.length; i++) {
            if (isElementAlreadySelected(edges[i])) {
                addSelectedElement(edges[i]);
            }
        }
    }

    public void addSelectedElement(INode node)
    {
        if (this.selectedNodes.contains(node))
        {
            this.removeElementFromSelection(node);
        }
        this.selectedNodes.add(node);
        this.isNodeSelectedAtLeast = true;
        this.isEdgeSelectedAtLeast = false;
    }

    public void addSelectedElement(IEdge edge)
    {
        if (this.selectedEdges.contains(edge))
        {
            this.removeElementFromSelection(edge);
        }
        this.selectedNodes.clear();
        this.selectedEdges.add(edge);
        this.isNodeSelectedAtLeast = false;
        this.isEdgeSelectedAtLeast = true;
    }

    public void removeElementFromSelection(INode node)
    {
        if (this.selectedNodes.contains(node))
        {
            int i = this.selectedNodes.indexOf(node);
            this.selectedNodes.remove(i);
        }
    }

    public void removeElementFromSelection(IEdge edge)
    {
        if (this.selectedEdges.contains(edge))
        {
            int i = this.selectedEdges.indexOf(edge);
            this.selectedEdges.remove(i);
        }
    }

    public boolean isElementAlreadySelected(INode node)
    {
        if (this.selectedNodes.contains(node)) return true;
        return false;
    }

    public boolean isElementAlreadySelected(IEdge edge)
    {
        if (this.selectedEdges.contains(edge)) return true;
        return false;
    }

    public void clearSelection()
    {
        this.selectedNodes.clear();
        this.selectedEdges.clear();
        this.isNodeSelectedAtLeast = false;
        this.isEdgeSelectedAtLeast = false;
    }

    public INode getLastSelectedNode()
    {
        return getLastElement(this.selectedNodes);
    }

    public IEdge getLastSelectedEdge()
    {
        return getLastElement(this.selectedEdges);
    }

    public boolean isNodeSelectedAtLeast()
    {
        return this.isNodeSelectedAtLeast;
    }

    public boolean isEdgeSelectedAtLeast()
    {
        return this.isEdgeSelectedAtLeast;
    }

    public List<INode> getSelectedNodes()
    {
        return Collections.unmodifiableList(selectedNodes);
    }

    public List<IEdge> getSelectedEdges()
    {
        return Collections.unmodifiableList(selectedEdges);
    }

    private <T> T getLastElement(List<T> list)
    {
        int size = list.size();
        if (size <= 0)
        {
            return null;
        }
        return list.get(size - 1);
    }

    private List<INode> selectedNodes = new ArrayList<INode>();

    private List<IEdge> selectedEdges = new ArrayList<IEdge>();

    private boolean isNodeSelectedAtLeast = false;
    private boolean isEdgeSelectedAtLeast = false;

}
