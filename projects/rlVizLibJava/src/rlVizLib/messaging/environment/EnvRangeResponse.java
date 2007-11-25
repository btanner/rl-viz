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


public class EnvRangeResponse extends AbstractResponse{
	private Vector<Double> mins=null;
	private Vector<Double> maxs=null;
	
	public EnvRangeResponse(Vector<Double> mins, Vector<Double> maxs){
		this.mins=mins;
		this.maxs=maxs;
	}

	public EnvRangeResponse(String responseMessage) throws NotAnRLVizMessageException {

		GenericMessage theGenericResponse = new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");

		String numValuesToken=obsTokenizer.nextToken();
		int numValues=Integer.parseInt(numValuesToken);


		mins=new Vector<Double>();
		maxs=new Vector<Double>();

		for(int i=0;i<numValues;i++){
			mins.add(Double.parseDouble(obsTokenizer.nextToken()));
			maxs.add(Double.parseDouble(obsTokenizer.nextToken()));
		}

	}

	public String toString() {
		String theResponse="EnvRangeResponse: " + mins.size()+" variables, they are:";
		for(int i=0;i<mins.size();i++){
			theResponse+=" ("+mins.get(i)+","+maxs.get(i)+") ";
		}
		// TODO Auto-generated method stub
		return theResponse;
	}

	public Vector<Double> getMins() {
		return mins;
	}

	public Vector<Double> getMaxs() {
		return maxs;
	}

	@Override
	public String makeStringResponse() {

		String thePayLoadString=mins.size()+":";
		
		for(int i=0;i<mins.size();i++){
			thePayLoadString+=mins.get(i)+":"+maxs.get(i)+":";
		}
		
		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnv.id(),
				EnvMessageType.kEnvResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadString);
		
		return theResponse;
	
		
	}
};