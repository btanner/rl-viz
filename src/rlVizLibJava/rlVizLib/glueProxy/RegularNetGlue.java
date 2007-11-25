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

import rlglue.types.Observation_action;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation_action_terminal;
import rlglue.types.State_key;
import rlglue.RLGlue;

public class RegularNetGlue implements RLGlueProxyInterface {

	public String RL_agent_message(String message) {
		return RLGlue.RL_agent_message(message);
	}

	public void RL_cleanup() {
		RLGlue.RL_cleanup();
	}

	public String RL_env_message(String message) {
		return RLGlue.RL_env_message(message);
	}

	public void RL_episode(int numSteps) {
		RLGlue.RL_episode(numSteps);
	}

	public void RL_freeze() {
		RLGlue.RL_freeze();
	}

	public Random_seed_key RL_get_random_seed() {
		return RLGlue.RL_get_random_seed();
	}

	public State_key RL_get_state() {
		return RLGlue.RL_get_state();
	}

	public void RL_init() {
		RLGlue.RL_init();
	}

	public int RL_num_episodes() {
		return RLGlue.RL_num_episodes();
	}

	public int RL_num_steps() {
		return RLGlue.RL_num_steps();
	}

	public double RL_return() {
		return RLGlue.RL_return();
	}

	public void RL_set_random_seed(Random_seed_key rsk) {
		RLGlue.RL_set_random_seed(rsk);
	}

	public void RL_set_state(State_key sk) {
		RLGlue.RL_set_state(sk);
		}

	public Observation_action RL_start() {
		return RLGlue.RL_start();
		}

	public Reward_observation_action_terminal RL_step() {
		return RLGlue.RL_step();
	}

}
