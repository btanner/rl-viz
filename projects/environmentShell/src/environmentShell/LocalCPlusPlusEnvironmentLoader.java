/* EnvironmentShell, a dynamic loader for C++ and Java RL-Glue environments
 * Copyright (C) 2007, Matthew Radkie radkie@gmail.com
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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import rlVizLib.general.ParameterHolder;
import rlglue.environment.Environment;

/**
 *	Java class to talk to a C++ counterpart, 
 *
 *	environmentShell -> LocalCPlusPlusEnvironmentLoader (java) -> CPlusPlusEnvironmentLoader (C++)
 */
public class LocalCPlusPlusEnvironmentLoader implements EnvironmentLoaderInterface {

    // C++ functions to be called from within Java
    //public native void JNIloadEnvironment();
    public native void JNImakeEnvList(String path);

    public native String JNIgetEnvName(int index);

    public native String JNIgetEnvParams(int index);

    public native int JNIgetEnvCount();
    private String libDir;
    private Vector<URI> envUriList = new Vector<URI>();

    /**
     * CPPENV.dylib is assumed to be in the same directory as the envShell jar.
     */
    public LocalCPlusPlusEnvironmentLoader() {
        libDir = getPathFromString(this.getClass().getProtectionDomain().getCodeSource().getLocation().toString());
        EnvironmentShellPreferences.getInstance().setLibDir(libDir);
        this.refreshEnvUrlList();
        loadLoader();
    }

    /**
     * path is the path to the CPPENV.dylib
     * 
     * @param path
     */
    public LocalCPlusPlusEnvironmentLoader(String path) {
        libDir = path;
        EnvironmentShellPreferences.getInstance().setLibDir(libDir);
        loadLoader();
    }

    /**
     * clear the envUriList and populate it with the list from the preferences
     */
    private void refreshEnvUrlList() {
        envUriList.clear();
        envUriList.addAll(EnvironmentShellPreferences.getInstance().getList());
    }

    /**
     * .getClass().getProtectionDomain().getCodeSource().getLocation().toString()
     * 
     * returns a 
     * @param input
     * @return
     */
    private String getPathFromString(String input) {
        String temp;
        String thePath = new String();
        thePath = "/";
        if (input.endsWith(".jar")) {
            StringTokenizer theTokenizer = new StringTokenizer(input, "/");
            while (theTokenizer.hasMoreTokens()) {
                temp = theTokenizer.nextToken();
                if (!temp.endsWith(".jar") && !temp.endsWith(":")) {
                    thePath += temp + "/";
                }
            }
            return thePath;
        } else {
            return input;
        }
    }

    /**
     * The CPPENV.dylib is the library that allows the c++ environments to
     * be used in java
     */
    private void loadLoader() {
        System.load(libDir + "CPPENV.dylib");
    }

    /**
     * refresh the EnvUrlList to ensure its up to date, then for each URI in
     * the list, find all the dylibs in that directory.
     * 
     * @return true if there were no errors
     */
    public boolean makeList() {
        this.refreshEnvUrlList();
        for (URI thisDir : envUriList) {
            JNImakeEnvList(thisDir.toString());
        }
        return true;
    }

    /**
     * This method gets a count of the number of environments, then for each
     * of the environments it gets its name through a JNI call.
     * 
     * @return a Vector of env names
     */
    public Vector<String> getNames() {
        Vector<String> theEnvNames = new Vector<String>();
        int numEnvs = JNIgetEnvCount();//JNI call that returns the number of envs that were found with makeEnvList()

        for (int i = 0; i < numEnvs; i++) {
            String thisName = JNIgetEnvName(i);
            theEnvNames.add(thisName);
        }
        return theEnvNames;
    }

    /**
     * 
     * @return
     */
    public Vector<ParameterHolder> getParameters() {
        Vector<ParameterHolder> theEnvParams = new Vector<ParameterHolder>();
        int numEnvs = JNIgetEnvCount();

        for (int i = 0; i < numEnvs; i++) {
            String ParamHolderString = JNIgetEnvParams(i);//JNI call like getParamHolderInStringFormat(i)
            ParameterHolder thisParamHolder = new ParameterHolder(ParamHolderString);
            theEnvParams.add(thisParamHolder);
        }
        theEnvParams.add(null);
        return theEnvParams;
    }

    /**
     * 
     * @param envName
     * @param theParams
     * @return
     */
    public Environment loadEnvironment(String envName, ParameterHolder theParams) {
        String thename = libDir + envName;
        JNIEnvironment theEnv = new JNIEnvironment(thename, theParams);
        if (theEnv.isValid()) {
            return theEnv;
        } else {
            return null;
        }
    }

    public String getTypeSuffix() {
        return " - C++";
    }
}
