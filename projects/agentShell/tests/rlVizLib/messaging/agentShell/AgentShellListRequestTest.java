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

package rlVizLib.messaging.agentShell;

import org.junit.Test;
import org.rlcommunity.rlviz.agentshell.AgentShell;
import static org.junit.Assert.*;


/**
 *
 * @author btanner
 */
public class AgentShellListRequestTest {

    public AgentShellListRequestTest() {
    }

    @Test
    public void TestEnvList(){
    System.setProperty("RLVIZ_LIB_PATH", "/Users/btanner/Documents/Java-Projects/rl-library/system/dist");
    AgentShell AS=new AgentShell();
    String theQuery=rlVizLib.messaging.agentShell.AgentShellListRequest.makeRequest();
        
    String firstResponse=AS.agent_message(theQuery);
    String secondResponse=AS.agent_message(theQuery);
        
    assertTrue(firstResponse.compareTo(secondResponse)==0);
    }

}