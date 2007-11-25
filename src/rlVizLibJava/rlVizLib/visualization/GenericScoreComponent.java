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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.interfaces.GlueStateProvider;
import java.lang.Double;


public class GenericScoreComponent implements VizComponent{
	private GlueStateProvider theGlueStateProvider = null;
	private int lastUpdateTimeStep=-1;
	
	public GenericScoreComponent(GlueStateProvider theVis){
		this.theGlueStateProvider = theVis;
	}

	public void render(Graphics2D g) {
            DecimalFormat myFormatter = new DecimalFormat("##.###");
		//This is some hacky stuff, someone better than me should clean it up
		Font f = new Font("Verdana",0,8);     
		g.setFont(f);
	    //SET COLOR
	    g.setColor(Color.RED);
	    //DRAW STRING
	    AffineTransform saveAT = g.getTransform();
   	    g.scale(.005, .005);
            TinyGlue theGlueState=theGlueStateProvider.getTheGlueState();
            
            //used for rounding
            String theRewardString;
            double preRound;
                preRound = theGlueState.getLastReward();

            if(Double.isNaN(preRound)){
                theRewardString = "None";
            }
            else
                theRewardString = myFormatter.format(preRound);

	    g.drawString("E/S/T/R: " +theGlueState.getEpisodeNumber()+"/"+theGlueState.getTimeStep()+"/"+theGlueState.getTotalSteps()+"/"+theRewardString,0.0f, 10.0f);
	    g.setTransform(saveAT);
	}

	public boolean update() {
		//Only draw if we're on a new time step
		int currentTimeStep=theGlueStateProvider.getTheGlueState().getTotalSteps();
    		if(currentTimeStep!=lastUpdateTimeStep){
			lastUpdateTimeStep=currentTimeStep;
			return true;
		}
		return false;
	}
	
	
}
