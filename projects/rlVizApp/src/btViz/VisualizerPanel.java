/* RLViz Application, a visualizer and dynamic loader for C++ and Java RL-Glue agents/environments
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package btViz;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.VisualizerPanelInterface;

public class VisualizerPanel extends JPanel implements ComponentListener, VisualizerPanelInterface, visualizerLoadListener {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    public static boolean vizLoaded = false;
    AbstractVisualizer theVisualizer = null;
    //Going to use this timer so that all of the cascase resizing that happens is on a delay so that window resizing is more responsive

    Timer resizeChildrenTimer = null;

    public VisualizerPanel(Dimension initialSize) {
        super();
        this.setSize((int) initialSize.getWidth(), (int) initialSize.getHeight());
        addComponentListener(this);

    }

    @Override
	public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        if (theVisualizer != null) {
            g2.drawImage(theVisualizer.getLatestImage(), 0, 0, this);
        }

    }

    public void componentHidden(ComponentEvent arg0) {
    // TODO Auto-generated method stub

    }

    public void componentMoved(ComponentEvent arg0) {
    // TODO Auto-generated method stub

    }

    public void componentResized(ComponentEvent arg0) {
        resizeChildrenTimer = new Timer();
        resizeChildrenTimer.schedule(new TimerTask() {

                    public void run() {
                        tellChildrenToResize();
                    }
                }, 100);

    }

    public void tellChildrenToResize() {
        if (theVisualizer != null) {
            theVisualizer.notifyPanelSizeChange();
        }
    }

    public void componentShown(ComponentEvent arg0) {
    // TODO Auto-generated method stub

    }

    public void startVisualizing() {
        if (theVisualizer != null) {
            theVisualizer.startVisualizing();
        }
    }

    public void stopVisualizing() {
        if (theVisualizer != null) {
            theVisualizer.stopVisualizing();
        }
    }

    public void receiveNotificationVizChanged() {
        this.repaint();
    }

    public void notifyVisualizerLoaded(AbstractVisualizer theNewVisualizer) {
        if (this.theVisualizer != null) {
            this.theVisualizer.stopVisualizing();
        }
        if (theNewVisualizer != null) {
            this.theVisualizer = theNewVisualizer;

            theVisualizer.setParentPanel(this);
            theVisualizer.notifyPanelSizeChange();

            this.setNamedBorder();
            vizLoaded = true;
        } else {
            System.err.println("Failed To Load a Visualizer.");
        }
    }

    public void notifyVisualizerUnLoaded() {
        theVisualizer = null;
    }

    private void setNamedBorder() {
        TitledBorder titled = null;
        Border loweredetched = null;
        loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        titled = BorderFactory.createTitledBorder(loweredetched, theVisualizer.getName());
        setBorder(titled);

    }
}
