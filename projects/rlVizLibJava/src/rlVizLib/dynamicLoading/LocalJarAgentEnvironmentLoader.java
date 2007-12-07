package rlVizLib.dynamicLoading;

/* Dynamic loader   Java RL-Glue agent ands environments
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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.Vector;

import rlVizLib.general.ParameterHolder;

/**
 * @author btanner
 *
 */
/**
 * @author btanner
 *
 */
public class LocalJarAgentEnvironmentLoader implements DynamicLoaderInterface {
    private boolean debugClassLoading=false;

    private Vector<String> theNames = null;
    private Vector<Class<?>> theClasses = null;
    private Vector<ParameterHolder> theParamHolders = null;
    private String jarDirString;

/**

@param subDir The directory for these specific Jars, like envJars or agentJars
*/
    public LocalJarAgentEnvironmentLoader(String subDir) {
        this(rlVizLib.utilities.UtilityShop.getLibraryPath(),subDir);
    }

/**

@param path Path to the main library dir of RLViz, like /.../library
@param subDir 
*/    public LocalJarAgentEnvironmentLoader(String path, String subDir) {
        jarDirString = path + "/"+subDir;
    }

    public boolean makeList() {
        theNames = new Vector<String>();
        theClasses = new Vector<Class<?>>();
        theParamHolders = new Vector<ParameterHolder>();

        File JarDir = new File(jarDirString);
        File[] theFileList = JarDir.listFiles();

        if (theFileList == null) {
            System.err.println("Unable to find useable jars, quitting");
            System.err.println("Was looking in: " + jarDirString);
            System.exit(1);
        }

        for (File thisFile : theFileList) {
            if (thisFile.getName().endsWith(".jar")) {
                String thisName = thisFile.getName().substring(0, thisFile.getName().length() - 4);
                String thisClassName=thisName+"."+thisName;
                //Load the class file first and make sure it works
                Class<?> theClass = rlVizLib.general.JarClassLoader.loadClassFromFileQuiet(thisFile, thisClassName,debugClassLoading);
                if (theClass != null) {
                    theClasses.add(theClass);
                    theNames.add(thisName);
                    theParamHolders.add(loadParameterHolderFromFile(theClass));
                }
            }
        }
        return true;
    }

    public Vector<String> getNames() {
        if (theClasses == null) {
            makeList();
        }

        return theNames;
    }

    public Vector<ParameterHolder> getParameters() {
        return theParamHolders;
    }

    public Object load(String requestedName, ParameterHolder theParams) {
        if (theClasses == null) {
            makeList();
        }
        String fullName=requestedName+"."+requestedName;
        //Get the file from the list
        for (Class<?> theClass : theClasses) {
            if (theClass.getName().equals(fullName)) {
                //this is the right one load it
                return loadFromClass(theClass, theParams);
            }
        }
        return null;
    }

    private ParameterHolder loadParameterHolderFromFile(Class<?> theClass) {
        ParameterHolder theParamHolder = null;

        Class<?>[] emptyParams = new Class<?>[0];

        try {
            Method paramMakerMethod = theClass.getDeclaredMethod("getDefaultParameters", emptyParams);
            if (paramMakerMethod != null) {
                theParamHolder = (ParameterHolder) paramMakerMethod.invoke((Object[]) null, (Object[]) null);
            }
        } catch (Exception e) {
            return null;
        }

        return theParamHolder;
    }


    /**
     * Creates an object of type theClass using theParams if possible.  Also checks to see if the version of RLVizLib
     * that was used to compile theClass is the same as the current runtime version.  For now, doesn't do anything except 
     * print a warning if they don't match.
     * @param theClass Class to instantiate
     * @param theParams ParameterHolder to initialize the new object with
     * @return newly instantiated class of type theClass
     */
    private Object loadFromClass(Class<?> theClass, ParameterHolder theParams) {
    	//before we do this, lets check compatibility
    	boolean goodVersion=checkVersions(theClass);
  

    	
        Object theModule = null;

//Try to load a constructor that takes a parameterholder
        try {
            Constructor<?> paramBasedConstructor = theClass.getConstructor(ParameterHolder.class);
            theModule = (Object)paramBasedConstructor.newInstance(theParams);
        } catch (Exception paramBasedE) {
            //There is no ParameterHolder constructor
            if (theParams != null) {
                if (!theParams.isNull()) {
                    System.err.println("Loading Class: "+theClass.getName()+" :: A parameter holder was provided, but the JAR doesn't have a constructor that takes a parameter holder");
                }
            }
            try {
                Constructor<?> emptyConstructor = theClass.getConstructor();
                theModule =  (Object)emptyConstructor.newInstance();
            } catch (Exception noParamsE) {
                System.err.println("Could't load instance of: " + theClass.getName() + " with parameters or without");
                System.err.println(noParamsE);
            }


        }
        return theModule;

    }


    private boolean checkVersions(Class<?> theClass) {
  		String rlVizVersion=rlVizLib.rlVizCore.getVersion();
		String thisClassVersion=rlVizLib.rlVizCore.getRLVizLinkVersionOfClass(theClass);
		
		if(!rlVizVersion.equals(thisClassVersion)){
			System.err.println("Warning :: Possible RLVizLib Incompatibility");
			System.err.println("Warning :: Runtime version used by the Loader is:  "+rlVizVersion);
			System.err.println("Warning :: Compile version used to build "+theClass+" is:  "+thisClassVersion);
			return false;
		}
		return true;
}

	public String getTypeSuffix() {
        return "- Java";
    }


}
