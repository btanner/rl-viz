/* CPlusPlusAgentLoader, a dynamic loader for C++ and Java RL-Glue Agents
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

#include "jni.h" 
#include "agentShell_JNIAgent.h"
#include "agentShell_LocalCPlusPlusAgentLoader.h" 
#include <iostream>
#include <unistd.h>
#include <dlfcn.h>
#include "RL_common.h"
#include "ParameterHolder.h"

#include <sys/types.h>
#include <dirent.h>
#include <errno.h>
#include <vector>
#include <string>

#define debugCPPAGENTLOAD 0

typedef void (*agentinit_t)(const Task_specification spec);
agentinit_t agent_init;

typedef Action (*agentstart_t)(Observation o);
agentstart_t agent_start;

typedef Action (*agentstep_t)(double r, Observation o);
agentstep_t agent_step;

typedef void (*agentend_t)(double r);
agentend_t agent_end;

typedef void (*agentcleanup_t)();
agentcleanup_t agent_cleanup;

typedef void (*agentfreeze_t)();
agentfreeze_t agent_freeze;

typedef Message (*agentmessage_t)(const Message inMessage);
agentmessage_t agent_message;

typedef char* (*agentgetparams_t)();
agentgetparams_t agent_getDefaultParameters;

typedef void (*agentsetparams_t)(ParameterHolder theParams);
agentsetparams_t agent_setDefaultParameters;

//global variables
void *handle;
//global RL_abstract_type used for C accessor methods
RL_abstract_type genericReturn;
Reward_observation rewardObs;
std::vector<std::string> agentNames;
std::vector<std::string> theParamHolderStrings;
int numAgents;

void checkValidHandle(){
	if(!handle){
		std::cerr << "Cannot open Environment: " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
}

void printSymError(char* symname){
	std::cerr << "Cannot load symbol " << symname << ": " << dlerror() << '\n';
	std::cerr << "This function is REQUIRED, cannot proceed... handle will be closed\n";
	dlclose(handle);
	return;	
}

void printSymWarning(std::vector<std::string> symnames){
    int i =0;
    if(symnames.size() > 0){
        std::cerr << "Cannot load symbols: ";
    }
    while(i<symnames.size()){
	std::cerr <<  symnames[i] << " ";
        i++;
    }
    if(symnames.size() > 0){
        std::cerr << "\nThese functions were not required, handle will not be closed yet!\n";
    }
}

void printWarning(char* symname){
	std::cerr << "Cannot load symbol " << symname << ": " << dlerror() << '\n';
	std::cerr << "Warning, this function is not Required.. so the handle will not be closed yet \n";
}

JNIEXPORT jboolean JNICALL Java_agentShell_JNIAgent_JNIloadAgent(JNIEnv *env, jobject obj, jstring libName)  
{
	//make a C cipy of the java string
	const char *lib = env->GetStringUTFChars(libName, 0);
	handle = dlopen(lib, RTLD_NOW | RTLD_LOCAL);
	std::vector<std::string> notSym;
	//ensure the handle is valid
	checkValidHandle();
	
	agent_init = (agentinit_t) dlsym(handle, "agent_init");
	if(!agent_init){
		printSymError("agent_init");
		return false;
	}
	agent_start = (agentstart_t) dlsym(handle, "agent_start");
	if(!agent_start){
		printSymError("agent_start");
		return false;
	}
	
	agent_step = (agentstep_t) dlsym(handle, "agent_step");
	if(!agent_step){
		printSymError("agent_step");
		return false;
	}
	agent_end = (agentend_t) dlsym(handle, "agent_end");
	if(!agent_end){
		printSymError("agent_end");
		return false;
	}
	agent_cleanup = (agentcleanup_t) dlsym(handle, "agent_cleanup");
	if(!agent_cleanup){
		printSymError("agent_cleanup");
		return false;
	}
	agent_freeze = (agentfreeze_t) dlsym(handle, "agent_freeze");
	if(!agent_freeze){
		notSym.push_back("agent_freeze");
	}
	agent_message = (agentmessage_t) dlsym(handle, "agent_message");
	if(!agent_message){
		printSymError("agent_message");
		return false;
	}
	printSymWarning(notSym);
        if(debugCPPAGENTLOAD)std::cout<<"\n-----------\nSuccessfully dlsymed the bare essentials\n------------"<<std::endl;
        
	return true;
}

JNIEXPORT void JNICALL Java_agentShell_JNIAgent_JNIagentinit(JNIEnv *env, jobject obj, jstring taskSpec)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent init"<<std::endl;
	//ensure the handle is still valid
	checkValidHandle();
	if(!agent_init){
		std::cerr << "Cannot load symbol 'agent_init': " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	//jstring thenewstring = (*agent).NewStringUTF(env, *env_init());
	const char* temp = env->GetStringUTFChars(taskSpec, 0);
	agent_init((char *)temp);
} 

JNIEXPORT void JNICALL Java_agentShell_JNIAgent_JNIagentstart(JNIEnv *env, jobject obj, jint numInts, jint numDoubles, jintArray intArray, jdoubleArray doubleArray)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent start"<<std::endl;

	//ensure the handle is still valid
	checkValidHandle();
	if(!agent_start){
		std::cerr << "Cannot load symbol 'agent_start': " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	//create a new observation to pass in from the 4 parameters. This is needed because the actual Java object cannot be passed in,
	//so the data from the object is passed in, then put into the C equivalent of an observation
	Observation a;
	a.numInts=numInts;
	a.intArray    = (int*)malloc(sizeof(int)*a.numInts);
	a.numDoubles=numDoubles;
	a.doubleArray    = (double*)malloc(sizeof(double)*a.numDoubles);
	env->GetIntArrayRegion(intArray, 0, numInts, (jint*) a.intArray);
	env->GetDoubleArrayRegion(doubleArray, 0, numDoubles, (jdouble*) a.doubleArray);
	// get the return from env_step and parse it into a form that java can check.
	genericReturn = agent_start(a);	
	// clean up allocated memory... hoping to write memory-leak free code
	free(a.intArray);
	free(a.doubleArray);
} 
JNIEXPORT void JNICALL Java_agentShell_JNIAgent_JNIagentstep(JNIEnv *env, jobject obj, jdouble rew, jint numInts, jint numDoubles, jintArray intArray, jdoubleArray doubleArray)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent step"<<std::endl;

	//ensure the handle is still valid
	checkValidHandle();
	if(!agent_step){
		std::cerr << "Cannot load symbol 'agent_step': " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	Observation a;
	a.numInts=numInts;
	a.intArray    = (int*)malloc(sizeof(int)*a.numInts);
	a.numDoubles=numDoubles;
	a.doubleArray    = (double*)malloc(sizeof(double)*a.numDoubles);
	env->GetIntArrayRegion(intArray, 0, numInts, (jint*) a.intArray);
	env->GetDoubleArrayRegion(doubleArray, 0, numDoubles, (jdouble*) a.doubleArray);
	// get the return from env_step and parse it into a form that java can check.
	genericReturn = agent_step(rew, a);	
	// clean up allocated memory... hoping to write memory-leak free code
	free(a.intArray);
	free(a.doubleArray);
} 

JNIEXPORT void JNICALL Java_agentShell_JNIAgent_JNIagentend(JNIEnv *env, jobject obj, jdouble rew)  
{     if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent end"<<std::endl;

	//ensure the handle is still valid
	checkValidHandle();
	if(!agent_end){
		std::cerr << "Cannot load symbol 'agent_end': " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	
	agent_end(rew);
}

JNIEXPORT void JNICALL Java_agentShell_JNIAgent_JNIagentcleanup(JNIEnv *env, jobject obj)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent cleanup"<<std::endl;

	//ensure the handle is still valid
	checkValidHandle();
	if(!agent_cleanup){
		std::cerr << "Cannot load symbol 'agent_cleanup': " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	agent_cleanup();
} 
JNIEXPORT void JNICALL Java_agentShell_JNIAgent_JNIagentfreeze(JNIEnv *env, jobject obj)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent freeze"<<std::endl;

	//ensure the handle is still valid
	checkValidHandle();
	if(!agent_freeze){
		std::cerr << "Cannot load symbol 'agent_freeze': " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	agent_freeze();
} 

JNIEXPORT jstring JNICALL Java_agentShell_JNIAgent_JNIagentmessage(JNIEnv *env, jobject obj, jstring themessage)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent message"<<std::endl;

	static const char *message=NULL;
	//get rid of the old message
	if(message!=NULL){
		free((char *)message);
		message=NULL;
	}
	//ensure the handle is still valid
	checkValidHandle();

	if(!agent_message){
		std::cerr << "'agent_message' has been lost! OH NOES!: " << dlerror() << '\n';
		dlclose(handle);
		return NULL;
	}
	message = env->GetStringUTFChars(themessage,0);
        if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent message -- Sending: "<<message<<std::endl;

        jstring response=env->NewStringUTF(agent_message((const Message)message));
	return response;
}

JNIEXPORT void JNICALL Java_agentShell_JNIAgent_JNIsetDefaultParameters(JNIEnv *env, jobject obj, jstring paramString)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI agent setdefaultParams"<<std::endl;

	static const char *theparams=NULL;
	//get rid of the old message
	if(theparams!=NULL){
		free((char *)theparams);
		theparams=NULL;
	}
	//ensure the handle is still valid
	checkValidHandle();

	if(!agent_setDefaultParameters){
		std::cerr << "'agent_setDefaultParameters' has been lost! OH NOES!: " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	theparams = env->GetStringUTFChars(paramString,0);
        //might be an ERROR here... need testing to verify
	ParameterHolder newParamHolder = ParameterHolder(std::string(theparams));
        agent_setDefaultParameters(newParamHolder);
}

/*
*
*	Methods for accessing the genericReturn struct... as each method can only have 
*	1 return. These accessor functions return the numInts, numDoubles, intArray and doubleArray
*	values.
*/
//get the integer from the current struct
JNIEXPORT jint JNICALL Java_agentShell_JNIAgent_JNIgetInt(JNIEnv *env, jobject obj)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI getint"<<std::endl;

	return (jint) genericReturn.numInts;	
}
//get the integer array
JNIEXPORT jintArray JNICALL Java_agentShell_JNIAgent_JNIgetIntArray(JNIEnv *env, jobject obj)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI getintArray"<<std::endl;

	jintArray temp = (env)->NewIntArray(genericReturn.numInts);
	jsize arrSize = (jsize)(genericReturn.numInts);
	env->SetIntArrayRegion(temp, 0, arrSize, (jint*)genericReturn.intArray);
	return temp;
}
//get the double from the current struct
JNIEXPORT jint JNICALL Java_agentShell_JNIAgent_JNIgetDouble(JNIEnv *env, jobject obj)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI getdouble"<<std::endl;

	return (jnit) genericReturn.numDoubles;	
}
//get the double array
JNIEXPORT jdoubleArray JNICALL Java_agentShell_JNIAgent_JNIgetDoubleArray(JNIEnv *env, jobject obj)  
{ 
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI getDoubleArray"<<std::endl;

	jdoubleArray temp = (env)->NewDoubleArray(genericReturn.numDoubles);
	jsize arrSize = (jsize)(genericReturn.numDoubles);
	env->SetDoubleArrayRegion(temp, 0, arrSize, (jdouble*)genericReturn.doubleArray);
	return temp;
}

/*
*	getDir and checkEnvironment are private functions used by JNImakeEnvList to make the code more readable.
*	getDir is stolen from bt-glue? (Brians code)
*	checkEnvironment tries to open the lib passed into it, if it cant open, 0 is returned, if it can open 
*	but it does not contain the env_getDefaultParameterHolder method, 1 is returned, otherwise 2 is returned.
*/
int getdir (std::string dir, std::vector<std::string> &files)
{
	DIR *dp;
	struct dirent *dirp;
	if((dp = opendir(dir.c_str())) == NULL) {
		std::cout << "Error(" << errno << ") opening " << dir << std::endl;
		return errno;
	}
	
	while ((dirp = readdir(dp)) != NULL) {
		files.push_back(std::string(dirp->d_name));
	}
	closedir(dp);
	return 0;
}

int checkEnvironment(JNIEnv *env, jobject obj, std::string libName)  
{
    if(debugCPPAGENTLOAD)std::cout<<" ---- Check Environment"<<std::endl;

	//make a C copy of the java string and check if its a valid library
	handle = dlopen(libName.c_str(), RTLD_NOW | RTLD_LOCAL);
	if(!handle){
		checkValidHandle();
		return 0;
	}
	//check if there is a getDefaultParameterHolder method
	agent_getDefaultParameters = (agentgetparams_t) dlsym(handle, "agent_getDefaultParameters");
	if(!agent_getDefaultParameters){
		printWarning("agent_getDefaultParameters");
		return 1;
	}
	if(!agent_setDefaultParameters){
		printWarning("agent_setDefaultParameters");
	}
	return 2;
}
//This mehtod makes a list of C/C++ environment names and parameter holders.
JNIEXPORT void JNICALL Java_agentShell_LocalCPlusPlusAgentLoader_JNImakeAgentList(JNIEnv *env, jobject obj, jstring envPath)
{
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI makeAgentList"<<std::endl;

	std::vector<std::string> files;
	std::string thePath=std::string(env->GetStringUTFChars(envPath, 0));
	getdir(thePath,files);
	numAgents = 0;
	int check = 0;
	for (unsigned int i = 0;i < files.size();i++) {
		size_t libSuffixPos=files[i].find(".dylib");
		if(libSuffixPos!=std::string::npos){
			std::string theAgentName=files[i];
			check = checkEnvironment(env, obj, thePath + theAgentName);
			if((check > 0) && (theAgentName != "CPPAGENT.dylib")){
				if(check > 1){
					char* thePHString=agent_getDefaultParameters();
					theParamHolderStrings.push_back(std::string(thePHString)) ;
				}
				else{
					theParamHolderStrings.push_back("NULL");
				}
				agentNames.push_back(theAgentName);
				numAgents++;
			}
			dlclose(handle);
		}
	}
}	

/*
*	Simple C accessor methods, that allow Java to get the returns from this C library
*/
JNIEXPORT jint JNICALL Java_agentShell_LocalCPlusPlusAgentLoader_JNIgetAgentCount(JNIEnv *env, jobject obj)
{
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI getAgentCount"<<std::endl;

	return (jint)numAgents;
}
JNIEXPORT jstring JNICALL Java_agentShell_LocalCPlusPlusAgentLoader_JNIgetAgentName(JNIEnv *env, jobject obj, jint index)
{
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI getAgentName"<<std::endl;
	return env->NewStringUTF((agentNames.at((int)index)).c_str());
}
JNIEXPORT jstring JNICALL Java_agentShell_LocalCPlusPlusAgententLoader_JNIgetAgentParams(JNIEnv *env, jobject obj, jint index)
{
    if(debugCPPAGENTLOAD)std::cout<<" ----JNI getAgentParams"<<std::endl;
	return env->NewStringUTF((theParamHolderStrings.at((int)index)).c_str());
}
