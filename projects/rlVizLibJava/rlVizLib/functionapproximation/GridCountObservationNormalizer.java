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

import rlglue.types.Observation;

public class GridCountObservationNormalizer implements	ObservationScalerInterface {
	double doubleMins[] = null;
	double doubleMaxs[]=null;
	double observationDividers[]=null;
	boolean rangeDetermined = false;

	double numGrids=8.0f;

	public GridCountObservationNormalizer(int numVariables, double numGrids){
		doubleMins=new double[numVariables];
		doubleMaxs=new double[numVariables];
		observationDividers=new double[numVariables];
		this.numGrids=numGrids;

		for(int i=0;i<numVariables;i++){
			doubleMins[i]=Double.MAX_VALUE;
			doubleMaxs[i]=Double.MIN_VALUE;
			observationDividers[i]=Double.MAX_VALUE;
		}
	}
	public void notifyOfValues(Observation theObservations) {
		if(!rangeDetermined){
			//if we haven't seen any observations yet, we initialize our min and max arrays to hold the 
			//current values of the observation variables and then break
			this.rangeDetermined = true;
			for(int i=0; i< theObservations.doubleArray.length; i++){
				this.doubleMins[i] = theObservations.doubleArray[i];
				this.doubleMaxs[i] = theObservations.doubleArray[i];
			}
			return;
		}

		//updating our double Max and Mins
		for(int i =0; i< theObservations.doubleArray.length; i++){
			if(theObservations.doubleArray[i] < this.doubleMins[i]){
				this.doubleMins[i] = theObservations.doubleArray[i];
			}
			if(theObservations.doubleArray[i] > this.doubleMaxs[i]){
				this.doubleMaxs[i] = theObservations.doubleArray[i];
			}
		}

		for(int i=0; i< this.observationDividers.length; i++){
			this.observationDividers[i] = (this.doubleMaxs[i] - this.doubleMins[i])/numGrids;

			if(observationDividers[i]==0)
				System.out.println("Ahh, its 0!");
		}

	}

	public double scale(int whichDoubleVariable, double thisValue) {
		double returnValue= (thisValue-doubleMins[whichDoubleVariable])/observationDividers[whichDoubleVariable];
return returnValue;
	}
	public Observation scaleObservation(Observation originalObservation) {
		System.out.println("Scale Observation not implemented for GridCountOBservationNormalizer");
		return null;
	}

    public double unScale(int whichDoubleVariable, double scaledValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
