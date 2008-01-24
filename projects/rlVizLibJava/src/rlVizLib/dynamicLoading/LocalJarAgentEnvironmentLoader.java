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
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

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

    private Vector<String> theNames = null;
    private Vector<Class<?>> theClasses = null;
    private Vector<ParameterHolder> theParamHolders = null;

    private Map<String, String> publicNameToFullName = new TreeMap<String, String>();
    private Set<String> allFullClassName = new TreeSet<String>();
    private Vector<URI> theUriList = new Vector<URI>(); 

    private ClassExtractor theClassExtractor;

    //This seems like we're breaking OO rules
    private EnvOrAgentType theLoaderType;

    public String getClassName(String theName) {
        StringTokenizer theTokenizer = new StringTokenizer(theName, ".");
        String name = "undefined";
        while(theTokenizer.hasMoreTokens()){
            name = theTokenizer.nextToken();
        }
        return name;
    }


    public LocalJarAgentEnvironmentLoader(Vector<URI> uriList, EnvOrAgentType theLoaderType) {
        theUriList.addAll(uriList);
        this.theLoaderType = theLoaderType;

        CompositeResourceGrabber theCompJarGrabber = new CompositeResourceGrabber();
        
        FileFilter theJarFileFilter=new JarFileFilter();
        for (URI uri : uriList) {
            LocalDirectoryGrabber thisGrabber=new LocalDirectoryGrabber(uri);
            thisGrabber.addFilter(theJarFileFilter);
            
            theCompJarGrabber.add(thisGrabber);
        }

        theClassExtractor = new ClassExtractor(theCompJarGrabber);
    }


    public boolean makeList() {
        theNames = new Vector<String>();
        theClasses = new Vector<Class<?>>();
        theParamHolders = new Vector<ParameterHolder>();

        Vector <Class<?>> allMatching = new Vector<Class<?>>();
        
        File JarDir = new File(theUriList.elementAt(0).toString());
        if (theLoaderType.id() == EnvOrAgentType.kBoth.id()) {
            //System.out.println("-------Loading both types");
            allMatching = theClassExtractor.getAllClassesThatImplement(Environment.class, Unloadable.class);
            allMatching.addAll(theClassExtractor.getAllClassesThatImplement(Agent.class, Unloadable.class));
        }
        if (theLoaderType.id() == EnvOrAgentType.kEnv.id()) {
            //System.out.println("-------Loading kEnv types");
            allMatching = theClassExtractor.getAllClassesThatImplement(Environment.class, Unloadable.class);
        }
        if (theLoaderType.id() == EnvOrAgentType.kAgent.id()) {
            //System.out.println("-------Loading kAgent types");
            allMatching = theClassExtractor.getAllClassesThatImplement(Agent.class, Unloadable.class);
        }
 
        for (Class<?> thisClass : allMatching) {
            if ((!allFullClassName.contains(thisClass.getName())) && !isAbstractClass(thisClass)) {
                allFullClassName.add(thisClass.getName());
                String shortName = addFullNameToMap(thisClass.getName());
                theClasses.add(thisClass);
                theNames.add(shortName);
                
                ParameterHolder thisP = loadParameterHolderFromFile(thisClass);
                //FIX THIS
                String sourceJarPath=thisClass.getProtectionDomain().getCodeSource().getLocation().getPath();
                UtilityShop.addSourceDetails(thisP, thisClass.getName(), sourceJarPath);
                theParamHolders.add(thisP);
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

    private boolean isAbstractClass(Class<?> theClass) {
        int theModifiers = theClass.getModifiers();
        return Modifier.isAbstract(theModifiers);
    //return false;
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
}


