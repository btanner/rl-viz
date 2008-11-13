/*
Copyright 2007 Brian Tanner
brian@tannerpages.com
http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/


package btViz;

import org.rlcommunity.rlviz.app.RLVizPreferences;
import org.rlcommunity.rlviz.app.frames.RLVizFrame;
import java.io.IOException;

import org.rlcommunity.rlglue.codec.RLGlue;

import agentShell.AgentShell;

import environmentShell.EnvironmentShell;
import rlVizLib.glueProxy.LocalGlue;

/**
 * @deprecated Use the classes in org.rlcommunity.rlviz.app
 * @author btanner
 */
public class LocalGraphicalDriverBothDynamicBothViz {

	public static void main(String [] args) throws IOException {
            

            //Setup what loaders we want
		RLVizPreferences.getInstance().setDynamicAgentLoading(true);
		RLVizPreferences.getInstance().setDynamicEnvironmentLoading(true);

		EnvironmentShell E= new EnvironmentShell();
		AgentShell A = new AgentShell();
		RLGlue.setGlue(new LocalGlue(E, A));
		
		

		new RLVizFrame(true,true);
	}
}
