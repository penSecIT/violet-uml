package com.horstmann.violet.product.workspace.editorpart.behavior;

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

    public CutCopyPasteBehavior(IEditorPart editorPart)
    {
        this.editorPart = editorPart;
        BeanInjector.getInjector().inject(this);
    }

    public void cut()
    {
        copy();
        editorPart.removeSelected();
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
            Collection<INode> pastedNodes = this.clipboard.pasteOut(graph, lastSelectedNode);
            if (pastedNodes != null)
            {
                editorPart.clearSelection();
                for (INode n : pastedNodes)
                    editorPart.selectElement(n);
            }
        }
        finally
        {
        }
    }

}
