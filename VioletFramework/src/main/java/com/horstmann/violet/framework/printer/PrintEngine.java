package com.horstmann.violet.framework.printer;

import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import com.horstmann.violet.framework.diagram.IGraph;
import com.horstmann.violet.framework.dialog.DialogFactory;
import com.horstmann.violet.framework.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.resources.annotation.ResourceBundleBean;

public class PrintEngine
{

    public PrintEngine(IGraph graph)
    {
        ResourceBundleInjector.getInjector().inject(this);
        this.graph = graph;
    }

    public void start()
    {
        PrintPanel printPanel = new PrintPanel(this.graph);
        JOptionPane optionPane = new JOptionPane();
        optionPane.setOptions(new String[]
        {
            this.printCloseText
        });
        optionPane.setMessage(printPanel);
        optionPane.setBorder(new EmptyBorder(0, 0, 10, 0));
        DialogFactory.getInstance().showDialog(optionPane, this.printTitle, true);
    }

    @ResourceBundleBean(key = "dialog.print.cancel.text", resourceReference = PrintPanel.class)
    private String printCloseText;
    
    @ResourceBundleBean(key = "dialog.print.print.text", resourceReference = PrintPanel.class)
    private String printTitle;
    
    private IGraph graph;

}
