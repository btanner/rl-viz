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
package rlVizLib.visualization.interfaces;

import java.awt.Component;
import java.util.Vector;

//If you implement this interface, it means that you have a JPanel and you are willing to let random people
//plop controls down onto that panel.  They will worry about listening to changes and stuff, you just have to 
//give them a spot.

//This will be useful because the visualizer may want to plot a control down, like a value function resolution slider
public interface DynamicControlTarget {
    
    //Only call this once per visualizer
    public void addControls(Vector<Component> c);

}
