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

import java.util.Observable;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Observation_action;
import org.rlcommunity.rlglue.codec.types.Reward_observation_action_terminal;

/*
 * TinyGlue has the distinct priviledge of calling RL_start() and RL_stop().  We can count on TinyGlue to tell us when certain things are new
 * because the step counter or episode counter will go up.
 * 
 * We're moving to an observable implementation so that components can 
 * subscribe to updates, instead of having them poll.
 */
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

public class TinyGlue extends Observable {

    Observation lastObservation = null;
    Action lastAction = null;
    double lastReward = 0.0d;
    int episodeNumber = 0;
    int timeStep = 0;
    int totalSteps = 0;
    double returnThisEpisode;
    double totalReturn;

    private void updateObservers(Object theEvent) {
        setChanged();
        super.notifyObservers(theEvent);
        super.clearChanged();

    }
    //returns true of the episode is over
    public boolean step() {
        if (!RLGlue.isInited()) {
            RLGlue.RL_init();
        }


        if (RLGlue.isCurrentEpisodeOver()) {
            Observation firstObservation = RLGlue.RL_env_start();


            synchronized (this) {
                lastObservation = firstObservation;
                lastReward = Double.NaN;

                episodeNumber++;
                timeStep = 1;
                totalSteps++;
                returnThisEpisode = 0.0d;
            }
            updateObservers(firstObservation);
            Action firstAction = RLGlue.RL_agent_start(firstObservation);

            synchronized (this) {
                lastAction = firstAction;
            }
            updateObservers(firstAction);

        } else {
            Reward_observation_action_terminal ROAT = new Reward_observation_action_terminal();

            System.out.println("Calling env_step");
            Reward_observation_terminal ROT = RLGlue.RL_env_step(lastAction);
            System.out.println("Back from env_step");
            ROAT.o = ROT.getObservation();
            ROAT.r = ROT.getReward();
            ROAT.terminal = ROT.getTerminal();



            synchronized (this) {
                totalSteps++;
                timeStep++;
                lastObservation = ROAT.getObservation();
                lastReward = ROAT.getReward();

                returnThisEpisode += lastReward;
                totalReturn += lastReward;
            }
            System.out.println("\t notifying on ROT");
            updateObservers(ROT);
            System.out.println("\t done notifying on ROT");
            if (ROT.isTerminal()) {
                RLGlue.RL_agent_end(ROT.getReward());
            } else {
            System.out.println("Calling agent_step");
                ROAT.a = RLGlue.RL_agent_step(ROT.getReward(), ROT.getObservation());
                            System.out.println("back from agent_step");

            }

            synchronized (this) {
                if (!ROAT.isTerminal()) {
                    lastAction = ROAT.getAction();
                }
            }
                        System.out.println("\t notifying on ROAT");

            updateObservers(ROAT);
            System.out.println("\t done notifying on ROT");

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

    synchronized public double getTotalReturn() {
        return totalReturn;
    }

    synchronized public double getReturnThisEpisode() {
        return returnThisEpisode;
    }
}