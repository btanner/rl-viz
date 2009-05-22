#	READ:
#
#	These makefiles require jni.h and jni_md.h
#
#	If they dont work for you
#	1) type 'locate jni.h'
#	   set JAVA_HOME to be this path (not including the jni.h on the end)
#   2) type 'locate jni_md.h'
#      set JAVA_HEADER to be this path (not including the jni_md.h on the end)
#	NOTE: to set a variable from command line do:
#		export JAVA_HOME = path
#
#	If you get an error saying -javah command not found (or similar), you need
#   to add javah to your $PATH. To do this:
#   1) type 'locate javah'
#   2) type 'export PATH=$PATH:"insert path to javah here"'(dont include the trailing javah)

#This is a Mac default
ifndef JAVA_HOME
	JAVA_HOME=/Library/Java/Home/include
endif

ifndef JAVA_HEADER
	JAVA_HEADER = $(JAVA_HOME)
endif

BASE_PATH=../../../system
OUT_PATH=$(BASE_PATH)/dist
INCLUDE_PATH=$(BASE_PATH)/includes
#Needed?
GLUE_PATH=../../rl-glue/RL-Glue
AGENT_HEADERS=$(BASE_PATH)/dist


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


LIB_HEADERS= CPPAgentLoader.h JNIAgent.h DylibGrabber.h

ifeq ("$(OSTYPE)","$(OSTYPE_MAC)")
	SFLAGS := -dynamiclib 
else 
	SFLAGS := -shared
#This is for linux
endif

CPPAGENT.dylib: $(LIB_HEADERS)
	$(CC) -o $(OUT_PATH)/CPPAGENT.dylib $(SFLAGS) -fPIC -I $(JAVA_HOME) -I $(JAVA_HEADER) -I $(INCLUDE_PATH) -I$(GLUE_PATH) src/CPlusPlusAgentLoader.cpp $(INCLUDE_PATH)/ParameterHolder.cpp
	
JNIAgent.h:
	javah -o src/JNIAgent.h -classpath .:$(AGENT_HEADERS)/agentShell.jar agentShell.JNIAgent
	
CPPAgentLoader.h:
	javah -o src/JNI_CPPAgentLoader.h -classpath .:$(AGENT_HEADERS)/AgentShell.jar agentShell.LocalCPlusPlusAgentLoader

DylibGrabber.h:
	javah -o src/JNI_DylibGrabber.h -classpath .:$(AGENT_HEADERS)/RLVizLib.jar rlVizLib.dynamicLoading.DylibGrabber

clean:
	rm -f $(OUT_PATH)/CPPAGENT.dylib
