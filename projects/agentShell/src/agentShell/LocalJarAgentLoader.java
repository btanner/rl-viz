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

package agentShell;

import rlVizLib.dynamicLoading.EnvOrAgentType;
import rlVizLib.dynamicLoading.LocalJarAgentEnvironmentLoader;
import rlVizLib.general.ParameterHolder;
import rlglue.agent.Agent;

public class LocalJarAgentLoader extends LocalJarAgentEnvironmentLoader implements AgentLoaderInterface{

    public LocalJarAgentLoader() {
        super(AgentShellPreferences.getInstance().getList(),EnvOrAgentType.kAgent);
    }


    public Agent loadAgent(String requestedName, ParameterHolder theParams) {
        Object theAgentObject=load(requestedName, theParams);
        if(theAgentObject!=null)return (Agent)theAgentObject;
        return null;
    }

}