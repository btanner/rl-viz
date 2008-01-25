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
package rlVizLib.messaging.agent;


import rlVizLib.general.RLVizVersion;
import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.rlVizCore;
import rlglue.agent.Agent;


public class AgentVersionSupportedRequest extends AgentMessages{

	public AgentVersionSupportedRequest(GenericMessage theMessageObject){
		super(theMessageObject);
	}

	public static AgentVersionSupportedResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kAgent.id(),
				MessageUser.kBenchmark.id(),
				AgentMessageType.kAgentQuerySupportedVersion.id(),
				MessageValueType.kNone.id(),
				"NULL");

		String responseMessage=RLGlueProxy.RL_agent_message(theRequest);

		AgentVersionSupportedResponse theResponse;
		try {
			theResponse = new AgentVersionSupportedResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			//if we didn't get back anything good from the Agent, we'll assume its supporting version 0.0 of rlViz :P
			theResponse= new AgentVersionSupportedResponse(RLVizVersion.NOVERSION);
		}
		return theResponse;
	}

	@Override
	public String handleAutomatically(Agent theAgent) {
                RLVizVersion theVersion=rlVizCore.getRLVizSpecVersionOfClassWhenCompiled(theAgent.getClass());
		AgentVersionSupportedResponse theResponse=new AgentVersionSupportedResponse(theVersion);
		return theResponse.makeStringResponse();
	}

	@Override
	public boolean canHandleAutomatically(Object theAgent) {
            return true;
	}
	
	
}
