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
package rlVizLib.visualization;

import java.awt.Dimension;

/**
 * This used to be the workhorse of redrawing images, now that functionality 
 * has moved up to RenderObject.  This class now gets to focus on sleeping
 * and polling :)
 * @author btanner
 */
public class SelfUpdatingRenderObject extends RenderObject implements VizComponentChangeListener {
    public SelfUpdatingRenderObject(Dimension currentVisualizerPanelSize, SelfUpdatingVizComponent theComponent, ImageAggregator theBoss) {
        super(currentVisualizerPanelSize, theBoss);
        theComponent.setVizComponentChangeListener(this);
    }

    public void kill() {}

    public void vizComponentChanged(BasicVizComponent theComponent) {
                       redrawImages(theComponent);

    }
    
}
