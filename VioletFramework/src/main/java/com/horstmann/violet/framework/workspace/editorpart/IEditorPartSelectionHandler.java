package com.horstmann.violet.framework.workspace.editorpart;

import java.util.List;

import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;

public interface IEditorPartSelectionHandler {

	public abstract void setSelectedElement(INode node);

	public abstract void setSelectedElement(IEdge edge);

	public abstract void addSelectedElement(INode node);

	public abstract void addSelectedElement(IEdge edge);

	public abstract void removeElementFromSelection(INode node);

	public abstract void removeElementFromSelection(IEdge edge);

	public abstract boolean isElementAlreadySelected(INode node);

	public abstract boolean isElementAlreadySelected(IEdge edge);

	public abstract void clearSelection();

	public abstract INode getLastSelectedNode();

	public abstract IEdge getLastSelectedEdge();

	public abstract boolean isNodeSelectedAtLeast();

	public abstract boolean isEdgeSelectedAtLeast();

	public abstract List<INode> getSelectedNodes();

	public abstract List<IEdge> getSelectedEdges();

}