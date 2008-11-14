/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rlcommunity.rlviz.app.frames;

import org.rlcommunity.rlviz.app.VisualizerPanel;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.DimensionUIResource;

/**
 *
 * @author btanner
 */
public abstract class VisualizerVizFrame extends GenericVizFrame{
    protected VisualizerPanel theVizPanel=null;
    protected VizFrameControlTarget theDynamicControlTargetPanel=null;
    protected JPanel theCompositePanel=null;
    
    protected abstract void register();
    protected abstract String getWindowName();


    public VisualizerVizFrame(String theName,Dimension theSize){
        super(theName);
        setPreferredSize(theSize);
        
        theCompositePanel=new JPanel();
        theCompositePanel.setLayout(new BoxLayout(theCompositePanel,BoxLayout.X_AXIS));

        int eachHeight=theSize.height;
        Dimension halfSize=new Dimension(theSize.width/2, eachHeight);
        Dimension thirdSize=new Dimension(theSize.width/3, eachHeight);
        Dimension twoThirdSize=new Dimension(2*theSize.width/3, eachHeight);
        theCompositePanel.setPreferredSize(theSize);

 
        theDynamicControlTargetPanel=new VizFrameControlTarget(thirdSize);
        
        theVizPanel=new VisualizerPanel(twoThirdSize);
        theVizPanel.setPreferredSize(twoThirdSize);
        //Setup the border for the Visualizer
        //NOTE: WE DO THIS AGAIN in VisualizerPanel.notifyOfVisualizerLoaded()
            TitledBorder titled = null;
            Border loweredetched = null;
            loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
            titled = BorderFactory.createTitledBorder(loweredetched,getWindowName());
            theVizPanel.setBorder(titled);
            
            titled=BorderFactory.createTitledBorder(loweredetched, "Controls");
            theDynamicControlTargetPanel.setBorder(titled);

        
        theCompositePanel.add(theDynamicControlTargetPanel);
        theCompositePanel.add(theVizPanel);
        
        //Register to be told about env/agent loads ad unloads
        register();
       getContentPane().add(theCompositePanel);
       pack();
       setVisible(true);
    }
    
   
    public void setVisible(boolean b){
        if(b)theVizPanel.startVisualizing();
        if(!b)theVizPanel.stopVisualizing();

        super.setVisible(b);

}

}
