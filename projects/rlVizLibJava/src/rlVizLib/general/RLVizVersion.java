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

import java.util.StringTokenizer;
import rlVizLib.rlVizCore;

public class RLVizVersion implements Comparable<RLVizVersion>{
	int majorRevision;
	int minorRevision;
	
	public static final RLVizVersion NOVERSION=new RLVizVersion(0,0);
	public static final RLVizVersion CURRENTVERSION;
        
        static{
            CURRENTVERSION=rlVizCore.getRLVizSpecVersion();
        }
	
	public RLVizVersion(int majorRevision, int minorRevision){
		this.majorRevision=majorRevision;
		this.minorRevision=minorRevision;
	}
	
	public RLVizVersion(String serialized){
            try {
		StringTokenizer theTokenizer=new StringTokenizer(serialized,".");
		majorRevision=Integer.parseInt(theTokenizer.nextToken());
		minorRevision=Integer.parseInt(theTokenizer.nextToken());
            } catch (Exception exception) {
                majorRevision=NOVERSION.majorRevision;
                minorRevision=NOVERSION.minorRevision;
            }

	}

	public int getMajorRevision() {
		return majorRevision;
	}

	public int getMinorRevision() {
		return minorRevision;
	}
	
	public String serialize(){
		String theString=majorRevision+"."+minorRevision;
		return theString;
	}
        
    @Override
        public String toString(){
            return ""+getMajorRevision()+"."+getMinorRevision();
        }

	public int compareTo(RLVizVersion otherVersion) {
		
		if(otherVersion.getMajorRevision()<getMajorRevision())
			return 1;
		if(otherVersion.getMajorRevision()>getMajorRevision())
			return -1;
		if(otherVersion.getMinorRevision()<getMinorRevision())
			return 1;
		if(otherVersion.getMinorRevision()>getMinorRevision())
			return -1;
		
		return 0;

	}

}
