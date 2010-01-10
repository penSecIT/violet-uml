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

package com.horstmann.violet.framework.propertyeditor.customeditor;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.horstmann.violet.framework.resources.ResourceBundleConstant;
import com.horstmann.violet.product.diagram.common.DiagramLink;

/**
 * A PropertyEditor for FileLink objects that lets the user select a file and/or open it.
 * 
 * @author Alexandre de Pellegrin
 */
public class DiagramLinkEditor extends PropertyEditorSupport
{

    /** The file chooser used for selecting files */
    protected JFileChooser m_FileChooser;

    /** The panel displayed */
    private JPanel m_Panel;

    /** Ressource bundle */
    private ResourceBundle resourceBundle;

    /**
     * Returns a representation of the current property value as java source.
     * 
     * @return a value of type 'String'
     */
    public String getJavaInitializationString()
    {

        DiagramLink fl = (DiagramLink) getValue();
        if (fl == null)
        {
            return "null";
        }

        return "new File(\"" + fl.getURL().getFile() + "\")";
    }

    /**
     * Returns true because we do support a custom editor.
     * 
     * @return true
     */
    public boolean supportsCustomEditor()
    {
        return true;
    }

    /**
     * Gets the custom editor component.
     * 
     * @return a value of type 'java.awt.Component'
     */
    public java.awt.Component getCustomEditor()
    {
        if (this.m_Panel == null)
        {
            this.m_Panel = new JPanel();
            this.m_Panel.add(this.getFileChooser());
            JButton goButton = new JButton(this.getResourceBundle().getString("file.link.open.text"));
            goButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    DiagramLink newVal = (DiagramLink) getValue();
                    if (newVal != null && newVal.getURL() != null)
                    {
                        newVal.setOpenFlag(new Boolean(true));
                        setValue(newVal);
                        firePropertyChange();
                    }
                }
            });
            this.m_Panel.add(goButton);
        }
        return this.m_Panel;

    }

    private JFileChooser getFileChooser()
    {
        if (m_FileChooser == null)
        {
            DiagramLink currentFileLink = (DiagramLink) getValue();
            if (currentFileLink != null)
            {
                m_FileChooser = new JFileChooser();
                URL url = currentFileLink.getURL();
                m_FileChooser.setSelectedFile(new File(url.getFile()));
            }
            else
            {
                m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
            }
            m_FileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            m_FileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            m_FileChooser.setFileFilter(new VioletFileFilter());
            m_FileChooser.setControlButtonsAreShown(false);
            m_FileChooser.setAcceptAllFileFilterUsed(false);
            m_FileChooser.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String cmdString = e.getActionCommand();
                    if (cmdString.equals(JFileChooser.APPROVE_SELECTION))
                    {
                        try
                        {
                            DiagramLink newVal = new DiagramLink();
                            newVal.setURL(m_FileChooser.getSelectedFile().toURI().toURL());
                            setValue(newVal);
                            firePropertyChange();
                        }
                        catch (MalformedURLException e1)
                        {
                            // Humm... we tried
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }
        return m_FileChooser;
    }

    /**
     * Returns true since this editor is paintable.
     * 
     * @return true.
     */
    public boolean isPaintable()
    {
        return true;
    }

    /**
     * Paints a representation of the current Object.
     * 
     * @param gfx the graphics context to use
     * @param box the area we are allowed to paint into
     */
    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box)
    {

        FontMetrics fm = gfx.getFontMetrics();
        int vpad = (box.height - fm.getHeight()) / 2;
        DiagramLink fl = (DiagramLink) getValue();
        String val = "No file";
        URL url = fl.getURL();
        if (fl != null && url != null)
        {
            val = url.getFile();
        }
        gfx.drawString(val, 2, fm.getHeight() + vpad);
    }

    private ResourceBundle getResourceBundle()
    {
        if (this.resourceBundle == null)
        {
            this.resourceBundle = ResourceBundle.getBundle(ResourceBundleConstant.OTHER_STRINGS, Locale.getDefault());
        }
        return this.resourceBundle;
    }

    private class VioletFileFilter extends javax.swing.filechooser.FileFilter
    {

        private String fileExtension;

        public boolean accept(File file)
        {
            String filename = file.getName();
            return (filename.endsWith(this.getFileExtension()) || file.isDirectory());
        }

        public String getDescription()
        {
            return "*" + this.getFileExtension();
        }

        private String getFileExtension()
        {
            if (this.fileExtension == null)
            {
                ResourceBundle rb = ResourceBundle.getBundle(ResourceBundleConstant.FILE_STRINGS, Locale.getDefault());
                this.fileExtension = rb.getString("files.global.extension");
            }
            return this.fileExtension;
        }
    }

}
