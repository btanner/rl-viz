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

#include "/Library/Java/Home/include/jni.h" 
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

typedef std::string (*agentgetparams_t)();
agentgetparams_t agent_getDefaultParameterHolder;

//global variables
void *handle;
//global RL_abstract_type used for C accessor methods
RL_abstract_type genericReturn;
Reward_observation_terminal rewardObs;
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
	std::cerr << "This function is REQUIRED, cannot proceed... handle will be closed";
	dlclose(handle);
	return;	
}

void printSymWarning(char* symname){
	std::cerr << "Cannot load symbol " << symname << ": " << dlerror() << '\n';
	std::cerr << "Warning, this function is not Required.. so the handle will not be closed yet \n";
}

JNIEXPORT jboolean JNICALL Java_agentShell_JNIAgent_JNIloadAgent(JNIEnv *env, jobject obj, jstring libName)  
{
        std::cerr<< "Hitting the new code"<<std::endl;
	//make a C cipy of the java string
	const char *lib = env->GetStringUTFChars(libName, 0);
	handle = dlopen(lib, RTLD_LOCAL);
	
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
		printSymWarning("agent_freeze");
	}
	agent_message = (agentmessage_t) dlsym(handle, "agent_message");
	if(!agent_message){
		printSymError("agent_message");
		return false;
        }else{
            std::cerr<<"set agent_message ok..."<<std::endl;
        }
	
	return true;
}

JNIEXPORT void JNICALL Java_agentShell_JNIAgent_JNIagentinit(JNIEnv *env, jobject obj, jstring taskSpec)  
{ 
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
{ 
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
	static const char *message=NULL;
	//get rid of the old message
	if(message!=NULL){
		free((char *)message);
		message=NULL;
	}
	//ensure the handle is still valid
	checkValidHandle();

	if(!agent_message){
		std::cerr << "'agent_message' TEST BRIAN has been lost! OH NOES!: " << dlerror() << '\n';
		dlclose(handle);
		return NULL;
	}
	message = env->GetStringUTFChars(themessage,0);
	return env->NewStringUTF(agent_message((const Message)message));
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
	return (jint) genericReturn.numInts;	
}
//get the integer array
JNIEXPORT jintArray JNICALL Java_agentShell_JNIAgent_JNIgetIntArray(JNIEnv *env, jobject obj)  
{ 
	jintArray temp = (env)->NewIntArray(genericReturn.numInts);
	jsize arrSize = (jsize)(genericReturn.numInts);
	env->SetIntArrayRegion(temp, 0, arrSize, (jint*)genericReturn.intArray);
	return temp;
}
//get the double from the current struct
JNIEXPORT jint JNICALL Java_agentShell_JNIAgent_JNIgetDouble(JNIEnv *env, jobject obj)  
{ 
	return (jdouble) genericReturn.numDoubles;	
}
//get the double array
JNIEXPORT jdoubleArray JNICALL Java_agentShell_JNIAgent_JNIgetDoubleArray(JNIEnv *env, jobject obj)  
{ 
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
	//make a C cipy of the java string and check if its a valid library
	handle = dlopen(libName.c_str(), RTLD_LOCAL);
	if(!handle){
		checkValidHandle();
		return 0;
	}
	//check if there is a getDefaultParameterHolder method
	agent_getDefaultParameterHolder = (agentgetparams_t) dlsym(handle, "agent_getDefaultParameterHolder");
	if(!agent_getDefaultParameterHolder){
		printSymWarning("env_getDefaultParameterHolder");
		return 1;
	}
	return 2;
}
//This mehtod makes a list of C/C++ environment names and parameter holders.
JNIEXPORT void JNICALL Java_agentShell_LocalCPlusPlusAgentLoader_JNImakeAgentList(JNIEnv *env, jobject obj, jstring envPath)
{
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
					std::string thePHString=agent_getDefaultParameterHolder();
					theParamHolderStrings.push_back(thePHString);
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
	return (jint)numAgents;
}
JNIEXPORT jstring JNICALL Java_agentShell_LocalCPlusPlusAgentLoader_JNIgetAgentName(JNIEnv *env, jobject obj, jint index)
{
	return env->NewStringUTF((agentNames.at((int)index)).c_str());
}
JNIEXPORT jstring JNICALL Java_agentShell_LocalCPlusPlusAgententLoader_JNIgetAgentParams(JNIEnv *env, jobject obj, jint index)
{
	return env->NewStringUTF((theParamHolderStrings.at((int)index)).c_str());
}
	
	
	