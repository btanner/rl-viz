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
package environmentShell;

import java.util.Map;

import java.util.TreeMap;
import java.util.Vector;

import rlVizLib.dynamicLoading.Unloadable;
import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.NotAnRLVizMessageException;
import rlVizLib.messaging.environmentShell.EnvShellListResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadRequest;
import rlVizLib.messaging.environmentShell.EnvShellLoadResponse;
import rlVizLib.messaging.environmentShell.EnvShellRefreshResponse;
import rlVizLib.messaging.environmentShell.EnvShellMessageType;
import rlVizLib.messaging.environmentShell.EnvShellUnLoadResponse;
import rlVizLib.messaging.environmentShell.EnvironmentShellMessageParser;
import rlVizLib.messaging.environmentShell.EnvironmentShellMessages;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Random_seed_key;
import org.rlcommunity.rlglue.codec.types.Reward_observation;
import org.rlcommunity.rlglue.codec.types.State_key;

public class EnvironmentShell implements EnvironmentInterface, Unloadable {

    protected String libDir;
    

    static {
        RLVizVersion theLinkedLibraryVizVersion = rlVizLib.rlVizCore.getRLVizSpecVersion();
        RLVizVersion ourCompileVersion = rlVizLib.rlVizCore.getRLVizSpecVersionOfClassWhenCompiled(EnvironmentShell.class);

        if (!theLinkedLibraryVizVersion.equals(ourCompileVersion)) {
            System.err.println("Warning :: Possible RLVizLib Incompatibility");
            System.err.println("Warning :: Runtime version used by AgentShell is:  " + theLinkedLibraryVizVersion);
            System.err.println("Warning :: Compile version used to build AgentShell is:  " + ourCompileVersion);
        }
    }
    private EnvironmentInterface theEnvironment = null;
    Map<String, EnvironmentLoaderInterface> mapFromUniqueNameToLoader = null;
    Map<String, String> mapFromUniqueNameToLocalName = null;
    Vector<EnvironmentLoaderInterface> theEnvironmentLoaders = null;
    Vector<String> envNameVector = null;
    Vector<ParameterHolder> envParamVector = null;

    public EnvironmentShell() {
        //See if the environment variable for the path to the Jars has been defined
        this.refreshList();
    }

    public void refreshList() {
        mapFromUniqueNameToLoader = new TreeMap<String, EnvironmentLoaderInterface>();
        mapFromUniqueNameToLocalName = new TreeMap<String, String>();
        theEnvironmentLoaders = new Vector<EnvironmentLoaderInterface>();
        envNameVector = new Vector<String>();
        envParamVector = new Vector<ParameterHolder>();

        if (!theEnvironmentLoaders.isEmpty()) {
            theEnvironmentLoaders.clear();
        }
        //See if the environment variable for the path to the Jars has been defined
        theEnvironmentLoaders.add(new LocalJarEnvironmentLoader());

        //Check if we should do CPP loading
        String CPPEnvLoaderString = System.getProperty("CPPEnv");

        //Short circuit to check the pointer in case not defined
        if (CPPEnvLoaderString != null && CPPEnvLoaderString.equalsIgnoreCase("true")) {
            try {
                theEnvironmentLoaders.add(new LocalCPlusPlusEnvironmentLoader());
            } catch (UnsatisfiedLinkError failure) {
                System.err.println("Unable to load CPPENV.dylib, unable to load C/C++ environments: " + failure);
            }
        }

        for (EnvironmentLoaderInterface thisEnvLoader : theEnvironmentLoaders) {
            thisEnvLoader.makeList();
            Vector<String> thisEnvNameVector = thisEnvLoader.getNames();
            for (String localName : thisEnvNameVector) {
                String uniqueName = localName + " " + thisEnvLoader.getTypeSuffix();
                envNameVector.add(uniqueName);
                mapFromUniqueNameToLocalName.put(uniqueName, localName);
                mapFromUniqueNameToLoader.put(uniqueName, thisEnvLoader);
            }

            Vector<ParameterHolder> thisParameterVector = thisEnvLoader.getParameters();
            for (ParameterHolder thisParam : thisParameterVector) {
                envParamVector.add(thisParam);
            }

        }
    }

    public void env_cleanup() {
        theEnvironment.env_cleanup();
    }

    public Random_seed_key env_get_random_seed() {
        return theEnvironment.env_get_random_seed();
    }

    public State_key env_get_state() {
        return theEnvironment.env_get_state();
    }

    public String env_init() {
        return theEnvironment.env_init();
    }

    public String env_message(String theMessage) {
        GenericMessage theGenericMessage;
        try {
            theGenericMessage = new GenericMessage(theMessage);
        } catch (NotAnRLVizMessageException e) {
            System.err.println("Someone sent EnvironmentShell a message that wasn't RL-Viz compatible");
            return "I only respond to RL-Viz messages!";
        }
        if (theGenericMessage.getTo().id() == MessageUser.kEnvShell.id()) {

            //Its for me
            EnvironmentShellMessages theMessageObject = EnvironmentShellMessageParser.makeMessage(theGenericMessage);

            //Handle a request for the list of environments
            if (theMessageObject.getTheMessageType() == EnvShellMessageType.kEnvShellListQuery.id()) {

                this.refreshList();
                EnvShellListResponse theResponse = new EnvShellListResponse(envNameVector, envParamVector);

                return theResponse.makeStringResponse();
            }

            //Handle a request to actually load the environment
            if (theMessageObject.getTheMessageType() == EnvShellMessageType.kEnvShellLoad.id()) {
                EnvShellLoadRequest theCastedRequest = (EnvShellLoadRequest) theMessageObject;

                String envName = theCastedRequest.getEnvName();
                ParameterHolder theParams = theCastedRequest.getParameterHolder();


                theEnvironment = loadEnvironment(envName, theParams);

                EnvShellLoadResponse theResponse = new EnvShellLoadResponse(theEnvironment != null);

                return theResponse.makeStringResponse();
            }

            //Handle a request to actually load the environment
            if (theMessageObject.getTheMessageType() == EnvShellMessageType.kEnvShellUnLoad.id()) {
                //Actually "load" the environment
                theEnvironment = null;

                EnvShellUnLoadResponse theResponse = new EnvShellUnLoadResponse();

                return theResponse.makeStringResponse();
            }

            if (theMessageObject.getTheMessageType() == EnvShellMessageType.kEnvShellRefresh.id()) {
                this.refreshList();

                EnvShellRefreshResponse theResponse = new EnvShellRefreshResponse(true);

                return theResponse.makeStringResponse();
            }
            System.err.println("Env shell doesn't know how to handle message: " + theMessage);
        }
        //IF it wasn't for me, pass it on
        String response = theEnvironment.env_message(theMessage);
        return response;


    }

    EnvironmentInterface loadEnvironment(String uniqueEnvName, ParameterHolder theParams) {
        EnvironmentLoaderInterface thisEnvLoader = mapFromUniqueNameToLoader.get(uniqueEnvName);
        String localName = mapFromUniqueNameToLocalName.get(uniqueEnvName);
        return thisEnvLoader.loadEnvironment(localName, theParams);
    }

    public void env_set_random_seed(Random_seed_key arg0) {
        theEnvironment.env_set_random_seed(arg0);
    }

    public void env_set_state(State_key arg0) {
        theEnvironment.env_set_state(arg0);
    }

    public Observation env_start() {
        Observation o = theEnvironment.env_start();
        return o;
    }

    public Reward_observation env_step(Action arg0) {
        Reward_observation ro = theEnvironment.env_step(arg0);
        return ro;
    }

    public static void main(String[] args) {
        EnvironmentShell e = new EnvironmentShell();
    }
    
}
