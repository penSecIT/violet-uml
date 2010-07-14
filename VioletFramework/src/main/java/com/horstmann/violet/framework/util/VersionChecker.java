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

package com.horstmann.violet.framework.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.horstmann.violet.framework.display.dialog.DialogFactory;
import com.horstmann.violet.framework.injection.resources.ResourceBundleConstant;

/**
 * Checks if the Java version of the current VM is at least a given version.
 */
public class VersionChecker
{
    
    /** Minimum compliant Java version */
    private static final String JAVA_VERSION = "1.6";
    
    /** Needed to display dialog boxes */
    private DialogFactory dialogFactory;
    
    
    /**
     * Checks if the current VM has at least the given version, and exits the program with an error dialog if not.
     * 
     */
    public void check()
    {
        String actualVersion = System.getProperty("java.version");
        boolean versionOk;
        try
        {
            versionOk = versionCompare(actualVersion, JAVA_VERSION) >= 0;
        }
        catch (NumberFormatException exception)
        {
            versionOk = false;
        }
        if (!versionOk)
        {
            ResourceBundle resources = ResourceBundle.getBundle(ResourceBundleConstant.OTHER_STRINGS, Locale.getDefault());
            String versionError = resources.getString("dialog.error_version.text");
            MessageFormat formatter = new MessageFormat(versionError);
            String message = formatter.format(new Object[]
            {
                 JAVA_VERSION
            });
            String title = resources.getString("dialog.error_version.title");
            JOptionPane optionPane = new JOptionPane();
            optionPane.setMessage(message);
            this.dialogFactory.showDialog(optionPane, title, true);
            System.exit(1);
        }
    }

    public static int versionCompare(String v1, String v2)
    {
        StringTokenizer t1 = new StringTokenizer(v1, "._");
        StringTokenizer t2 = new StringTokenizer(v2, "._");
        while (t1.hasMoreTokens())
        {
            if (!t2.hasMoreTokens()) return 1;
            int n1 = Integer.parseInt(t1.nextToken());
            int n2 = Integer.parseInt(t2.nextToken());
            int d = n1 - n2;
            if (d != 0) return d;
        }
        return t2.hasMoreTokens() ? -1 : 0;
    }

    /**
     * Spring setter
     * @param dialogFactory
     */
    public void setDialogFactory(DialogFactory dialogFactory)
    {
        this.dialogFactory = dialogFactory;
    }


}
