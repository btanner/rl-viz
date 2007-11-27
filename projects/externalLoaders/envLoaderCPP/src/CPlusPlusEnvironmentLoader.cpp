//
#include "jni.h" 
#include "environmentShell_JNIEnvironment.h"
#include "environmentShell_LocalCPlusPlusEnvironmentLoader.h" 
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

#define DEBUGMODE = 0

// not sure if this is valid... but here goes!
typedef Task_specification (*envinit_t)();
envinit_t env_init;
//
typedef Observation (*envstart_t)();
envstart_t env_start;
//
typedef Reward_observation (*envstep_t)(Action a);
envstep_t env_step;
//
typedef void (*envcleanup_t)();
envcleanup_t env_cleanup;
//
typedef State_key (*envgetstate_t)();
envgetstate_t env_get_state;
//
typedef void (*envsetstate_t)(State_key sk);
envsetstate_t env_set_state;
//
typedef void (*envsetrandomseed_t)(Random_seed_key rsk);
envsetrandomseed_t env_set_random_seed;
//
typedef Random_seed_key (*envgetrandomseed_t)();
envgetrandomseed_t env_get_random_seed;
//
typedef Message (*envmessage_t)(const Message inMessage);
envmessage_t env_message;
//
typedef const char* (*envgetparams_t)();
envgetparams_t env_getDefaultParameters;
//
typedef void (*envsetparams_t)(const char* theParams);
envsetparams_t env_setDefaultParameters;

//global variables
void *handle;
//global RL_abstract_type used for C accessor methods
RL_abstract_type genericReturn;
Reward_observation rewardObs;
std::vector<std::string> envNames;
std::vector<std::string> theParamHolderStrings;
int numEnvs;

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

void setParams(JNIEnv *env, jstring paramString){
    	static const char *theParamCString=NULL;

        //get rid of the old message
	if(theParamCString!=NULL){
		free((char *)theParamCString);
		theParamCString=NULL;
	}
	//ensure the handle is still valid
	checkValidHandle();

	if(!env_setDefaultParameters){
		std::cerr << "'env_setDefaultParameters' is not implemented by this environment (or we lost the function pointer)! " << dlerror() << '\n';
                return;
	}
	theParamCString = env->GetStringUTFChars(paramString,0);
        //might be an ERROR here... need testing to verify
	//ParameterHolder newParamHolder = ParameterHolder(std::string(theparams));
        env_setDefaultParameters(theParamCString);
}

JNIEXPORT jboolean JNICALL Java_environmentShell_JNIEnvironment_JNIloadEnvironment(JNIEnv *env, jobject obj, jstring libName, jstring theParamString)  
{
	//make a C cipy of the java string
	const char *lib = env->GetStringUTFChars(libName, 0);
	handle = dlopen(lib, RTLD_LOCAL);
	
        std::vector<std::string> notSym;
	//ensure the handle is valid
	checkValidHandle();
	//LOAD THE SYMBOLS, make sure they are valid
	//env_start
	env_start = (envstart_t) dlsym(handle, "env_start");
	if(!env_start){
		printSymError("env_start");
		return false;
	}
	
	//env_init
	env_init = (envinit_t) dlsym(handle, "env_init");
	if(!env_init){
		printSymError("env_init");
		return false;
	}
	
	//env_cleanup
	env_cleanup = (envcleanup_t) dlsym(handle, "env_cleanup");
	if(!env_cleanup){
		notSym.push_back("env_cleanup");
	}
	
	//env_get_state
	env_get_state = (envgetstate_t) dlsym(handle, "env_get_state");
	if(!env_get_state){
		notSym.push_back("env_getstate");
	}
	
	//env_step
	env_step = (envstep_t) dlsym(handle, "env_step");
	if(!env_step){
		printSymError("env_step");
		return false;
	}
	
	//env_set_state
	env_set_state = (envsetstate_t) dlsym(handle, "env_set_state");
	if(!env_set_state){
		notSym.push_back("env_set_state");
	}
	
	//env_set_random_seed
	env_set_random_seed = (envsetrandomseed_t) dlsym(handle, "env_set_random_seed");
	if(!env_set_random_seed){
		notSym.push_back("env_set_random_seed");
	}
	
	//env_message
	env_message = (envmessage_t) dlsym(handle, "env_message");
	if(!env_message){
		notSym.push_back("env_message");
	}
        //
        //
        //
        env_setDefaultParameters = (envsetparams_t) dlsym(handle, "env_setDefaultParameters");
	if(!env_setDefaultParameters){
		notSym.push_back("env_setDefaultParameters");
        }else{
            const char *theParamCString=NULL;
        
            theParamCString = env->GetStringUTFChars(theParamString,0);
        //might be an ERROR here... need testing to verify
	//ParameterHolder newParamHolder = ParameterHolder(std::string(theparams));
        env_setDefaultParameters(theParamCString);
        }
        printSymWarning(notSym);
        return true;
} 

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvstart(JNIEnv *env, jobject obj)  
{ 
	//ensure the handle is still valid
	checkValidHandle();

	if(!env_start){
		std::cerr << "'env_start' has been lost! OH NOES!: " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	//dont know if this does what i want it to... but we shall see!
	genericReturn = env_start();
}


JNIEXPORT jstring JNICALL Java_environmentShell_JNIEnvironment_JNIenvinit(JNIEnv *env, jobject obj)  
{ 
	//ensure the handle is still valid
	checkValidHandle();
	if(!env_init){
		std::cerr << "Cannot load symbol 'env_init': " << dlerror() << '\n';
		dlclose(handle);
		return NULL;
	}
	//jstring thenewstring = (*env).NewStringUTF(env, *env_init());
	return env->NewStringUTF(env_init());
} 


JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvstep(JNIEnv *env, jobject obj, jint numInts, jint numDoubles, jintArray intArray, jdoubleArray doubleArray)  
{ 
	//ensure the handle is still valid
	checkValidHandle();

	if(!env_step){
		std::cerr << "'env_step' has been lost! OH NOES!: " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
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
	rewardObs = env_step(a);
	genericReturn = rewardObs.o;	
	// clean up allocated memory... hoping to write memory-leak free code
	free(a.intArray);
	free(a.doubleArray);
} 

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvsetrandomseed(JNIEnv *env, jobject obj, jint numInts, jint numDoubles, jintArray intArray, jdoubleArray doubleArray)  
{ 
	//ensure the handle is still valid
	checkValidHandle();

	if(!env_set_random_seed){
		std::cerr << "'env_set_random_seed' has been lost! OH NOES!: " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
	State_key sk;
	env->GetIntArrayRegion(intArray, 0, numInts, (jint*) sk.intArray);
	env->GetDoubleArrayRegion(doubleArray, 0, numDoubles, (jdouble*) sk.doubleArray);
	env_set_random_seed(sk);
} 
JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvgetrandomseed(JNIEnv *env, jobject obj)  
{ 
	//ensure the handle is still valid
	checkValidHandle();

	if(!env_get_random_seed){
		printWarning("env_get_random_seed");
	}
	genericReturn = env_get_random_seed();
}

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIenvcleanup(JNIEnv *env, jobject obj)  
{ 
	//ensure the handle is still valid
	checkValidHandle();

	if(!env_cleanup){
		printWarning("env_cleanup");
	}
	env_cleanup();
} 

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIsetstate(JNIEnv *env, jobject obj)  
{ 
	//ensure the handle is still valid
	checkValidHandle();
	
	if(!env_init){
		std::cerr << "Cannot load symbol 'env_init': " << dlerror() << '\n';
		dlclose(handle);
		return;
	}

} 

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIgetstate(JNIEnv *env, jobject obj)  
{ 
	//ensure the handle is still valid
	checkValidHandle();
	
	if(!env_init){
		std::cerr << "Cannot load symbol 'env_init': " << dlerror() << '\n';
		dlclose(handle);
		return;
	}
} 

JNIEXPORT jstring JNICALL Java_environmentShell_JNIEnvironment_JNIenvmessage(JNIEnv *env, jobject obj, jstring themessage)  
{ 
	static const char *message=NULL;
	//get rid of the old message
	if(message!=NULL){
		free((char *)message);
		message=NULL;
	}
	//ensure the handle is still valid
	checkValidHandle();

	if(!env_message){
		std::cerr << "'env_message' is not implemented by this environment (or we lost the function pointer)! " << dlerror() << '\n';
		return NULL;
	}
	message = env->GetStringUTFChars(themessage,0);
	return env->NewStringUTF(env_message((const Message)message));
}

JNIEXPORT void JNICALL Java_environmentShell_JNIEnvironment_JNIsetDefaultParameters(JNIEnv *env, jobject obj, jstring paramString)  
{ 
	setParams(env, paramString);
}


/*
*
*	Methods for accessing the genericReturn struct... as each method can only have 
*	1 return. These accessor functions return the numInts, numDoubles, intArray and doubleArray
*	values.
*/
//get the integer from the current struct
JNIEXPORT jint JNICALL Java_environmentShell_JNIEnvironment_JNIgetInt(JNIEnv *env, jobject obj)  
{ 
	return (jint) genericReturn.numInts;	
}
//get the integer array
JNIEXPORT jintArray JNICALL Java_environmentShell_JNIEnvironment_JNIgetIntArray(JNIEnv *env, jobject obj)  
{ 
	jintArray temp = (env)->NewIntArray(genericReturn.numInts);
	jsize arrSize = (jsize)(genericReturn.numInts);
	env->SetIntArrayRegion(temp, 0, arrSize, (jint*)genericReturn.intArray);
	return temp;
}
//get the double from the current struct
JNIEXPORT jint JNICALL Java_environmentShell_JNIEnvironment_JNIgetDouble(JNIEnv *env, jobject obj)  
{ 
	return (jdouble) genericReturn.numDoubles;	
}
//get the double array
JNIEXPORT jdoubleArray JNICALL Java_environmentShell_JNIEnvironment_JNIgetDoubleArray(JNIEnv *env, jobject obj)  
{ 
	jdoubleArray temp = (env)->NewDoubleArray(genericReturn.numDoubles);
	jsize arrSize = (jsize)(genericReturn.numDoubles);
	env->SetDoubleArrayRegion(temp, 0, arrSize, (jdouble*)genericReturn.doubleArray);
	return temp;
}
/*
*	These accessor functions are used to return the reward and terminal values for the reward-observation
*	in env_step
*/
JNIEXPORT jdouble JNICALL Java_environmentShell_JNIEnvironment_JNIgetReward(JNIEnv *env, jobject obj)
{
	return (jdouble) rewardObs.r;
}
JNIEXPORT jint JNICALL Java_environmentShell_JNIEnvironment_JNIgetTerminal(JNIEnv *env, jobject obj)
{
	return (jint) rewardObs.terminal;
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
	env_getDefaultParameters = (envgetparams_t) dlsym(handle, "env_getDefaultParameters");
	if(!env_getDefaultParameters){
		//printWarning("env_getDefaultParameters");
		return 1;
	}
        env_setDefaultParameters = (envsetparams_t) dlsym(handle, "env_setDefaultParameters");
        if(!env_setDefaultParameters){
		//printWarning("env_setDefaultParameters in check env");
	}
	return 2;
}

//This mehtod makes a list of C/C++ environment names and parameter holders.
JNIEXPORT void JNICALL Java_environmentShell_LocalCPlusPlusEnvironmentLoader_JNImakeEnvList(JNIEnv *env, jobject obj, jstring envPath)
{
	std::vector<std::string> files;
	std::string thePath=std::string(env->GetStringUTFChars(envPath, 0));
	getdir(thePath,files);
	numEnvs = 0;
	int check = 0;
	for (unsigned int i = 0;i < files.size();i++) {
		size_t libSuffixPos=files[i].find(".dylib");
		if(libSuffixPos!=std::string::npos){
			std::string theEnvName=files[i];
			check = checkEnvironment(env, obj, thePath + theEnvName);
			if((check > 0) && (theEnvName != "CPPENV.dylib")){
				if(check > 1){
					const char* thePHString=env_getDefaultParameters();
					theParamHolderStrings.push_back(std::string(thePHString));
				}
				else{
					theParamHolderStrings.push_back("NULL");
				}
				envNames.push_back(theEnvName);
				numEnvs++;
			}
			dlclose(handle);
		}
	}
}	

/*
*	Simple C accessor methods, that allow Java to get the returns from this C library
*/
JNIEXPORT jint JNICALL Java_environmentShell_LocalCPlusPlusEnvironmentLoader_JNIgetEnvCount(JNIEnv *env, jobject obj)
{
	return (jint)numEnvs;
}
JNIEXPORT jstring JNICALL Java_environmentShell_LocalCPlusPlusEnvironmentLoader_JNIgetEnvName(JNIEnv *env, jobject obj, jint index)
{
	return env->NewStringUTF((envNames.at((int)index)).c_str());
}
JNIEXPORT jstring JNICALL Java_environmentShell_LocalCPlusPlusEnvironmentLoader_JNIgetEnvParams(JNIEnv *env, jobject obj, jint index)
{
	return env->NewStringUTF((theParamHolderStrings.at((int)index)).c_str());
}