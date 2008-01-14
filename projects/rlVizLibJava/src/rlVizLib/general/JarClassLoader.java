/* RL-VizLib, a library for C++ and Java for adding advanced visualization and dynamic capabilities to RL-Glue.
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
package rlVizLib.general;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**

@author btanner
*/
public class JarClassLoader {
/**

@param theFile A File object that is the Jar we want to load from
@param className The fully qualified name of the class to load
@return If this method returns anything it is the class requested.
@throws CouldNotLoadJarException
 */	
public static Class<?> loadClassFromFile(File theFile,String className) throws CouldNotLoadJarException {
		Class<?> theClass=null;
		String theFileName=theFile.getAbsolutePath();

		URLClassLoader urlLoader = null;


			URL theURL=null;
                        
		try {
			theURL=new URL("file",null, theFileName);
			urlLoader = new URLClassLoader(new URL[]{theURL});
                        theClass=urlLoader.loadClass(className);
		} catch (Throwable e) {
                        String errorString="SERIOUS ERROR: When JarClassLoader tried to load class: "+className+" from file: "+theFile.toString()+"\nURL is: "+theURL.toString()+"\n"+e.toString();
                        throw new CouldNotLoadJarException(errorString, e);
                }
		return theClass;
	}


/**

@param theJarFile A File object that is the Jar we want to load from
@param theClassName The fully qualified name of the class to load
@return Like loadClassFromFile, except returns null if there is a problem instead of throwing an Exception
 */	
    public static Class<?> loadClassFromFileQuiet(File theJarFile, String theClassName) {
        return loadClassFromFileQuiet(theJarFile, theClassName,false);
}
/**

@param theJarFile A File object that is the Jar we want to load from
@param theClassName The fully qualified name of the class to load
@param dumpTheStack If this is true, a stack trace will be produced if there is an error
@return Like loadClassFromFile, except returns null if there is a problem instead of throwing an Exception
 */	
public static Class<?> loadClassFromFileQuiet(File theJarFile,String theClassName, boolean dumpTheStack){
        Class<?> theClass = null;
        try {
            theClass = loadClassFromFile(theJarFile, theClassName);
        } catch (CouldNotLoadJarException e) {
            System.out.println(e.getErrorString());
            System.out.println(e.getOriginalProblem());
            if(dumpTheStack)Thread.dumpStack();
            return null;
        }
        return theClass;
    }
}
