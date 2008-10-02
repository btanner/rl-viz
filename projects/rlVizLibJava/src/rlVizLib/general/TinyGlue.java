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

  
package rlVizLib.general;

import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Observation_action;
import org.rlcommunity.rlglue.codec.types.Reward_observation_action_terminal;

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
		if(!RLGlue.isInited())
                    RLGlue.RL_init();


		if(RLGlue.isCurrentEpisodeOver()){
			Observation_action firstAO=RLGlue.RL_start();
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
                   
			Reward_observation_action_terminal whatHappened=RLGlue.RL_step();
			lastObservation=whatHappened.o;
			lastAction=whatHappened.a;
			lastReward=whatHappened.r;

                        returnThisEpisode+=lastReward;
                        totalReturn+=lastReward;

		}
		return RLGlue.isCurrentEpisodeOver();
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