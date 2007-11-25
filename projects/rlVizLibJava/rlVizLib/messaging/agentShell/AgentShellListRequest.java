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
package rlVizLib.messaging.agentShell;

import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlglue.RLGlue;

public class AgentShellListRequest extends AgentShellMessages{

	public AgentShellListRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}
	
	

	public static AgentShellListResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kAgentShell.id(),
				MessageUser.kBenchmark.id(),
				AgentShellMessageType.kAgentShellListRequest.id(),
				MessageValueType.kNone.id(),
				"NULL");


		String responseMessage=RLGlueProxy.RL_agent_message(theRequest);
		
		AgentShellListResponse theResponse;
		try {
			theResponse = new AgentShellListResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In kAgentShellShellListRequest: response was not an RLViz Message");
			return null;
		}
		return theResponse;


	}
	
}
