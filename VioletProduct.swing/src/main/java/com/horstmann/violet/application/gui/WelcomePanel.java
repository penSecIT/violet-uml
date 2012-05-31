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

package com.horstmann.violet.application.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import com.horstmann.violet.application.menu.FileMenu;
import com.horstmann.violet.application.swingextension.WelcomeButtonUI;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.BeanInjector;
import com.horstmann.violet.framework.injection.bean.ManiocFramework.InjectedBean;
import com.horstmann.violet.framework.injection.resources.ResourceBundleInjector;
import com.horstmann.violet.framework.injection.resources.annotation.ResourceBundleBean;
import com.horstmann.violet.framework.theme.ITheme;
import com.horstmann.violet.framework.theme.ThemeManager;

public class WelcomePanel extends JPanel {

	public WelcomePanel(FileMenu fileMenu) {
		ResourceBundleInjector.getInjector().inject(this);
		BeanInjector.getInjector().inject(this);
		this.fileMenu = fileMenu;

		setOpaque(false);
		setLayout(new BorderLayout());

		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				int x = 34;
				int y = 34;
				int w = getWidth() - 68;
				int h = getHeight() - 68;
				int arc = 30;

				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(Color.WHITE);
				g2.fillRoundRect(x, y, w, h, arc, arc);

				g2.setStroke(new BasicStroke(3f));
				g2.setColor(themeManager.getTheme().getWelcomeBackgroundEndColor());
				g2.drawRoundRect(x, y, w, h, arc, arc);

				g2.dispose();
			}
		};
		panel.setOpaque(false);
		panel.setLayout(new GridBagLayout());

		JTextPane editorPane = new JTextPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		try {
			editorPane.setPage(this.getClass().getResource("WelcomePanel.html"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// JEditorPane editorPane = new JEditorPane("text/html",
		// "<a href='#'>Welcome</a>");
		editorPane.setOpaque(false);
		editorPane.setBorder(new EmptyBorder(40, 40, 40, 40));
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (HyperlinkEvent.EventType.ACTIVATED != e.getEventType()) {
					return;
				}
				URL url = e.getURL();
				System.out.println(e.getDescription());
				System.out.println(leftPanelIcon);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(40, 40, 40, 40);
		c.fill = GridBagConstraints.BOTH;

		panel.add(getWelcomeSVGImage(), c);

		add(panel, BorderLayout.CENTER);
		add(getFootTextPanel(), BorderLayout.SOUTH);

	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Paint currentPaint = g2.getPaint();
		ITheme cLAF = this.themeManager.getTheme();
		GradientPaint paint = new GradientPaint(getWidth() / 2, -getHeight() / 4, cLAF.getWelcomeBackgroundStartColor(), getWidth() / 2, getHeight() + getHeight() / 4,
				cLAF.getWelcomeBackgroundEndColor());
		g2.setPaint(paint);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setPaint(currentPaint);
		super.paint(g);
	}

	private JPanel getLeftPanel() {
		if (this.leftPanel == null) {
			leftPanel = new JPanel();
			leftPanel.setOpaque(false);
			leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
			leftPanel.setBorder(new EmptyBorder(0, 0, 0, 45));

			final JMenu newMenu = this.fileMenu.getFileNewMenu();
			for (int i = 0; i < newMenu.getItemCount(); i++) {
				final JMenuItem item = newMenu.getItem(i);
				String label = item.getText();
				JButton newDiagramShortcut = new JButton(label.toLowerCase());
				newDiagramShortcut.setUI(new WelcomeButtonUI());
				newDiagramShortcut.setAlignmentX(Component.RIGHT_ALIGNMENT);
				newDiagramShortcut.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						item.doClick();
					}
				});
				leftPanel.add(newDiagramShortcut);
			}

		}
		return this.leftPanel;
	}

	private JPanel getRightPanel() {
		if (this.rightPanel == null) {
			this.rightPanel = new JPanel();
			this.rightPanel.setOpaque(false);
			this.rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
			this.rightPanel.setBorder(new EmptyBorder(0, 45, 0, 45));

			final JMenu recentMenu = this.fileMenu.getFileRecentMenu();
			for (int i = 0; i < recentMenu.getItemCount(); i++) {
				final JMenuItem item = recentMenu.getItem(i);
				String label = item.getText();
				JButton fileShortcut = new JButton(label.toLowerCase());
				fileShortcut.setUI(new WelcomeButtonUI());
				fileShortcut.setAlignmentX(Component.LEFT_ALIGNMENT);
				fileShortcut.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						item.doClick();
					}
				});
				rightPanel.add(fileShortcut);
			}

		}
		return this.rightPanel;
	}

	private JPanel getLeftTitlePanel() {
		if (this.leftTitlePanel == null) {
			JLabel icon = new JLabel();
			icon.setIcon(this.leftPanelIcon);

			JLabel title = new JLabel(this.fileMenu.getFileNewMenu().getText().toLowerCase());
			ITheme cLAF = ThemeManager.getInstance().getTheme();
			title.setFont(cLAF.getWelcomeBigFont());
			title.setForeground(cLAF.getWelcomeBigForegroundColor());
			title.setBorder(new EmptyBorder(0, 30, 0, 0));

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.add(icon);
			panel.add(title);
			panel.setOpaque(false);

			this.leftTitlePanel = new JPanel();
			this.leftTitlePanel.setOpaque(false);
			this.leftTitlePanel.setLayout(new BorderLayout());
			this.leftTitlePanel.add(panel, BorderLayout.EAST);
			this.leftTitlePanel.setBorder(new EmptyBorder(0, 0, 30, 45));
		}
		return this.leftTitlePanel;
	}

	private JPanel getRightTitlePanel() {
		if (this.rightTitlePanel == null) {
			JLabel icon = new JLabel();
			icon.setIcon(this.rightPanelIcon);
			icon.setAlignmentX(Component.LEFT_ALIGNMENT);

			JLabel title = new JLabel(this.fileMenu.getFileRecentMenu().getText().toLowerCase());
			ITheme cLAF = this.themeManager.getTheme();
			title.setFont(cLAF.getWelcomeBigFont());
			title.setForeground(cLAF.getWelcomeBigForegroundColor());
			title.setBorder(new EmptyBorder(0, 0, 0, 30));

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.add(title);
			panel.add(icon);
			panel.setOpaque(false);

			this.rightTitlePanel = new JPanel();
			this.rightTitlePanel.setOpaque(false);
			this.rightTitlePanel.setLayout(new BorderLayout());
			this.rightTitlePanel.add(panel, BorderLayout.WEST);
			this.rightTitlePanel.setBorder(new EmptyBorder(0, 45, 30, 0));
		}
		return this.rightTitlePanel;
	}

	private JPanel getFootTextPanel() {
		if (this.footTextPanel == null) {
			this.footTextPanel = new JPanel();
			this.footTextPanel.setOpaque(false);
			this.footTextPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
			this.footTextPanel.setLayout(new BoxLayout(this.footTextPanel, BoxLayout.Y_AXIS));
			this.footTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

			JLabel text = new JLabel(this.footText);
			ITheme cLAF = this.themeManager.getTheme();
			text.setFont(cLAF.getWelcomeSmallFont());
			text.setForeground(cLAF.getWelcomeBigForegroundColor());
			text.setAlignmentX(Component.CENTER_ALIGNMENT);

			this.footTextPanel.add(text);
		}

		return this.footTextPanel;
	}

	private JSVGCanvas getWelcomeSVGImage() {
		if (this.welcomeSVGImage == null) {
			this.welcomeSVGImage = new JSVGCanvas();
			InputStream templateStream = this.getClass().getResourceAsStream("test.svg");
			String parser = XMLResourceDescriptor.getXMLParserClassName();
			SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
			SVGDocument doc = null;
			try {
				doc = f.createSVGDocument(null, templateStream);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			this.welcomeSVGImage.setSVGDocument(doc);
		}
		return this.welcomeSVGImage;
	}

	private JSVGCanvas welcomeSVGImage;

	private JPanel footTextPanel;;

	private JPanel rightTitlePanel;

	private JPanel leftTitlePanel;

	private JPanel leftPanel;

	private JPanel rightPanel;

	private FileMenu fileMenu;

	@ResourceBundleBean(key = "welcomepanel.new_diagram.icon")
	private ImageIcon leftPanelIcon;

	@ResourceBundleBean(key = "welcomepanel.recent_files.icon")
	private ImageIcon rightPanelIcon;

	@ResourceBundleBean(key = "welcomepanel.foot_text")
	private String footText;

	@InjectedBean
	private ThemeManager themeManager;

}
