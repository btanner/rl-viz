/* RL-VizLib, a library for C++ and Java for adding advanced visualization and dynamic capabilities to RL-Glue.
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
package rlVizLib.visualization;
import rlVizLib.visualization.interfaces.AgentOnValueFunctionDataProvider;
import rlVizLib.utilities.UtilityShop;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


public class AgentOnValueFunctionVizComponent implements VizComponent {
	private AgentOnValueFunctionDataProvider dataProvider=null;

	public AgentOnValueFunctionVizComponent(AgentOnValueFunctionDataProvider dataProvider){
		this.dataProvider=dataProvider;
	}

	public void render(Graphics2D g) {
		g.setColor(Color.BLUE);

		double transX=UtilityShop.normalizeValue( dataProvider.getCurrentStateInDimension(0),
				dataProvider.getMinValueForDim(0),
				dataProvider.getMaxValueForDim(0));

		double transY=UtilityShop.normalizeValue( dataProvider.getCurrentStateInDimension(1),
				dataProvider.getMinValueForDim(1),
				dataProvider.getMaxValueForDim(1));


		Rectangle2D agentRect=new Rectangle2D.Double(transX,transY,.02,.02);
		g.fill(agentRect);
	}

	public boolean update() {
		dataProvider.updateAgentState();
		return true;
	}

}
