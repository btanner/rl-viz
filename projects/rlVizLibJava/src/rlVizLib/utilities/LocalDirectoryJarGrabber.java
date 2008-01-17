/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rlVizLib.utilities;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mradkie
 */
public class LocalDirectoryJarGrabber extends AbstractJarGrabber{
String theDirectoryString=".";

    public LocalDirectoryJarGrabber(String theDirectoryString){
        super();
        this.theDirectoryString=theDirectoryString;
    } 
     /**
     * This method returns a vector of Files from theJarDir directory
     * @param theJarDir
     * @return
     */
    public void refreshJarURIList() {
            theJarURIs.clear();

        //create a list of all files in theJarDir
        File JarDir = new File(theDirectoryString);
        assert(JarDir.isDirectory());
        File[] theFileList = JarDir.listFiles();

        if (theFileList == null) {
            System.err.println("Unable to find useable jars, quitting");
            System.err.println("Was looking in: " + theDirectoryString);
        }

        for (File thisFile : theFileList) {
            if (thisFile.getName().endsWith(".jar")) {
                    theJarURIs.add(thisFile.toURI());
            }
        }
    }

}
