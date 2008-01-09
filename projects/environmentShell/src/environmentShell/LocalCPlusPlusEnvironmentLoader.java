/* EnvironmentShell, a dynamic loader for C++ and Java RL-Glue environments
* Copyright (C) 2007, Matthew Radkie radkie@gmail.com
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
package environmentShell;

import java.io.File;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlglue.environment.Environment;

/**
*	Java class to talk to a C++ counterpart, 
*
*	environmentShell -> LocalCPlusPlusEnvironmentLoader (java) -> CPlusPlusEnvironmentLoader (C++)
*/
public class LocalCPlusPlusEnvironmentLoader implements EnvironmentLoaderInterface {
	
	// C++ functions to be called from within Java
	//public native void JNIloadEnvironment();
	public native void JNImakeEnvList(String path); 
	public native String JNIgetEnvName(int index); 
	public native String JNIgetEnvParams(int index);
	public native int JNIgetEnvCount();
	
	String libDir;
	
	//load the library so java knows where to find the above functions
	public LocalCPlusPlusEnvironmentLoader(){
		String curDir = System.getProperty("user.dir");
        File thisDirectoryFile= new File(curDir);
		
		String mainLibraryDir=thisDirectoryFile.getParent();
        File parentDirectoryFile=new File(mainLibraryDir);
        
		String workSpaceDirString=parentDirectoryFile.getParent();
		libDir=workSpaceDirString+"/rlcomplibrary/libraries/envJars/";
		String loaderDir = workSpaceDirString+"/rl-viz/system/dist/SharedLibraries/";
		System.load(loaderDir + "CPPENV.dylib");
	}
	
	public LocalCPlusPlusEnvironmentLoader(String path){
		libDir = path;
		System.load(libDir + "CPPENV.dylib");
	}
	public boolean makeList() {
		JNImakeEnvList(libDir);
		return true;
	}

	public Vector<String> getNames() {
		Vector<String> theEnvNames=new Vector<String>();	
		int numEnvs=JNIgetEnvCount();//JNI call that returns the number of envs that were found with makeEnvList()
		
		for(int i=0;i<numEnvs;i++){
			String thisName=JNIgetEnvName(i);
			theEnvNames.add(thisName);
		}
		return theEnvNames;
	}

	public Vector<ParameterHolder> getParameters() {
		Vector<ParameterHolder> theEnvParams=new Vector<ParameterHolder>();
		int numEnvs=JNIgetEnvCount();
                
		for(int i=0;i<numEnvs;i++){
			String ParamHolderString = JNIgetEnvParams(i);//JNI call like getParamHolderInStringFormat(i)
			ParameterHolder thisParamHolder=new ParameterHolder(ParamHolderString);
			theEnvParams.add(thisParamHolder);
		}
		theEnvParams.add(null);
		return theEnvParams;
	}
	
	public Environment loadEnvironment(String envName, ParameterHolder theParams) {
		String thename = libDir + envName;
		JNIEnvironment theEnv=new JNIEnvironment(thename,theParams);
                if(theEnv.isValid())
                    return theEnv;
                else
                    return null;
	}

	public String getTypeSuffix() {
		return " - C++";
	}
}
