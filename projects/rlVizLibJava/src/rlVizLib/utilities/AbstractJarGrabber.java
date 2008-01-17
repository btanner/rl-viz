/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rlVizLib.utilities;

import java.net.URI;
import java.util.Vector;

/**
 *
 * @author mradkie
 */
public abstract class AbstractJarGrabber {
    protected Vector<URI> theJarURIs=new  Vector<URI>();
    
     /**
     * This method returns a vector of Files from theJarDir directory
     * @param theJarDir
     * @return
     */
    public abstract void refreshJarURIList();
    
    protected final void addJarURI(URI newURI){
        theJarURIs.add(newURI);
    }
    public Vector<URI> getAllJarURIs(){
        return this.theJarURIs;
    }
    
}
