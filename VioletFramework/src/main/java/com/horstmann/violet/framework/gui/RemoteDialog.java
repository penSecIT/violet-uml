package com.horstmann.violet.framework.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RemoteDialog extends JPanel
{

    public RemoteDialog()
    {
        setLayout(new GridLayout(3, 2));
        add(urlText);
        add(url);
        add(idText);
        add(id);
       
        setVisible(true);

    }

    public JTextField getId()
    {
        return id;
    }

    public void setId(JTextField id)
    {
        this.id = id;
    }

    public JLabel getIdText()
    {
        return idText;
    }

    public void setIdText(JLabel idText)
    {
        this.idText = idText;
    }





    public JTextField getUrl()
    {
        return url;
    }

    public void setUrl(JTextField url)
    {
        this.url = url;
    }

    public JLabel getUrlText()
    {
        return urlText;
    }

    public void setUrlText(JLabel urlText)
    {
        this.urlText = urlText;
    }


    private JLabel urlText = new JLabel("URL:");
    private JTextField url = new JTextField(40);
    private JLabel idText = new JLabel("ID session :");
    private JTextField id = new JTextField(40);

}
