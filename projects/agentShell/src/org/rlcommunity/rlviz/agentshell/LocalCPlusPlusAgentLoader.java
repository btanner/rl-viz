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

package org.rlcommunity.rlviz.agentshell;

import java.io.File;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import org.rlcommunity.rlglue.codec.AgentInterface;

//LocalCPlusPlusAgentLoader loads the c dynamic library factory. This library essentially wraps
import rlVizLib.messaging.agentShell.TaskSpecResponsePayload;
//an agent library providing information for the JNI api to allow the java rl-viz to make
//use of c/c++ agents

public class LocalCPlusPlusAgentLoader implements AgentLoaderInterface {
	
	// C++ functions to be called from within Java
	//public native void JNIloadAgent();
	public native void JNImakeAgentList(String path); 
	public native String JNIgetAgentName(int index); 
	public native String JNIgetAgentParams(int index);
	public native int JNIgetAgentCount();
	
	String libDir;
	
	//load the library so java knows where to find the above functions
	public LocalCPlusPlusAgentLoader(){
		String curDir = System.getProperty("user.dir");
        File thisDirectoryFile= new File(curDir);
		
		String mainLibraryDir=thisDirectoryFile.getParent();
        File parentDirectoryFile=new File(mainLibraryDir);
        
		String workSpaceDirString=parentDirectoryFile.getParent();
		libDir=workSpaceDirString+"/rlcomplibrary/libraries/agentJars/";
		String loaderDir = workSpaceDirString+"/rl-viz/system/dist/SharedLibraries/";
	}
	
	public LocalCPlusPlusAgentLoader(String path){
		libDir = path;
		System.load(libDir + "CPPAGENT.dylib");
	}
	public boolean makeList() {
		JNImakeAgentList(libDir);
		return true;
	}

	public Vector<String> getNames() {
		//I'm picturing something like this, maybe you can do better
		Vector<String> theAgentNames=new Vector<String>();	
		int numAgents=JNIgetAgentCount();
		
		for(int i=0;i<numAgents;i++){
			String thisName=JNIgetAgentName(i);
			theAgentNames.add(thisName);
		}	
		return theAgentNames;
	}

	public Vector<ParameterHolder> getParameters() {
		Vector<ParameterHolder> theAgentParams=new Vector<ParameterHolder>();
		
		int numAgents=JNIgetAgentCount();
		
		for(int i=0;i<numAgents;i++){
			String ParamHolderString = "NULL";
			ParameterHolder thisParamHolder=new ParameterHolder(ParamHolderString);
			theAgentParams.add(thisParamHolder);
		}
		
		theAgentParams.add(null);
		return theAgentParams;
	}
	
	public AgentInterface loadAgent(String agentName, ParameterHolder theParams) {
		String thename = libDir + agentName;
		AgentInterface theAgent=new JNIAgent(thename,theParams);
		return theAgent;
	}

	public String getTypeSuffix() {
		return " - C++";
	}

    public TaskSpecResponsePayload loadTaskSpecCompat(String localName, ParameterHolder theParams, String TaskSpec) {
        return new TaskSpecResponsePayload(true, "C++ Agent Loader does not yet support checking task spec compatibility.");
    }

}
