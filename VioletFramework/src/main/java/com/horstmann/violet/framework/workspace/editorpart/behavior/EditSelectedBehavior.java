package com.horstmann.violet.framework.workspace.editorpart.behavior;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.diagram.edge.IEdge;
import com.horstmann.violet.framework.diagram.node.INode;
import com.horstmann.violet.framework.dialog.DialogFactory;
import com.horstmann.violet.framework.propertyeditor.CustomPropertyEditor;
import com.horstmann.violet.framework.propertyeditor.ICustomPropertyEditor;
import com.horstmann.violet.framework.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.spring.SpringDependencyInjector;
import com.horstmann.violet.framework.spring.annotation.SpringBean;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPart;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartBehaviorManager;
import com.horstmann.violet.framework.workspace.editorpart.IEditorPartSelectionHandler;
import com.horstmann.violet.product.diagram.common.DiagramLinkNode;

public class EditSelectedBehavior extends AbstractEditorPartBehavior
{

    public EditSelectedBehavior(IEditorPart editorPart)
    {
        SpringDependencyInjector.getInjector().inject(this);
        ResourceBundleInjector.getInjector().inject(this);
        this.editorPart = editorPart;
        this.graph = editorPart.getGraph();
        this.selectionHandler = editorPart.getSelectionHandler();
        this.behaviorManager = editorPart.getBehaviorManager();
    }

    @Override
    public void onMousePressed(MouseEvent event)
    {
        boolean isButton1Clicked = (event.getModifiers() & InputEvent.BUTTON1_MASK) == 0;
        if (event.getClickCount() > 1 || isButton1Clicked)
        {
            double zoom = editorPart.getZoomFactor();
            Point2D mouseLocation = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
            this.selectionHandler.clearSelection();
            INode node = this.graph.findNode(mouseLocation);
            IEdge edge = this.graph.findEdge(mouseLocation);
            if (node != null) {
                this.selectionHandler.setSelectedElement(node);
            } else if (edge != null) {
                this.selectionHandler.addSelectedElement(edge);
            }
            editSelected();
        }
    }

    public void editSelected()
    {
        final Object edited = selectionHandler.isNodeSelectedAtLeast() ? selectionHandler.getLastSelectedNode() : selectionHandler.getLastSelectedEdge();
        if (edited == null)
        {
            return;
        }
        final ICustomPropertyEditor sheet = new CustomPropertyEditor(edited);

        sheet.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(final PropertyChangeEvent event)
            {
                // TODO : fix open file event
                if (event.getSource() instanceof DiagramLinkNode)
                {
                    // DiagramLinkNode ln = (DiagramLinkNode) event.getSource();
                    // DiagramLink dl = ln.getDiagramLink();
                    // if (dl != null && dl.getOpenFlag().booleanValue())
                    // {
                    // diagramPanel.fireMustOpenFile(dl.getFile());
                    // dl.setOpenFlag(new Boolean(false));
                    // }
                }

                if (edited instanceof INode)
                {
                    behaviorManager.fireWhileEditingNode((INode) edited);
                }
                if (edited instanceof IEdge)
                {
                    behaviorManager.fireWhileEditingEdge((IEdge) edited);
                }
            }
        });

        JOptionPane optionPane = new JOptionPane();
        optionPane.setOpaque(false);
        optionPane.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if ((event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && event.getNewValue() != null && event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE)
                {
                    if (sheet.isEditable())
                    {
                        // This manages optionPane submits through a property
                        // listener because, as dialog display could be
                        // delegated
                        // (to Eclipse for example), host system can work in
                        // other threads
                        if (edited instanceof INode)
                        {
                            behaviorManager.fireAfterEditingNode((INode) edited);
                        }
                        if (edited instanceof IEdge)
                        {
                            behaviorManager.fireAfterEditingEdge((IEdge) edited);
                        }
                    }
                }
            }
        });

        if (sheet.isEditable())
        {
            if (edited instanceof INode)
            {
                this.behaviorManager.fireBeforeEditingNode((INode) edited);
            }
            if (edited instanceof IEdge)
            {
                this.behaviorManager.fireBeforeEditingEdge((IEdge) edited);
            }
            optionPane.setMessage(sheet.getAWTComponent());
        }
        if (!sheet.isEditable())
        {
            JLabel label = new JLabel(this.uneditableBeanMessage);
            label.setFont(label.getFont().deriveFont(Font.PLAIN));
            optionPane.setMessage(label);
        }
        this.dialogFactory.showDialog(optionPane, this.dialogTitle, true);
    }
    
  

    private IEditorPartSelectionHandler selectionHandler;
    private IEditorPart editorPart;
    private IGraph graph;
    private IEditorPartBehaviorManager behaviorManager;

    @SpringBean(name = "dialogFactory")
    private DialogFactory dialogFactory;

    @ResourceBundleBean(key = "edit.properties.title")
    private String dialogTitle;

    @ResourceBundleBean(key = "edit.properties.empty_bean_message")
    private String uneditableBeanMessage;

}
