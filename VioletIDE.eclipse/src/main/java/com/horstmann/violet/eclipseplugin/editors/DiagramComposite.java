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

package com.horstmann.violet.eclipseplugin.editors;

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.horstmann.violet.framework.gui.DiagramPanel;
import com.horstmann.violet.framework.gui.GraphPanel;
import com.horstmann.violet.framework.gui.sidebar.ISideBar;

/**
 * Main Eclipse plugin GUI component
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class DiagramComposite extends Composite
{

    /**
     * Default construcctor
     * 
     * @param parent component
     * @param UMLDiagramPanel swing panel
     */
    public DiagramComposite(final Composite parent, DiagramPanel UMLDiagramPanel)
    {
        super(parent, SWT.EMBEDDED | SWT.BORDER);

        this.UMLDiagramPanel = UMLDiagramPanel;
        this.fixLayoutProblem(this.UMLDiagramPanel);
        this.initializePanel(this.UMLDiagramPanel, parent);
        this.frame = SWT_AWT.new_Frame(this);

        this.frame.setVisible(true);
        this.frame.add(UMLDiagramPanel);
        this.frame.pack();

        GridData gridData2 = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(gridData2);

    }

    /**
     * Retuen UML Graph diagram panel (JPanel)
     * 
     * @return
     */
    private void initializePanel(DiagramPanel diagramPanel, final Composite parent)
    {
        final GraphPanel graphPanel = diagramPanel.view.getGraphPanel(diagramPanel);

        final ISideBar sideBar = diagramPanel.getSideBar();

        this.addListener(SWT.KeyUp, new Listener()
        {

            public void handleEvent(Event event)
            {
                if (event.keyCode == SWT.DEL)
                {
                    graphPanel.removeSelected();
                }
            }

        });

        this.addListener(SWT.MouseWheel, new Listener()
        {
            public void handleEvent(Event event)
            {
                if (event.count > 0)
                {
                    sideBar.getSideToolPanel().selectPreviousButton();
                }
                if (event.count < 0)
                {
                    sideBar.getSideToolPanel().selectNextButton();
                }
            }
        });

        this.addListener(SWT.Activate, new Listener()
        {
            public void handleEvent(Event e)
            {
                setFocus();
            }
        });

    }

    /**
     * Fixes layout (and repaint problem) on side bar collapsing
     * 
     * @param diagramPanel
     */
    private void fixLayoutProblem(DiagramPanel diagramPanel)
    {
        final JComponent sideBar = (JComponent) diagramPanel.getSideBar();
        final JScrollPane scrollPane = diagramPanel.view.getScrollableGraphPanel(diagramPanel);
        sideBar.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent e)
            {
                sideBar.validate();
                scrollPane.validate();
            }
        });
    }

    /**
     * UML diagram panel
     */
    private DiagramPanel UMLDiagramPanel;

    /**
     * Diagram Panel
     */
    private Frame frame;

}
