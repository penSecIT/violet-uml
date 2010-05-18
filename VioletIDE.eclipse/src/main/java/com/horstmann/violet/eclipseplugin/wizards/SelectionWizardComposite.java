/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.eclipseplugin.wizards;

import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.horstmann.violet.framework.plugin.IDiagramPlugin;
import com.horstmann.violet.framework.plugin.PluginRegistry;
import com.horstmann.violet.framework.spring.SpringDependencyInjector;
import com.horstmann.violet.framework.spring.annotation.SpringBean;

/**
 * Eclipse plugin Wizard GUI
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class SelectionWizardComposite extends Composite
{

    /**
     * Default constructor
     * 
     * @param parent component
     * @param UMLDiagramPanel swing panel
     */
    public SelectionWizardComposite(final Composite parent)
    {
        super(parent, SWT.EMBEDDED | SWT.BORDER);

        SpringDependencyInjector.getInjector().inject(this);
        
        this.frame = SWT_AWT.new_Frame(this);
        this.frame.add(getWizardPanel());
        this.frame.setVisible(true);
        this.frame.pack();

        GridData gridData2 = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(gridData2);

    }

    /**
     * @return swing panel containing a radio button group needed to select a kind of diagram
     */
    private JPanel getWizardPanel()
    {
        JPanel wizardPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup group = new ButtonGroup();
        for (final IDiagramPlugin aDiagramPlugin : this.pluginRegistry.getDiagramPlugins())
        {
            final JRadioButton aRadioButton = new JRadioButton(aDiagramPlugin.getName());
            aRadioButton.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (aRadioButton.isSelected())
                    {
                        selectedDiagramPlugin = aDiagramPlugin;
                    }
                }
            });
            // Force to select at least the first button
            if (selectedDiagramPlugin == null)
            {
                selectedDiagramPlugin = aDiagramPlugin;
                aRadioButton.setSelected(true);
            }
            group.add(aRadioButton);
            wizardPanel.add(aRadioButton);

        }
        return wizardPanel;
    }

    /**
     * @return currently selected diagram plugin
     */
    public IDiagramPlugin getSelectedDiagramPlugin()
    {
        return this.selectedDiagramPlugin;
    }

    /**
     * Selected diagram plugin 
     */
    private IDiagramPlugin selectedDiagramPlugin;

    /**
     * Plugin registry
     */
    @SpringBean
    private PluginRegistry pluginRegistry;

    /**
     * Diagram Panel
     */
    private Frame frame;

}
