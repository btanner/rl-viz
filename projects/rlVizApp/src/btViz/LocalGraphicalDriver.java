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

import btViz.frames.RLVizFrame;
import java.io.IOException;

import rlVizLib.glueProxy.RLGlueProxy;

import agentShell.AgentShell;

import environmentShell.EnvironmentShell;
import javax.swing.UIManager;

public class LocalGraphicalDriver {

	public static void main(String [] args) throws IOException {
            

            //Setup what loaders we want
		RLVizPreferences.getInstance().setDynamicAgentLoading(true);
		RLVizPreferences.getInstance().setDynamicEnvironmentLoading(true);

		EnvironmentShell E= new EnvironmentShell();
		AgentShell A = new AgentShell();
		RLGlueProxy.useLocalGlue(E, A);
		
		

		new RLVizFrame();
	}
}
