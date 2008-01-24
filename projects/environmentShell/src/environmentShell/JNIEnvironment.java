/* EnvironmentShell, a dynamic loader for C++ and Java RL-Glue environments
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
package environmentShell;

import rlVizLib.dynamicLoading.Unloadable;
import rlVizLib.general.ParameterHolder;
import rlglue.environment.Environment;
import rlglue.types.Action;
import rlglue.types.Observation;
import rlglue.types.Random_seed_key;
import rlglue.types.Reward_observation;
import rlglue.types.State_key;

//
//  JNIEnvironment.java
//  
//
//  Created by mradkie on 9/11/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class JNIEnvironment implements Environment, Unloadable {

    public native boolean JNIloadEnvironment(String theFilePath, String theParams);
    public native String JNIenvinit();
    public native void JNIenvstart();
    public native void JNIenvstep(int numI, int numD, int[] intArray, double[] doubleArray);
    public native void JNIenvcleanup();
    public native void JNIenvsetrandomseed(int numI, double numD, int[] intArray, double[] doubleArray);
    public native void JNIgetrandomseed();
    public native String JNIenvmessage(String s);
    public native int JNIgetInt();
    public native int[] JNIgetIntArray();
    public native int JNIgetDouble();
    public native double[] JNIgetDoubleArray();
    public native double JNIgetReward();
    public native int JNIgetTerminal();
    public  boolean validEnv = false;

    private void load_environment(String theFullFilePath, ParameterHolder theParams) {
        System.out.println("Loading: " + theFullFilePath + " with Param holder: " + theParams.stringSerialize());
        validEnv = JNIloadEnvironment(theFullFilePath, theParams.stringSerialize());
    }

    public JNIEnvironment(String theFullFilePath, ParameterHolder theParams) {
        load_environment(theFullFilePath, theParams);
    }

    public String env_init() {
        return JNIenvinit();
    }

    public Observation env_start() {
        JNIenvstart();
        int numI = JNIgetInt();
        int numD = JNIgetDouble();
        Observation o = new Observation(numI, numD);
        int[] theInts = JNIgetIntArray();
        double[] theDoubles = JNIgetDoubleArray();
        System.arraycopy(theInts, 0, o.intArray, 0, numI);
        System.arraycopy(theDoubles, 0, o.doubleArray, 0, numD);
        return o;
    }

    public Reward_observation env_step(Action a) {
        JNIenvstep(a.intArray.length, a.doubleArray.length, a.intArray, a.doubleArray);

        int numI = JNIgetInt();
        int numD = JNIgetDouble();

        Observation o = new Observation(numI, numD);
        int[] theInts = JNIgetIntArray();
        double[] theDoubles = JNIgetDoubleArray();

        System.arraycopy(theInts, 0, o.intArray, 0, numI);
        System.arraycopy(theDoubles, 0, o.doubleArray, 0, numD);

        double rew = JNIgetReward();

        int term = JNIgetTerminal();

        Reward_observation ret = new Reward_observation(rew, o, term);

        return ret;
    }

    public void env_cleanup() {
        JNIenvcleanup();
    }

    public void env_set_random_seed(Random_seed_key sk) {
        JNIenvsetrandomseed(sk.intArray.length, sk.doubleArray.length, sk.intArray, sk.doubleArray);
    }

    public Random_seed_key env_get_random_seed() {
        // TODO Auto-generated method stub
        return null;
    }

    public State_key env_get_state() {
        // TODO Auto-generated method stub
        return null;
    }

    public String env_message(String message) {
        return JNIenvmessage(message);
    }

    public void env_set_state(State_key key) {
        //return JNIenvsetstate();
    }
    
    public boolean isValid(){
        return validEnv;
    }
}
