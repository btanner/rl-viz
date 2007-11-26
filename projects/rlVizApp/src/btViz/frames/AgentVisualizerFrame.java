/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package btViz.frames;

import btViz.RLGlueLogic;
import btViz.VisualizerPanel;
import java.awt.Dimension;

/**
 *
 * @author btanner
 */
public class AgentVisualizerFrame extends VisualizerVizFrame {

    public AgentVisualizerFrame(Dimension theSize){
        super("Agent Visualizer",theSize);
    }

    @Override
    protected void register() {
       RLGlueLogic.getGlobalGlueLogic().setAgentVisualizerControlTarget(theDynamicControlTargetPanel); 
       RLGlueLogic.getGlobalGlueLogic().addAgentVisualizerLoadListener(theDynamicControlTargetPanel);
       RLGlueLogic.getGlobalGlueLogic().addAgentVisualizerLoadListener(super.theVizPanel);
 }

    @Override
    protected String getWindowName() {
        return "Agent Visualizer";
    }

}
