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
package btViz.frames;

import btViz.*;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class RLVizFrame extends GenericVizFrame{

    //Components

    VisualizerPanel ePanel = null;
    VisualizerPanel aPanel = null;
    RLGlueLogic theGlueConnection = null;
    static String programName = "RLVizApp";
    private static final long serialVersionUID = 1L;

    public RLVizFrame() {
        super();

        boolean useEnvVisualizer = true;
        boolean useAgentVisualizer = false;


        theGlueConnection = RLGlueLogic.getGlobalGlueLogic();
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));


        int panelWidth = 800;
        int panelHeight = 600;


        if (useEnvVisualizer && useAgentVisualizer) {
            panelHeight /= 2;
        }

        JPanel controlPanel = new RLControlPanel(theGlueConnection);
        getContentPane().add(controlPanel);

        if (useEnvVisualizer) {
            envVizFrame=new EnvVisualizerFrame(new Dimension(panelWidth,panelHeight));
            envVizFrame.setLocation(10, 10);
        }

        if (useAgentVisualizer) {
            agentVizFrame=new AgentVisualizerFrame(new Dimension(panelWidth,panelHeight));
            agentVizFrame.setLocation(10, 10);


            if(!useEnvVisualizer)
            agentVizFrame.setLocation(10, 10);
            else
            agentVizFrame.setLocation(10,panelHeight+30);
        }

        setFrames(this, envVizFrame, agentVizFrame);
        makeMenus();

        if(envVizFrame!=null){
            envVizFrame.setFrames(this, envVizFrame, agentVizFrame);
            envVizFrame.makeMenus();
            envVizFrame.setVisible(true);
        }
        if(agentVizFrame!=null){
            agentVizFrame.setFrames(this, envVizFrame, agentVizFrame);
            agentVizFrame.makeMenus();
            agentVizFrame.setVisible(true);
        }
        
        if(!useAgentVisualizer&&!useEnvVisualizer)
            setLocation(10,10);
        else
            setLocation(panelWidth+20,10);





        pack();
        setVisible(true);

        //Make sure we exit if they close the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(programName);
    }


}