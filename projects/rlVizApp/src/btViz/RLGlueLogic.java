/* RLViz Application, a visualizer and dynamic loader for C++ and Java RL-Glue agents/environments
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
package btViz;



import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.general.TinyGlue;
import rlVizLib.glueProxy.RLGlueProxy;
import rlVizLib.messaging.agentShell.AgentShellListRequest;
import rlVizLib.messaging.agentShell.AgentShellListResponse;
import rlVizLib.messaging.agentShell.AgentShellLoadRequest;
import rlVizLib.messaging.agentShell.AgentShellUnLoadRequest;
import rlVizLib.messaging.environment.EnvVersionSupportedRequest;
import rlVizLib.messaging.environment.EnvVersionSupportedResponse;
import rlVizLib.messaging.environment.EnvVisualizerNameRequest;
import rlVizLib.messaging.environment.EnvVisualizerNameResponse;
import rlVizLib.messaging.environmentShell.EnvShellListRequest;
import rlVizLib.messaging.environmentShell.EnvShellListResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadRequest;
import rlVizLib.messaging.environmentShell.EnvShellUnLoadRequest;
import rlVizLib.visualization.AbstractVisualizer;
import btViz.glueStepper.GlueStepper;
import rlVizLib.messaging.agent.AgentVersionSupportedRequest;
import rlVizLib.messaging.agent.AgentVersionSupportedResponse;
import rlVizLib.messaging.agent.AgentVisualizerNameRequest;
import rlVizLib.messaging.agent.AgentVisualizerNameResponse;
import rlVizLib.messaging.agentShell.AgentShellLoadResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadResponse;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

public class RLGlueLogic {

//	Singleton pattern, should make sure its thread safe
	static RLGlueLogic theGlobalGlueLogic=null;

	static public RLGlueLogic getGlobalGlueLogic(){
		if(theGlobalGlueLogic==null)theGlobalGlueLogic=new RLGlueLogic();

		return theGlobalGlueLogic;
	}

	boolean debugLocal=false;

	TinyGlue myGlueState=null;
	AbstractVisualizer theEnvVisualizer=null;
        DynamicControlTarget theEnvVisualizerControlTarget=null;
	AbstractVisualizer theAgentVisualizer=null;
        DynamicControlTarget theAgentVisualizerControlTarget=null;


	Vector<visualizerLoadListener> envVisualizerLoadListeners=new Vector<visualizerLoadListener>();
	Vector<visualizerLoadListener> agentVisualizerLoadListeners=new Vector<visualizerLoadListener>();
	private RLVizVersion theEnvVersion=null;
	private RLVizVersion theAgentVersion=null;

	GlueStepper theTimeKeeper=null;

	protected RLGlueLogic(){
		theTimeKeeper=new GlueStepper(this);
	}

	public RLVizVersion getEnvVersion(){
		return theEnvVersion;
	}
	public RLVizVersion getAgentVersion(){
		return theAgentVersion;
	}


	public void step(){
		myGlueState.step();
	}


	private EnvShellListResponse theEnvListResponseObject=null;
	private AgentShellListResponse theAgentListResponseObject=null;

	public Vector<String> getEnvNameList(){
		//Get the Environment Names
		if(theEnvListResponseObject==null)theEnvListResponseObject=EnvShellListRequest.Execute();
		return theEnvListResponseObject.getTheEnvList();
	}

	public Vector<ParameterHolder> getEnvParamList(){
		//Get the Environment Parameters
		if(theEnvListResponseObject==null)theEnvListResponseObject=EnvShellListRequest.Execute();
		return theEnvListResponseObject.getTheParamList();
	}


	public Vector<String> getAgentNameList(){
		//Get the Agent Names
		if(theAgentListResponseObject==null)theAgentListResponseObject=AgentShellListRequest.Execute();
		return theAgentListResponseObject.getTheAgentList();
	}

	public Vector<ParameterHolder> getAgentParamList() {
		//Get the Agent Parameters
		if(theAgentListResponseObject==null)theAgentListResponseObject=AgentShellListRequest.Execute();
		return theAgentListResponseObject.getTheParamList();	}



	public void loadEnvironmentVisualizer(){
		//Get the visualizer name if I Can
		EnvVisualizerNameResponse theNameResponse=EnvVisualizerNameRequest.Execute();
		String theVisualizerName=theNameResponse.getTheVisualizerClassName();

                //Only load the env visualizer if someone will care to draw it
                if(envVisualizerLoadListeners.size()>0){
                    theEnvVisualizer=VisualizerFactory.createEnvVisualizerFromClassName(theVisualizerName,myGlueState, theEnvVisualizerControlTarget);
                    
                    if(theEnvVisualizer != null)
                        notifyEnvVisualizerListenersNewEnv();
                    else
                        System.out.println("Caught a NULL ENV visualizer. Vizualiser not Loaded");
                }
	}
        
    public void loadAgentVisualizer() {
		//Get the visualizer name if I Can
		AgentVisualizerNameResponse theNameResponse=AgentVisualizerNameRequest.Execute();
		String theVisualizerName=theNameResponse.getTheVisualizerClassName();

                
                //Only load the agent visualizer if someone will care to draw it
                if(agentVisualizerLoadListeners.size()>0){
    		theAgentVisualizer=VisualizerFactory.createAgentVisualizerFromClassName(theVisualizerName,myGlueState, theAgentVisualizerControlTarget);

                if(theAgentVisualizer != null)
                        notifyAgentVisualizerListenersNewAgent();
                    else
                        System.out.println("Caught a NULL AGENT visualizer. Vizualiser not Loaded");
                }
    }

            
	public boolean loadEnvironment(String envName, ParameterHolder currentParams) {
		EnvShellLoadResponse theLoadResponse= EnvShellLoadRequest.Execute(envName,currentParams);
                if(!theLoadResponse.getTheResult())
                    return false;
                EnvVersionSupportedResponse versionResponse=EnvVersionSupportedRequest.Execute();

		//this shouldn't happen anyway
		if(versionResponse!=null)
			theEnvVersion=versionResponse.getTheVersion();
		else
			theEnvVersion=RLVizVersion.NOVERSION;
                
                return true;

	}

	public boolean loadAgent(String agentName, ParameterHolder agentParams) {
		AgentShellLoadResponse theLoadResponse = AgentShellLoadRequest.Execute(agentName,agentParams);
                if(!theLoadResponse.getTheResult())
                    return false;                
		AgentVersionSupportedResponse versionResponse=AgentVersionSupportedRequest.Execute();

//		//this shouldn't happen anyway
		if(versionResponse!=null)
		theEnvVersion=versionResponse.getTheVersion();
		else
		theAgentVersion=RLVizVersion.NOVERSION;
                
                return true;
	}

	public void setNewStepDelay(int stepDelay) {
		theTimeKeeper.setNewStepDelay(stepDelay);
	}
    

    public void setEnvironmentVisualizerControlTarget(DynamicControlTarget theTarget) {
        theEnvVisualizerControlTarget=theTarget;
    }
   public void setAgentVisualizerControlTarget(DynamicControlTarget theTarget) {
        theAgentVisualizerControlTarget=theTarget;
    }

    void RL_init() {
        RLGlueProxy.RL_init();
    }

    void startVisualizers() {
		//This is not ideal.. getting bad fast
		if(theEnvVisualizer!=null)
			if(!theEnvVisualizer.isCurrentlyRunning()){
				theEnvVisualizer.startVisualizing();
			}
		if(theAgentVisualizer!=null)
			if(!theAgentVisualizer.isCurrentlyRunning()){
				theAgentVisualizer.startVisualizing();
			}
    }

	private void notifyEnvVisualizerListenersNewEnv() {
		for (visualizerLoadListener thisListener : envVisualizerLoadListeners)
			thisListener.notifyVisualizerLoaded(theEnvVisualizer);
	}

	private void notifyAgentVisualizerListenersNewAgent() {
		for (visualizerLoadListener thisListener : agentVisualizerLoadListeners)
			thisListener.notifyVisualizerLoaded(theAgentVisualizer);
	}

	public void addEnvVisualizerLoadListener(visualizerLoadListener panel) {
		envVisualizerLoadListeners.add(panel);
	}
        public void addAgentVisualizerLoadListener(visualizerLoadListener panel) {
                agentVisualizerLoadListeners.add(panel);
        }

	public void startNewExperiment(){
		myGlueState=new TinyGlue();
	}

	public void unloadExperiment(){
		notifyEnvVisualizerListenersUnloadEnv();
		notifyAgentVisualizerListenersUnloadAgent();

		startUnloadEnvironment();
		startUnloadAgent();

		myGlueState=null;

		//Only cleanup if we have inited before
                if(RLGlueProxy.isInited())RLGlueProxy.RL_cleanup();

		finishUnloadEnvironment();
		finishUnloadAgent();
}

	private void startUnloadEnvironment() {
    		if(theEnvVisualizer!=null)theEnvVisualizer.stopVisualizing();
		theEnvVisualizer=null;
	}
	private void startUnloadAgent() {
		if(theAgentVisualizer!=null)theAgentVisualizer.stopVisualizing();
		theAgentVisualizer=null;
	}

	private void finishUnloadEnvironment() {
		if(RLVizPreferences.getInstance().isDynamicEnvironmentLoading())
			EnvShellUnLoadRequest.Execute();
	}


	private void finishUnloadAgent(){
		if(RLVizPreferences.getInstance().isDynamicAgentLoading())
			AgentShellUnLoadRequest.Execute();
	}


	private void notifyEnvVisualizerListenersUnloadEnv() {
		for (visualizerLoadListener thisListener : envVisualizerLoadListeners)
			thisListener.notifyVisualizerUnLoaded();
	}
	private void notifyAgentVisualizerListenersUnloadAgent() {
		for (visualizerLoadListener thisListener : agentVisualizerLoadListeners)
			thisListener.notifyVisualizerUnLoaded();
	}

	public void start() {
		theTimeKeeper.start();
	}

	public void stop() {
		theTimeKeeper.stop();
	}




}





