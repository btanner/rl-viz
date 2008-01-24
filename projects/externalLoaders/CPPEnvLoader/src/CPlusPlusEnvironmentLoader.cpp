//
#include "jni.h"
#include "JNI_Env.h"
#include "JNI_CPPEnvLoader.h"
#include "JNI_DylibGrabber.h"

#include <iostream>
#include <unistd.h>
#include <dlfcn.h>
#include "RL_common.h"
#include "ParameterHolder.h"
#include "LoaderConstants.h"
#include "CPlusPlusEnvironmentLoader.h"

#include <sys/types.h>
#include <errno.h>
#include <vector>
#include <string>

#define DEBUGMODE 1


//global variables
envStruct theEnvironment={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

//global RL_abstract_type used for C accessor methods
RL_abstract_type genericReturn;
Reward_observation rewardObs;

std::string theNullParamHolder("NULL");

int loadEnvironmentToStructFromFile(envStruct &thisEnvironment, std::string theFileLocation, jboolean printVerboseErrors=false);
std::vector<std::string> loadEnvironmentToStruct(envStruct &thisEnvironment);
const char* getParameterHolder(std::string theFilePath);


JNIEXPORT jboolean JNICALL Java_environmentShell_JNIEnvironment_JNIloadEnvironment(JNIEnv *env, jobject obj, jstring envFilePath, jstring theParamString) {
    std::string stdStringPath=std::string(env->GetStringUTFChars(envFilePath, 0));

    loadEnvironmentToStructFromFile(theEnvironment, stdStringPath);
    
    
    int status = loadEnvironmentToStructFromFile(theEnvironment,stdStringPath);


    if(status!=DLSYM_SUCCESS)return false;

    setDefaultParams(theEnvironment, env->GetStringUTFChars(theParamString, 0));

    return true;
}

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvstart(JNIEnv *env, jobject obj) {
    genericReturn = theEnvironment.env_start();
}


JNIEXPORT jstring JNICALL Java_environmentShell_JNIEnvironment_JNIenvinit(JNIEnv *env, jobject obj) {
    return env->NewStringUTF(theEnvironment.env_init());
}


JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvstep(JNIEnv *env, jobject obj, jint numInts, jint numDoubles, jintArray intArray, jdoubleArray doubleArray) {
    //create a new action to pass in from the 4 parameters. This is needed because the actual Java object cannot be passed in,
    //so the data from the object is passed in, then put into the C equivalent of an action
    Action a;
    a.numInts=numInts;
    a.intArray    = (int*)malloc(sizeof(int)*a.numInts);
    a.numDoubles=numDoubles;
    a.doubleArray    = (double*)malloc(sizeof(double)*a.numDoubles);
    env->GetIntArrayRegion(intArray, 0, numInts, (jint*) a.intArray);
    env->GetDoubleArrayRegion(doubleArray, 0, numDoubles, (jdouble*) a.doubleArray);
    // get the return from env_step and parse it into a form that java can check.
    rewardObs = theEnvironment.env_step(a);
    genericReturn = rewardObs.o;
    // clean up allocated memory... hoping to write memory-leak free code
    free(a.intArray);
    free(a.doubleArray);
}

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvsetrandomseed(JNIEnv *env, jobject obj, jint numInts, jint numDoubles, jintArray intArray, jdoubleArray doubleArray) {
    State_key sk;
    env->GetIntArrayRegion(intArray, 0, numInts, (jint*) sk.intArray);
    env->GetDoubleArrayRegion(doubleArray, 0, numDoubles, (jdouble*) sk.doubleArray);
    theEnvironment.env_set_random_seed(sk);
}
JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvgetrandomseed(JNIEnv *env, jobject obj) {
    genericReturn = theEnvironment.env_get_random_seed();
}

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvcleanup(JNIEnv *env, jobject obj) {
    theEnvironment.env_cleanup();
}

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIsetstate(JNIEnv *env, jobject obj) {
    std::cerr<<"setState not implemented in JNI"<<std::endl;
}

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIgetstate(JNIEnv *env, jobject obj) {
    std::cerr<<"getState not implemented in JNI"<<std::endl;
}

JNIEXPORT jstring JNICALL Java_environmentShell_JNIEnvironment_JNIenvmessage(JNIEnv *env, jobject obj, jstring themessage) {
    static const char *message=NULL;
    //get rid of the old message
    if(message!=NULL){
        free((char *)message);
        message=NULL;
    }
    message = env->GetStringUTFChars(themessage, 0);
    return env->NewStringUTF(theEnvironment.env_message((const Message)message));
}


/*
 *
 *	Methods for accessing the genericReturn struct... as each method can only have
 *	1 return. These accessor functions return the numInts, numDoubles, intArray and doubleArray
 *	values.
 */
//get the integer from the current struct
JNIEXPORT jint JNICALL Java_environmentShell_JNIEnvironment_JNIgetInt(JNIEnv *env, jobject obj) {
    return (jint) genericReturn.numInts;
}
//get the integer array
JNIEXPORT jintArray JNICALL Java_environmentShell_JNIEnvironment_JNIgetIntArray(JNIEnv *env, jobject obj) {
    jintArray temp = (env)->NewIntArray(genericReturn.numInts);
    jsize arrSize = (jsize)(genericReturn.numInts);
    env->SetIntArrayRegion(temp, 0, arrSize, (jint*)genericReturn.intArray);
    return temp;
}
//get the double from the current struct
JNIEXPORT jint JNICALL Java_environmentShell_JNIEnvironment_JNIgetDouble(JNIEnv *env, jobject obj) {
    return (jint) genericReturn.numDoubles;
}
//get the double array
JNIEXPORT jdoubleArray JNICALL Java_environmentShell_JNIEnvironment_JNIgetDoubleArray(JNIEnv *env, jobject obj) {
    jdoubleArray temp = (env)->NewDoubleArray(genericReturn.numDoubles);
    jsize arrSize = (jsize)(genericReturn.numDoubles);
    env->SetDoubleArrayRegion(temp, 0, arrSize, (jdouble*)genericReturn.doubleArray);
    return temp;
}
/*
 *	These accessor functions are used to return the reward and terminal values for the reward-observation
 *	in env_step
 */
JNIEXPORT jdouble JNICALL Java_environmentShell_JNIEnvironment_JNIgetReward(JNIEnv *env, jobject obj) {
    return (jdouble) rewardObs.r;
}
JNIEXPORT jint JNICALL Java_environmentShell_JNIEnvironment_JNIgetTerminal(JNIEnv *env, jobject obj) {
    return (jint) rewardObs.terminal;
}


//This mehtod makes a list of C/C++ environment names and parameter holders.
JNIEXPORT jint JNICALL Java_rlVizLib_dynamicLoading_DylibGrabber_jniIsThisAValidEnv(JNIEnv *env, jobject obj, jstring envFilePath, jboolean verboseErrors) {
    std::string stdStringPath=std::string(env->GetStringUTFChars(envFilePath, 0));

    envStruct tmpEnvironment;
    int errorCode=loadEnvironmentToStructFromFile(tmpEnvironment, stdStringPath,verboseErrors);
    closeEnvironment(tmpEnvironment);
    
    return errorCode;
}

/*
 *	Simple C accessor methods, that allow Java to get the returns from this C library
 */
JNIEXPORT jstring JNICALL Java_environmentShell_LocalCPlusPlusEnvironmentLoader_JNIgetEnvParams(JNIEnv *env, jobject obj, jstring envFilePath) {
    std::string stdStringFileLocation(env->GetStringUTFChars(envFilePath, 0));
    const char* paramHolderString=getParameterHolder(stdStringFileLocation);
    return env->NewStringUTF(paramHolderString);
}


void closeEnvironment(envStruct &theEnv){
        closeFile(theEnv.handle);
}


void* openFile(std::string fileName){
    void* theLocalHandle=dlopen(fileName.c_str(), RTLD_NOW | RTLD_LOCAL);
    return theLocalHandle;
}

void closeFile(void *theLocalHandle){
    dlclose(theLocalHandle);
}

void checkEnvironmentStruct(envStruct &thisEnvironment, std::vector<std::string> &symFailures){
    if(!thisEnvironment.env_start)symFailures.push_back("env_start");
    if(!thisEnvironment.env_init)symFailures.push_back("env_init");
    if(!thisEnvironment.env_cleanup)symFailures.push_back("env_cleanup");
    if(!thisEnvironment.env_get_state)symFailures.push_back("env_get_state");
    if(!thisEnvironment.env_step)symFailures.push_back("env_step");
    if(!thisEnvironment.env_set_state)symFailures.push_back("env_set_state");
    if(!thisEnvironment.env_set_random_seed)symFailures.push_back("env_set_random_seed");
    if(!thisEnvironment.env_get_state)symFailures.push_back("env_get_state");
    if(!thisEnvironment.env_get_random_seed)symFailures.push_back("env_get_random_seed");
    if(!thisEnvironment.env_message)symFailures.push_back("env_message");
}

int loadEnvironmentToStructFromFile(envStruct &thisEnvironment, std::string theFileLocation,jboolean verboseErrors){
        thisEnvironment.handle = openFile(theFileLocation);
        
        if(!thisEnvironment.handle){
            if(verboseErrors){
                std::cout<<"JNI ::Failed to load file: "<<theFileLocation<<std::endl;
                std::cout<<"JNI ::Failure getting handle from dlopen(): "<<dlerror()<<std::endl;
            }
            return DLSYM_HANDLE_ERROR;
        }
        
        std::vector<std::string> symFailures=loadEnvironmentToStruct(thisEnvironment);
        
        if(symFailures.size()>0){
            if(verboseErrors){
                std::cout<<"JNI ::Failed to dlsym all required functions from file: "<<theFileLocation<<std::endl;
                std::cout<<"JNI ::Undefined required functions:"<<std::endl;
                for(unsigned int i=0;i<symFailures.size();i++)
                    std::cout<<"\t\t"<<symFailures[i]<<std::endl;
            }

            return DLSYM_FUNCTIONS_MISSING;
        }
        
        return DLSYM_SUCCESS;
}

std::vector<std::string> loadEnvironmentToStruct(envStruct &thisEnvironment){
    std::vector<std::string> symFailures;
    
    if(!thisEnvironment.handle){
        std::cout<<"WTF NO HANDLE!"<<std::endl;
        exit(1);
    }
        
    
    thisEnvironment.env_start = (envstart_t) dlsym(thisEnvironment.handle, "env_start");
    thisEnvironment.env_init = (envinit_t) dlsym(thisEnvironment.handle, "env_init");
    thisEnvironment.env_cleanup = (envcleanup_t) dlsym(thisEnvironment.handle, "env_cleanup");
    thisEnvironment.env_get_state = (envgetstate_t) dlsym(thisEnvironment.handle, "env_get_state");
    thisEnvironment.env_step = (envstep_t) dlsym(thisEnvironment.handle, "env_step");
    thisEnvironment.env_set_state = (envsetstate_t) dlsym(thisEnvironment.handle, "env_set_state");
    thisEnvironment.env_set_random_seed = (envsetrandomseed_t) dlsym(thisEnvironment.handle, "env_set_random_seed");
    thisEnvironment.env_get_state = (envgetstate_t) dlsym(thisEnvironment.handle, "env_get_state");
    thisEnvironment.env_get_random_seed = (envgetrandomseed_t) dlsym(thisEnvironment.handle, "env_get_random_seed");
    thisEnvironment.env_message = (envmessage_t) dlsym(thisEnvironment.handle, "env_message");
    thisEnvironment.env_setDefaultParameters = (envsetparams_t) dlsym(thisEnvironment.handle, "env_setDefaultParameters");
    thisEnvironment.env_getDefaultParameters = (envgetparams_t) dlsym(thisEnvironment.handle, "env_getDefaultParameters");
    

    checkEnvironmentStruct(thisEnvironment, symFailures);
        
    return symFailures;
}


const char* getParameterHolder(std::string theFilePath){
    const char* thePHString = theNullParamHolder.c_str();

    envStruct tmpEnvironment;
    int errorCode=loadEnvironmentToStructFromFile(tmpEnvironment, theFilePath);

    if (errorCode==DLSYM_SUCCESS)
         if(tmpEnvironment.env_getDefaultParameters)
            thePHString=tmpEnvironment.env_getDefaultParameters();

    closeEnvironment(tmpEnvironment);
    return thePHString;
}


void setDefaultParams(envStruct &thisEnvironment, const char *paramString){
    if(thisEnvironment.env_setDefaultParameters)
        thisEnvironment.env_setDefaultParameters(paramString);
}


void printSymError(std::vector<std::string> &symnames){
    std::cerr << "Cannot load all required symbols"<<std::endl;
    for(unsigned int i=0;i<symnames.size();i++){
        std::cerr<<symnames[i]<<" was missing"<<std::endl;
    }
}
