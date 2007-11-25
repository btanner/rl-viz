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

public interface ObservationScalerInterface {

	
	//Given a variable number and its value, this function will return some sort of scaling to use
	//In our example, it does the dividing so the values work well for tile coding
	double scale(int whichDoubleVariable, double thisValue);
        double unScale(int whichDoubleVariable, double scaledValue);

	Observation scaleObservation(Observation originalObservation);
	//We call this every time we get new observations so that the scaler knows what values we've seen
	void notifyOfValues(Observation theObservation);

	
}
