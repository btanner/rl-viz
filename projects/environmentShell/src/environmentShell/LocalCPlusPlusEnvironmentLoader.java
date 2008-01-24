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
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlVizLib.utilities.UtilityShop;
import rlglue.environment.Environment;

/**
 *	Java class to talk to a C++ counterpart, 
 *
 *	environmentShell -> LocalCPlusPlusEnvironmentLoader (java) -> CPlusPlusEnvironmentLoader (C++)
 */
public class LocalCPlusPlusEnvironmentLoader implements EnvironmentLoaderInterface {

    // C++ functions to be called from within Java
    public native String JNIgetEnvParams(String fullFilePath);

    Vector<URI> allCPPEnvURIs = new Vector<URI>();
    private Vector<String> theNames = new Vector<String>();
    private Vector<ParameterHolder> theParamHolders = new Vector<ParameterHolder>();
    private Map<String, URI> publicNameToFullName = new TreeMap<String, URI>();
    private Set<URI> allFullURIName = new TreeSet<URI>();

    /**
     * CPPENV.dylib is assumed to be in the same directory as the envShell jar.
     */
    public LocalCPlusPlusEnvironmentLoader() {
        loadLoader();
    }


    /**
     * The CPPENV.dylib is the library that allows the c++ environments to
     * be used in java
     */
    private void loadLoader() {
        System.load(EnvironmentShellPreferences.getInstance().getJNILoaderLibDir() + File.separator+ "CPPENV.dylib");
    }

    private String getShortEnvNameFromURI(URI theURI) {
        String pathAsString = theURI.getPath();

        StringTokenizer toke = new StringTokenizer(pathAsString, File.separator);

        String fileName = "";
        while (toke.hasMoreTokens()) {
            fileName = toke.nextToken();
        }
        return fileName;
    }


    private String addFullNameToMap(URI theURI) {
        int num = 0;
        String theEndName = getShortEnvNameFromURI(theURI);

        String proposedShortName = theEndName;

        while (publicNameToFullName.containsKey(proposedShortName)) {
            num++;
            proposedShortName = theEndName + "(" + num + ")";
        }
        publicNameToFullName.put(proposedShortName, theURI);
        return proposedShortName;
    }

    /**
     * refresh the EnvUrlList to ensure its up to date, then for each URI in
     * the list, find all the dylibs in that directory.
     * 
     * @return true if there were no errors
     */
    public boolean makeList() {
        DylibGrabber theGrabber = new DylibGrabber();
        allCPPEnvURIs = theGrabber.getValidEnvDylibURIs();
        

        for (URI thisURI : allCPPEnvURIs) {
            allFullURIName.add(thisURI);
            String shortName = addFullNameToMap(thisURI);
            theNames.add(shortName);

            String ParamHolderString = JNIgetEnvParams(thisURI.getPath());//JNI call like getParamHolderInStringFormat(i)
            ParameterHolder thisParamHolder = new ParameterHolder(ParamHolderString);
            theParamHolders.add(thisParamHolder);

            String sourcePath = thisURI.getPath();
        }
        System.out.println("Added a total of "+theNames.size()+" cpp envs");
        return true;
    }

    /**
     * This method gets a count of the number of environments, then for each
     * of the environments it gets its name through a JNI call.
     * 
     * @return a Vector of env names
     */
    public Vector<String> getNames() {
        return theNames;
    }

    /**
     * 
     * @return
     */
    public Vector<ParameterHolder> getParameters() {
        return theParamHolders;
    }

    /**
     * 
     * @param envName
     * @param theParams
     * @return
     */
    public Environment loadEnvironment(String envName, ParameterHolder theParams) {
        URI theEnvURI=publicNameToFullName.get(envName);
        
        JNIEnvironment theEnv = new JNIEnvironment(theEnvURI.getPath(), theParams);
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
