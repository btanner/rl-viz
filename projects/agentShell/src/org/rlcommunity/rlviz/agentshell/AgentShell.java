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
package org.rlcommunity.rlviz.agentshell;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import rlVizLib.dynamicLoading.Unloadable;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
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
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlviz.settings.RLVizSettings;
import rlVizLib.messaging.agentShell.AgentShellTaskSpecCompatRequest;
import rlVizLib.messaging.agentShell.AgentShellTaskSpecCompatResponse;
import rlVizLib.messaging.agentShell.TaskSpecResponsePayload;

public class AgentShell implements AgentInterface, Unloadable {

    static {
        RLVizVersion theLinkedLibraryVizVersion = rlVizLib.rlVizCore.getRLVizSpecVersion();
        RLVizVersion ourCompileVersion = rlVizLib.rlVizCore.getRLVizSpecVersionOfClassWhenCompiled(AgentShell.class);

        if (!theLinkedLibraryVizVersion.equals(ourCompileVersion)) {
            System.err.println("Warning :: Possible RLVizLib Incompatibility");
            System.err.println("Warning :: Runtime version used by AgentShell is:  " + theLinkedLibraryVizVersion);
            System.err.println("Warning :: Compile version used to build AgentShell is:  " + ourCompileVersion);
        }
    }
    private AgentInterface theAgent = null;
    Map<String, AgentLoaderInterface> mapFromUniqueNameToLoader = null;
    Map<String, String> mapFromUniqueNameToLocalName = null;
    Vector<AgentLoaderInterface> theAgentLoaders = null;
    Vector<String> agentNameVector = null;
    Vector<ParameterHolder> agentParamVector = null;

    public AgentShell() {
        if (RLVizSettings.isStringParamSet("agent-environment-jar-path")) {
            RLVizSettings.overrideStringSetting("agent-jar-path", RLVizSettings.getStringSetting("agent-environment-jar-path"));
        }
        refreshList();
    }

    public static ParameterHolder getSettings() {
        ParameterHolder agentShellSettings = new ParameterHolder();
        agentShellSettings.addStringParam("agent-jar-path", ".");
        agentShellSettings.addStringParam("agent-environment-jar-path");

        if (System.getProperty("RLVIZ_LIB_PATH") != null) {
            agentShellSettings.setStringParam("agent-jar-path", System.getProperty("RLVIZ_LIB_PATH"));
            System.err.println("Don't use the system property anymore, use the command line property agent-jar-path");
        }

        return agentShellSettings;
    }

    public static void main(String[] args) {
        RLVizSettings.initializeSettings(args);
        RLVizSettings.addNewParameters(getSettings());


        AgentLoader L = new AgentLoader(new AgentShell());
        L.run();
    }

    public void agent_cleanup() {
        theAgent.agent_cleanup();
    }

    public void refreshList() {
        mapFromUniqueNameToLoader = new TreeMap<String, AgentLoaderInterface>();
        mapFromUniqueNameToLocalName = new TreeMap<String, String>();
        theAgentLoaders = new Vector<AgentLoaderInterface>();
        agentNameVector = new Vector<String>();
        agentParamVector = new Vector<ParameterHolder>();

        if (!theAgentLoaders.isEmpty()) {
            theAgentLoaders.clear();
        }
        //See if the environment variable for the path to the Jars has been defined
        theAgentLoaders.add(new LocalJarAgentLoader());

        //Check if we should do CPP loading
        String CPPAgentLoaderString = System.getProperty("CPPAgent");

        //Short circuit to check the pointer in case not defined
        if (CPPAgentLoaderString != null && CPPAgentLoaderString.equalsIgnoreCase("true")) {
            try {
                theAgentLoaders.add(new LocalCPlusPlusAgentLoader());
            } catch (UnsatisfiedLinkError failure) {
                System.err.println("Unable to load CPPAgent.dylib, unable to load C/C++ agents: " + failure);
            }
        }

        for (AgentLoaderInterface thisAgentLoader : theAgentLoaders) {
            thisAgentLoader.makeList();

            Vector<String> thisAgentNameVector = thisAgentLoader.getNames();
            for (String localName : thisAgentNameVector) {
                String uniqueName = localName + " " + thisAgentLoader.getTypeSuffix();
                agentNameVector.add(uniqueName);
                mapFromUniqueNameToLocalName.put(uniqueName, localName);
                mapFromUniqueNameToLoader.put(uniqueName, thisAgentLoader);
            }

            Vector<ParameterHolder> thisParameterVector = thisAgentLoader.getParameters();
            for (ParameterHolder thisParam : thisParameterVector) {
                agentParamVector.add(thisParam);
            }
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
        if (theGenericMessage.getTo().id() == MessageUser.kAgentShell.id()) {
            //Its for me
            AgentShellMessages theMessageObject = AgentShellMessageParser.makeMessage(theGenericMessage);

            //Handle a request for the list of agents
            if (theMessageObject.getTheMessageType() == AgentShellMessageType.kAgentShellListRequest.id()) {
                this.refreshList();
                AgentShellListResponse theResponse = new AgentShellListResponse(agentNameVector, agentParamVector);
                return theResponse.makeStringResponse();
            }

            //Handle a request to actually load the agent
            if (theMessageObject.getTheMessageType() == AgentShellMessageType.kAgentShellLoad.id()) {
                AgentShellLoadRequest theCastedRequest = (AgentShellLoadRequest) theMessageObject;

                String envName = theCastedRequest.getAgentName();
                ParameterHolder theParams = theCastedRequest.getParameterHolder();


                theAgent = loadAgent(envName, theParams);


                AgentShellLoadResponse theResponse = new AgentShellLoadResponse(theAgent != null);

                return theResponse.makeStringResponse();
            }

            //Handle a request to actually load the agent
            if (theMessageObject.getTheMessageType() == AgentShellMessageType.kAgentShellUnload.id()) {
                //Actually "load" the agent
                theAgent = null;

                AgentShellUnLoadResponse theResponse = new AgentShellUnLoadResponse();

                return theResponse.makeStringResponse();
            }
            
                        //Handle a request to get a copy of the task spec from the environment
            if (theMessageObject.getTheMessageType() == AgentShellMessageType.kAgentShellTaskSpecCompat.id()) {
                AgentShellTaskSpecCompatRequest theCastedRequest = (AgentShellTaskSpecCompatRequest) theMessageObject;

                String agentName = theCastedRequest.getAgentName();
                ParameterHolder theParams = theCastedRequest.getParameterHolder();
                String theTaskSpec=theCastedRequest.getTaskSpec();

                AgentShellTaskSpecCompatResponse theResponse = checkCompat(agentName,theParams,theTaskSpec);

                return theResponse.makeStringResponse();
            }
            


            System.err.println("Agent shell doesn't know how to handle message: " + theMessage);
        }
        //IF it wasn't for me, pass it on
        return theAgent.agent_message(theMessage);


    }
    
        AgentShellTaskSpecCompatResponse checkCompat(String uniqueAgentName, ParameterHolder theParams, String TaskSpec) {
        AgentLoaderInterface thisAgentLoader = mapFromUniqueNameToLoader.get(uniqueAgentName);
        String localName = mapFromUniqueNameToLocalName.get(uniqueAgentName);
        
        TaskSpecResponsePayload theTSP=thisAgentLoader.loadTaskSpecCompat(localName, theParams, TaskSpec);
        return new AgentShellTaskSpecCompatResponse(theTSP);
    }


    private AgentInterface loadAgent(String uniqueAgentName, ParameterHolder theParams) {
        AgentLoaderInterface thisAgentLoader = mapFromUniqueNameToLoader.get(uniqueAgentName);
        String localName = mapFromUniqueNameToLocalName.get(uniqueAgentName);
        return thisAgentLoader.loadAgent(localName, theParams);
    }

    public void agent_end(double reward) {
        theAgent.agent_end(reward);
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
