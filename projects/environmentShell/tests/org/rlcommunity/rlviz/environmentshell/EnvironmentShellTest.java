/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rlcommunity.rlviz.environmentshell;

import java.io.IOException;
import java.util.Vector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rlcommunity.rlviz.settings.RLVizSettings;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import rlVizLib.general.ParameterHolder;
import static org.junit.Assert.*;

/**
 *
 * @author btanner
 */
public class EnvironmentShellTest {

    public EnvironmentShellTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testDylib() throws IOException {
       RLVizSettings.initializeSettings(new String[]{"do-cpp-loading=true", "environment-jar-path=/Users/btanner/Documents/JavaProjects/rl-library/products"});
        RLVizSettings.addNewParameters(EnvironmentShell.getSettings());

        EnvironmentShell theShell=new EnvironmentShell();

        Vector<String> theEnvNames=theShell.getEnvNames();
        System.out.println("Total envs is: "+theEnvNames.size());
        System.out.println(theEnvNames);

        EnvironmentInterface theEnvironment=theShell.loadEnvironment("SampleMinesEnvironment - C++", new ParameterHolder());
        String taskSpec=theEnvironment.env_init();
        System.out.println("Task spec is: "+taskSpec);
        theEnvironment.env_start();
        theEnvironment.env_step(new Action(1,0,0));
        theEnvironment.env_message("print-state");
        theEnvironment.env_cleanup();

        assertTrue(true);
    }


}