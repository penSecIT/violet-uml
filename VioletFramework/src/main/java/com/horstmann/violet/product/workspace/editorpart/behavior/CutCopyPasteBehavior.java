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
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
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

	private String clipboardString;

	private ByteBuffer bb;

	private StandardJavaFilePersistenceService persistenceService = new StandardJavaFilePersistenceService();

	/**
	 * Keep mouse location to paste on just above the current mouse location
	 */
	private Point2D lastMouseLocation = new Point2D.Double(0, 0);

	public CutCopyPasteBehavior(IEditorPart editorPart) {
		this.editorPart = editorPart;
		BeanInjector.getInjector().inject(this);
	}

	@Override
	public void onMousePressed(MouseEvent event) {
		double zoom = editorPart.getZoomFactor();
		this.lastMouseLocation = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
	}

	public void cut() {
		copy();
		editorPart.removeSelected();
		editorPart.getSwingComponent().repaint();
	}

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
			String xmlContent = bb_to_str(serializedGraph);
			putToSystemClipboard(xmlContent);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void putToSystemClipboard(String content) {
		StringSelection dataToClip = new StringSelection(content);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(dataToClip, dataToClip);
	}

	private String getFromSystemClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipData = clipboard.getContents(clipboard);
		if (clipData != null) {
			try {
				if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
					return s;
				}
			} catch (UnsupportedFlavorException ufe) {
				return "";
			} catch (IOException ioe) {
				return "";
			}
		}
		return "";
	}

	public void paste() {

		IGraph graph = this.editorPart.getGraph();
		try {
			String xmlContent = getFromSystemClipboard();
			xmlContent = xmlContent.trim();
			System.out.println(xmlContent);
			ByteBuffer byteBuffer = str_to_bb(xmlContent);
			IGraph deserializedGraph = persistenceService.deserializeGraph(byteBuffer);
			deserializedGraph = translateToMouseLocation(deserializedGraph, this.lastMouseLocation);

			Collection<INode> nodes = deserializedGraph.getAllNodes();
			for (INode aNode : nodes) {
				if (isAncestorRelationship(aNode, nodes))
					continue;

				graph.addNode(aNode, aNode.getLocation());
			}
			Collection<IEdge> edges = deserializedGraph.getAllEdges();
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

	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static CharsetDecoder decoder = charset.newDecoder();

	private ByteBuffer str_to_bb(String msg) {
		try {
			return encoder.encode(CharBuffer.wrap(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String bb_to_str(ByteBuffer buffer) {
		String data = "";
		try {
			int old_position = buffer.position();
			data = decoder.decode(buffer).toString();
			// reset buffer's position to its original so it is not altered:
			buffer.position(old_position);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return data;
	}

	private boolean isAncestorRelationship(INode childNode, Collection<INode> ancestorList) {
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
