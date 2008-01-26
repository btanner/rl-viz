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

import java.io.IOException;

import rlglue.network.Network;
import rlglue.types.Observation_action;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation_action_terminal;
import rlglue.types.State_key;

public interface RLGlueProxyInterface {
	public   void RL_init();
	public   Observation_action RL_start();
	public   Reward_observation_action_terminal RL_step();
	public   void RL_cleanup();
	public   String RL_agent_message(String message);
	public   String RL_env_message(String message);
	public   double RL_return();
	public   int RL_num_steps();
	public   int RL_num_episodes();
	public   void RL_episode(int numSteps);
	public   void RL_freeze();
	public   void RL_set_state(State_key sk);
	public   void RL_set_random_seed(Random_seed_key rsk);
	public   State_key RL_get_state();
	public   Random_seed_key RL_get_random_seed();
}
