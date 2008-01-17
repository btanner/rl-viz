/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rlVizLib.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mradkie
 */
public class getClassesFromJars {

    private static boolean debugClassLoading = false;

    /**
     * gACTI stands for get All Classes That Implement.
     * 
     * This method checks all Jars in theJardDir for classes that implement
     * theInterface. 
     * 
     * @param theJarDir
     * @param theInterface
     * @return a vector of classes that implement theInterface found in
     *         theJarDir
     * 
     */
    public static Vector<Class<?>> gACTI(String theJarDir, Class<?> theInterface) {
        Vector<File> theJars = getAllJarsFromDir(theJarDir);
        Vector<Class<?>> allClasses = new Vector<Class<?>>();
        Vector<Class<?>> matchingClasses = new Vector<Class<?>>();
        
        for (File thisJar : theJars) {
            allClasses.addAll(gACFJ(thisJar));
        }
        for (Class<?> thisClass : allClasses) {
            if (checkIfDescendantOf(thisClass, theInterface)) {
                matchingClasses.add(thisClass);
            }
        }
        return matchingClasses;
    }

    /**
     * This method returns a vector of Files from theJarDir directory
     * @param theJarDir
     * @return
     */
    public static Vector<File> getAllJarsFromDir(String theJarDir) {
        Vector<File> theJars = new Vector<File>();

        //create a list of all files in theJarDir
        File JarDir = new File(theJarDir);
        File[] theFileList = JarDir.listFiles();

        if (theFileList == null) {
            System.err.println("Unable to find useable jars, quitting");
            System.err.println("Was looking in: " + theJarDir);
            return theJars;
        }

        for (File thisFile : theFileList) {
            if (thisFile.getName().endsWith(".jar")) {
                theJars.add(thisFile);
            }
        }

        return theJars;
    }

    /**
     * gACFJ stands for get ALL Classes From Jar
     * 
     * This Method returns a Vector of classes that are contained within
     * a Jar
     * @param theJar - the jar to get classes from
     * @return
     */
    public static Vector<Class<?>> gACFJ(File thisFile) {
        Vector<Class<?>> theClasses = new Vector<Class<?>>();
        
        try {
            JarFile theJar = new JarFile(thisFile);
            Enumeration<JarEntry> allJarEntries = theJar.entries();

            while (allJarEntries.hasMoreElements()) {
                JarEntry thisEntry = allJarEntries.nextElement();
                Class<?> theClass = getClassFromJarEntry(thisEntry, thisFile);
                if (theClass != null) {
                    theClasses.add(theClass);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(getClassesFromJars.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return theClasses;
    }

    public static Set<String> getAncestorNames(Class<?> theClass) {
        Set<String> ancestorSet = new TreeSet<String>();
        getAllAncestors(ancestorSet, theClass);
        return ancestorSet;
    }

    /**
     * This Method calls getInterfaceNames to get a list of all interfaces 
     * implemented by thisEntry. If this set of interfaceNames contains
     * theInterface.getname() (the name of the interface to see of thisEntry 
     * implements) true is returned, otherwise false.
     * 
     * @param thisEntry - the class file we wish to check
     * @param thisFile - the jar file the class file is located in
     * @param theInterface - the interface we want to check if thisEntry implements+
     * @return
     */
    public static boolean checkIfDescendantOf(Class<?> sourceClass, Class<?> theAncestor) {
        Set<String> ancestorSet = getAncestorNames(sourceClass);
        return ancestorSet.contains(theAncestor.getName());
    }

    /**
     * This method gets a list of all interfaces that the class implements
     * 
     * @param theSet - the list of all interfaces sourceClass implements
     * @param sourceClass
     */
    public static void getAllAncestors(Set<String> theSet, Class sourceClass) {
        if (sourceClass == null) {
            return;
        }

        Class[] theInterfaces = sourceClass.getInterfaces();
        for (Class thisInterface : theInterfaces) {
            theSet.add(thisInterface.getName());
            getAllAncestors(theSet, thisInterface.getSuperclass());
        }
        Class superClass=sourceClass.getSuperclass();
        theSet.add(superClass.getName());
        getAllAncestors(theSet, superClass);
    }

    /**
     * This method loads a class (thisEntry) from a jar file (thisFile)
     * 
     * @param thisEntry - the class file to load
     * @param thisFile - the jar file to load it from
     * @return
     */
    public static Class<?> getClassFromJarEntry(JarEntry thisEntry, File thisFile) {
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
}
