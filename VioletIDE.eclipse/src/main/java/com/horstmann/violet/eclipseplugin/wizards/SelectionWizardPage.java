package com.horstmann.violet.eclipseplugin.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.horstmann.violet.framework.diagram.Graph;

/**
 * Wizard page where the end user can select which kind of diagram he wants to create
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class SelectionWizardPage extends WizardPage
{

    /**
     * Default constructor
     */
    protected SelectionWizardPage()
    {
        super("Diagram Type");
        setTitle("Violet UML Editor");
        setDescription("Select a diagram type");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        this.composite = new SelectionWizardComposite(parent);
        setControl(composite);
    }

    /**
     * @return file extension for the selected diagram type
     */
    public String getSelectedDiagramFileExtention()
    {
        return this.composite.getSelectedDiagramPlugin().getFileExtension();
    }

    /**
     * @return graph class for the selected diagram type
     */
    public Class<? extends Graph> getSelectedGraphType()
    {
        return this.composite.getSelectedDiagramPlugin().getGraphClass();
    }

    /**
     * wizard composite
     */
    private SelectionWizardComposite composite;
}
