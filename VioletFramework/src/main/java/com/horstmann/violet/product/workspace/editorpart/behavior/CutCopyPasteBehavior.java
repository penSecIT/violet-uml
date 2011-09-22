package com.horstmann.violet.product.workspace.editorpart.behavior;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;

import com.horstmann.violet.framework.display.clipboard.Clipboard;
import com.horstmann.violet.framework.injection.bean.BeanInjector;
import com.horstmann.violet.framework.injection.bean.annotation.InjectedBean;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.horstmann.violet.product.diagram.abstracts.node.INode;
import com.horstmann.violet.product.workspace.editorpart.IEditorPart;

public class CutCopyPasteBehavior extends AbstractEditorPartBehavior
{

    private IEditorPart editorPart;
    
    /** The clipboard that is shared among all diagrams */
    @InjectedBean
    private Clipboard clipboard;
    
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
        this.clipboard.copyIn(graph, editorPart.getSelectedNodes());
    }

    public void paste()
    {
        try
        {
            IGraph graph = editorPart.getGraph();
            List<INode> selectedNodes = editorPart.getSelectedNodes();
            INode lastSelectedNode = selectedNodes.isEmpty() ? null : selectedNodes.get(selectedNodes.size() - 1);
            Collection<INode> pastedNodes = this.clipboard.pasteOut(graph, lastSelectedNode, this.lastMouseLocation);
            if (pastedNodes != null)
            {
                editorPart.clearSelection();
                for (INode n : pastedNodes)
                    editorPart.selectElement(n);
            }
        }
        finally
        {
            editorPart.getSwingComponent().invalidate();
            editorPart.getSwingComponent().repaint();
        }
    }

}
