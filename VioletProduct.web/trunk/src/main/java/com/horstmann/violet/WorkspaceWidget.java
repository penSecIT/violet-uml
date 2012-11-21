package com.horstmann.violet;

import java.awt.Graphics;

import javax.swing.JPanel;

import com.horstmann.violet.framework.file.GraphFile;
import com.horstmann.violet.product.diagram.classes.ClassDiagramGraph;
import com.horstmann.violet.workspace.Workspace;
import com.horstmann.violet.workspace.WorkspacePanel;

import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WPaintDevice;
import eu.webtoolkit.jwt.WPaintedWidget;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.utils.WebGraphics2D;

public class WorkspaceWidget extends WPaintedWidget {

	private JPanel mainPanel;
	private WebGraphics2D graphics = new WebGraphics2D(new WPainter());

	
	public WorkspaceWidget() {
        GraphFile graphFile = new GraphFile(ClassDiagramGraph.class);
        Workspace workspace = new Workspace(graphFile);
        WorkspacePanel workspacePanel = workspace.getAWTComponent();
        mainPanel = new JPanel() {
        	public void repaint() {
        		WorkspaceWidget.this.update();    		
        	};
        	@Override
        	public Graphics getGraphics() {
        		return graphics;
        	}
        };
        mainPanel.add(workspacePanel);
        mainPanel.setVisible(true);
	}
	

	@Override
	public void resize(WLength width, WLength height) {
		super.resize(width, height);
		mainPanel.setSize((int) width.toPixels(), (int) height.toPixels());
	}
	
	@Override
	protected void layoutSizeChanged(int width, int height) {
		super.layoutSizeChanged(width, height);
		mainPanel.setSize(width, height);
	}
	
	@Override
	protected void paintEvent(WPaintDevice paintDevice) {
		WPainter painter = new WPainter(paintDevice);
        Graphics graphics = new WebGraphics2D(painter);
        mainPanel.paint(graphics);
	}

}
