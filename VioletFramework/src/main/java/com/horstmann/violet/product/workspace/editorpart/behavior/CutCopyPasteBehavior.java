package com.horstmann.violet.product.workspace.editorpart.behavior;

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

import com.horstmann.violet.framework.display.clipboard.Clipboard;
import com.horstmann.violet.framework.file.GraphFile;
import com.horstmann.violet.framework.file.IGraphFile;
import com.horstmann.violet.framework.file.persistence.StandardJavaFilePersistenceService;
import com.horstmann.violet.framework.injection.bean.BeanInjector;
import com.horstmann.violet.framework.injection.bean.annotation.InjectedBean;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.edge.IEdge;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.workspace.editorpart.IEditorPart;
import com.horstmann.violet.product.workspace.editorpart.IEditorPartBehaviorManager;
import com.horstmann.violet.product.workspace.editorpart.IEditorPartSelectionHandler;

public class CutCopyPasteBehavior extends AbstractEditorPartBehavior
{

    private IEditorPart editorPart;
    
    /** The clipboard that is shared among all diagrams */
    @InjectedBean
    private Clipboard clipboard;
    
    private String clipboardString;
    
    private ByteBuffer bb;
    
    private StandardJavaFilePersistenceService persistenceService = new StandardJavaFilePersistenceService();
    
    /**
     * Keep mouse location to paste on just above the current mouse location
     */
    private Point2D lastMouseLocation = new Point2D.Double(0, 0);

    public CutCopyPasteBehavior(IEditorPart editorPart)
    {
        this.editorPart = editorPart;
        BeanInjector.getInjector().inject(this);
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        double zoom = editorPart.getZoomFactor();
        this.lastMouseLocation = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
    }
    
    
    public void cut()
    {
        copy();
        editorPart.removeSelected();
        editorPart.getSwingComponent().repaint();
    }

    public void copy()
    {
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
//        for (IEdge aSelectedEdge : selectedEdges) {
//            IEdge clone = aSelectedEdge.clone();
//            Point2D locationOnGraph = aSelectedNode.getLocationOnGraph();
//            newGraph.connect(clone, start, startLocation, end, endLocation)addNode(n, p)Node(clone, locationOnGraph);
//        }
        try
        {
            ByteBuffer serializedGraph = persistenceService.serializeGraph(newGraph);
            this.bb = serializedGraph;
            this.clipboardString = bb_to_str(serializedGraph);
            System.out.println(clipboardString);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //this.clipboard.copyIn(graph, editorPart.getSelectedNodes());
    }

    public void paste()
    {
        IEditorPartBehaviorManager behaviorManager = this.editorPart.getBehaviorManager();
        List<AddNodeBehavior> nodeBehaviorsFound = behaviorManager.getBehaviors(AddNodeBehavior.class);
        if (nodeBehaviorsFound.size() != 1) {
            return;
        }
        AddNodeBehavior addNodeBehavior = nodeBehaviorsFound.get(0);
        
        List<AddEdgeBehavior> edgeBehaviorsFound = behaviorManager.getBehaviors(AddEdgeBehavior.class);
        if (edgeBehaviorsFound.size() != 1) {
            return;
        }
        AddEdgeBehavior addEdgeBehavior = edgeBehaviorsFound.get(0);
        
        try
        {
           ByteBuffer byteBuffer = this.bb;
           IGraph deserializedGraph = persistenceService.deserializeGraph(byteBuffer);
           Rectangle2D clipBounds = deserializedGraph.getClipBounds();
           Collection<INode> nodes = deserializedGraph.getAllNodes();
           for (INode aNode : nodes) {
               if (isAncestorRelationship(aNode, nodes)) continue;
               //aNode.translate(clipBounds.getMinX(), clipBounds.getMinY());
               addNodeBehavior.addNodeAtPoint(aNode, aNode.getLocation());
           }
           Collection<IEdge> edges = deserializedGraph.getAllEdges();
           for (IEdge anEdge : edges) {
               Point2D startLocation = anEdge.getStartLocation();
               Point2D endLocation = anEdge.getEndLocation();
               addEdgeBehavior.addEdgeAtPoints(anEdge, startLocation, endLocation);
           }
           
           
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            editorPart.getSwingComponent().invalidate();
            editorPart.getSwingComponent().repaint();
        }
    }
    
    public static Charset charset = Charset.forName("UTF-8");
    public static CharsetEncoder encoder = charset.newEncoder();
    public static CharsetDecoder decoder = charset.newDecoder();

    private ByteBuffer str_to_bb(String msg){
      try{
        return encoder.encode(CharBuffer.wrap(msg));
      }catch(Exception e){e.printStackTrace();}
      return null;
    }

    private String bb_to_str(ByteBuffer buffer){
      String data = "";
      try{
        int old_position = buffer.position();
        data = decoder.decode(buffer).toString();
        // reset buffer's position to its original so it is not altered:
        buffer.position(old_position);  
      }catch (Exception e){
        e.printStackTrace();
        return "";
      }
      return data;
    }

    private boolean isAncestorRelationship(INode childNode, Collection<INode> ancestorList) {
        for (INode anAncestorNode : ancestorList) {
            boolean ancestorRelationship = isAncestorRelationship(childNode, anAncestorNode);
            if (ancestorRelationship) return true;
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
    private boolean isAncestorRelationship(INode childNode, INode ancestorNode)
    {
        INode parent = childNode.getParent();
        if (parent == null)
        {
            return false;
        }
        List<INode> fifo = new ArrayList<INode>();
        fifo.add(parent);
        while (!fifo.isEmpty())
        {
            INode aParentNode = fifo.get(0);
            fifo.remove(0);
            if (aParentNode.equals(ancestorNode))
            {
                return true;
            }
            INode aGranParent = aParentNode.getParent();
            if (aGranParent != null)
            {
                fifo.add(aGranParent);
            }
        }
        return false;
    }

}
