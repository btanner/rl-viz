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

import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.GenericMessageParser;
import rlVizLib.messaging.NotAnRLVizMessageException;

public class EnvironmentMessageParser extends GenericMessageParser{
	public static EnvironmentMessages parseMessage(String theMessage) throws NotAnRLVizMessageException{
		GenericMessage theGenericMessage=new GenericMessage(theMessage);

		int cmdId=theGenericMessage.getTheMessageType();

		if(cmdId==EnvMessageType.kEnvQueryVarRanges.id()) 				return new EnvRangeRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvQueryObservationsForState.id()) 	return new EnvObsForStateRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvQuerySupportedVersion.id()) 	return new EnvVersionSupportedRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvReceiveRunTimeParameters.id()) 	return new EnvReceiveRunTimeParametersRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvQueryVisualizerName.id()) 	return new EnvVisualizerNameRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvQueryEpisodeSummary.id()) 	return new EpisodeSummaryRequest(theGenericMessage);
		if(cmdId==EnvMessageType.kEnvCustom.id())		return new EnvCustomRequest(theGenericMessage);


		System.out.println("EnvironmentMessageParser - unknown query type: "+theMessage);
		Thread.dumpStack();
		System.exit(1);
		return null;
	}
}
