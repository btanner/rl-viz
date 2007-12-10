#/bin/bash
glueRoot=../projects/rl-glue
glueExe=$glueRoot/RL-Glue/bin/RL_glue

RLVizDir=../dist
RLVizAppJar=$RLVizDir/RLVizApp.jar
RLVizLibJar=$RLVizDir/RLVizLib.jar
EnvShellJar=$RLVizDir/EnvironmentShell.jar
AgentShellJar=$RLVizDir/AgentShell.jar



$glueExe &
java -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/libraries/ -classpath $RLVizLibJar:$RLVizAppJar btViz.GraphicalDriver &
java -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/libraries/ -classpath $RLVizLibJar:$AgentShellJar rlglue.agent.AgentLoader agentShell.AgentShell &
java -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/libraries/ -classpath $RLVizLibJar:$EnvShellJar rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell 

