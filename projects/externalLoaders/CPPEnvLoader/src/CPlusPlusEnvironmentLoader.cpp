//
#include "jni.h"
#include "JNI_Env.h"
#include "JNI_CPPEnvLoader.h"
#include "JNI_JNIEnvironmentSharedLibraryContentFilter.h"

#include <iostream>
#include <cassert>
#include <unistd.h>
#include <dlfcn.h>

#include "ParameterHolder.h"
#include "LoaderConstants.h"
#include "CPlusPlusEnvironmentLoader.h"

#include <sys/types.h>
#include <errno.h>


#include <rlglue/utils/C/RLStruct_util.h>

//CEL_DEBUG will be the CPP_ENV_LOADER DEBUG MODE
#ifndef CEL_DEBUG
#define CEL_DEBUG 0
#endif

/**
 * This is the C/C++ Environment Loader for EnvironmentShell.  This code can
 * pick RL-Glue environments out of a dynamic library.  Basically, it extends
 * EnvironmentShell style runtime loading (parameters, localglue, etc) to C/C++.
 *
 * We use dlsym to grab function pointers to all of the RL-Glue functions in the
 * dynamic library, and then Java calls us through JNI to interact with the
 * underlying environment.  We need some global variables because JNI doesn't
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
static envStruct envFuncPointers = {0, 0, 0, 0, 0, 0, 0, 0};

//global RL_abstract_type used for C accessor methods
static const rl_abstract_type_t *sharedReturnVariable;
static reward_observation_terminal_t *rewardObs;

static const char* theNullParamHolder="NULL";


/**
 * This function takes a file path (presumably to a shared library)
 *  and loads an environment from it into the global variable "theEnvironment"
 *
 * It uses the string-serialized parameterHolder in theParamString to initialize
 * the environment.
 */
JNIEXPORT jboolean JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIloadEnvironment(JNIEnv *env, jobject obj, jstring envFilePath, jstring theParamString) {
    const char* stringPath=env->GetStringUTFChars(envFilePath,0);

    loadEnvironmentToStructFromFile(envFuncPointers, stringPath);
    int status = loadEnvironmentToStructFromFile(envFuncPointers, stringPath);

    env->ReleaseStringUTFChars(envFilePath, stringPath);

    if (status != DLSYM_SUCCESS)return false;

    setDefaultParams(envFuncPointers, env->GetStringUTFChars(theParamString, 0));

    return true;
}

/**
 * Pass off the call to env_start, store the response in a global variable
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvstart(JNIEnv *env, jobject obj) {
    sharedReturnVariable = envFuncPointers.env_start();
}

/**
 * Pass off the call to env_init
 */
JNIEXPORT jstring JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvinit(JNIEnv *env, jobject obj) {
    return env->NewStringUTF(envFuncPointers.env_init());
}

/**
 * Pass off the call to env_step
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvstep(JNIEnv *env, jobject obj, jintArray intArray, jdoubleArray doubleArray, jcharArray charArray) {
    //create a new action to pass in from the 4 parameters. This is needed because the actual Java object cannot be passed in,
    //so the data from the object is passed in, then put into the C equivalent of an action

    jsize numInts,numDoubles,numChars=0;
    numInts = env->GetArrayLength(intArray);
    numDoubles = env->GetArrayLength(doubleArray);
    numChars = env->GetArrayLength(charArray);

    action_t* theAction = allocateRLStructPointer(numInts, numDoubles, numChars);
    //    action_t a;
    //    a.numInts = numInts;
    //    a.intArray = (int*) malloc(sizeof (int) * a.numInts);
    //    a.numDoubles = numDoubles;
    //    a.doubleArray = (double*) malloc(sizeof (double) * a.numDoubles);
    //    a.numChars = numChars;
    //    a.charArray = (char*) malloc(sizeof (char) * a.numChars);
    env->GetIntArrayRegion(intArray, 0, numInts, (jint*) theAction->intArray);
    env->GetDoubleArrayRegion(doubleArray, 0, numDoubles, (jdouble*) theAction->doubleArray);
    env->GetCharArrayRegion(charArray, 0, numChars, (jchar*) theAction->charArray);

    // get the return from env_step and parse it into a form that java can check.
    rewardObs = envFuncPointers.env_step(theAction);
    freeRLStructPointer(theAction);
    sharedReturnVariable = (observation_t *) rewardObs->observation;
}

/**
 * Pass off env_cleanup
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvcleanup(JNIEnv *env, jobject obj) {
    envFuncPointers.env_cleanup();
}

/**
 * Pass of env_message
 */
JNIEXPORT jstring JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvmessage(JNIEnv *env, jobject obj, jstring j_theMessage) {
    const char *c_theMessage = 0;

    /* Now c_messageVar points to a representation of the jstring*/
    c_theMessage = env->GetStringUTFChars(j_theMessage, 0);

    /* No worries, the environment will manage the memory for the response*/
    const char* c_messageResponse=envFuncPointers.env_message(c_theMessage);

    /* Tell Java we are done with whatever c_messageVar points to  */
    env->ReleaseStringUTFChars(j_theMessage, c_theMessage);

    jstring j_messageResponse=env->NewStringUTF(c_messageResponse);
    return j_messageResponse;
}



/* Get the integer array */
JNIEXPORT jintArray JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetIntArray(JNIEnv *env, jobject obj) {
    /* Create a new Java Array.  Hope that this will get cleaned up automagically*/
    jintArray jIntArray = (env)->NewIntArray(sharedReturnVariable->numInts);
    /* Figure out how big the intarray from the last observation was */
    jsize arrSize = (jsize) (sharedReturnVariable->numInts);
    /* Copy the values from the C/C++ intArray TO the new Java array*/
    env->SetIntArrayRegion(jIntArray, 0, arrSize, (jint*) sharedReturnVariable->intArray);
    return jIntArray;
}

/* Get the double array */
JNIEXPORT jdoubleArray JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetDoubleArray(JNIEnv *env, jobject obj) {
    /* Create a new Java Array.  Hope that this will get cleaned up automagically*/
    jdoubleArray jDoubleArray = (env)->NewDoubleArray(sharedReturnVariable->numDoubles);
    jsize arrSize = (jsize) (sharedReturnVariable->numDoubles);
    /* Copy the values from the C/C++ doubleArray TO the new Java array*/
    env->SetDoubleArrayRegion(jDoubleArray, 0, arrSize, (jdouble*) sharedReturnVariable->doubleArray);
    return jDoubleArray;
}

/* Get the char array */
JNIEXPORT jcharArray JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetCharArray(JNIEnv *env, jobject obj) {
    jcharArray jCharArray = (env)->NewCharArray(sharedReturnVariable->numChars);
    jsize arrSize = (jsize) (sharedReturnVariable->numChars);
    /* Copy the values from the C/C++ charArray TO the new Java array*/
    env->SetCharArrayRegion(jCharArray, 0, arrSize, (jchar*) sharedReturnVariable->charArray);
    return jCharArray;
}

/*
 *	These accessor functions are used to return the reward and terminal values for the reward-observation
 *	in env_step
 */
JNIEXPORT jdouble JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetReward(JNIEnv *env, jobject obj) {
    return (jdouble) rewardObs->reward;
}

JNIEXPORT jint JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetTerminal(JNIEnv *env, jobject obj) {
    return (jint) rewardObs->terminal;
}


/*
 * Returns whether a particular shared library is a valid environment
 */
JNIEXPORT jint JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironmentSharedLibraryContentFilter_JNIvalidEnv(JNIEnv *env, jobject obj, jstring j_envFilePath, jboolean verboseErrors) {
    const char* c_envFilePath=env->GetStringUTFChars(j_envFilePath, 0);

    envStruct tmpEnvironment;
    int errorCode = loadEnvironmentToStructFromFile(tmpEnvironment, c_envFilePath, verboseErrors);

    closeEnvironment(tmpEnvironment);

    env->ReleaseStringUTFChars(j_envFilePath,c_envFilePath);

    return errorCode;
}

/*
 * Given a particular shared library, pull out the parameter holder string
 * if possible.
 */
JNIEXPORT jstring JNICALL Java_org_rlcommunity_rlviz_environmentshell_LocalCPlusPlusEnvironmentLoader_JNIgetEnvParams(JNIEnv *env, jobject obj, jstring j_envFilePath) {
    const char *c_envFilePath=env->GetStringUTFChars(j_envFilePath, 0);
    const char* paramHolderString = getParameterHolder(c_envFilePath);

    env->ReleaseStringUTFChars(j_envFilePath,c_envFilePath);
    
    jstring j_paramHolderString=env->NewStringUTF(paramHolderString);
    return j_paramHolderString;
}


void closeEnvironment(envStruct &theEnv) {
    closeFile(theEnv.handle);
    theEnv.env_init=NULL;
    theEnv.env_start=NULL;
    theEnv.env_step=NULL;
    theEnv.env_message=NULL;
    theEnv.env_cleanup=NULL;

}

void* openFile(const char *c_fileName) {
    void* theLocalHandle = dlopen(c_fileName, RTLD_NOW | RTLD_LOCAL);
    return theLocalHandle;
}

void closeFile(void *theLocalHandle) {
    dlclose(theLocalHandle);
}

unsigned int checkEnvironmentStruct(envStruct &thisEnvironment,unsigned int shouldPrintErrors) {
    unsigned int missingFunctionCount=0;

    if (!thisEnvironment.env_message){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: env_message"<<std::endl;
        }
    }
    if (!thisEnvironment.env_init){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: env_init"<<std::endl;
        }
    }
    if (!thisEnvironment.env_start){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: env_start"<<std::endl;
        }
    }
    if (!thisEnvironment.env_step){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: env_step"<<std::endl;
        }
    }
    if (!thisEnvironment.env_cleanup){
        missingFunctionCount++;
        if(shouldPrintErrors){
            std::cout<<"\t Missing Function: env_cleanup"<<std::endl;
        }
    }
    return missingFunctionCount;
}

/**
 * Given  the path to a dynamic library file, try to open it and dlsym
 * the appropriate functions into thisEnvironment.
 */
int loadEnvironmentToStructFromFile(envStruct &thisEnvironment, const char* c_fileLocation, jboolean verboseErrors) {
    thisEnvironment.handle = openFile(c_fileLocation);

    if (!thisEnvironment.handle) {
        if (verboseErrors) {
            std::cout << "JNI ::Failed to load file: " << c_fileLocation << std::endl;
            std::cout << "JNI ::Failure getting handle from dlopen(): " << dlerror() << std::endl;
        }
        return DLSYM_HANDLE_ERROR;
    }

    
    unsigned int numMissingFunctions = loadEnvironmentToStruct(thisEnvironment);

    if (numMissingFunctions > 0) {
        if (verboseErrors) {
            std::cout << "JNI ::Failed to dlsym all required functions from file: " << c_fileLocation << std::endl;
            std::cout << "JNI ::Undefined required functions:" << std::endl;
            checkEnvironmentStruct(thisEnvironment,1);
        }
        return DLSYM_FUNCTIONS_MISSING;
    }

    return DLSYM_SUCCESS;
}

unsigned int loadEnvironmentToStruct(envStruct &thisEnvironment) {
    assert(thisEnvironment.handle);

    thisEnvironment.env_start = (envstart_t) dlsym(thisEnvironment.handle, "env_start");
    thisEnvironment.env_init = (envinit_t) dlsym(thisEnvironment.handle, "env_init");
    thisEnvironment.env_cleanup = (envcleanup_t) dlsym(thisEnvironment.handle, "env_cleanup");
    thisEnvironment.env_step = (envstep_t) dlsym(thisEnvironment.handle, "env_step");
    thisEnvironment.env_message = (envmessage_t) dlsym(thisEnvironment.handle, "env_message");
    thisEnvironment.env_setDefaultParameters = (envsetparams_t) dlsym(thisEnvironment.handle, "env_setDefaultParameters");
    thisEnvironment.env_getDefaultParameters = (envgetparams_t) dlsym(thisEnvironment.handle, "env_getDefaultParameters");


    int missingFunctionCount=checkEnvironmentStruct(thisEnvironment, 0);
    return missingFunctionCount;
}

const char* getParameterHolder(const char* c_filePath) {
    const char* thePHString = theNullParamHolder;

    envStruct tmpEnvironment;
    int errorCode = loadEnvironmentToStructFromFile(tmpEnvironment, c_filePath);

    if (errorCode == DLSYM_SUCCESS) {
        if (tmpEnvironment.env_getDefaultParameters) {
            thePHString = tmpEnvironment.env_getDefaultParameters();
        }
        closeEnvironment(tmpEnvironment);
    }
    return thePHString;
}

void setDefaultParams(envStruct &thisEnvironment, const char *paramString) {
    if (thisEnvironment.env_setDefaultParameters){
        thisEnvironment.env_setDefaultParameters(paramString);
    }
}

