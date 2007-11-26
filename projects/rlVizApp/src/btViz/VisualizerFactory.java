/* RLViz Application, a visualizer and dynamic loader for C++ and Java RL-Glue agents/environments
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
package btViz;

import java.io.File;
import java.lang.reflect.Constructor;
import rlVizLib.general.TinyGlue;
import rlVizLib.visualization.AbstractVisualizer;
import rlVizLib.visualization.interfaces.DynamicControlTarget;

public class VisualizerFactory {

    static String defaultEnvVisualizerClassName = "visualizers.Generic.GenericEnvVisualizer";
    static String defaultAgentVisualizerClassName = "visualizers.Generic.GenericAgentVisualizer";

    public static AbstractVisualizer createEnvVisualizerFromClassName(String VizClassName, TinyGlue theGlueState, DynamicControlTarget theControlTarget) {
        return createVisualizerFromClassName(VizClassName, "envVizJars", defaultEnvVisualizerClassName, theGlueState, theControlTarget);
    }

    public static AbstractVisualizer createAgentVisualizerFromClassName(String VizClassName, TinyGlue theGlueState, DynamicControlTarget theControlTarget) {
        return createVisualizerFromClassName(VizClassName, "agentVizJars", defaultAgentVisualizerClassName, theGlueState, theControlTarget);
    }

    private static AbstractVisualizer createVisualizerFromClassName(String theVisualizerClassName, String subDirectory, String defaultClassName, TinyGlue theGlueState, DynamicControlTarget theControlTarget) {
        boolean debugThis=false;
        
        if(debugThis)System.out.println("AbstractVisualizer::createVisualizerFromClassName");
        if(debugThis)System.out.println("\t\t theVisualizerClassName="+theVisualizerClassName);
        if(debugThis)System.out.println("\t\t subDirectory="+subDirectory);
        if(debugThis)System.out.println("\t\t defaultClassName="+defaultClassName);
        AbstractVisualizer theViz = null;
        String libPath = rlVizLib.utilities.UtilityShop.getLibraryPath();
        String envVizJarPath = libPath + "/" + subDirectory;

        //Lets say that the Jar needs to have the same name as the Visualizer
        int lastPeriodPosition = theVisualizerClassName.lastIndexOf(".");
        String JarName = theVisualizerClassName.substring(lastPeriodPosition + 1) + ".jar";

        File theJarFile = new File(envVizJarPath + "/" + JarName);
        Class<?> theClass = rlVizLib.general.JarClassLoader.loadClassFromFileQuiet(theJarFile, theVisualizerClassName);

        //If we couldn't load the class they asked for (shock!) we should load the default visualizer
        if (theClass != null) {
            if(debugThis)System.out.println("\t Loaded Class Successfully");
            //First try and load the one with all parameters
            theViz = createVisualizer(theClass, theGlueState, theControlTarget);

            //IF that didn't work, try without theControlTarget
            if (theViz == null) {
                if(debugThis)System.out.println("\t Failed to load triple constructor");
                theViz = createVisualizer(theClass, theGlueState);
            }
            //IF that didn't work, try without theGlueState
            if (theViz == null) {
                if(debugThis)System.out.println("\t Failed to load double constructor");
                theViz = createVisualizer(theClass);
            }

        }else{
                        if(debugThis)System.out.println("\t Failed to load "+theVisualizerClassName+"  from "+theJarFile);
        }
        if (theViz == null) {
                if(debugThis)System.out.println("\t Failed to load single constructor");
            //Ok at this point, either we didn't load the class for the real visualizer or we couldn't load any constructors
            //In either case, move on to the default visualizer
            lastPeriodPosition = defaultClassName.lastIndexOf(".");
            String defaultJarName = defaultClassName.substring(lastPeriodPosition + 1) + ".jar";
            
            File genericVizJarFile = new File(envVizJarPath + "/" + defaultJarName);
            theClass = rlVizLib.general.JarClassLoader.loadClassFromFileQuiet(genericVizJarFile, defaultClassName);
            //In this case we can't even load the generic class file, give up
            if (theClass == null) {
                   if(debugThis)System.out.println("\t Failed to load generic vizualier:"+theVisualizerClassName+"  from "+theJarFile);
                   return null;
            }

            theViz = createVisualizer(theClass, theGlueState, theControlTarget);
        }

        //Not sure what we ended up with, but in any case, return it
        return theViz;
    }

    private static AbstractVisualizer createVisualizer(Class<?> theVizClass) {
        AbstractVisualizer theVisualizer = null;
        Class<?>[] emptyParams = new Class<?>[0];

        Constructor<?> theConstructor = null;
        try {
            theConstructor = theVizClass.getConstructor(emptyParams);
            theVisualizer = (AbstractVisualizer) theConstructor.newInstance();
        } catch (Exception e) {
            return null;
        }
        return theVisualizer;
    }

    private static AbstractVisualizer createVisualizer(Class<?> theVizClass, TinyGlue theGlueState) {
        AbstractVisualizer theVisualizer = null;
        Class<?>[] emptyParams = new Class<?>[0];

        Constructor<?> theConstructor = null;
        try {
            theConstructor = theVizClass.getConstructor(TinyGlue.class);
            theVisualizer = (AbstractVisualizer) theConstructor.newInstance(theGlueState);
        } catch (Exception e) {
            return null;
        }
        return theVisualizer;
    }

    private static AbstractVisualizer createVisualizer(Class<?> theVizClass, TinyGlue theGlueState, DynamicControlTarget theControlTarget) {
        AbstractVisualizer theVisualizer = null;
        Class<?>[] emptyParams = new Class<?>[0];

        Constructor<?> theConstructor = null;
        try {
            theConstructor = theVizClass.getConstructor(TinyGlue.class, DynamicControlTarget.class);
            theVisualizer = (AbstractVisualizer) theConstructor.newInstance(theGlueState, theControlTarget);
        } catch (Exception e) {
            return null;
        }
        return theVisualizer;
    }


}


