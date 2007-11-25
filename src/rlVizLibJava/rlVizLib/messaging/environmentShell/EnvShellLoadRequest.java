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

import rlVizLib.general.ParameterHolder;
import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlglue.RLGlue;

public class EnvShellLoadRequest extends EnvironmentShellMessages{
	private String envName;
	private ParameterHolder theParams;

	public EnvShellLoadRequest(GenericMessage theMessageObject) {
		super(theMessageObject);

		StringTokenizer st=new StringTokenizer(super.getPayLoad(),":");
		this.envName=st.nextToken();
		theParams=new ParameterHolder(st.nextToken());
	}



	public ParameterHolder getTheParams() {
		return theParams;
	}



	//This is intended for debugging but works well to be just called to save code duplication
	public static String getRequestMessage(String envName, ParameterHolder theParams){

		String paramString="NULL";
		if(theParams!=null)paramString=theParams.stringSerialize();

		String payLoadString=envName+":"+paramString;

		return AbstractMessage.makeMessage(
				MessageUser.kEnvShell.id(),
				MessageUser.kBenchmark.id(),
				EnvShellMessageType.kEnvShellLoad.id(),
				MessageValueType.kString.id(),
				payLoadString);

	}
	public static EnvShellLoadResponse Execute(String envName, ParameterHolder theParams){
		String theRequestString=getRequestMessage(envName,theParams);

		String responseMessage=RLGlueProxy.RL_env_message(theRequestString);

		EnvShellLoadResponse theResponse;
		try {
			theResponse = new EnvShellLoadResponse(responseMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("In EnvShellLoadRequest: response was not an RLViz Message");
			return null;
                }       return theResponse;


	}

	public String getEnvName() {
		return envName;
	}
	
	public ParameterHolder getParameterHolder(){
		return theParams;
	}

}
