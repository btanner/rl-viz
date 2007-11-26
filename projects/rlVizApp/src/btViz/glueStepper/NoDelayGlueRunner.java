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

import btViz.RLGlueLogic;

public class NoDelayGlueRunner implements GlueRunner,Runnable {
	RLGlueLogic theGlueLogic=null;
	Thread theThread=null;
	
	volatile boolean shouldDie=false;
	
	public NoDelayGlueRunner(RLGlueLogic theGlueLogic){
		this.theGlueLogic=theGlueLogic;	
	}

	public void start() {
		theThread=new Thread(this);
		theThread.start();

	}

	public void stop() {
		shouldDie=true;
		try {
			theThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(!shouldDie)theGlueLogic.step();
	}

}
