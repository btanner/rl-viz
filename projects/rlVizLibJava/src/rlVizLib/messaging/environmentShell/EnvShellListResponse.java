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
package rlVizLib.messaging.environmentShell;

import java.util.StringTokenizer;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;


public class EnvShellListResponse extends AbstractResponse{
	private Vector<String> theEnvList = new Vector<String>();
	private Vector<ParameterHolder> theParamList = new Vector<ParameterHolder>();

	public EnvShellListResponse(String responseMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse=new GenericMessage(responseMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer envListTokenizer = new StringTokenizer(thePayLoadString, ":");

		String numEnvironmentsToken=envListTokenizer.nextToken();

		int numEnvironments=Integer.parseInt(numEnvironmentsToken);

		for(int i=0;i<numEnvironments;i++){
			theEnvList.add(envListTokenizer.nextToken());
			theParamList.add(new ParameterHolder(envListTokenizer.nextToken()));
		}

	}


	public EnvShellListResponse(Vector<String> envNameVector, Vector<ParameterHolder> envParamVector) {
		this.theEnvList=envNameVector;
		this.theParamList=envParamVector;
	}


	@Override
	public String makeStringResponse() {

		String thePayLoadString=theEnvList.size()+":";

		for(int i=0;i<theEnvList.size();i++){
			thePayLoadString+=theEnvList.get(i)+":";
			if(theParamList.get(i)!=null)
				thePayLoadString+=theParamList.get(i).stringSerialize()+":";
			else
				thePayLoadString+="NULL:";//When we pass this into a parameter holder constructor it will just create an empty param holder
				
		}

		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnvShell.id(),
				EnvShellMessageType.kEnvShellResponse.id(),
				MessageValueType.kStringList.id(),
				thePayLoadString);


		return theResponse;
	}

	public String toString() {
		String theString= "EnvShellList Response: "+theEnvList.toString();
		return theString;
	}


	public Vector<String> getTheEnvList() {
		return theEnvList;
	}
	public Vector<ParameterHolder> getTheParamList() {
		return theParamList;
	}

};