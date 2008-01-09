BIN_PATH=.
OUT_PATH=.
INCLUDE_PATH=../../../dist/includes

CC=g++


CPPENV.dylib:
	$(CC) -o $(OUT_PATH)/CPPENV.dylib -dynamiclib -fPIC -I $(JAVA_HOME)/include -I$(JAVA_HOME1)/include -I$(INCLUDE_PATH) -I../../RL-Glue/RL-Glue src/CPlusPlusEnvironmentLoader.cpp $(INCLUDE_PATH)/ParameterHolder.cpp

clean:
	rm -f $(OUT_PATH)/CPPENV.dylib
