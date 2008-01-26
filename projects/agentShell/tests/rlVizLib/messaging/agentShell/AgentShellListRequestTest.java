/*
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package rlVizLib.messaging.agentShell;

import agentShell.*;
import org.junit.Test;
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