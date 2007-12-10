#/bin/bash
glueRoot=../projects/rl-glue
glueExe=$glueRoot/RL-Glue/bin/RL_glue

RLVizDir=../system/dist
RLVizAppJar=$RLVizDir/RLVizApp.jar
RLVizLibJar=$RLVizDir/RLVizLib.jar
EnvShellJar=$RLVizDir/EnvironmentShell.jar
AgentShellJar=$RLVizDir/AgentShell.jar



$glueExe &
java -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/system/dist/ -classpath $RLVizLibJar:$RLVizAppJar btViz.NetGraphicalDriverBothDynamic &
java -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/system/dist/ -classpath $RLVizLibJar:$AgentShellJar rlglue.agent.AgentLoader agentShell.AgentShell &
java -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/system/dist/ -classpath $RLVizLibJar:$EnvShellJar rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell 

