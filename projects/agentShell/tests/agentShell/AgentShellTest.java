/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agentShell;
import org.junit.Test;
import static org.junit.Assert.*;
import rlVizLib.rlVizCore;
import agentShell.EnvironmentShell;
/**
 *
 * @author btanner
 */
public class AgentShellTest {

    @Test
    public void testAgentShellVersion(){
       assertEquals(rlVizCore.getSpecVersion(),rlVizLib.rlVizCore.getRLVizLinkVersionOfClass(AgentShell.class));
    }
}
