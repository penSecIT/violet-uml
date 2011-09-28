package com.horstmann.violet.product.workspace.editorpart.behavior;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.horstmann.violet.framework.file.GraphFile;
import com.horstmann.violet.framework.file.IGraphFile;
import com.horstmann.violet.framework.file.persistence.StandardJavaFilePersistenceService;
import com.horstmann.violet.framework.injection.bean.BeanInjector;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.workspace.editorpart.IEditorPart;
import com.horstmann.violet.product.workspace.editorpart.IEditorPartSelectionHandler;

public class CutCopyPasteBehavior extends AbstractEditorPartBehavior {

	private IEditorPart editorPart;

	private StandardJavaFilePersistenceService persistenceService = new StandardJavaFilePersistenceService();

	/**
	 * Keep mouse location to paste on just above the current mouse location
	 */
	private Point2D lastMouseLocation = new Point2D.Double(0, 0);

	/**
	 * Default constructor
	 * 
	 * @param editorPart
	 */
	public CutCopyPasteBehavior(IEditorPart editorPart) {
		this.editorPart = editorPart;
		BeanInjector.getInjector().inject(this);
	}

	@Override
	public void onMousePressed(MouseEvent event) {
		double zoom = editorPart.getZoomFactor();
		this.lastMouseLocation = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
	}

	/**
	 * Cuts selected graph elements
	 */
	public void cut() {
		copy();
		editorPart.removeSelected();
		editorPart.getSwingComponent().repaint();
	}

	/**
	 * Copies selected graph elements to system clipboard
	 */
	public void copy() {
		IGraph graph = editorPart.getGraph();
		Class<? extends IGraph> graphClass = graph.getClass();
		IGraphFile newGraphFile = new GraphFile(graphClass);
		IGraph newGraph = newGraphFile.getGraph();
		IEditorPartSelectionHandler selectionHandler = editorPart.getSelectionHandler();
		List<INode> selectedNodes = selectionHandler.getSelectedNodes();
		for (INode aSelectedNode : selectedNodes) {
			INode clone = aSelectedNode.clone();
			Point2D locationOnGraph = aSelectedNode.getLocationOnGraph();
			newGraph.addNode(clone, locationOnGraph);
		}
		List<IEdge> selectedEdges = selectionHandler.getSelectedEdges();
		for (IEdge aSelectedEdge : selectedEdges) {
			IEdge clone = aSelectedEdge.clone();
			Point2D startLocation = clone.getStartLocation();
			Point2D endLocation = clone.getEndLocation();
			INode startNode = newGraph.findNode(clone.getStart().getId());
			INode endNode = newGraph.findNode(clone.getEnd().getId());
			if (startNode != null && endNode != null) {
				newGraph.connect(clone, startNode, startLocation, endNode, endLocation);
			}
		}
		try {
			ByteBuffer serializedGraph = persistenceService.serializeGraph(newGraph);
			String xmlContent = convertToString(serializedGraph);
			pushContentToSystemClipboard(xmlContent);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Paste elements from system wide clipboard to graph
	 */
	public void paste() {
		IGraph graph = this.editorPart.getGraph();
		try {
			String xmlContent = getContentFromSystemClipboard();
			if (xmlContent == null) {
				return; // If no content, we stop here
			}
			ByteBuffer byteBuffer = convertToByteBuffer(xmlContent);
			IGraph deserializedGraph = persistenceService.deserializeGraph(byteBuffer);
			deserializedGraph = translateToMouseLocation(deserializedGraph, this.lastMouseLocation);

			Collection<INode> nodesFromClipboard = deserializedGraph.getAllNodes();
			List<INode> nodes = filterOnNodePrototypes(nodesFromClipboard);
			for (INode aNode : nodes) {
				if (isAncestorInCollection(aNode, nodes))
					continue;

				graph.addNode(aNode, aNode.getLocation());
			}
			
			Collection<IEdge> edgesFromClipboard = deserializedGraph.getAllEdges();
			List<IEdge> edges = filterOnEdgePrototypes(edgesFromClipboard);
			for (IEdge anEdge : edges) {
				Point2D startLocation = anEdge.getStartLocation();
				Point2D endLocation = anEdge.getEndLocation();
				INode startNode = graph.findNode(anEdge.getStart().getId());
				INode endNode = graph.findNode(anEdge.getEnd().getId());
				if (startNode != null && endNode != null) {
					graph.connect(anEdge, startNode, startLocation, endNode, endLocation);
				}
			}
			
			editorPart.getSwingComponent().invalidate();
			editorPart.getSwingComponent().repaint();
		} catch (IOException e) {
			// Nothing to do
		}
	}
	
	/**
	 * As we can copy/paste on many diagrams, we ensure that we paste only node types acceptable for the current diagram
	 * 
	 * @param nodes from clipboard
	 * @return nodes acceptable for the current diagram
	 */
	private List<INode> filterOnNodePrototypes(Collection<INode> nodes) {
		IGraph currentGraph = this.editorPart.getGraph();
		List<INode> nodePrototypes = currentGraph.getNodePrototypes();
		List<Class<? extends INode>> classPrototypes = new ArrayList<Class<? extends INode>>();
		for (INode aNodePrototype : nodePrototypes) {
			classPrototypes.add(aNodePrototype.getClass());
		}

		List<INode> result = new ArrayList<INode>();
		for (INode aNode : nodes) {
			Class<? extends INode> nodeClass = aNode.getClass();
			if (classPrototypes.contains(nodeClass)) {
				result.add(aNode);
			}
		}
		return result;
	}
	
	/**
 	 * As we can copy/paste on many diagrams, we ensure that we paste only edge types acceptable for the current diagram
	 * 
	 * @param edges from clipboard
	 * @return edges acceptable for the current diagram
	 */
	private List<IEdge> filterOnEdgePrototypes(Collection<IEdge> edges) {
		IGraph currentGraph = this.editorPart.getGraph();
		List<IEdge> edgePrototypes = currentGraph.getEdgePrototypes();
		List<Class<? extends IEdge>> classPrototypes = new ArrayList<Class<? extends IEdge>>();
		for (IEdge aEdgePrototype : edgePrototypes) {
			classPrototypes.add(aEdgePrototype.getClass());
		}

		List<IEdge> result = new ArrayList<IEdge>();
		for (IEdge aEdge : edges) {
			Class<? extends IEdge> nodeClass = aEdge.getClass();
			if (classPrototypes.contains(nodeClass)) {
				result.add(aEdge);
			}
		}
		return result;
	}
	

	/**
	 * Moves all the nodes of a graph to a location
	 * 
	 * @param graph
	 * @param mouseLocation
	 * @return the modified graph
	 */
	private IGraph translateToMouseLocation(IGraph graph, Point2D mouseLocation) {
		Rectangle2D clipBounds = graph.getClipBounds();
		double dx = mouseLocation.getX() - clipBounds.getX();
		double dy = mouseLocation.getY() - clipBounds.getY();
		Collection<INode> nodes = graph.getAllNodes();
		for (INode aNode : nodes) {
			boolean hasParent = (aNode.getParent() != null);
			if (!hasParent) {
				aNode.translate(dx, dy);
			}
		}
		return graph;
	}

	/**
	 * Deals with system wide clipboard
	 * 
	 * @param content
	 */
	private void pushContentToSystemClipboard(String content) {
		StringSelection dataToClip = new StringSelection(content);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(dataToClip, dataToClip);
	}

	/**
	 * Deals with system wide clipboard
	 * 
	 * @return
	 */
	private String getContentFromSystemClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipData = clipboard.getContents(clipboard);
		if (clipData != null) {
			try {
				if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
					return s;
				}
			} catch (UnsupportedFlavorException ufe) {
				return null;
			} catch (IOException ioe) {
				return null;
			}
		}
		return null;
	}
	
	
	/**
	 * Converts a string into a bytebuffer
	 * 
	 * @param msg
	 * @return
	 */
	private ByteBuffer convertToByteBuffer(String msg) {
		return ByteBuffer.wrap(msg.getBytes());
	}
	

	/**
	 * Converts a bytebuffer into a string
	 * 
	 * @param bytebuffer
	 * @return
	 */
	private String convertToString(ByteBuffer bytebuffer) {
		byte[] bytearray = new byte[bytebuffer.remaining()];
		bytebuffer.get(bytearray);
		String s = new String(bytearray);
		return s;
	}

	/**
	 * Checks if the given list contains an ancestor of the given node
	 * 
	 * @param childNode
	 * @param ancestorList
	 * @return b
	 */
	private boolean isAncestorInCollection(INode childNode, Collection<INode> ancestorList) {
		for (INode anAncestorNode : ancestorList) {
			boolean ancestorRelationship = isAncestorRelationship(childNode, anAncestorNode);
			if (ancestorRelationship)
				return true;
		}
		return false;
	}

	/**
	 * Checks if ancestorNode is a parent node of child node
	 * 
	 * @param childNode
	 * @param ancestorNode
	 * @return b
	 */
	private boolean isAncestorRelationship(INode childNode, INode ancestorNode) {
		INode parent = childNode.getParent();
		if (parent == null) {
			return false;
		}
		List<INode> fifo = new ArrayList<INode>();
		fifo.add(parent);
		while (!fifo.isEmpty()) {
			INode aParentNode = fifo.get(0);
			fifo.remove(0);
			if (aParentNode.equals(ancestorNode)) {
				return true;
			}
			INode aGranParent = aParentNode.getParent();
			if (aGranParent != null) {
				fifo.add(aGranParent);
			}
		}
		return false;
	}

}
