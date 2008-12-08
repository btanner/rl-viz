package rlVizLib.dynamicLoading;

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


import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import rlVizLib.general.ParameterHolder;
import rlVizLib.general.RLVizVersion;
import rlVizLib.utilities.UtilityShop;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;

/**
 * My belief is that our classes are leaking like crazy because we are forever 
 * loading them using new classloaders and a bunch of the statically allocated 
 * stuff is just hanging around.
 * 
 * I want to cache loaded classes so we only load them a single time.
 * @author btanner
 *
 */
public class LocalJarAgentEnvironmentLoader implements DynamicLoaderInterface {

    private Vector<String> theNames = null;
    protected Vector<Class<?>> theClasses = null;
    private Vector<ParameterHolder> theParamHolders = null;
    private Map<String, String> publicNameToFullName = null;
    private Set<String> allFullClassName = null;
    private Vector<URI> theUriList = new Vector<URI>();
    private ClassExtractor theClassExtractor;

    //This seems like we're breaking OO rules
    private EnvOrAgentType theLoaderType;

    public String getClassName(String theName) {
        StringTokenizer theTokenizer = new StringTokenizer(theName, ".");
        String name = "undefined";
        while (theTokenizer.hasMoreTokens()) {
            name = theTokenizer.nextToken();
        }
        return name;
    }

    public LocalJarAgentEnvironmentLoader(Vector<URI> uriList, EnvOrAgentType theLoaderType) {
        theUriList.addAll(uriList);
        this.theLoaderType = theLoaderType;

        CompositeResourceGrabber theCompJarGrabber = new CompositeResourceGrabber();

        FileFilter theJarFileFilter = new JarFileFilter();
        for (URI uri : uriList) {
            LocalDirectoryGrabber thisGrabber = new LocalDirectoryGrabber(uri);
            thisGrabber.addFilter(theJarFileFilter);

            theCompJarGrabber.add(thisGrabber);
            
        }
        theClassExtractor = new ClassExtractor(theCompJarGrabber);
    }

    public boolean makeList() {
        theNames = new Vector<String>();
        theClasses = new Vector<Class<?>>();
        theParamHolders = new Vector<ParameterHolder>();
        allFullClassName = new TreeSet<String>();
        Vector<Class<?>> allMatching = new Vector<Class<?>>();
        publicNameToFullName=new TreeMap<String, String>();

        if (theLoaderType.id() == EnvOrAgentType.kBoth.id()) {
            //System.out.println("-------Loading both types");
            allMatching = theClassExtractor.getAllClassesThatImplement(EnvironmentInterface.class, Unloadable.class);
            allMatching.addAll(theClassExtractor.getAllClassesThatImplement(AgentInterface.class, Unloadable.class));
        }
        if (theLoaderType.id() == EnvOrAgentType.kEnv.id()) {
            //System.out.println("-------Loading kEnv types");
            allMatching = theClassExtractor.getAllClassesThatImplement(EnvironmentInterface.class, Unloadable.class);
        }
        if (theLoaderType.id() == EnvOrAgentType.kAgent.id()) {
            //System.out.println("-------Loading kAgent types");
            allMatching = theClassExtractor.getAllClassesThatImplement(AgentInterface.class, Unloadable.class);
        }

        for (Class<?> thisClass : allMatching) {
            if ((!allFullClassName.contains(thisClass.getName())) && !isAbstractClass(thisClass)) {
                allFullClassName.add(thisClass.getName());
                String shortName = addFullNameToMap(thisClass.getName());
                theClasses.add(thisClass);
                theNames.add(shortName);

                
                checkVersions(thisClass);
                ParameterHolder thisP = loadParameterHolderFromFile(thisClass);
                
                String sourceJarPath="unknown";
                try{
                    sourceJarPath = thisClass.getProtectionDomain().getCodeSource().getLocation().toURI().normalize().toString();
                }catch(URISyntaxException e){
                    sourceJarPath+=" :: couldn't parse URI";
                }
                
                UtilityShop.addSourceDetails(thisP, thisClass.getName(), sourceJarPath);
                theParamHolders.add(thisP);
            }
        }
        return true;
    }

    protected String getFullClassNameFromShortName(String shortName) {
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
                    System.err.println(paramBasedE);
                    System.err.println("Nested exception: " + paramBasedE.getCause());
                }
            }
            try {
                Constructor<?> emptyConstructor = theClass.getConstructor();
                if (emptyConstructor == null) {
                    System.err.println("WTF emptyConstructor is null");
                }
                theModule = (Object) emptyConstructor.newInstance();
            } catch (Exception noParamsE) {
                System.err.println("Could't load instance of: " + theClass.getName() + " with parameters or without");
                System.err.println("Exception was: " + noParamsE);
                System.err.println("\tNested exception: " + noParamsE.getCause());
            }


        }
        return theModule;

    }

    private boolean checkVersions(Class<?> theClass) {
		RLVizVersion theLinkedLibraryVizVersion=rlVizLib.rlVizCore.getRLVizSpecVersion();
		RLVizVersion ourCompileVersion=rlVizLib.rlVizCore.getRLVizSpecVersionOfClassWhenCompiled(theClass);
		
		if(!theLinkedLibraryVizVersion.equals(ourCompileVersion)){
			System.err.println("Warning :: Possible RLVizLib Incompatibility");
			System.err.println("Warning :: Runtime version used by "+theClass.getName()+" is:  "+theLinkedLibraryVizVersion);
			System.err.println("Warning :: Compile version used to build "+theClass.getName()+" is:  "+ourCompileVersion);
                        return false;
                }
        return true;
    }

    public String getTypeSuffix() {
        return "- Java";
    }
}


