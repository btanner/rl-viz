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
package btViz.glueStepper;

import java.util.Timer;
import java.util.TimerTask;

import btViz.RLGlueLogic;

public class FixedIntervalGlueRunner implements GlueRunner {
	Timer currentTimer=null;

	int timeStepDelay=100;
	boolean running=false;

	RLGlueLogic theGlueLogic=null;
	
	public FixedIntervalGlueRunner(RLGlueLogic theGlueLogic, int timeStepDelay){
		this.theGlueLogic=theGlueLogic;
		this.timeStepDelay=timeStepDelay;
	}

	public void start() {
		currentTimer = new Timer();
	    
	    currentTimer.scheduleAtFixedRate(new TimerTask() {
	            public void run() {
	            	theGlueLogic.step();
	            }
	        }, 0, timeStepDelay);		
		
	}

	public void stop() {
		if(currentTimer!=null){
			currentTimer.cancel();
			currentTimer=null;
		}
	}

}
