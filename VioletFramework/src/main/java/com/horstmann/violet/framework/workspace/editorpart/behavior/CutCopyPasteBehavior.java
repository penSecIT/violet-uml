package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.util.Collection;
import java.util.List;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.gui.Clipboard;
import com.horstmann.violet.framework.spring.SpringDependencyInjector;
import com.horstmann.violet.framework.spring.annotation.SpringBean;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;

public class CutCopyPasteBehavior extends AbstractEditorPartBehavior
{

    private IEditorPart editorPart;
    
    /** The clipboard that is shared among all diagrams */
    @SpringBean(name="clipboard")
    private Clipboard clipboard;

    public CutCopyPasteBehavior(IEditorPart editorPart)
    {
        this.editorPart = editorPart;
        SpringDependencyInjector.getInjector().inject(this);
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
