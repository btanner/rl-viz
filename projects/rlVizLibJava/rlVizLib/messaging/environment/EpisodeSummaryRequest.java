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
package rlVizLib.messaging.environment;


import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.interfaces.HasAVisualizerInterface;
import rlVizLib.messaging.interfaces.ProvidesEpisodeSummariesInterface;
import rlglue.environment.Environment;

public class EpisodeSummaryRequest extends EnvironmentMessages{

	public EpisodeSummaryRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static EpisodeSummaryResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvQueryEpisodeSummary.id(),
				MessageValueType.kNone.id(),
				"NULL");

		String responseMessage=RLGlueProxy.RL_env_message(theRequest);

		EpisodeSummaryResponse theResponse;
		try {
			theResponse = new EpisodeSummaryResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			//if we didn't get back anything good from the environment, we'll assume its supporting version 0.0 of rlViz :P
			theResponse= new EpisodeSummaryResponse();
		}
		return theResponse;
	}

	@Override
	public String handleAutomatically(Environment theEnvironment) {
		ProvidesEpisodeSummariesInterface castedEnv = (ProvidesEpisodeSummariesInterface)theEnvironment;
		EpisodeSummaryResponse theResponse=new EpisodeSummaryResponse(castedEnv.getEpisodeSummary());
		return theResponse.makeStringResponse();
	}

	@Override
	public boolean canHandleAutomatically(Object theEnvironment) {
		return (theEnvironment instanceof ProvidesEpisodeSummariesInterface);
	}
	
	
}
