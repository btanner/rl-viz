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
import rlVizLib.general.ParameterHolder;
import rlVizLib.visualization.QueryableAgent;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;


public class JNIAgent implements AgentInterface, QueryableAgent, Unloadable {
    private boolean debugThis=false;
    
	public native boolean JNIloadAgent(String s);
	//JNI methods for Agent
	public native void JNIagentinit(String taskSpecification);
	public native void JNIagentstart(int numI, int numD, int[] intArray, double[] doubleArray);
	public native void JNIagentstep(double reward, int numI, int numD, int[] intArray, double[] doubleArray);
	public native void JNIagentend(double reward);
	public native void JNIagentcleanup();
	public native void JNIagentfreeze();
	public native String JNIagentmessage(String message);
	//JNI methods for Queryable Agent
	public native double JNIgetvalueforstate(int numI, int numD, int[] intArray, double[] doubleArray);
	//C accessor methods
	public native int JNIgetInt();
	public native int[] JNIgetIntArray();
	public native int JNIgetDouble();
	public native double[] JNIgetDoubleArray();

	private void load_agent(String s, ParameterHolder theParams){
		JNIloadAgent(s);
	}
	public JNIAgent(String agentName, ParameterHolder theParams){
		load_agent(agentName, theParams);
	}
	
	// mehtods needed by the Agent interface
	public void agent_init(final String taskSpecification){
	//	System.out.println(taskSpecification);
		JNIagentinit(taskSpecification);
	}
	
	public Action agent_start(Observation o){
            if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_start");
		JNIagentstart(o.intArray.length, o.doubleArray.length, o.intArray, o.doubleArray);
		
		//get the data back from agent_step
		int numI = JNIgetInt();
		int numD = JNIgetDouble();
		int[] theInts=JNIgetIntArray();
		double[] theDoubles = JNIgetDoubleArray();
		//copy them into an action data type to return
		Action a = new Action(numI, numD);
		System.arraycopy(theInts,0,a.intArray, 0,numI);
		System.arraycopy(theDoubles,0,a.doubleArray, 0,numD);
		
		return a;
	}
	
	public Action agent_step(double reward, Observation o){
            if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_step");

		JNIagentstep(reward, o.intArray.length, o.doubleArray.length, o.intArray, o.doubleArray);
            if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_step  (back from first JNI call");
		
		//get the data back from agent_step
		int numI = JNIgetInt();
		int numD = JNIgetDouble();
            if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_step  (got counts)");
    		int[] theInts=JNIgetIntArray();
		double[] theDoubles = JNIgetDoubleArray();
            if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_step  (got arrays)");
		//copy them into an action data type to return
		Action a = new Action(numI, numD);
		System.arraycopy(theInts,0,a.intArray, 0,numI);
		System.arraycopy(theDoubles,0,a.doubleArray, 0,numD);
             if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_step  (done array copies): "+numI+" ints and "+numD+" doubles, the int action was: "+theInts[0]);
		
		return a;
	}
	
	public void agent_end(double reward){
            if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_end");
		JNIagentend(reward);
	}
	
	public void agent_cleanup(){
		JNIagentcleanup();
	}
	
	public void agent_freeze(){
		JNIagentfreeze();
	}
	
	public String agent_message(final String message){
                        if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_message");

		return JNIagentmessage(message);
	}
	
	//Queryable Agent
	public double getValueForState(Observation o){
                        if(debugThis)System.out.println("JAVA : JNIAGENT :: agent_getValueForState");

		return JNIgetvalueforstate(o.intArray.length, o.doubleArray.length, o.intArray, o.doubleArray);
	}
	
}