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

import java.util.Vector;

import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.interfaces.getEnvMaxMinsInterface;
import rlglue.RLGlue;
import rlglue.environment.Environment;

public class EnvRangeRequest extends EnvironmentMessages{

	public EnvRangeRequest(GenericMessage theMessageObject) {
		super(theMessageObject);
	}

	public static EnvRangeResponse Execute(){
		String theRequest=AbstractMessage.makeMessage(
				MessageUser.kEnv.id(),
				MessageUser.kBenchmark.id(),
				EnvMessageType.kEnvQueryVarRanges.id(),
				MessageValueType.kNone.id(),
		"NULL");

		String responseMessage=RLGlueProxy.RL_env_message(theRequest);

		EnvRangeResponse theResponse;
		try {
			theResponse = new EnvRangeResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In EnvRangeRequest, the response was not RL-Viz Compatible");
			theResponse = null;
		}

		return theResponse;

	}

	@Override
	public boolean canHandleAutomatically(Object theReceiver) {
		return (theReceiver instanceof getEnvMaxMinsInterface);
	}

	@Override
	public String handleAutomatically(Environment theEnvironment) {
		
		getEnvMaxMinsInterface castedEnv = (getEnvMaxMinsInterface)theEnvironment;
		//			//Handle a request for the ranges
		Vector<Double> mins = new Vector<Double>();
		Vector<Double> maxs = new Vector<Double>();

		int numVars=castedEnv.getNumVars();

		for(int i=0;i<numVars;i++){
			mins.add(castedEnv.getMinValueForQuerableVariable(i));
			maxs.add(castedEnv.getMaxValueForQuerableVariable(i));
		}

		EnvRangeResponse theResponse=new EnvRangeResponse(mins, maxs);

		return theResponse.makeStringResponse();

	}

}
