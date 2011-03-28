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

package com.horstmann.violet.framework.display.propertyeditor.customeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.horstmann.violet.product.diagram.abstracts.property.MultiLineString;

/**
 * A property editor for the MultiLineString type.
 */
public class MultiLineStringEditor extends PropertyEditorSupport
{
    public boolean supportsCustomEditor()
    {
        return true;
    }

    public Component getCustomEditor()
    {
        final MultiLineString mls = (MultiLineString) getValue();
        final JPanel panel = new JPanel();
        final JTextArea textArea = new JTextArea(ROWS, COLUMNS);

        textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, tab);
        textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, shiftTab);

        textArea.setText(mls.getText());
        textArea.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                mls.setText(textArea.getText());
                firePropertyChange();
            }

            public void removeUpdate(DocumentEvent e)
            {
                mls.setText(textArea.getText());
                firePropertyChange();
            }

            public void changedUpdate(DocumentEvent e)
            {
            }
        });
        panel.add(new JScrollPane(textArea));
        return panel;
    }

    private static final int ROWS = 5;
    private static final int COLUMNS = 30;

    private static Set<KeyStroke> tab = new HashSet<KeyStroke>(1);
    private static Set<KeyStroke> shiftTab = new HashSet<KeyStroke>(1);
    static
    {
        tab.add(KeyStroke.getKeyStroke("TAB"));
        shiftTab.add(KeyStroke.getKeyStroke("shift TAB"));
    }
}
