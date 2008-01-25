/* EnvironmentShell, a dynamic loader for C++ and Java RL-Glue environments
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
import rlglue.environment.Environment;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.State_key;

public class EnvironmentShell implements Environment, Unloadable {

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
    private Environment theEnvironment = null;
    Map<String, EnvironmentLoaderInterface> mapFromUniqueNameToLoader = new TreeMap<String, EnvironmentLoaderInterface>();
    Map<String, String> mapFromUniqueNameToLocalName = new TreeMap<String, String>();
    Vector<EnvironmentLoaderInterface> theEnvironmentLoaders = new Vector<EnvironmentLoaderInterface>();

    public EnvironmentShell() {
        //See if the environment variable for the path to the Jars has been defined
        this.refreshList();
    }

    public void refreshList() {
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
                Vector<String> envNameVector = new Vector<String>();
                Vector<ParameterHolder> envParamVector = new Vector<ParameterHolder>();

                this.refreshList();

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

    private Environment loadEnvironment(String uniqueEnvName, ParameterHolder theParams) {
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
