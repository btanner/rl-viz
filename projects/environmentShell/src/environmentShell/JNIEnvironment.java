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

package environmentShell;

import rlVizLib.dynamicLoading.Unloadable;
import rlVizLib.general.ParameterHolder;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Random_seed_key;
import org.rlcommunity.rlglue.codec.types.Reward_observation;
import org.rlcommunity.rlglue.codec.types.State_key;

//
//  JNIEnvironment.java
//  
//
//  Created by mradkie on 9/11/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

public class JNIEnvironment implements EnvironmentInterface, Unloadable {

    public native boolean JNIloadEnvironment(String theFilePath, String theParams);
    public native String JNIenvinit();
    public native void JNIenvstart();
    public native void JNIenvstep(int numI, int numD, int[] intArray, double[] doubleArray);
    public native void JNIenvcleanup();
    public native void JNIenvsetrandomseed(int numI, double numD, int[] intArray, double[] doubleArray);
    public native void JNIgetrandomseed();
    public native void JNIenvsetstate(int numI, double numD, int[] intArray, double[] doubleArray);
    public native void JNIgetstate();   
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
        int numI = JNIgetInt();
        int numD = JNIgetDouble();
        
        Random_seed_key rsk = new Random_seed_key(numI, numD);
        int[] theInts = JNIgetIntArray();
        double[] theDoubles = JNIgetDoubleArray();

        System.arraycopy(theInts, 0, rsk.intArray, 0, numI);
        System.arraycopy(theDoubles, 0, rsk.doubleArray, 0, numD);
        return rsk;
    }

    public State_key env_get_state() {
        int numI = JNIgetInt();
        int numD = JNIgetDouble();
        
        State_key sk = new State_key(numI, numD);
        int[] theInts = JNIgetIntArray();
        double[] theDoubles = JNIgetDoubleArray();

        System.arraycopy(theInts, 0, sk.intArray, 0, numI);
        System.arraycopy(theDoubles, 0, sk.doubleArray, 0, numD);
        return sk;
    }

    public String env_message(String message) {
        return JNIenvmessage(message);
    }

    public void env_set_state(State_key sk) {
        JNIenvsetstate(sk.intArray.length, sk.doubleArray.length, sk.intArray, sk.doubleArray);
    }
    
    public boolean isValid(){
        return validEnv;
    }
}
