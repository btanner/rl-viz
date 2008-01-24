/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package environmentShell;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.Vector;

/**
 *
 * @author mradkie
 */
public class DylibGrabber {

    public native int jniIsThisAValidEnv(String path);

        
    public DylibGrabber(){
    }
    
    public Vector<URI> getValidEnvDylibURIs(){
        //These are directories
        Vector<URI> allHidingPlaces=EnvironmentShellPreferences.getInstance().getList();
        Vector<URI> validDylibURIs=new Vector<URI>();

        for (URI thisURI : allHidingPlaces) {
            
            File F=new File(thisURI);
            Vector<URI> allDylibsInThisDir=getAllDylibs(F);
            for (URI thisFileURI : allDylibsInThisDir) {
                if(isReallyAnEnvDylib(thisFileURI))
                    validDylibURIs.add(thisFileURI);
            }


        }
        return validDylibURIs;
    }

    private Vector<URI> getAllDylibs(File F) {
        Vector<URI> allFileURIs=new Vector<URI>();
        File[] theFiles=F.listFiles(new DylibFileFilter());
        for (File thisFile : theFiles) {
            allFileURIs.add(thisFile.toURI());
        }
        return allFileURIs;
    }

    private boolean isReallyAnEnvDylib(URI thisFileURI) {
        int errorCode =  jniIsThisAValidEnv(thisFileURI.getPath());        
        if(errorCode==0)return true;
               
        return false;
    }
    
}

class DylibFileFilter implements FileFilter{
    public boolean accept(File pathname) {
        return(pathname.toString().endsWith(".dylib")||pathname.toString().endsWith(".so"));
    }
}
