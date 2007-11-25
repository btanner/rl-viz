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

import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.GenericMessageParser;
import rlVizLib.messaging.NotAnRLVizMessageException;

public class AgentShellMessageParser extends GenericMessageParser{

	public static AgentShellMessages parseMessage(String theMessage) throws NotAnRLVizMessageException{
		GenericMessage theGenericMessage=new GenericMessage(theMessage);
		return AgentShellMessageParser.makeMessage(theGenericMessage);
	}

	public static AgentShellMessages makeMessage(GenericMessage theGenericMessage) {
		int cmdId=theGenericMessage.getTheMessageType();
		if(cmdId==AgentShellMessageType.kAgentShellListRequest.id()) 				return new AgentShellListRequest(theGenericMessage);
		if(cmdId==AgentShellMessageType.kAgentShellLoad.id()) 				return new AgentShellLoadRequest(theGenericMessage);
		if(cmdId==AgentShellMessageType.kAgentShellUnload.id()) 				return new AgentShellUnLoadRequest(theGenericMessage);


		System.out.println("AgentShellMessageParser - unknown query type: "+cmdId);
		Thread.dumpStack();
		System.exit(1);
		return null;
	}
}
