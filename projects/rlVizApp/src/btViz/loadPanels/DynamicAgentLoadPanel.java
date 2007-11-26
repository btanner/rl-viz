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
import rlVizLib.general.ParameterHolder;

public class DynamicAgentLoadPanel extends DynamicLoadPanel {

	public DynamicAgentLoadPanel(RLGlueLogic theGlueConnection){
		super(theGlueConnection,"No Agents Available");
	}
	
	public void updateList(){
		theNames=theGlueConnection.getAgentNameList();
		theParams=theGlueConnection.getAgentParamList();
		
		super.refreshList(theNames, "No Agents Available");
		
	}
        @Override
	public boolean load(String thisName, ParameterHolder thisP) {
		boolean loadCheck = theGlueConnection.loadAgent(thisName,thisP);
                if(loadCheck){
                    theGlueConnection.loadAgentVisualizer();
                    return true;
                }
                return false;
	}

	@Override
	public String getStringType() {
		return "Agent";
	}

}
