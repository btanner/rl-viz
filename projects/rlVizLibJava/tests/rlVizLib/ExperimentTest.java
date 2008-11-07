/*
 * Copyright 2008 Brian Tanner
 * http://bt-recordbook.googlecode.com/
 * brian@tannerpages.com
 * http://brian.tannerpages.com
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package rlVizLib;
import org.junit.Test;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.tests.Test_1_Agent;
import org.rlcommunity.rlglue.codec.tests.Test_1_Environment;
import org.rlcommunity.rlglue.codec.tests.Test_1_Experiment;
import org.rlcommunity.rlglue.codec.tests.Test_Empty_Agent;
import org.rlcommunity.rlglue.codec.tests.Test_Empty_Environment;
import org.rlcommunity.rlglue.codec.tests.Test_Empty_Experiment;
import org.rlcommunity.rlglue.codec.tests.Test_Message_Agent;
import org.rlcommunity.rlglue.codec.tests.Test_Message_Environment;
import org.rlcommunity.rlglue.codec.tests.Test_Message_Experiment;
import org.rlcommunity.rlglue.codec.tests.Test_RL_Episode_Experiment;
import org.rlcommunity.rlglue.codec.tests.Test_Sanity_Experiment;
import org.rlcommunity.rlglue.codec.tests.Test_Speed_Environment;
import org.rlcommunity.rlglue.codec.tests.Test_Speed_Experiment;
import rlVizLib.glueProxy.LocalGlue;
import static org.junit.Assert.*;

/**
 *
 * @author Brian Tanner
 */
public class ExperimentTest {

@Test
public void testSanityLocal(){
    AgentInterface theAgent=new Test_1_Agent();
    EnvironmentInterface theEnvironment=new Test_1_Environment();
    
    RLGlue.setGlue(new LocalGlue(theEnvironment,theAgent));
 
    /* Can't do this without duplicating experiment code because
     * the rl-glue stuff doesn't want to use the glueproxy, it's
     * not written to*/
    int failures=Test_Sanity_Experiment.runTest();
    
    assertEquals(failures, 0);
}

@Test
public void test1Local(){
    AgentInterface theAgent=new Test_1_Agent();
    EnvironmentInterface theEnvironment=new Test_1_Environment();
    
    RLGlue.setGlue(new LocalGlue(theEnvironment,theAgent));
 
    /* Can't do this without duplicating experiment code because
     * the rl-glue stuff doesn't want to use the glueproxy, it's
     * not written to*/
    int failures=Test_1_Experiment.runTest();
    
    assertEquals(failures, 0);
}

@Test
public void testMessageLocal(){
    AgentInterface theAgent=new Test_Message_Agent();
    EnvironmentInterface theEnvironment=new Test_Message_Environment();
    
    RLGlue.setGlue(new LocalGlue(theEnvironment,theAgent));
 
    /* Can't do this without duplicating experiment code because
     * the rl-glue stuff doesn't want to use the glueproxy, it's
     * not written to*/
    int failures=Test_Message_Experiment.runTest();
    
    assertEquals(failures, 0);
}

@Test
public void testEmptyLocal(){
    AgentInterface theAgent=new Test_Empty_Agent();
    EnvironmentInterface theEnvironment=new Test_Empty_Environment();
    
    RLGlue.setGlue(new LocalGlue(theEnvironment,theAgent));
 
    /* Can't do this without duplicating experiment code because
     * the rl-glue stuff doesn't want to use the glueproxy, it's
     * not written to*/
    int failures=Test_Empty_Experiment.runTest();
    
    assertEquals(failures, 0);
}

@Test
public void testRLEpisodeLocal(){
    AgentInterface theAgent=new Test_1_Agent();
    EnvironmentInterface theEnvironment=new Test_1_Environment();
    
    RLGlue.setGlue(new LocalGlue(theEnvironment,theAgent));
 
    /* Can't do this without duplicating experiment code because
     * the rl-glue stuff doesn't want to use the glueproxy, it's
     * not written to*/
    int failures=Test_RL_Episode_Experiment.runTest();
    
    assertEquals(failures, 0);
}
    

    
@Test
public void testSpeedLocal(){
    AgentInterface theAgent=new Test_1_Agent();
    EnvironmentInterface theEnvironment=new Test_Speed_Environment();
    
    RLGlue.setGlue(new LocalGlue(theEnvironment,theAgent));
 
    /* Can't do this without duplicating experiment code because
     * the rl-glue stuff doesn't want to use the glueproxy, it's
     * not written to*/
    int failures=Test_Speed_Experiment.runTest();
    
    System.out.println("testSpeed failures: "+failures);
    assertEquals(failures, 0);
}

public static void main(String args[]){
    ExperimentTest t = new ExperimentTest();
    t.testSpeedLocal();
}

}
