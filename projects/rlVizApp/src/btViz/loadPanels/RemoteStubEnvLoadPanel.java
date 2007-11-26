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
package btViz.loadPanels;

import javax.swing.JLabel;
import javax.swing.JPanel;

import btViz.RLGlueLogic;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class RemoteStubEnvLoadPanel implements LoadPanelInterface {

    JPanel thePanel = null;
    RLGlueLogic theGlueConnection = null;

    public RemoteStubEnvLoadPanel(RLGlueLogic theGlueConnection) {
        this.theGlueConnection = theGlueConnection;
        thePanel = new JPanel();
        //
                //Setup the border for the publicPanel 
                //
        TitledBorder titled = null;
        Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        titled = BorderFactory.createTitledBorder(loweredetched, "Dynamic Environment Loading Disabled");
        titled.setTitleJustification(TitledBorder.CENTER);
        thePanel.setBorder(titled);
        thePanel.add(new JLabel("Please run a separate environment process"));

    }

    public JPanel getPanel() {
        return thePanel;
    }

    public boolean load() {
        //This does nothing because this panel is a stub
        theGlueConnection.loadEnvironmentVisualizer();
        return true; // might need to change later if it causes bugs
    }

    public void setEnabled(boolean b) {
        //This is important
        thePanel.setEnabled(b);
    }

    public void updateList() {
    //This does nothing because this panel is a stub
    }

    public boolean canLoad() {
        return true;
    }
}
