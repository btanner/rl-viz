/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rlVizLib.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
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
public class ClassExtractor {

    private boolean debugClassLoading = false;
    
    private AbstractJarGrabber theJarGrabber;
   
    public String theMainUrl;
    public Vector<File> theJars;
    public Vector<URI> theJarURIs;
    
    public ClassExtractor(AbstractJarGrabber theJarGrabber){
        this.theJarGrabber = theJarGrabber;
        
        theJars = new Vector<File>();
        theJarURIs = new Vector<URI>();
        
        refreshJars();
        
    }

    public void refreshJars(){
        theJarGrabber.refreshJarURIList();

        theJarURIs.clear();
        theJarURIs.addAll(theJarGrabber.getAllJarURIs());
    }
    
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
    public Vector<Class<?>> gACTI(Class<?> theInterface) {
        
        
        Vector<Class<?>> allClasses = new Vector<Class<?>>();
        Vector<Class<?>> matchingClasses = new Vector<Class<?>>();
        
        for (URI thisURI : theJarURIs) {
            allClasses.addAll(gACFJ(thisURI));
        }
        for (Class<?> thisClass : allClasses) {
            if (checkIfDescendantOf(thisClass, theInterface)) {
                matchingClasses.add(thisClass);
            }
        }
        return matchingClasses;
    }


    /**
     * gACFJ stands for get ALL Classes From Jar
     * 
     * This Method returns a Vector of classes that are contained within
     * a Jar
     * @param theJar - the jar to get classes from
     * @return
     */
    public Vector<Class<?>> gACFJ(URI theURI) {
        Vector<Class<?>> theClasses = new Vector<Class<?>>();
        
        try {
            JarFile theJar = new JarFile(new File(theURI));
            Enumeration<JarEntry> allJarEntries = theJar.entries();

            while (allJarEntries.hasMoreElements()) {
                JarEntry thisEntry = allJarEntries.nextElement();
                Class<?> theClass = getClassFromJarEntry(thisEntry, theURI);
                if (theClass != null) {
                    theClasses.add(theClass);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ClassExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return theClasses;
    }

    public Set<String> getAncestorNames(Class<?> theClass) {
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
     * @param theURL - the jar file the class file is located in
     * @param theInterface - the interface we want to check if thisEntry implements+
     * @return
     */
    public boolean checkIfDescendantOf(Class<?> sourceClass, Class<?> theAncestor) {
        Set<String> ancestorSet = getAncestorNames(sourceClass);
        return ancestorSet.contains(theAncestor.getName());
    }

    /**
     * This method gets a list of all interfaces that the class implements
     * 
     * @param theSet - the list of all interfaces sourceClass implements
     * @param sourceClass
     */
    public void getAllAncestors(Set<String> theSet, Class sourceClass) {
        if (sourceClass == null) {
            return;
        }

        Class[] theInterfaces = sourceClass.getInterfaces();
        for (Class thisInterface : theInterfaces) {
            theSet.add(thisInterface.getName());
            getAllAncestors(theSet, thisInterface.getSuperclass());
        }
        Class superClass=sourceClass.getSuperclass();
        if(superClass!=null){
            theSet.add(superClass.getName());
            getAllAncestors(theSet, superClass);
        }
    }

    /**
     * This method loads a class (thisEntry) from a jar file (theURL)
     * 
     * @param thisEntry - the class file to load
     * @param theURL - the jar file to load it from
     * @return
     */
    public Class<?> getClassFromJarEntry(JarEntry thisEntry, URI thisFileURI) {
        if (thisEntry.getName().endsWith(".class")) {
            //Cut off the .class
            String thisClassName = thisEntry.getName().substring(0, thisEntry.getName().length() - 6);
            thisClassName = thisClassName.replace(File.separator, ".");

            //Load the class file first and make sure it works
            Class<?> theClass = rlVizLib.general.JarClassLoader.loadClassFromFileQuiet(thisFileURI, thisClassName, debugClassLoading);
            return theClass;
        }
        return null;
    }
}
