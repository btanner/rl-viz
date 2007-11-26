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

import btViz.RLGlueLogic;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class RemoteStubAgentLoadPanel implements LoadPanelInterface {
JPanel thePanel=null;
RLGlueLogic theGlueConnection=null;

public boolean canLoad(){
    return true;
}

public RemoteStubAgentLoadPanel(RLGlueLogic theGlueConnection){
		thePanel=new JPanel();
                this.theGlueConnection=theGlueConnection;
                   
                //
                //Setup the border for the publicPanel 
                //
              TitledBorder titled=null;
                Border loweredetched= BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
                titled = BorderFactory.createTitledBorder(loweredetched,"Dynamic Agent Loading Disabled");
                titled.setTitleJustification(TitledBorder.CENTER);
                thePanel.setBorder(titled);

                thePanel.add(new JLabel("Please run a separate agent process"));
	}

	public JPanel getPanel() {
		return thePanel;
	}

	public boolean load() {
            theGlueConnection.loadAgentVisualizer();
            return true; //might cause bugs later, dunno what else to return for now
	}


	public void setEnabled(boolean b) {
		//This is important
		thePanel.setEnabled(b);
	}

	public void updateList() {
		//This does nothing because this panel is a stub
	}


}
