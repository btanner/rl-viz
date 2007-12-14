/* RL-VizLib, a library for C++ and Java for adding advanced visualization and dynamic capabilities to RL-Glue.
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
