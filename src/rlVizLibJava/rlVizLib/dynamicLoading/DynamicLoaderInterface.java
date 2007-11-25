/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rlVizLib.dynamicLoading;

import java.util.Vector;
import rlVizLib.general.ParameterHolder;

/**
 *
 * @author btanner
 */
public interface DynamicLoaderInterface {
	public boolean makeList();
	public Vector<String> getNames();
	public Vector<ParameterHolder> getParameters();
	
	//Something like: (C/C++) or (Java Jar) or (Web Jar)
	public String getTypeSuffix();

}
