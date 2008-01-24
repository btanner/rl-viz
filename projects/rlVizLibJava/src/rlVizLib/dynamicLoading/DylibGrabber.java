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

    
//
//    public Vector<URI> getValidEnvDylibURIs() {
//        //These are directories
//        Vector<URI> allHidingPlaces = EnvironmentShellPreferences.getInstance().getList();
//        Vector<URI> validDylibURIs = new Vector<URI>();
//
//        for (URI thisURI : allHidingPlaces) {
//
//            File F = new File(thisURI);
//            Vector<URI> allDylibsInThisDir = getAllDylibs(F);
//            for (URI thisFileURI : allDylibsInThisDir) {
//                if (isReallyAnEnvDylib(thisFileURI)) {
//                    validDylibURIs.add(thisFileURI);
//                }
//            }
//
//
//        }
//        return validDylibURIs;
//    }
//
//    private Vector<URI> getAllDylibs(File F) {
//        Vector<URI> allFileURIs = new Vector<URI>();
//
//        
//        File[] theFiles = F.listFiles(new DylibFileFilter());
//
//        System.out.println("Looking in: "+F.toString());
//        
//        if (theFiles != null) {
//            System.out.println(""+theFiles.length+" files");
//            for (File thisFile : theFiles) {
//                allFileURIs.add(thisFile.toURI());
//            }
//        }else{
//            System.out.println(" 0 files");
//        }
//        return allFileURIs;
//    }

    private boolean isReallyAnEnvDylib(URI thisFileURI) {
        int errorCode = jniIsThisAValidEnv(thisFileURI.getPath(),DEBUG);
        if(DEBUG)System.out.println("Rejected: "+thisFileURI.toString()+" for error code: "+errorCode);
        if (errorCode == 0) {
            return true;
        }

        return false;
    }

}
