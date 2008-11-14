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


import rlVizLib.dynamicLoading.Unloadable;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * @deprecated, use org.rlcommunity.rlviz.agentshell.AgentShell
 * @author btanner
 */
public class AgentShell implements AgentInterface, Unloadable {

    private final org.rlcommunity.rlviz.agentshell.AgentShell as;

    public void agent_cleanup() {
        as.agent_cleanup();
    }

    public AgentShell() {
        as = new org.rlcommunity.rlviz.agentshell.AgentShell();
    }

    public void refreshList() {
        as.refreshList();
    }

    public String agent_message(String theMessage) {
        return as.agent_message(theMessage);
    }

    public void agent_end(double reward) {
        as.agent_end(reward);
    }

    public void agent_init(String taskSpecification) {
        as.agent_init(taskSpecification);
    }

    public Action agent_start(Observation observation) {
        return as.agent_start(observation);
    }

    public Action agent_step(double reward, Observation observation) {
        return as.agent_step(reward, observation);
    }
}
