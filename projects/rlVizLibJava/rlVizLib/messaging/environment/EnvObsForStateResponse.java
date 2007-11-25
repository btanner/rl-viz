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

import java.util.StringTokenizer;
import java.util.Vector;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.utilities.UtilityShop;
import rlglue.types.Observation;

public class EnvObsForStateResponse extends AbstractResponse{
	private Vector<Observation> theObservations=null;

	
	public EnvObsForStateResponse(Vector<Observation> theObservations) {
		this.theObservations=theObservations;
	}

	
	public EnvObsForStateResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");

		theObservations = new Vector<Observation>();
		String numValuesToken=obsTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);

		for(int i=0;i<numValues;i++){
			String thisObsString=obsTokenizer.nextToken();
			theObservations.add(UtilityShop.buildObservationFromString(thisObsString));
		}
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer bufferedResponse=new StringBuffer();

		bufferedResponse.append("EnvObsForStateResponse: " + theObservations.size()+" observations, they are: (serialized for fun)");

		for(int i=0;i<theObservations.size();i++){
			UtilityShop.serializeObservation(bufferedResponse,theObservations.get(i));
			bufferedResponse.append(" ");
		}

		return bufferedResponse.toString();
	}


	/**
	 * @return the theObservations
	 */
	public Vector<Observation> getTheObservations() {
		return theObservations;
	}

	
	//So, when you create on of these in an environment, this gives you the response to send
	public String makeStringResponse() {
		StringBuffer thePayLoadBuffer= new StringBuffer();

		//Tell them how many
		thePayLoadBuffer.append(theObservations.size());

		for(int i=0;i<theObservations.size();i++){
			thePayLoadBuffer.append(":");
			UtilityShop.serializeObservation(thePayLoadBuffer,theObservations.get(i));
		}

		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnv.id(),
				EnvMessageType.kEnvResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadBuffer.toString());

		return theResponse;
	}
};