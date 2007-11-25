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
