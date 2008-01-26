/*
Copyright 2007 Brian Tanner
brian@tannerpages.com
http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

  
package rlVizLib.messaging;

import java.util.StringTokenizer;

import rlVizLib.messaging.agent.AgentMessageType;
import rlVizLib.messaging.agentShell.AgentShellMessageType;
import rlVizLib.messaging.environment.EnvMessageType;
import rlVizLib.messaging.environmentShell.EnvShellMessageType;

/**
 * Generic Message is where much of the actual guts of the messaging system is based.  Basically, every message 
 * in RLViz is made from:
 * <p>
 * <b>to:</b> 	The intended recipient of this message (Experiment, Agent, Environment, AgentShell, EnvironmentShell)
 * <br />
 * The type of <b>to</b> is {@link MessageUser}
 * <p>
 * <b>from</b>: 	The originator of this message  (Experiment, Agent, Environment, AgentShell, EnvironmentShell)
 * <br />
 * The type of <b>from</b> is {@link MessageUser}
 * <p>
 * <p>
 * <b>theMessageType</b>: Identifier of the message type.  These types are all predefined inside RLViz (there is a "custom"
 * for making custom message types)
 * <br />
 * The interpreted type of <b>theMessageType</b> varies from subclass to subclass {@link AgentMessageType}, {@link EnvMessageType}, {@link EnvShellMessageType}, {@link AgentShellMessageType}
 * <p>
 * <b>payLoadType</b>: formatting hints about the payload of the message
 * <br />
 * The type of <b>payLoadType</b> is {@link MessageValueType}
 * <p>
 * <b>payLoad</b>: formatted string with the deliverable aspect of this message
 * <br />
 * The type of <b>payLoad</b> is {@link String}
 * 
 * @author Brian Tanner
 * @since The Beginning
 */
public class GenericMessage {
	
	
	private int theMessageType;
	protected MessageUser from;
	protected MessageUser to;
	protected MessageValueType payLoadType;
	protected String payLoad;

	public GenericMessage(String theMessage) throws NotAnRLVizMessageException{
		try {
			StringTokenizer theTokenizer=new StringTokenizer(theMessage, " ");


			String toString=theTokenizer.nextToken();
			String fromString=theTokenizer.nextToken();
			String typeString=theTokenizer.nextToken();
			String valueString=theTokenizer.nextToken();
			String payLoadString=theTokenizer.nextToken("\f");

			from=GenericMessageParser.parseUser(fromString);
			to=GenericMessageParser.parseUser(toString);

			theMessageType=GenericMessageParser.parseInt(typeString);
			payLoadType=GenericMessageParser.parseValueType(valueString);
			payLoad=GenericMessageParser.parsePayLoad(payLoadString);
		} catch (Exception e) {
			//The message was NOT what we expected!
			throw new NotAnRLVizMessageException();
		}		

	}

	/**
	 * @return the theMessageType
	 */
	public int getTheMessageType() {
		return theMessageType;
	}

	/**
	 * @return the from
	 */
	public MessageUser getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public MessageUser getTo() {
		return to;
	}

	/**
	 * @return the payLoadType
	 */
	public MessageValueType getPayLoadType() {
		return payLoadType;
	}

	/**
	 * @return the payLoad
	 */
	public String getPayLoad() {
		return payLoad;
	}

}
