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

struct envFuncs{
    envsetparams_t env_setDefaultParameters;
    envgetparams_t env_getDefaultParameters;
    envmessage_t env_message;
    envcleanup_t env_cleanup;
    envinit_t env_init;
    envstart_t env_start;
    envstep_t env_step;
    void *handle;
};
typedef struct envFuncs envStruct;


//int loadEnvironment(std::string fileName, envStruct &theEnv);
void closeEnvironment(envStruct &theEnv);

void* openFile(std::string fileName);
void closeFile(void *theLocalHandle);
//int checkEnv(std::string fileName);
void setDefaultParams(envStruct &theEnvironment, const char *paramString);
const char* getParameterHolder(std::string theFilePath);
void printSymError(std::vector<std::string> &symnames);
void checkValidHandle(void *theHandle);
#endif

