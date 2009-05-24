#ifndef CPLUSPLUSAGENTLOADER_H
#define CPLUSPLUSAGENTLOADER_H

#include <rlglue/Agent_common.h>

/*
We want to define some types to represent our different function prototypes
For example:
typedef const char*(*agentinit_t)();

This creates a type called agentinit_t which points to a function that
takes no parameters and returns a const char *.
*/
typedef void (*agentinit_t)(const char*);
typedef action_t* (*agentstart_t)(const observation_t*);
typedef action_t* (*agentstep_t)(double,const observation_t*);
typedef action_t* (*agentend_t)(double);
typedef void (*agentcleanup_t)();
typedef const char* (*agentmessage_t)(const char * message);
typedef const char* (*agentgetparams_t)();
typedef void (*agentsetparams_t)(const char* theParams);

/**
 * This structure holds pointers to the various RL-Glue/RL-Viz functions in the
 * dynamic library.
 */
struct agentFuncs{
/*Handle to the actual open library*/
    void *handle;

/*RL-Glue Methods */
    agentinit_t agent_init;
    agentstart_t agent_start;
    agentstep_t agent_step;
    agentend_t agent_end;
    agentmessage_t agent_message;
    agentcleanup_t agent_cleanup;
/*RL-Viz Methods (for run-time parameterization) */
    agentsetparams_t agent_setDefaultParameters;
    agentgetparams_t agent_getDefaultParameters;
};
typedef struct agentFuncs agentStruct;

/*
 * Here are the prototypes for the NON-JNI functions we create
 */

/*
 * Close the file handle for an open shared library
 */
void closeAgent(agentStruct &theAgent);

int loadAgentToStructFromFile(agentStruct &thisAgent, const char* c_fileLocation, jboolean printVerboseErrors = false);

/*
 * Tries to dlsym the RL-Glue agent methods from the handle in agentStruct to the
 * pointers in agentStruct.  Returns the number of core methods that are missing.
 * To work, the number of core methods missing should be 0.
 */
unsigned int loadAgentToStruct(agentStruct &thisAgent);

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
 * to make this a valid RL-Glue agentironment.
 *
 * Returns the number of missing core methods.  If shouldPrintFailures==1,
 * this will print out which methods are missing.
 */
unsigned int checkAgentStruct(agentStruct &thisAgent, unsigned int shouldPrintFailures);


/**
 * Pass a string serialized parameter holder into the shared library that is in
 * theAgent
 */
void setDefaultParams(agentStruct &theAgent, const char *paramString);

/**
 * Get the string representation of the parameterholder for the agentironment in
 * the shared library at c_filePath
 */
const char* getParameterHolder(const char* c_filePath);
void checkValidHandle(void *theHandle);
#endif

