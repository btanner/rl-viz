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
package rlVizLib.glueProxy;

import rlglue.agent.Agent;
import rlglue.environment.Environment;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Observation_action;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.Reward_observation_action_terminal;
import rlglue.types.State_key;

public class LocalGlue implements RLGlueProxyInterface {
	Environment E=null;
	Agent A=null;

	Action lastAction=null;

	int steps=0;
	
	public LocalGlue(Environment E, Agent A){
		this.E=E;
		this.A=A;
	}



	public synchronized Reward_observation_action_terminal RL_step()	{
		Reward_observation RO=E.env_step(lastAction);
		if(RO.terminal==1){
			A.agent_end(RO.r);
		}else{
			steps++;
			lastAction=A.agent_step(RO.r, RO.o);
		}
		return new Reward_observation_action_terminal(RO.r,RO.o,lastAction, RO.terminal);
	}

	public synchronized String RL_env_message(String theString){
		return E.env_message(theString);
	}
	public synchronized String RL_agent_message(String theString){
		return A.agent_message(theString);
	}

	public synchronized void RL_init() {
		A.agent_init(E.env_init());
	}
	public synchronized Observation_action RL_start() {
		steps=1;
		Observation o=E.env_start();
		lastAction=A.agent_start(o);
		Observation_action ao=new Observation_action(o, lastAction);
		return ao;
	}



	public synchronized void RL_cleanup() {
            E.env_cleanup();
            A.agent_cleanup();
	}


	public synchronized void RL_episode(int numSteps) {
		Observation o=E.env_start();
		lastAction=A.agent_start(o);

		int whatStep=1;
		boolean terminal=false;
		
		while(!terminal&&(whatStep<numSteps || numSteps<0)){
			Reward_observation RO=E.env_step(lastAction);
			terminal=(RO.terminal==1);
			if(terminal){
				A.agent_end(RO.r);
			}else{
				whatStep++;
				lastAction=A.agent_step(RO.r, RO.o);
			}
		}
		steps=whatStep;
	}

	public synchronized void RL_freeze() { 
            A.agent_freeze();
	}

	public synchronized Random_seed_key RL_get_random_seed() {
            return E.env_get_random_seed();
	}

	public synchronized State_key RL_get_state() {
            return E.env_get_state();
	}



	public synchronized int RL_num_episodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	public synchronized int RL_num_steps() {
		return steps;
	}

	public synchronized double RL_return() {
		// TODO Auto-generated method stub
		return 0;
	}

	public synchronized void RL_set_random_seed(Random_seed_key rsk) {
            E.env_set_random_seed(rsk);
	}

	public synchronized void RL_set_state(State_key sk) {
            E.env_set_state(sk);
	}



}
