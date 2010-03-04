package com.horstmann.violet.framework.workspace.editorpart.behavior;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartSelectionHandler;

public class DragSelectedBehavior extends AbstractEditorPartBehavior
{

    public DragSelectedBehavior(IEditorPart editorPart)
    {
        this.editorPart = editorPart;
        this.graph = editorPart.getGraph();
        this.selectionHandler = editorPart.getSelectionHandler();

    }
    
    private IGraph graph;

    private IEditorPartSelectionHandler selectionHandler;

    private IEditorPart editorPart;
}
