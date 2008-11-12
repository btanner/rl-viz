/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package btViz.frames;

import btViz.*;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

/**
 *
 * @author btanner
 */
public class VizFrameControlTarget extends JPanel implements DynamicControlTarget, visualizerLoadListener {

    Vector<Component> dynamicComponents = null;
    Component theFiller=null;
    Dimension defaultSize=null;
    public VizFrameControlTarget(Dimension theSize) {
        this.defaultSize=theSize;
        dynamicComponents = new Vector<Component>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        setPreferredSize(theSize);
        addFiller();

    }
    
    private void addFiller(){
        Dimension minSize = new Dimension(5, 5);
        Dimension prefSize = defaultSize;
        Dimension maxSize = new Dimension(defaultSize.width,Integer.MAX_VALUE);
        theFiller=new Box.Filler(minSize, prefSize, maxSize);
        add(theFiller);

    }
    private void removeFiller(){
        remove(theFiller);
    }
    
    /**
     * I threw this method in sortof quickly to let me take controls off a panel.
     * @param toRemoveComponents
     */
    
    public void removeControls(Vector<Component> toRemoveComponents){
        for (Component component : toRemoveComponents) {
            dynamicComponents.remove(component);
        }
        addControls(new Vector<Component>());
    }

    public void addControls(Vector<Component> newComponents) {
        removeFiller();
        
        removeControlsFromFrame();
        for (Component component : newComponents) {
            dynamicComponents.add(component);
        }
        addControlsToFrame();
        addFiller();
        getParent().validate();
        validate();
    }
    
    private void addControlsToFrame(){
        for (Component component : dynamicComponents) {
            add(component);
        }
    }
    
    private void removeControlsFromFrame(){
        for (Component component : dynamicComponents) {
            remove(component);
        }
        
    }

    public void clear() {
        //This is going to need to be refactored maybe to be environment and agent components, maybe not
        removeControlsFromFrame();
        dynamicComponents.removeAllElements();
        validate();
    }

    public void notifyVisualizerLoaded(AbstractVisualizer theNewVisualizer) {
    //nothing here
    }

    public void notifyVisualizerUnLoaded() {
        clear();
    }
}
