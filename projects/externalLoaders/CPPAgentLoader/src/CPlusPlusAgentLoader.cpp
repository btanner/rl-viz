//
#include "jni.h"
#include "JNI_Agent.h"
#include "JNI_CPPAgentLoader.h"
#include "JNI_JNIAgentSharedLibraryContentFilter.h"

#include <iostream>
#include <cassert>
#include <unistd.h>
#include <dlfcn.h>

#include "ParameterHolder.h"
#include "LoaderConstants.h"
#include "CPlusPlusAgentLoader.h"

#include <sys/types.h>
#include <errno.h>


#include <rlglue/utils/C/RLStruct_util.h>

//CAL_DEBUG will be the CPP_AGENT_LOADER DEBUG MODE
#ifndef CAL_DEBUG
#define CAL_DEBUG 0
#endif

/**
 * This is the C/C++ Agent Loader for AgentShell.  This code can
 * pick RL-Glue agents out of a dynamic library.  Basically, it extends
 * AgentShell style runtime loading (parameters, localglue, etc) to C/C++.
 *
 * We use dlsym to grab function pointers to all of the RL-Glue functions in the
 * dynamic library, and then Java calls us through JNI to interact with the
 * underlying agent.  We need some global variables because JNI doesn't
 * allow us to have composite return types, so we have to keep some things
 * hanging around and retrieve them with a couple of separate method calls.
*/

/**
 * We should be using a global action and observation type, and using the
 * utility functions from RL-Glue to fill it.  They do some checking to save
 * work... like not mallocing and freeing every step.
 *
 * That actually might be lies: a future enhancement/improvement for RL-Glue
 */

/**
 * Global Variables
 */
static agentStruct agentFuncPointers = {0, 0, 0, 0, 0, 0, 0, 0, 0};

//global RL_abstract_type used for C accessor methods
static const rl_abstract_type_t *sharedReturnVariable;
static const char* theNullParamHolder="NULL";


/**
 * This function takes a file path (presumably to a shared library)
 *  and loads an agent from it into the global variable "theAgent"
 *
 * It uses the string-serialized parameterHolder in theParamString to initialize
 * the agent.
 */
JNIEXPORT jboolean JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIloadAgent(JNIEnv *env, jobject obj, jstring agentFilePath, jstring theParamString) {
    const char* stringPath=env->GetStringUTFChars(agentFilePath,0);

    loadAgentToStructFromFile(agentFuncPointers, stringPath);
    int status = loadAgentToStructFromFile(agentFuncPointers, stringPath);

    env->ReleaseStringUTFChars(agentFilePath, stringPath);

    if (status != DLSYM_SUCCESS)return false;

    setDefaultParams(agentFuncPointers, env->GetStringUTFChars(theParamString, 0));

    return true;
}


/**
 * Pass off the call to agent_init
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIagentinit(JNIEnv *env, jobject obj,jstring j_theMessage) {
    const char *c_theMessage = 0;

    /* Now c_messageVar points to a representation of the jstring*/
    c_theMessage = env->GetStringUTFChars(j_theMessage, 0);

    agentFuncPointers.agent_init(c_theMessage);

    /* Tell Java we are done with whatever c_messageVar points to  */
    env->ReleaseStringUTFChars(j_theMessage, c_theMessage);
}


/**
 * Pass off the call to agent_start
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIagentstart(JNIEnv *env, jobject obj, jintArray intArray, jdoubleArray doubleArray, jcharArray charArray) {
    //create a new action to pass in from the 4 parameters. This is needed because the actual Java object cannot be passed in,
    //so the data from the object is passed in, then put into the C equivalent of an action
    jsize numInts,numDoubles,numChars=0;
    numInts = env->GetArrayLength(intArray);
    numDoubles = env->GetArrayLength(doubleArray);
    numChars = env->GetArrayLength(charArray);

    observation_t* theObservation = allocateRLStructPointer(numInts, numDoubles, numChars);
    env->GetIntArrayRegion(intArray, 0, numInts, (jint*) theObservation->intArray);
    env->GetDoubleArrayRegion(doubleArray, 0, numDoubles, (jdouble*) theObservation->doubleArray);
    env->GetCharArrayRegion(charArray, 0, numChars, (jchar*) theObservation->charArray);

    /* Keep track of the action */
    sharedReturnVariable=agentFuncPointers.agent_start(theObservation);

    // get the return from agent_step and parse it into a form that java can check.
    freeRLStructPointer(theObservation);
}

/**
 * Pass off the call to agent_step
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIagentstep(JNIEnv *env, jobject obj, jdouble reward, jintArray intArray, jdoubleArray doubleArray, jcharArray charArray) {
    //create a new action to pass in from the 4 parameters. This is needed because the actual Java object cannot be passed in,
    //so the data from the object is passed in, then put into the C equivalent of an action
    jsize numInts,numDoubles,numChars=0;
    numInts = env->GetArrayLength(intArray);
    numDoubles = env->GetArrayLength(doubleArray);
    numChars = env->GetArrayLength(charArray);

    observation_t* theObservation = allocateRLStructPointer(numInts, numDoubles, numChars);
    env->GetIntArrayRegion(intArray, 0, numInts, (jint*) theObservation->intArray);
    env->GetDoubleArrayRegion(doubleArray, 0, numDoubles, (jdouble*) theObservation->doubleArray);
    env->GetCharArrayRegion(charArray, 0, numChars, (jchar*) theObservation->charArray);

    /* Keep track of the action */
    sharedReturnVariable=agentFuncPointers.agent_step(reward,theObservation);
    
    // get the return from agent_step and parse it into a form that java can check.
    freeRLStructPointer(theObservation);
}

/**
 * Pass off the call to agent_end
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIagentend(JNIEnv *env, jobject obj, jdouble reward) {
    agentFuncPointers.agent_end(reward);
}

/**
 * Pass off agent_cleanup
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIagentcleanup(JNIEnv *env, jobject obj) {
    agentFuncPointers.agent_cleanup();
}

/**
 * Pass of agent_message
 */
JNIEXPORT jstring JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIagentmessage(JNIEnv *env, jobject obj, jstring j_theMessage) {
    const char *c_theMessage = 0;

    /* Now c_messageVar points to a representation of the jstring*/
    c_theMessage = env->GetStringUTFChars(j_theMessage, 0);

    /* No worries, the agent will manage the memory for the response*/
    const char* c_messageResponse=agentFuncPointers.agent_message(c_theMessage);

    /* Tell Java we are done with whatever c_messageVar points to  */
    env->ReleaseStringUTFChars(j_theMessage, c_theMessage);


    jstring j_messageResponse=env->NewStringUTF(c_messageResponse);
    return j_messageResponse;
}



/* Get the integer array */
JNIEXPORT jintArray JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIgetIntArray(JNIEnv *env, jobject obj) {
    /* Create a new Java Array.  Hope that this will get cleaned up automagically*/
    jintArray jIntArray = env->NewIntArray(sharedReturnVariable->numInts);
    /* Figure out how big the intarray from the last observation was */
    jsize arrSize = (jsize) (sharedReturnVariable->numInts);
    /* Copy the values from the C/C++ intArray TO the new Java array*/
    env->SetIntArrayRegion(jIntArray, 0, arrSize, (jint*) sharedReturnVariable->intArray);
    return jIntArray;
}

/* Get the double array */
JNIEXPORT jdoubleArray JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIgetDoubleArray(JNIEnv *env, jobject obj) {
    /* Create a new Java Array.  Hope that this will get cleaned up automagically*/
    jdoubleArray jDoubleArray = env->NewDoubleArray(sharedReturnVariable->numDoubles);
    jsize arrSize = (jsize) (sharedReturnVariable->numDoubles);
    /* Copy the values from the C/C++ doubleArray TO the new Java array*/
    env->SetDoubleArrayRegion(jDoubleArray, 0, arrSize, (jdouble*) sharedReturnVariable->doubleArray);
    return jDoubleArray;
}

/* Get the char array */
JNIEXPORT jcharArray JNICALL Java_org_rlcommunity_rlviz_agentshell_JNIAgent_JNIgetCharArray(JNIEnv *env, jobject obj) {
    jcharArray jCharArray = env->NewCharArray(sharedReturnVariable->numChars);
    jsize arrSize = (jsize) (sharedReturnVariable->numChars);
    /* Copy the values from the C/C++ charArray TO the new Java array*/
    env->SetCharArrayRegion(jCharArray, 0, arrSize, (jchar*) sharedReturnVariable->charArray);
    return jCharArray;
}


/*
 * Returns whether a particular shared library is a valid agent
 */
JNIEXPORT jint JNICALL  Java_org_rlcommunity_rlviz_agentshell_JNIAgentSharedLibraryContentFilter_JNIvalidAgent(JNIEnv *env, jobject obj, jstring j_agentFilePath, jboolean verboseErrors) {
    const char* c_agentFilePath=env->GetStringUTFChars(j_agentFilePath, 0);

    agentStruct tmpAgent;
    int errorCode = loadAgentToStructFromFile(tmpAgent, c_agentFilePath, verboseErrors);
    closeAgent(tmpAgent);

    env->ReleaseStringUTFChars(j_agentFilePath,c_agentFilePath);

    return errorCode;
}

/*
 * Given a particular shared library, pull out the parameter holder string
 * if possible.
 */
JNIEXPORT jstring JNICALL Java_org_rlcommunity_rlviz_agentshell_LocalCPlusPlusAgentLoader_JNIgetAgentParams(JNIEnv *env, jobject obj, jstring j_agentFilePath) {
    const char *c_agentFilePath=env->GetStringUTFChars(j_agentFilePath, 0);
    const char* paramHolderString = getParameterHolder(c_agentFilePath);

    env->ReleaseStringUTFChars(j_agentFilePath,c_agentFilePath);
    
    jstring j_paramHolderString=env->NewStringUTF(paramHolderString);
    return j_paramHolderString;
}


void closeAgent(agentStruct &theAgent) {
    closeFile(theAgent.handle);
    theAgent.agent_init=NULL;
    theAgent.agent_start=NULL;
    theAgent.agent_step=NULL;
    theAgent.agent_message=NULL;
    theAgent.agent_cleanup=NULL;

}

void* openFile(const char *c_fileName) {
    void* theLocalHandle = dlopen(c_fileName, RTLD_NOW | RTLD_LOCAL);
    return theLocalHandle;
}

void closeFile(void *theLocalHandle) {
    dlclose(theLocalHandle);
}

unsigned int checkAgentStruct(agentStruct &thisAgent,unsigned int shouldPrintErrors) {
    unsigned int missingFunctionCount=0;

    if (!thisAgent.agent_message){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: agent_message"<<std::endl;
        }
    }
    if (!thisAgent.agent_init){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: agent_init"<<std::endl;
        }
    }
    if (!thisAgent.agent_start){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: agent_start"<<std::endl;
        }
    }
    if (!thisAgent.agent_step){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: agent_step"<<std::endl;
        }
    }
    if (!thisAgent.agent_end){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: agent_end"<<std::endl;
        }
    }

    if (!thisAgent.agent_cleanup){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: agent_cleanup"<<std::endl;
        }
    }
    return missingFunctionCount;
}

/**
 * Given  the path to a dynamic library file, try to open it and dlsym
 * the appropriate functions into thisAgent.
 */
int loadAgentToStructFromFile(agentStruct &thisAgent, const char* c_fileLocation, jboolean verboseErrors) {
    thisAgent.handle = openFile(c_fileLocation);

    if (!thisAgent.handle) {
        if (verboseErrors) {
            std::cout << "JNI ::Failed to load file: " << c_fileLocation << std::endl;
            std::cout << "JNI ::Failure getting handle from dlopen(): " << dlerror() << std::endl;
        }else{
            std::cout << "JNI_DEBUG :: Successfully loaded from: "<<c_fileLocation<<std::endl;
        }
        return DLSYM_HANDLE_ERROR;
    }

    
    unsigned int numMissingFunctions = loadAgentToStruct(thisAgent);

    if (numMissingFunctions > 0) {
        if (verboseErrors) {
            std::cout << "JNI ::Failed to dlsym all required functions from file: " << c_fileLocation << std::endl;
            std::cout << "JNI ::Undefined required functions:" << std::endl;
            checkAgentStruct(thisAgent,1);
        }
        return DLSYM_FUNCTIONS_MISSING;
    }

    return DLSYM_SUCCESS;
}

unsigned int loadAgentToStruct(agentStruct &thisAgent) {
    assert(thisAgent.handle);

    thisAgent.agent_start = (agentstart_t) dlsym(thisAgent.handle, "agent_start");
    thisAgent.agent_init = (agentinit_t) dlsym(thisAgent.handle, "agent_init");
    thisAgent.agent_cleanup = (agentcleanup_t) dlsym(thisAgent.handle, "agent_cleanup");
    thisAgent.agent_step = (agentstep_t) dlsym(thisAgent.handle, "agent_step");
    thisAgent.agent_end = (agentend_t) dlsym(thisAgent.handle, "agent_end");
    thisAgent.agent_message = (agentmessage_t) dlsym(thisAgent.handle, "agent_message");
    thisAgent.agent_setDefaultParameters = (agentsetparams_t) dlsym(thisAgent.handle, "agent_setDefaultParameters");
    thisAgent.agent_getDefaultParameters = (agentgetparams_t) dlsym(thisAgent.handle, "agent_getDefaultParameters");


    int missingFunctionCount=checkAgentStruct(thisAgent, 0);
    return missingFunctionCount;
}

const char* getParameterHolder(const char* c_filePath) {
    const char* thePHString = theNullParamHolder;

    agentStruct tmpAgent;
    int errorCode = loadAgentToStructFromFile(tmpAgent, c_filePath);

    if (errorCode == DLSYM_SUCCESS) {
        if (tmpAgent.agent_getDefaultParameters) {
            thePHString = tmpAgent.agent_getDefaultParameters();
        }
        closeAgent(tmpAgent);
    }
    return thePHString;
}

void setDefaultParams(agentStruct &thisAgent, const char *paramString) {
    if (thisAgent.agent_setDefaultParameters){
        thisAgent.agent_setDefaultParameters(paramString);
    }
}

