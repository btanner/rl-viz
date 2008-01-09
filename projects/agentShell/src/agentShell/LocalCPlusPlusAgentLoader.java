/* AgentShell, a dynamic loader for C++ and Java RL-Glue agents
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package agentShell;

import java.io.File;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlglue.agent.Agent;

//LocalCPlusPlusAgentLoader loads the c dynamic library factory. This library essentially wraps
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
	
	public Agent loadAgent(String agentName, ParameterHolder theParams) {
		String thename = libDir + agentName;
		Agent theAgent=new JNIAgent(thename,theParams);
		return theAgent;
	}

	public String getTypeSuffix() {
		return " - C++";
	}

}
