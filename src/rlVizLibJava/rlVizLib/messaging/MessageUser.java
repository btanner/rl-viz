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
package rlVizLib.messaging;


public enum MessageUser{
	kBenchmark(0),
	kEnvShell(1),
	kAgentShell(2),
	kEnv(3),
	kAgent(4);
	
	private final int id;
	
	MessageUser(int id){
        this.id = id;
    }
    public int id()   {return id;}
    
        public static String name(int id){
        if(id == kBenchmark.id())return "kBenchmark";
        if(id == kEnvShell.id())return "kEnvShell";
        if(id == kAgentShell.id())return "kAgentShell";
        if(id == kEnv.id())return "kEnv";
        if(id == kAgent.id())return "kAgent";
        return "Type: "+id+" is unknown MessageUser";
    }

    public static String name(MessageUser user) {
        return name(user.id());
    }
        

}
