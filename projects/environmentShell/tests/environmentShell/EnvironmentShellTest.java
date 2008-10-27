/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package environmentShell;
import java.io.File;
import java.util.Vector;
import org.junit.Test;
import rlVizLib.general.ParameterHolder;
import static org.junit.Assert.*;
import rlVizLib.rlVizCore;
/**
 *
 * @author btanner
 */
public class EnvironmentShellTest {

    @Test
    public void testEnvShellVersion(){
       //assertEquals(rlVizCore.getSpecVersion(),rlVizLib.rlVizCore.getRLVizLinkVersionOfClass(EnvironmentShell.class));
    }
    
    @Test
    public void testLoads(){
        System.out.println(new File("/home/btanner/Documents/Java-Projects/rl-library/system/dist").getAbsolutePath());
        System.setProperty("RLVIZ_LIB_PATH", "/home/btanner/Documents/Java-Projects/rl-library/system/dist");
        EnvironmentShell es=new EnvironmentShell();
        es.refreshList();
        Vector<String> names=es.envNameVector;
        Vector<ParameterHolder> params=es.envParamVector;
        es.loadEnvironment(names.get(0),params.get(0));
        System.out.println(Runtime.getRuntime().freeMemory()/10000);
//        System.out.println(this.getClass().getClassLoader().

    }
}
