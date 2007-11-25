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

import java.util.StringTokenizer;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;


public class AgentShellListResponse extends AbstractResponse{
	private Vector<String> theAgentList = new Vector<String>();
	private Vector<ParameterHolder> theParamList = new Vector<ParameterHolder>();

	public AgentShellListResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse=new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer agentListTokenizer = new StringTokenizer(thePayLoadString, ":");

		String numAgentsToken=agentListTokenizer.nextToken();

		int numAgents=Integer.parseInt(numAgentsToken);

		for(int i=0;i<numAgents;i++){
			theAgentList.add(agentListTokenizer.nextToken());
			theParamList.add(new ParameterHolder(agentListTokenizer.nextToken()));
		}

	}


	public AgentShellListResponse(Vector<String> agentNameVector, Vector<ParameterHolder> agentParamVector) {
		this.theAgentList=agentNameVector;
		this.theParamList=agentParamVector;
	}


	@Override
	public String makeStringResponse() {

		String thePayLoadString=theAgentList.size()+":";

		for(int i=0;i<theAgentList.size();i++){
			thePayLoadString+=theAgentList.get(i)+":";
			if(theParamList.get(i)!=null)
				thePayLoadString+=theParamList.get(i).stringSerialize()+":";
			else
				thePayLoadString+="NULL:";//When we pass this into a parameter holder constructor it will just create an empty param holder
				
		}

		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kAgentShell.id(),
				AgentShellMessageType.kAgentShellResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadString);


		return theResponse;
	}

	public String toString() {
		String theString= "AgentShellList Response: "+theAgentList.toString();
		return theString;
	}


	public Vector<String> getTheAgentList() {
		return theAgentList;
	}
	public Vector<ParameterHolder> getTheParamList() {
		return theParamList;
	}

};