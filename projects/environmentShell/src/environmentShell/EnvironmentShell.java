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


import org.rlcommunity.rlviz.dynamicloading.Unloadable;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;

/**
 * @deprecated Use org.rlcommunity.rlviz.environmentshell
 * @author btanner
 */
public class EnvironmentShell implements EnvironmentInterface, Unloadable {
    private final org.rlcommunity.rlviz.environmentshell.EnvironmentShell es;
  
    public EnvironmentShell() {
        es=new org.rlcommunity.rlviz.environmentshell.EnvironmentShell();
    }

    public void refreshList() {
        es.refreshList();
    }

    public void env_cleanup() {
        es.env_cleanup();
    }
    public String env_init() {
        return es.env_init();
    }

    public String env_message(String theMessage) {
        return es.env_message(theMessage);
    }

    public Observation env_start() {
        return es.env_start();
    }

    public Reward_observation_terminal env_step(Action arg0) {
        return es.env_step(arg0);
    }

    
}
