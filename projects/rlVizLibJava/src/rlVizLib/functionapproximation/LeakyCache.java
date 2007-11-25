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
package rlVizLib.functionapproximation;

import java.util.Vector;

public class LeakyCache {
	Vector<DataPoint> theData=new Vector<DataPoint>();
	int maxSize;
	int currentIndex;
	int actualSize;
	
	public LeakyCache(int maxSize){
		this.maxSize=maxSize;
		theData.setSize(maxSize);
		currentIndex=0;
		actualSize=0;
	}
	
	public void add(DataPoint d){
		if(currentIndex<theData.size())
			theData.set(currentIndex,d);
		else{
			currentIndex=0;
			theData.set(currentIndex,d);
		}
		currentIndex++;
		currentIndex%=theData.size();
		actualSize++;
		if(actualSize>maxSize)
			actualSize=maxSize;
	}
	
	public DataPoint sample(){
		int randIndex=(int)(Math.random()*actualSize);
		return theData.get(randIndex);
	}

}


