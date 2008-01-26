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
