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


public class TileCodingFunctionApproximator extends FunctionApproximator {
	int numVars;
	int numActions;
	int numTilings;
	double alpha=.25;

	TileCoder theTileCoder=new TileCoder();

	int MEMORY_SIZE=(int)Math.pow(2, 18);

	double theta[];							// modifyable parameter vector, aka memory, weights
	double observationDividers[];

	int lastAction;
	Observation lastObservation;

	LeakyCache theData=new LeakyCache(10000);

	public TileCodingFunctionApproximator(int numVars, int numActions, int numTilings, double alpha){
		this.numVars=numVars;
		this.numActions=numActions;
		this.numTilings=numTilings;
		this.alpha=alpha;

		observationDividers=new double[numVars];
		for(int i=0;i<numVars;i++) observationDividers[i]=.1;

		theta = new double[MEMORY_SIZE];
	}




	@Override
	public void init() {
		for (int i=0; i<MEMORY_SIZE; i++)theta[i]= 0.0;
	}

	@Override
	public void plan() {
		DataPoint theSample=theData.sample();
		update(theSample.s, theSample.action, theSample.reward, theSample.sprime);
	}

	@Override
	public void start(Observation theObservation, int theAction) {
		lastObservation=theObservation;
		lastAction=theAction;
	}

	@Override
	public void step(Observation theObservation, double r, int theAction) {
		theData.add(new DataPoint(lastObservation, lastAction, r, theObservation));
		double lastValue=query(lastObservation, lastAction);
		double thisValue=query(theObservation,theAction);


		double target=r+thisValue;
		double delta=target-lastValue;


		update(lastObservation, lastAction, delta);
		lastObservation=theObservation;
		lastAction=theAction;

	}


	@Override
	public void end(double r) {
		double target=r;
		double lastValue=query(lastObservation, lastAction);

		double delta=target-lastValue;
		update(lastObservation, lastAction, delta);
	}

	public double query(Observation theObservation, int theAction){
		double totalValue=0.0f;
		int F[]=fillF(theObservation,theAction);
		for (int j=0; j<numTilings; j++)  totalValue += theta[F[j]];
		return totalValue;
	}

	@Override
	//Used for 1-step simple updates
	public void update(Observation theObservation, int theAction, double delta) {
		int F[]=fillF(theObservation,theAction);
		for (int j=0; j<numTilings; j++)  theta[F[j]]+=(alpha/(float)numTilings)*delta;
	}

//	Used for planning updates
	private void update(Observation s, int action, double r, Observation sprime){
		double lastValue=query(s, action);

		double thisValue=query(sprime,0);
		for( int a=1;a<numActions;a++){
			double value=query(sprime,a);
			if(value>thisValue)
				thisValue=value;
		}


		double target=r+thisValue;
		double delta=target-lastValue;

		update(s,action,delta);
	}


	int[] fillF(Observation theObservation, int theAction){
		int F[] =new int[numTilings];

		int doubleCount=theObservation.doubleArray.length;
		int intCount=theObservation.intArray.length;
		double	double_vars[]=new double[doubleCount];
		int		int_vars[] = new int[intCount+1];

		for(int i=0;i<doubleCount;i++){
			double_vars[i] = theObservation.doubleArray[i] / observationDividers[i];
		}

		//int_vars[0] will be the action

		for(int i=0;i<intCount;i++){
			int_vars[i+1] = theObservation.intArray[i];
		}

		//This load all of them
		int_vars[0]=theAction;
		theTileCoder.tiles(F,0,numTilings,MEMORY_SIZE,double_vars,doubleCount,int_vars, intCount+1);
		return F;
	}
	public void setWidth(int which, double width){
		observationDividers[which]=width;
	}



}
