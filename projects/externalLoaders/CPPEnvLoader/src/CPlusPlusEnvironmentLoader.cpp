//
#include "jni.h"
#include "JNI_Env.h"
#include "JNI_CPPEnvLoader.h"
#include "JNI_DylibGrabber.h"

#include <iostream>
#include <unistd.h>
#include <dlfcn.h>
//#include "RL_common.h"
#include "ParameterHolder.h"
#include "LoaderConstants.h"
#include "CPlusPlusEnvironmentLoader.h"

#include <sys/types.h>
#include <errno.h>
#include <vector>
#include <string>

#include <rlglue/utils/C/RLStruct_util.h>
#define DEBUGMODE 1

/**
 * We should be using a global action and observation type, and using the
 * utility functions from RL-Glue to fill it.  They do some checking to save
 * work... like not mallocing and freeing every step.
 */
//global variables
envStruct theEnvironment = {0, 0, 0, 0, 0, 0, 0, 0};

//global RL_abstract_type used for C accessor methods
const rl_abstract_type_t *sharedReturnVariable;
reward_observation_terminal_t *rewardObs;

std::string theNullParamHolder("NULL");

/*
 * Some function prototypes... why aren't these in the header?
 */
int loadEnvironmentToStructFromFile(envStruct &thisEnvironment, std::string theFileLocation, jboolean printVerboseErrors = false);
std::vector<std::string> loadEnvironmentToStruct(envStruct &thisEnvironment);
const char* getParameterHolder(std::string theFilePath);

/**
 * This function takes a file path (presumably to a shared library)
 *  and loads an environment from it into the global variable "theEnvironment"
 *
 * It uses the string-serialized parameterHolder in theParamString to initialize
 * the environment.
 */
JNIEXPORT jboolean JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIloadEnvironment(JNIEnv *env, jobject obj, jstring envFilePath, jstring theParamString) {
    std::string stdStringPath = std::string(env->GetStringUTFChars(envFilePath, 0));

    loadEnvironmentToStructFromFile(theEnvironment, stdStringPath);
    int status = loadEnvironmentToStructFromFile(theEnvironment, stdStringPath);

    if (status != DLSYM_SUCCESS)return false;

    setDefaultParams(theEnvironment, env->GetStringUTFChars(theParamString, 0));

    return true;
}

/**
 * Pass off the call to env_start, store the response in a global variable
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvstart(JNIEnv *env, jobject obj) {
    sharedReturnVariable = theEnvironment.env_start();
}

/**
 * Pass off the call to env_init
 */
JNIEXPORT jstring JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvinit(JNIEnv *env, jobject obj) {
    return env->NewStringUTF(theEnvironment.env_init());
}

/**
 * Pass off the call to env_step
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvstep(JNIEnv *env, jobject obj, jint numInts, jint numChars, jint numDoubles, jintArray intArray, jdoubleArray doubleArray, jcharArray charArray) {
    //create a new action to pass in from the 4 parameters. This is needed because the actual Java object cannot be passed in,
    //so the data from the object is passed in, then put into the C equivalent of an action

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
    rewardObs = theEnvironment.env_step(theAction);
    freeRLStructPointer(theAction);
    sharedReturnVariable = (observation_t *) rewardObs->observation;
}

/**
 * Pass off env_cleanup
 */
JNIEXPORT void JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvcleanup(JNIEnv *env, jobject obj) {
    theEnvironment.env_cleanup();
}

/**
 * Pass of env_message
 */
JNIEXPORT jstring JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIenvmessage(JNIEnv *env, jobject obj, jstring themessage) {
    static const char *messageVar = NULL;
    //get rid of the old message
    if (messageVar != NULL) {
        free((char *) messageVar);
        messageVar = NULL;
    }
    messageVar = env->GetStringUTFChars(themessage, 0);
    return env->NewStringUTF(theEnvironment.env_message((const char *) messageVar));
}


/*
 *
 *	Methods for accessing the genericReturn struct... as each method can only have
 *	1 return. These accessor functions return the numInts, numDoubles, intArray and doubleArray
 *	values.
 */
//get the integer from the current struct

JNIEXPORT jint JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetIntCount(JNIEnv *env, jobject obj) {
    return (jint) sharedReturnVariable->numInts;
}
//get the integer array

JNIEXPORT jintArray JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetIntArray(JNIEnv *env, jobject obj) {
    jintArray temp = (env)->NewIntArray(sharedReturnVariable->numInts);
    jsize arrSize = (jsize) (sharedReturnVariable->numInts);
    env->SetIntArrayRegion(temp, 0, arrSize, (jint*) sharedReturnVariable->intArray);
    return temp;
}
//get the double from the current struct

JNIEXPORT jint JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetDoubleCount(JNIEnv *env, jobject obj) {
    return (jint) sharedReturnVariable->numDoubles;
}
//get the double array

JNIEXPORT jdoubleArray JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetDoubleArray(JNIEnv *env, jobject obj) {
    jdoubleArray temp = (env)->NewDoubleArray(sharedReturnVariable->numDoubles);
    jsize arrSize = (jsize) (sharedReturnVariable->numDoubles);
    env->SetDoubleArrayRegion(temp, 0, arrSize, (jdouble*) sharedReturnVariable->doubleArray);
    return temp;
}

JNIEXPORT jint JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetCharCount(JNIEnv *env, jobject obj) {
    return (jint) sharedReturnVariable->numChars;
}
//get the integer array

JNIEXPORT jcharArray JNICALL Java_org_rlcommunity_rlviz_environmentshell_JNIEnvironment_JNIgetCharArray(JNIEnv *env, jobject obj) {
    jcharArray temp = (env)->NewCharArray(sharedReturnVariable->numChars);
    jsize arrSize = (jsize) (sharedReturnVariable->numChars);
    env->SetCharArrayRegion(temp, 0, arrSize, (jchar*) sharedReturnVariable->charArray);
    return temp;
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


//This mehtod makes a list of C/C++ environment names and parameter holders.

JNIEXPORT jint JNICALL Java_rlVizLib_dynamicLoading_DylibGrabber_jniIsThisAValidEnv(JNIEnv *env, jobject obj, jstring envFilePath, jboolean verboseErrors) {
    std::string stdStringPath = std::string(env->GetStringUTFChars(envFilePath, 0));

    envStruct tmpEnvironment;
    int errorCode = loadEnvironmentToStructFromFile(tmpEnvironment, stdStringPath, verboseErrors);
    closeEnvironment(tmpEnvironment);

    return errorCode;
}

JNIEXPORT jstring JNICALL Java_org_rlcommunity_rlviz_environmentshell_LocalCPlusPlusEnvironmentLoader_JNIgetEnvParams(JNIEnv *env, jobject obj, jstring envFilePath) {

    std::string stdStringFileLocation(env->GetStringUTFChars(envFilePath, 0));
    const char* paramHolderString = getParameterHolder(stdStringFileLocation);
    return env->NewStringUTF(paramHolderString);
}

/*
 *	Simple C accessor methods, that allow Java to get the returns from this C library
 */
void closeEnvironment(envStruct &theEnv) {
    closeFile(theEnv.handle);
}

void* openFile(std::string fileName) {
    void* theLocalHandle = dlopen(fileName.c_str(), RTLD_NOW | RTLD_LOCAL);
    return theLocalHandle;
}

void closeFile(void *theLocalHandle) {
    dlclose(theLocalHandle);
}

void checkEnvironmentStruct(envStruct &thisEnvironment, std::vector<std::string> &symFailures) {
    if (!thisEnvironment.env_start)symFailures.push_back("env_start");
    if (!thisEnvironment.env_init)symFailures.push_back("env_init");
    if (!thisEnvironment.env_cleanup)symFailures.push_back("env_cleanup");
    if (!thisEnvironment.env_step)symFailures.push_back("env_step");
    if (!thisEnvironment.env_message)symFailures.push_back("env_message");
}

int loadEnvironmentToStructFromFile(envStruct &thisEnvironment, std::string theFileLocation, jboolean verboseErrors) {
    thisEnvironment.handle = openFile(theFileLocation);

    if (!thisEnvironment.handle) {
        if (verboseErrors) {
            std::cout << "JNI ::Failed to load file: " << theFileLocation << std::endl;
            std::cout << "JNI ::Failure getting handle from dlopen(): " << dlerror() << std::endl;
        }
        return DLSYM_HANDLE_ERROR;
    }

    std::vector<std::string> symFailures = loadEnvironmentToStruct(thisEnvironment);

    if (symFailures.size() > 0) {
        if (verboseErrors) {
            std::cout << "JNI ::Failed to dlsym all required functions from file: " << theFileLocation << std::endl;
            std::cout << "JNI ::Undefined required functions:" << std::endl;
            for (unsigned int i = 0; i < symFailures.size(); i++)
                std::cout << "\t\t" << symFailures[i] << std::endl;
        }

        return DLSYM_FUNCTIONS_MISSING;
    }

    return DLSYM_SUCCESS;
}

std::vector<std::string> loadEnvironmentToStruct(envStruct &thisEnvironment) {
    std::vector<std::string> symFailures;

    if (!thisEnvironment.handle) {
        std::cout << "WTF NO HANDLE!" << std::endl;
        exit(1);
    }


    thisEnvironment.env_start = (envstart_t) dlsym(thisEnvironment.handle, "env_start");
    thisEnvironment.env_init = (envinit_t) dlsym(thisEnvironment.handle, "env_init");
    thisEnvironment.env_cleanup = (envcleanup_t) dlsym(thisEnvironment.handle, "env_cleanup");
    thisEnvironment.env_step = (envstep_t) dlsym(thisEnvironment.handle, "env_step");
    thisEnvironment.env_message = (envmessage_t) dlsym(thisEnvironment.handle, "env_message");
    thisEnvironment.env_setDefaultParameters = (envsetparams_t) dlsym(thisEnvironment.handle, "env_setDefaultParameters");
    thisEnvironment.env_getDefaultParameters = (envgetparams_t) dlsym(thisEnvironment.handle, "env_getDefaultParameters");


    checkEnvironmentStruct(thisEnvironment, symFailures);

    return symFailures;
}

const char* getParameterHolder(std::string theFilePath) {
    const char* thePHString = theNullParamHolder.c_str();

    envStruct tmpEnvironment;
    int errorCode = loadEnvironmentToStructFromFile(tmpEnvironment, theFilePath);

    if (errorCode == DLSYM_SUCCESS) {
        if (tmpEnvironment.env_getDefaultParameters) {
            thePHString = tmpEnvironment.env_getDefaultParameters();
        }
        closeEnvironment(tmpEnvironment);
    }

    return thePHString;
}

void setDefaultParams(envStruct &thisEnvironment, const char *paramString) {
    if (thisEnvironment.env_setDefaultParameters)
        thisEnvironment.env_setDefaultParameters(paramString);
}

void printSymError(std::vector<std::string> &symnames) {
    std::cerr << "Cannot load all required symbols" << std::endl;
    for (unsigned int i = 0; i < symnames.size(); i++) {
        std::cerr << symnames[i] << " was missing" << std::endl;
    }
}
