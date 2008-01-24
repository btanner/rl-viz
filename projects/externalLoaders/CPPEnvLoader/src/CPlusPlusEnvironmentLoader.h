#ifndef CPLUSPLUSENVIRONMENTLOADER_H
#define CPLUSPLUSENVIRONMENTLOADER_H

typedef Task_specification(*envinit_t)();
typedef Observation(*envstart_t)();
typedef Reward_observation(*envstep_t)(Action a);
typedef void (*envcleanup_t)();
typedef void (*hamburgers_t)();
typedef State_key(*envgetstate_t)();
typedef void (*envsetstate_t)(State_key sk);
typedef void (*envsetrandomseed_t)(Random_seed_key rsk);
typedef Random_seed_key(*envgetrandomseed_t)();
typedef Message(*envmessage_t)(const Message inMessage);
typedef const char* (*envgetparams_t)();
typedef void (*envsetparams_t)(const char* theParams);

struct envFuncs{
    envsetparams_t env_setDefaultParameters;
    envgetparams_t env_getDefaultParameters;
    envmessage_t env_message;
    envgetrandomseed_t env_get_random_seed;
    envsetrandomseed_t env_set_random_seed;
    envsetstate_t env_set_state;
    envgetstate_t env_get_state;
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

