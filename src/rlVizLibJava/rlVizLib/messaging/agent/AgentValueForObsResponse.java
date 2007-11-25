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
import java.util.Vector;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;


public class AgentValueForObsResponse extends AbstractResponse{
	private Vector<Double> theValues=null;
	

	public AgentValueForObsResponse(Vector<Double> theValues) {
		this.theValues=theValues;
	}




	public AgentValueForObsResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer valueTokenizer = new StringTokenizer(thePayLoadString, ":");
		theValues = new Vector<Double>();
		String numValuesToken=valueTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);

		for(int i=0;i<numValues;i++){
			theValues.add(Double.parseDouble(valueTokenizer.nextToken()));
		}
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String theResponse="AgentValuesForOBs: " + theValues.size()+" values, they are: ";
		for(int i=0;i<theValues.size();i++){
			theResponse+=theValues.get(i)+" ";
		}
		return theResponse;
	}




	/**
	 * @return the theValues
	 */
	public Vector<Double> getTheValues() {
		return theValues;
	}

//	@Override
	
	//So, when you create on of these in an environment, this gives you the response to send
	public String makeStringResponse() {
		StringBuffer thePayLoadBuffer= new StringBuffer();

		thePayLoadBuffer.append(theValues.size());
		thePayLoadBuffer.append(":");

		
		for(int i=0;i<theValues.size();i++){
			thePayLoadBuffer.append(theValues.get(i));
			thePayLoadBuffer.append(':');
		}
		
		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kAgent.id(),
				MessageUser.kBenchmark.id(),
				AgentMessageType.kAgentResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());

		return theResponse;
	}
};