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
package rlVizLib.general;

import rlVizLib.glueProxy.RLGlueProxy;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Observation_action;
import rlglue.types.Reward_observation_action_terminal;

/*
 * TinyGlue has the distinct priviledge of calling RL_start() and RL_stop().  We can count on TinyGlue to tell us when certain things are new
 * because the step counter or episode counter will go up.
 */
public class TinyGlue{
	
	Observation lastObservation=null;
	Action lastAction=null;
	double lastReward=0.0d;
	
	int episodeNumber=0;
	int timeStep=0;
	int totalSteps=0;

        double returnThisEpisode;
        double totalReturn;
	
	
	//returns true of the episode is over
	synchronized public boolean  step(){

		if(!RLGlueProxy.isInited())
                    RLGlueProxy.RL_init();


		if(RLGlueProxy.isCurrentEpisodeOver()){
			Observation_action firstAO=RLGlueProxy.RL_start();
			lastObservation=firstAO.o;
			lastAction=firstAO.a;
			lastReward=Double.NaN;

			episodeNumber++;
			timeStep=1;
			totalSteps++;
                        returnThisEpisode=0.0d;
		}else{
			totalSteps++;
			timeStep++;
                   
			Reward_observation_action_terminal whatHappened=RLGlueProxy.RL_step();
			lastObservation=whatHappened.o;
			lastAction=whatHappened.a;
			lastReward=whatHappened.r;

                        returnThisEpisode+=lastReward;
                        totalReturn+=lastReward;

		}
		return RLGlueProxy.isCurrentEpisodeOver();
	}

	synchronized public int getEpisodeNumber() {
		return episodeNumber;
	}

	synchronized public int getTotalSteps() {
		return totalSteps;
	}

	synchronized public int getTimeStep() {
		return timeStep;
	}

	synchronized public Observation getLastObservation() {
		return lastObservation;
	}

	synchronized public Action getLastAction() {
		return lastAction;
	}

	synchronized public Double getLastReward() {
		return lastReward;
	}

       synchronized  public double getTotalReturn(){
        return totalReturn;
        }

        synchronized public double getReturnThisEpisode(){
        return returnThisEpisode;
        }
}