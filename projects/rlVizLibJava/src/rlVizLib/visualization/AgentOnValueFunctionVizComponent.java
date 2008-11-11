/*
Copyright 2007 Brian Tanner
brian@tannerpages.com
http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

  
package rlVizLib.visualization;
import rlVizLib.visualization.interfaces.AgentOnValueFunctionDataProvider;
import rlVizLib.utilities.UtilityShop;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


public class AgentOnValueFunctionVizComponent implements PollingVizComponent {
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
