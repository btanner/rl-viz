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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;



public class ThreadRenderObject extends Thread {
	BufferedImage workImage=null;
	BufferedImage prodImage=null;

	VizComponent theComponent=null;

	volatile boolean shouldDie=false;
	
	AffineTransform theScaleTransform=null;
	int defaultSleepTime=100;
	ImageAggregator theBoss;
	
	Dimension mySize;
	
	public void receiveSizeChange(Dimension newPanelSize){
		mySize=newPanelSize;
		resizeImages();
	}

	private void resizeImages() {
		workImage=new BufferedImage((int)mySize.getWidth(),(int)mySize.getHeight(),BufferedImage.TYPE_INT_ARGB);
		//Set the transform on the image so we can draw everything in [0,1]
		theScaleTransform=new AffineTransform();
		theScaleTransform.scale(mySize.getWidth(),mySize.getHeight());

		prodImage=new BufferedImage((int)mySize.getWidth(),(int)mySize.getHeight(),BufferedImage.TYPE_INT_ARGB);
}

	
	
	public ThreadRenderObject(Dimension currentVisualizerPanelSize, VizComponent theComponent, ImageAggregator theBoss){
		this.theComponent=theComponent;
		this.mySize=currentVisualizerPanelSize;
		this.theBoss=theBoss;

		resizeImages();
	}
	

	@Override
	public void run() {
		
		while(!shouldDie){

			if(theComponent.update()){
				Graphics2D g=workImage.createGraphics();
				
				//Set the scaling transform
				AffineTransform currentTransform=g.getTransform();
				currentTransform.concatenate(theScaleTransform);
				g.setTransform(currentTransform);

				//Clear the screen to transparent
				Color myClearColor=new Color(0.0f,0.0f,0.0f,0.0f);
				g.setColor(myClearColor);
				g.setBackground(myClearColor);
				g.clearRect(0,0,1,1);

				theComponent.render(g);
				
				BufferedImage tmpImage=prodImage;
				prodImage=workImage;
				workImage=tmpImage;
				
				theBoss.receiveNotification();

				try {
					Thread.sleep(defaultSleepTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		}//end of the while loop
		//Now that we've died, can reset the shouldDie flag so that we can easily be restarted
		shouldDie=false;
	}

	public final BufferedImage getProductionImage() {
		return prodImage;
	}

	public void kill() {
		shouldDie=true;
	}
}
