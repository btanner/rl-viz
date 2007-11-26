#/bin/bash
glueRoot=../projects/rl-glue
glueExe=$glueRoot/RL-Glue/bin/RL_glue

RLVizDir=../dist
RLVizJar=$RLVizDir/RLVizApp.jar


$glueExe &
java -Xmx128M -DRLVIZ_LIB_PATH=../../../netBeans/libraries/ -classpath $RLVizJar btViz.GraphicalDriver &
java -Xmx128M -DRLVIZ_LIB_PATH=../../../netBeans/libraries/ -classpath $RLVizJar rlglue.agent.AgentLoader agentShell.AgentShell &
java -Xmx128M -DRLVIZ_LIB_PATH=../../../netBeans/libraries/ -classpath $RLVizJar rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell 
