#
#	These makefiles require jni.h and jni_md.h
#
#	If they dont work for you
#	1) type 'locate jni.h'
#	   set JAVA_HOME to be this path (not including the jni.h on the end)
#   2) type 'locate jni_md.h'
#      set JAVA_HEADER to be this path (not including the jni_md.h on the end)
ifndef JAVA_HOME
	JAVA_HOME=/Library/Java/Home
endif
ifndef JAVA_HEADER
	JAVA_HEADER = $(JAVA_HOME)
endif


BIN_PATH=../../bin
BASE_PATH=../../../../rlcomplibrary
OUT_PATH=$(BASE_PATH)/libraries
INCLUDE_PATH=$(BASE_PATH)/includes
GLUE_PATH=$(BASE_PATH)/RL-Glue/RL-Glue
ENV_HEADERS=$(BASE_PATH)/system/rl-viz
GET_BACK=../../../rl-viz/projects/externalLoaders/CPPAgentLoader

# supported substrings of OSTYPE
# The following words must be uppercase
# If they are found in the uppercased OSTYPE variable
# the particular O/S matches
OSTYPE_LINUX  := LINUX
OSTYPE_MAC    := DARWIN
OSTYPE_CYGWIN := CYGWIN
OSTYPE_MINGW  := MSYS

# convert OSTYPE to uppercase

ifeq ("$(OSTYPE)", "") 
	OSTYPE := $(shell uname | grep -i -E "(Darwin)|(Linux)|(CYGWIN)")
	OSTYPE := $(shell echo "$(OSTYPE)" | tr a-z A-Z )
else
	OSTYPE := $(shell echo -n "$(OSTYPE)" | tr a-z A-Z )
endif

CC=g++

ifeq ("$(OSTYPE)","$(OSTYPE_MAC)")
	SFLAGS := -dynamiclib
else 
	SFLAGS := -shared
endif

LIB_HEADERS=agentShell_LocalCPlusPlusAgentLoader.h agentShell_JNIAgent.h

CPPAGENT.dylib: $(LIB_HEADERS)
	g++ -o $(OUT_PATH)/CPPAGENT.dylib $(SFLAGS) -fPIC -I $(JAVA_HOME) -I $(JAVA_HEADER) -I$(INCLUDE_PATH) -I$(GLUE_PATH) src/CPlusPlusAgentLoader.cpp $(INCLUDE_PATH)/ParameterHolder.cpp
	cd $(ENV_HEADERS) && rm -rf META-INF
	cd $(ENV_HEADERS) && rm -rf agentShell

agentShell_LocalCPlusPlusAgentLoader.h:
	cd $(ENV_HEADERS) && unzip AgentShell.jar
	cd $(ENV_HEADERS) && javah agentShell.JNIAgent && mv agentShell_JNIAgent.h $(GET_BACK)/src

agentShell_JNIAgent.h:
	cd $(ENV_HEADERS) && javah agentShell.LocalCPlusPlusAgentLoader && mv agentShell_LocalCPlusPlusAgentLoader.h $(GET_BACK)/src

clean:
	cd $(ENV_HEADERS) && rm -rf META-INF
	cd $(ENV_HEADERS) && rm -rf agentShell
	rm -f $(OUT_PATH)/CPPAGENT.dylib
	