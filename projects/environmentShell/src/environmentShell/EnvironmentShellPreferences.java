/* Dynamic loader   Java RL-Glue agent ands environments
 * Copyright (C) 2007, Matt Radkie radkie@gmail.com
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 */
package environmentShell;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mradkie
 */
public class EnvironmentShellPreferences {

    private static EnvironmentShellPreferences ourInstance = new EnvironmentShellPreferences();
    private Vector<URI> envUriList = new Vector<URI>();
    private String jniLoaderLibDir = null;
    
    public static EnvironmentShellPreferences getInstance() {
        return ourInstance;
    }

    /**
     * Set's a default path to the same place as where this jar be livin'
     */
    private EnvironmentShellPreferences() {
        try {
            URL thisJarUrl = this.getClass().getProtectionDomain().getCodeSource().getLocation();
           jniLoaderLibDir = new File(thisJarUrl.toURI()).getParent();
        } catch (URISyntaxException ex) {
            Logger.getLogger(EnvironmentShellPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addToList(URI theURI){
        this.envUriList.add(theURI);
    }
    public Vector<URI> getList(){
        if(this.envUriList.isEmpty()){
//                envUriList.add(new File(jniLoaderLibDir).toURI());
  //              envUriList.add(new File("/Users/mradkie/competition/rlcomplibrary/libraries/envJars/").toURI());
//                envUriList.add(new File("../../rlcomplibrary/libraries/envJars/").toURI());
  //              envUriList.add(new File("../../rl-library/system/dist/").toURI());
                  envUriList.add(new File(rlVizLib.utilities.UtilityShop.getLibraryPath()).toURI());
        }
        return this.envUriList;
    }
    public String getJNILoaderLibDir(){
        return this.jniLoaderLibDir;
    }
    public void setJNILoaderLibDir(String path){
        this.jniLoaderLibDir = path;
    }
}
