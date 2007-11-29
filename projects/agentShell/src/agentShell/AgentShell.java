/* AgentShell, a dynamic loader for C++ and Java RL-Glue agents
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
package agentShell;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.agentShell.AgentShellListResponse;
import rlVizLib.messaging.agentShell.AgentShellLoadRequest;
import rlVizLib.messaging.agentShell.AgentShellLoadResponse;
import rlVizLib.messaging.agentShell.AgentShellMessageParser;
import rlVizLib.messaging.agentShell.AgentShellMessageType;
import rlVizLib.messaging.agentShell.AgentShellMessages;
import rlVizLib.messaging.agentShell.AgentShellUnLoadResponse;
import rlglue.agent.Agent;
import rlglue.types.Action;
import rlglue.types.Observation;


public class AgentShell implements Agent{
	private Agent theAgent = null;
	
	Map<String, AgentLoaderInterface> mapFromUniqueNameToLoader = new TreeMap<String, AgentLoaderInterface>();
	Map<String, String> mapFromUniqueNameToLocalName = new TreeMap<String,String>();
	
	
	Vector<AgentLoaderInterface> theAgentLoaders=new Vector<AgentLoaderInterface>();
	public void agent_cleanup() {
		theAgent.agent_cleanup();
	}

	public AgentShell(){
                String libraryPath = System.getProperty("RLVIZ_LIB_PATH");
                if(libraryPath==null)   
                    theAgentLoaders.add(new LocalJarAgentLoader());
                else
                    theAgentLoaders.add(new LocalJarAgentLoader(libraryPath));

               //Check if we should do CPP loading
                String CPPAgentLoaderString = System.getProperty("CPPAgent");
                
                //Short circuit to check the pointer in case not defined
                if(CPPAgentLoaderString!=null&&CPPAgentLoaderString.equalsIgnoreCase("true"))
                    try{
                        theAgentLoaders.add(new LocalCPlusPlusAgentLoader());
                    }
                    catch ( UnsatisfiedLinkError failure ){
                        System.err.println("Unable to load CPPAGENT.dylib, unable to load C/C++ agents");
                    }

	}
	
	public String agent_message(String theMessage) {

		GenericMessage theGenericMessage;
		try {
			theGenericMessage = new GenericMessage(theMessage);
		} catch (NotAnRLVizMessageException e) {
			System.err.println("Someone sent AgentShell a message that wasn't RL-Viz compatible");
			return "I only respond to RL-Viz messages!";
		}
		if(theGenericMessage.getTo().id()==MessageUser.kAgentShell.id()){

			//Its for me
			AgentShellMessages theMessageObject=AgentShellMessageParser.makeMessage(theGenericMessage);

			//Handle a request for the list of agents
			if(theMessageObject.getTheMessageType()==AgentShellMessageType.kAgentShellListRequest.id()){
				Vector<String> envNameVector=new Vector<String>();
				Vector<ParameterHolder> envParamVector=new Vector<ParameterHolder>();

				for (AgentLoaderInterface thisAgentLoader : theAgentLoaders) {
					thisAgentLoader.makeList();
					
					Vector<String> thisAgentNameVector=thisAgentLoader.getNames();
					for (String localName : thisAgentNameVector) {
						String uniqueName=localName+" "+thisAgentLoader.getTypeSuffix();
						envNameVector.add(uniqueName);
						mapFromUniqueNameToLocalName.put(uniqueName,localName);
						mapFromUniqueNameToLoader.put(uniqueName,thisAgentLoader);
					}
					
					Vector<ParameterHolder> thisParameterVector=thisAgentLoader.getParameters();
					for (ParameterHolder thisParam : thisParameterVector) {
						envParamVector.add(thisParam);
					}

				}
				AgentShellListResponse theResponse=new AgentShellListResponse(envNameVector,envParamVector);

				return theResponse.makeStringResponse();
			}

			//Handle a request to actually load the agent
			if(theMessageObject.getTheMessageType()==AgentShellMessageType.kAgentShellLoad.id()){
				AgentShellLoadRequest theCastedRequest=(AgentShellLoadRequest)theMessageObject;
				
				String envName=theCastedRequest.getAgentName();
				ParameterHolder theParams=theCastedRequest.getParameterHolder();
				
				
				theAgent=loadAgent(envName, theParams);


				AgentShellLoadResponse theResponse=new AgentShellLoadResponse(theAgent!=null);

				return theResponse.makeStringResponse();
			}

			//Handle a request to actually load the agent
			if(theMessageObject.getTheMessageType()==AgentShellMessageType.kAgentShellUnload.id()){
				//Actually "load" the agent
				theAgent=null;

				AgentShellUnLoadResponse theResponse=new AgentShellUnLoadResponse();

				return theResponse.makeStringResponse();
			}

			System.err.println("Agent shell doesn't know how to handle message: "+theMessage);
		}
		//IF it wasn't for me, pass it on
		return theAgent.agent_message(theMessage);


	}

	private Agent loadAgent(String uniqueEnvName,ParameterHolder theParams) {
		AgentLoaderInterface thisAgentLoader=mapFromUniqueNameToLoader.get(uniqueEnvName);
		String localName=mapFromUniqueNameToLocalName.get(uniqueEnvName);
		return thisAgentLoader.loadAgent(localName,theParams);
	}

	public void agent_end(double reward) {
		theAgent.agent_end(reward);
	}

	public void agent_freeze() {
		theAgent.agent_freeze();
	}

	public void agent_init(String taskSpecification) {
		theAgent.agent_init(taskSpecification);
	}

	public Action agent_start(Observation observation) {
		return theAgent.agent_start(observation);
	}

	public Action agent_step(double reward, Observation observation) {
		return theAgent.agent_step(reward, observation);
	}
}
