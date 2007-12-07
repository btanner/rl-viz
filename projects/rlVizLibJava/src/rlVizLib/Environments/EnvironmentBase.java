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
package rlVizLib.Environments;

import rlglue.environment.Environment;
import rlglue.types.Observation;
import rlglue.types.Reward_observation;


public abstract class EnvironmentBase implements Environment {

	abstract protected Observation makeObservation();
	
	protected Reward_observation makeRewardObservation(double reward, boolean isTerminal){
		Reward_observation RO=new Reward_observation();
		RO.o=makeObservation();
		RO.r=reward;
		
		RO.terminal=1;
		if(!isTerminal)
			RO.terminal=0;

		return RO;
	}
}
