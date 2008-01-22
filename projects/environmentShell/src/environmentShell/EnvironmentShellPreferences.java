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

import java.net.URI;
import java.net.URISyntaxException;
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
    private String libDir = "";
    
    public static EnvironmentShellPreferences getInstance() {
        return ourInstance;
    }

    private EnvironmentShellPreferences() {
    }
    
    public void addToList(URI theURI){
        this.envUriList.add(theURI);
    }
    public Vector<URI> getList(){
        if(this.envUriList.isEmpty()){
            try {
                this.envUriList.add(new URI(libDir));
                this.envUriList.add(new URI("/Users/mradkie/competition/rlcomplibrary/libraries/envJars/"));
            } catch (URISyntaxException ex) {
                System.err.println("Failed to add default location of " + libDir);
            }
        }
        return this.envUriList;
    }
    public String getLibDir(){
        return this.libDir;
    }
    public void setLibDir(String path){
        this.libDir = path;
    }
}
