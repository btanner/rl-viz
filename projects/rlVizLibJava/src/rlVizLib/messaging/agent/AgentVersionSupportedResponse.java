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



import java.util.StringTokenizer;

import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;

public class AgentVersionSupportedResponse extends AbstractResponse{
	RLVizVersion theVersion=null;
	

	public AgentVersionSupportedResponse(RLVizVersion theVersion){
		this.theVersion=theVersion;
	}
	

	public AgentVersionSupportedResponse(String responseMessage) throws NotAnRLVizMessageException {
            try{
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		
		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer versionTokenizer = new StringTokenizer(thePayLoadString, ":");

		theVersion=new RLVizVersion(versionTokenizer.nextToken());
             }catch(Exception e){
                throw new NotAnRLVizMessageException();
             }
	}


	@Override
	public String makeStringResponse() {
		StringBuffer thePayLoadBuffer= new StringBuffer();


		thePayLoadBuffer.append(theVersion.serialize());
		
		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kAgent.id(),
				AgentMessageType.kAgentResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());
		
		return theResponse;		
	
}

	public RLVizVersion getTheVersion() {
		return theVersion;
	}

};