#ifndef CPLUSPLUSENVIRONMENTLOADER_H
#define CPLUSPLUSENVIRONMENTLOADER_H

#include <rlglue/Environment_common.h>

/*
We want to define some types to represent our different function prototypes
For example:
typedef const char*(*envinit_t)();

This creates a type called envinit_t which points to a function that
takes no parameters and returns a const char *.
*/
typedef const char* (*envinit_t)();
typedef observation_t* (*envstart_t)();
typedef reward_observation_terminal_t* (*envstep_t)(const action_t*);
typedef void (*envcleanup_t)();
typedef const char* (*envmessage_t)(const char * message);
typedef const char* (*envgetparams_t)();
typedef void (*envsetparams_t)(const char* theParams);

/**
 * This structure holds pointers to the various RL-Glue/RL-Viz functions in the
 * dynamic library.
 */
struct envFuncs{
/*Handle to the actual open library*/
    void *handle;

/*RL-Glue Methods */
    envinit_t env_init;
    envstart_t env_start;
    envstep_t env_step;
    envmessage_t env_message;
    envcleanup_t env_cleanup;
/*RL-Viz Methods (for run-time parameterization) */
    envsetparams_t env_setDefaultParameters;
    envgetparams_t env_getDefaultParameters;
};
typedef struct envFuncs envStruct;

/*
 * Here are the prototypes for the NON-JNI functions we create
 */

/*
 * Close the file handle for an open shared library
 */
void closeEnvironment(envStruct &theEnv);

int loadEnvironmentToStructFromFile(envStruct &thisEnvironment, const char* c_fileLocation, jboolean printVerboseErrors = false);

/*
 * Tries to dlsym the RL-Glue env methods from the handle in envStruct to the
 * pointers in envStruct.  Returns the number of core methods that are missing.
 * To work, the number of core methods missing should be 0.
 */
unsigned int loadEnvironmentToStruct(envStruct &thisEnvironment);

const char* getParameterHolder(const char* c_fileLocation);

/*
 * Open and return a file handle to a shared library
 */
void* openFile(const char* c_fileName);
/*
 * Close the shared library associated with this handle
 */
void closeFile(void *theLocalHandle);

/*
 * Check to see if enough functions got parsed out if the library
 * to make this a valid RL-Glue environment.
 *
 * Returns the number of missing core methods.  If shouldPrintFailures==1,
 * this will print out which methods are missing.
 */
unsigned int checkEnvironmentStruct(envStruct &thisEnvironment, unsigned int shouldPrintFailures);


/**
 * Pass a string serialized parameter holder into the shared library that is in
 * theEnvironment
 */
void setDefaultParams(envStruct &theEnvironment, const char *paramString);

/**
 * Get the string representation of the parameterholder for the environment in
 * the shared library at c_filePath
 */
const char* getParameterHolder(const char* c_filePath);
void checkValidHandle(void *theHandle);
#endif

