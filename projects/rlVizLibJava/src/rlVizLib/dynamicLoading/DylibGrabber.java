/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rlVizLib.dynamicLoading;

import java.net.URI;
import java.util.Vector;


/**
 *
 * @author mradkie
 */
public class DylibGrabber extends LocalDirectoryGrabber{
    private boolean DEBUG=true;

    public native int jniIsThisAValidEnv(String path, boolean verbose);

    public DylibGrabber(String theDirectoryString) {
        super(theDirectoryString);
        super.addFilter(new DylibFileFilter());
    }

    public DylibGrabber(URI uri) {
        this(uri.getPath());
    }

    @Override
    public void refreshURIList() {
        //First get all .dylibs
        super.refreshURIList();
        
        
        Vector<URI> invalidDylibs=new Vector<URI>();
        //Filter out invalid .dylibs
            for (URI thisFileURI : validResourceURIs) {
                if (!isReallyAnEnvDylib(thisFileURI)) {
                    invalidDylibs.add(thisFileURI);
                }
            }
            super.validResourceURIs.removeAll(invalidDylibs);
        }

    

    private boolean isReallyAnEnvDylib(URI thisFileURI) {
        int errorCode = jniIsThisAValidEnv(thisFileURI.getPath(),DEBUG);
        if (errorCode == 0) {
            return true;
        }

        if(DEBUG)System.out.println("Rejected: "+thisFileURI.toString()+" for error code: "+errorCode);
        return false;
    }

}
