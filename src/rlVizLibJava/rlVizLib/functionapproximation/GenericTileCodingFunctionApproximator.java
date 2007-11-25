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


public class GenericTileCodingFunctionApproximator extends FunctionApproximator {
	int numVars;
	int numActions;
	int numTilings;
	double alpha=.25;
	double epsilon=.05;
	TileCoder theTileCoder=new TileCoder();

	//Just a default
	int memorySize=(int)Math.pow(2, 18);

	double weights[];							// modifyable parameter vector, aka memory, weights

	int lastAction;
	Observation lastObservation;

	LeakyCache theData=new LeakyCache(100000);
	ObservationScalerInterface theObsScaler=null;
	

	public GenericTileCodingFunctionApproximator(int numVars, int numActions, int numTilings, double alpha, double epsilon, int memorySize, ObservationScalerInterface observationConverer){
		this.numVars=numVars;
		this.numActions=numActions;
		this.numTilings=numTilings;
		this.alpha=alpha;
		this.epsilon=epsilon;
		this.memorySize=memorySize;
		this.theObsScaler=observationConverer;

		weights = new double[memorySize];
	}




	@Override
	public void init() {
		for (int i=0; i<memorySize; i++)weights[i]= 0.0;
	}

	//This should not be in here, it should be a plug in or something
	@Override
	public void plan() {
		DataPoint theSample=theData.sample();
		sarsaUpdate(theSample.s, theSample.action, theSample.reward, theSample.sprime);
	}

	@Override
	public void start(Observation theObservation, int theAction) {
		theObsScaler.notifyOfValues(theObservation);
		lastObservation=theObservation;
		lastAction=theAction;
	}

	//I like this, it feels very clean
	@Override
	public void step(Observation theObservation, double r, int theAction) {
		theObsScaler.notifyOfValues(theObservation);

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
		for (int j=0; j<numTilings; j++)  totalValue += weights[F[j]];
		return totalValue;
	}

	@Override
	//Used for 1-step simple updates
	public void update(Observation theObservation, int theAction, double delta) {
		int F[]=fillF(theObservation,theAction);
		for (int j=0; j<numTilings; j++)  weights[F[j]]+=(alpha/(float)numTilings)*delta;
	}

//	Used for planning updates
	private void sarsaUpdate(Observation s, int action, double r, Observation sprime){
		double lastValue=query(s, action);

		int theAction=chooseEpsilonGreedy(sprime);

		double thisValue=query(sprime,theAction);

		double target=r+thisValue;
		double delta=target-lastValue;

		update(s,action,delta);
	}

//	Used for planning updates
	private void expectedSarsaUpdate(Observation s, int action, double r, Observation sprime){
		double lastValue=query(s, action);

		double bestValue=query(sprime,0);
		double valueSum=bestValue;
		for( int a=1;a<numActions;a++){
			double value=query(sprime,a);
			valueSum+=value;
			if(value>bestValue)
				bestValue=value;
		}

		double thisValue=(1.0d-epsilon) * bestValue + epsilon * (valueSum/(double)numActions);
		
		double target=r+thisValue;
		double delta=target-lastValue;

		update(s,action,delta);
	}

	private void qUpdate(Observation s, int action, double r, Observation sprime){
		double lastValue=query(s, action);

		double bestValue=query(sprime,0);
		for( int a=1;a<numActions;a++){
			double value=query(sprime,a);
			if(value>bestValue)
				bestValue=value;
		}


		double target=r+bestValue;
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
			double_vars[i] = theObsScaler.scale(i, theObservation.doubleArray[i]);
		}
		//int_vars[0] will be the action

		for(int i=0;i<intCount;i++){
			int_vars[i+1] = theObservation.intArray[i];
		}

		//This load all of them
		int_vars[0]=theAction;
		theTileCoder.tiles(F,0,numTilings,memorySize,double_vars,doubleCount,int_vars, intCount+1);
		return F;
	}
	
//	make this more standard, return an int, forget the value
	int chooseEpsilonGreedy(Observation theObservation) {
		int action=0;

		if(Math.random()<=epsilon){
			action=(int)(Math.random()*(double)numActions);
		}else{
			action=chooseGreedy(theObservation);
		}
		return action;
	}


	int chooseGreedy(Observation theObservation){
		int num_ties=1;
		int action=0;
		double bestValue=query(theObservation,action);

		for(int a=1;a<numActions;a++){
			double thisActionValue=query(theObservation,a);		

			if(thisActionValue>=bestValue){
				if(thisActionValue>bestValue){
					action=a;
					bestValue=thisActionValue;
				}else {
					num_ties++;
					//FIXME: Did weird things, should look into this and fix it
					if (0 == (int)(Math.random()*num_ties))
					{
						bestValue = thisActionValue;
						action = a;
					}
				}
			}
		}
		return action;
	}

}
