/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

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
package com.horstmann.violet.framework.swingextension;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * Container for the toolbar. Allow to scroll on it with custom buttons
 * 
 * @author alexdp
 * 
 */
public class HorizontalScrollablePanel extends JPanel
{

    public HorizontalScrollablePanel(JComponent innerComponent)
    {
        LayoutManager l = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(l);
        this.add(this.getLeftButton(innerComponent));
        this.add(this.getToolBarJScrollPane(innerComponent));
        this.add(this.getRightButton(innerComponent));
    }

    /**
     * @return scrollpane
     */
    private JScrollPane getToolBarJScrollPane(final JComponent innerComponent)
    {
        if (this.innerJScrollPane == null)
        {
            this.innerJScrollPane = new JScrollPane(innerComponent);

            this.innerJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            this.innerJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

            this.innerJScrollPane.addComponentListener(new ComponentListener()
            {

                public void componentResized(ComponentEvent e)
                {
                    // Show button if necessary
                    if (innerJScrollPane.getWidth() < innerComponent.getWidth())
                    {
                        if (!getLeftButton(innerComponent).isVisible()) getLeftButton(innerComponent).setVisible(true);
                        if (!getRightButton(innerComponent).isVisible()) getRightButton(innerComponent).setVisible(true);
                    }
                    // Hide button if necessary
                    if (innerJScrollPane.getWidth() > innerComponent.getWidth())
                    {
                        if (getLeftButton(innerComponent).isVisible()) getLeftButton(innerComponent).setVisible(false);
                        if (getRightButton(innerComponent).isVisible()) getRightButton(innerComponent).setVisible(false);
                    }
                }

                public void componentMoved(ComponentEvent e)
                {
                    // Nothing to do here
                }

                public void componentShown(ComponentEvent e)
                {
                    // Nothing to do here
                }

                public void componentHidden(ComponentEvent e)
                {
                    // Nothing to do here
                }

            });
        }
        return this.innerJScrollPane;
    }

    /**
     * @return left custom button
     */
    private JButton getLeftButton(JComponent innerComponent)
    {
        if (this.leftButton == null)
        {
            this.leftButton = new JButton()
            {
                public void paint(Graphics g)
                {
                    super.paint(g);
                    int height = getHeight();
                    int width = getWidth();
                    int[] x = new int[3];
                    int[] y = new int[3];
                    x[0] = width - HGAP;
                    y[0] = VGAP;
                    x[1] = HGAP;
                    y[1] = height / 2;
                    x[2] = x[0];
                    y[2] = height - VGAP;
                    g.fillPolygon(x, y, 3);
                }

                private static final int VGAP = 12;
                private static final int HGAP = 6;
            };
            this.leftButton.setMargin(new Insets(0, 15, 0, 0));
            this.leftButton.setMaximumSize(new Dimension(15, Short.MAX_VALUE));
            this.leftButton.addMouseListener(new SlideListener(getToolBarJScrollPane(innerComponent).getHorizontalScrollBar(),
                    SLIDE_LENGTH * -1));
        }
        return this.leftButton;
    }

    /**
     * @return right custom button
     */
    private JButton getRightButton(JComponent innerComponent)
    {
        if (this.rightButton == null)
        {
            this.rightButton = new JButton()
            {
                public void paint(Graphics g)
                {
                    super.paint(g);
                    int height = getHeight();
                    int width = getWidth();
                    int[] x = new int[3];
                    int[] y = new int[3];
                    x[0] = HGAP;
                    y[0] = VGAP;
                    x[1] = width - HGAP;
                    y[1] = height / 2;
                    x[2] = x[0];
                    y[2] = height - VGAP;
                    g.fillPolygon(x, y, 3);
                }

                private static final int VGAP = 12;
                private static final int HGAP = 6;
            };
            this.rightButton.setMargin(new Insets(0, 15, 0, 0));
            this.rightButton.setMaximumSize(new Dimension(15, Short.MAX_VALUE));
            this.rightButton.addMouseListener(new SlideListener(getToolBarJScrollPane(innerComponent).getHorizontalScrollBar(),
                    SLIDE_LENGTH));
        }
        return this.rightButton;
    }

    /**
     * This inner class replace the mouse listener and manage sliding
     * 
     * @author alexdp
     * 
     */
    private class SlideListener implements MouseListener
    {

        /**
         * @param bar scrollbar to listen
         * @param slideLength length to slide each timean event is raised
         */
        public SlideListener(JScrollBar bar, int slideLength)
        {
            this.slider = new ScrollBarSlider(bar, slideLength);
            this.slider.start();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e)
        {
            // Slides one time
            this.slider.restart();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e)
        {
            // Starts continus sliding
            this.slider.enableInfiniteSliding();
            this.slider.restart();
        }

        public void mouseReleased(MouseEvent e)
        {
            // Stops current continus sliding
            slider.disableInifinteSliding();
        }

        public void mouseEntered(MouseEvent e)
        {
            // Nothing to do
        }

        public void mouseExited(MouseEvent e)
        {
            // Nothing to do
        }

        /**
         * the objet that performs sliding
         */
        private ScrollBarSlider slider;

    }

    /**
     * Inner that that performs sliding in a separate thread
     * 
     * @author alexdp
     * 
     */
    private class ScrollBarSlider extends Thread
    {

        /**
         * @param bar scrollbar to use
         * @param slideLength length to slide each timean event is raised
         */
        public ScrollBarSlider(JScrollBar bar, int slideLength)
        {
            this.bar = bar;
            this.slideLength = slideLength;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            while (true)
            {
                if (!repeatAction)
                {
                    this.sleeper.sleep();
                }
                performSlide();
            }
        }

        /**
         * Performs sliding in a limited duration
         */
        private void performSlide()
        {
            int initialBarValue = this.bar.getValue();
            long initialTime = this.getTime();
            long currentTime = initialTime;
            long endingTime = initialTime + TOTAL_PERFORM_DELAY;
            while (currentTime < endingTime)
            {
                currentTime = this.getTime();
                long barValue = initialBarValue + (currentTime - initialTime) * slideLength / (endingTime - initialTime);
                if ((slideLength < 0 && barValue < bar.getValue()) || (slideLength >= 0 && bar.getValue() < barValue))
                {
                    bar.setValue((int) barValue);
                }
                try
                {
                    Thread.sleep(SLEEP_DELAY);
                }
                catch (InterruptedException e)
                {
                    // TODO : log with log4j
                }

            }
        }

        /**
         * @return current timestamp in ms
         */
        private long getTime()
        {
            return (new Date()).getTime();
        }

        /**
         * Enables infinite sliding
         */
        public synchronized void enableInfiniteSliding()
        {
            this.repeatAction = true;
        }

        /**
         * Disable infinite sliding
         */
        public synchronized void disableInifinteSliding()
        {
            this.repeatAction = false;
        }

        /**
         * Restart jobs stack running
         */
        public void restart()
        {
            this.sleeper.wakeUp();
        }

        /**
         * The scrollbar to use
         */
        private JScrollBar bar;
        /**
         * length to slide each timean event is raised
         */
        private int slideLength;
        /**
         * delay betwenn two inner slide actions
         */
        private static final int SLEEP_DELAY = 10;
        /**
         * Time allowed to perform sliding
         */
        private static final int TOTAL_PERFORM_DELAY = 300;
        /**
         * Manager infinite sliding
         */
        private boolean repeatAction = false;

        private ThreadSleeper sleeper = new ThreadSleeper();

    }

    /**
     * The sleeper makes its owner thread sleep or wake up
     * 
     * @author alexdp
     * 
     */
    private class ThreadSleeper
    {

        /**
         * Perform a wait() that forces the owner thread to wait until a notify() sent on this object
         */
        public synchronized void sleep()
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }

        /**
         * Send s notify on this object to wake up the waiting owner thread
         */
        public synchronized void wakeUp()
        {
            notify();
        }

    }

    /**
     * the scrollpane
     */
    private JScrollPane innerJScrollPane;
    /**
     * left custom button used to perform "scroll to left"
     */
    private JButton leftButton;
    /**
     * right custom button used to perform "scroll to right"
     */
    private JButton rightButton;
    /**
     * length to slide each time an event is raised
     */
    private static final int SLIDE_LENGTH = 50;

}
