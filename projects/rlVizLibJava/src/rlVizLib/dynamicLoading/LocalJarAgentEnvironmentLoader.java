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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import rlVizLib.general.ParameterHolder;
import rlVizLib.utilities.UtilityShop;
import rlglue.agent.Agent;
import rlglue.environment.Environment;

/**
 * @author btanner
 *
 */
/**
 * @author btanner
 *
 */
public class LocalJarAgentEnvironmentLoader implements DynamicLoaderInterface {

    private boolean debugClassLoading = true;
    private Vector<String> theNames = null;
    private Vector<Class<?>> theClasses = null;
    private Vector<ParameterHolder> theParamHolders = null;
    private String jarDirString;
    private Map<String, String> publicNameToFullName = new TreeMap<String, String>();
    private Set<String> allFullClassName = new TreeSet<String>();

    //This seems like we're breaking OO rules
    private EnvOrAgentType theLoaderType;

    public void getAllInterfaces(Set<String> theSet, Class sourceClass) {
        if (sourceClass == null) {
            return;
        }


        Class[] theInterfaces = sourceClass.getInterfaces();
        for (Class thisClass : theInterfaces) {
            theSet.add(thisClass.getName());
            getAllInterfaces(theSet, thisClass.getSuperclass());
        }
        getAllInterfaces(theSet, sourceClass.getSuperclass());
    }

    public String getClassName(String theName) {
        StringTokenizer theTokenizer = new StringTokenizer(theName, ".");

        return theTokenizer.nextToken();
    //return theName;
    }

    public LocalJarAgentEnvironmentLoader(String subDir) {
        this(rlVizLib.utilities.UtilityShop.getLibraryPath(), subDir, EnvOrAgentType.kBoth);
    }

    /**
    @param subDir The directory for these specific Jars, like envJars or agentJars
     */
    public LocalJarAgentEnvironmentLoader(String subDir, EnvOrAgentType theLoaderType) {
        this(rlVizLib.utilities.UtilityShop.getLibraryPath(), subDir, theLoaderType);
    }

    /**
    @param path Path to the main library dir of RLViz, like /.../library
    @param subDir 
     */
    public LocalJarAgentEnvironmentLoader(String path, String subDir, EnvOrAgentType theLoaderType) {
        jarDirString = path + "/" + subDir;
        this.theLoaderType = theLoaderType;
    }
    /**
    @param path Path to the main library dir of RLViz, like /.../library
    @param subDir 
     */
    public LocalJarAgentEnvironmentLoader(String path, String subDir) {
        this(path,subDir,EnvOrAgentType.kBoth);
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
                try {
                    //So thisFile is a Jar File
                    JarFile theJarFile = new JarFile(thisFile);

                    Enumeration<JarEntry> allJarEntries = theJarFile.entries();

                    while (allJarEntries.hasMoreElements()) {
                        JarEntry thisEntry = allJarEntries.nextElement();


                        if (shouldLoadClass(thisEntry, thisFile)) {
                            Class<?> theClass = getClassFromJarEntry(thisEntry, thisFile);
                            String theFullClassName = theClass.getName();

                            if ((!allFullClassName.contains(theFullClassName)) && !isAbstractClass(theClass)) {
                                allFullClassName.add(theFullClassName);
                                String shortName = addFullNameToMap(theFullClassName);
                                theClasses.add(theClass);
                                theNames.add(shortName);

                                ParameterHolder thisP = loadParameterHolderFromFile(theClass);
                                UtilityShop.addSourceDetails(thisP, theFullClassName, thisFile.getName());
                                theParamHolders.add(thisP);
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LocalJarAgentEnvironmentLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return true;
    }

    private String getFullClassNameFromShortName(String shortName) {
        return publicNameToFullName.get(shortName);
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

    public Object load(String shortName, ParameterHolder theParams) {
        if (theClasses == null) {
            makeList();
        }
        String fullName = getFullClassNameFromShortName(shortName);
        //Get the file from the list
        for (Class<?> theClass : theClasses) {
            if (theClass.getName().equals(fullName)) {
                //this is the right one load it
                return loadFromClass(theClass, theParams);
            }
        }
        return null;
    }

    private String addFullNameToMap(String theFullClassName) {
        int num = 0;
        String theEndName = getClassName(theFullClassName);

        String proposedShortName = theEndName;

        while (publicNameToFullName.containsKey(proposedShortName)) {
            num++;
            proposedShortName = theEndName + "(" + num + ")";
        }
        publicNameToFullName.put(proposedShortName, theFullClassName);
        return proposedShortName;

    }
    
    private boolean isAbstractClass(Class<?> theClass){
        int theModifiers = theClass.getModifiers();
        return (theModifiers == java.lang.reflect.Modifier.ABSTRACT);
        //return false;
    }

    private Class<?> getClassFromJarEntry(JarEntry thisEntry, File thisFile) {
        if (thisEntry.getName().endsWith(".class")) {
            //Cut off the .class
            String thisClassName = thisEntry.getName().substring(0, thisEntry.getName().length() - 6);
            thisClassName = thisClassName.replace(File.separator, ".");

            //Load the class file first and make sure it works
            Class<?> theClass = rlVizLib.general.JarClassLoader.loadClassFromFileQuiet(thisFile, thisClassName, debugClassLoading);
            return theClass;
        }
        return null;
    }

    private Set<String> getInterfaceNames(JarEntry thisEntry, File thisFile) {
        Set<String> interfaceSet = new TreeSet<String>();
        Class<?> theClass = getClassFromJarEntry(thisEntry, thisFile);

        getAllInterfaces(interfaceSet, theClass);

        return interfaceSet;
    }

    private boolean checkIfAgent(JarEntry thisEntry, File thisFile) {
        Set<String> interfaceSet = getInterfaceNames(thisEntry, thisFile);
        return interfaceSet.contains(Agent.class.getName());
    }

    private boolean checkIfEnv(JarEntry thisEntry, File thisFile) {
        Set<String> interfaceSet = getInterfaceNames(thisEntry, thisFile);
        return interfaceSet.contains(Environment.class.getName());
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
        boolean goodVersion = checkVersions(theClass);



        Object theModule = null;

//Try to load a constructor that takes a parameterholder
        try {
            Constructor<?> paramBasedConstructor = theClass.getConstructor(ParameterHolder.class);
            theModule = (Object) paramBasedConstructor.newInstance(theParams);
        } catch (Exception paramBasedE) {
            //There is no ParameterHolder constructor
            if (theParams != null) {
                if (!theParams.isNull()) {
                    System.err.println("Loading Class: " + theClass.getName() + " :: A parameter holder was provided, but the JAR doesn't have a constructor that takes a parameter holder");
                }
            }
            try {
                Constructor<?> emptyConstructor = theClass.getConstructor();
                theModule = (Object) emptyConstructor.newInstance();
            } catch (Exception noParamsE) {
                System.err.println("Could't load instance of: " + theClass.getName() + " with parameters or without");
                System.err.println(noParamsE);
            }


        }
        return theModule;

    }

    private boolean checkVersions(Class<?> theClass) {
        String rlVizVersion = rlVizLib.rlVizCore.getVersion();
        String thisClassVersion = rlVizLib.rlVizCore.getRLVizLinkVersionOfClass(theClass);

        if (!rlVizVersion.equals(thisClassVersion)) {
            System.err.println("Warning :: Possible RLVizLib Incompatibility");
            System.err.println("Warning :: Runtime version used by the Loader is:  " + rlVizVersion);
            System.err.println("Warning :: Compile version used to build " + theClass + " is:  " + thisClassVersion);
            return false;
        }
        return true;
    }

    public String getTypeSuffix() {
        return "- Java";
    }

    private boolean shouldLoadClass(JarEntry thisEntry, File thisFile) {
        boolean isEnv = checkIfEnv(thisEntry, thisFile);
        boolean isAgent = checkIfAgent(thisEntry, thisFile);

        if (theLoaderType.id() == EnvOrAgentType.kBoth.id() && (isEnv || isAgent)) {
            return true;
        }
        if (theLoaderType.id() == EnvOrAgentType.kEnv.id() && isEnv) {
            return true;
        }
        if (theLoaderType.id() == EnvOrAgentType.kAgent.id() && isAgent) {
            return true;
        }
        return false;
    }
}


