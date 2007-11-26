/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package btViz.frames;

import btViz.RLGlueLogic;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

/**
 *
 * @author btanner
 */
public class EnvVisualizerFrame extends VisualizerVizFrame {

    public EnvVisualizerFrame(Dimension theSize){
        super("Environment Visualizer",theSize);
    }

    @Override
    protected void register() {
       RLGlueLogic.getGlobalGlueLogic().setEnvironmentVisualizerControlTarget(super.theDynamicControlTargetPanel); 
       RLGlueLogic.getGlobalGlueLogic().addEnvVisualizerLoadListener(super.theDynamicControlTargetPanel);
       RLGlueLogic.getGlobalGlueLogic().addEnvVisualizerLoadListener(super.theVizPanel);

    }

    @Override
    protected String getWindowName() {
        return "Environment Visualizer";
    }

  
}
