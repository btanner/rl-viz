#/bin/bash
glueExe=/usr/local/bin/rl_glue

RLVizDir=../system/dist
RLVizAppJar=$RLVizDir/RLVizApp.jar
RLVizLibJar=$RLVizDir/RLVizLib.jar
EnvShellJar=$RLVizDir/EnvironmentShell.jar
AgentShellJar=$RLVizDir/AgentShell.jar



$glueExe &
java -ea -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/system/dist/ -classpath $RLVizLibJar:$RLVizAppJar btViz.NetGraphicalDriverBothDynamic &
java -ea  -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/system/dist/ -classpath $RLVizLibJar:$AgentShellJar org.rlcommunity.rlglue.codec.util.AgentLoader agentShell.AgentShell &
java -ea  -Xmx128M -DRLVIZ_LIB_PATH=../../rl-library/system/dist/ -classpath $RLVizLibJar:$EnvShellJar org.rlcommunity.rlglue.codec.util.EnvironmentLoader environmentShell.EnvironmentShell 

