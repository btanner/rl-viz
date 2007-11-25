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

import java.util.StringTokenizer;

public class GenericMessageParser {

		public static MessageUser parseUser(String userChunk){
			StringTokenizer tok=new StringTokenizer(userChunk,"=");
			tok.nextToken();
			String theUserString=tok.nextToken();
			
			int theIntValue=Integer.parseInt(theUserString);
			
			if(theIntValue==MessageUser.kAgent.id())
				return MessageUser.kAgent;
			if(theIntValue==MessageUser.kEnv.id())
				return MessageUser.kEnv;
			if(theIntValue==MessageUser.kAgentShell.id())
				return MessageUser.kAgentShell;
			if(theIntValue==MessageUser.kBenchmark.id())
				return MessageUser.kBenchmark;
			if(theIntValue==MessageUser.kEnvShell.id())
				return MessageUser.kEnvShell;

			Thread.dumpStack();
			return null;
			
		}

		public static int parseInt(String typeString) {
			StringTokenizer typeTokenizer=new StringTokenizer(typeString,"=");
			typeTokenizer.nextToken();
			String theCMD=typeTokenizer.nextToken();
			int theCMDInt=Integer.parseInt(theCMD);
			return theCMDInt;
		}

		public static MessageValueType parseValueType(String typeString) {
			StringTokenizer typeTokenizer=new StringTokenizer(typeString,"=");
			typeTokenizer.nextToken();
			String theValueTypeString=typeTokenizer.nextToken();
			int theValueType=Integer.parseInt(theValueTypeString);

			if(theValueType==MessageValueType.kStringList.id())
				return MessageValueType.kStringList;
			if(theValueType==MessageValueType.kString.id())
				return MessageValueType.kString;
			if(theValueType==MessageValueType.kBoolean.id())
				return MessageValueType.kBoolean;
			if(theValueType==MessageValueType.kNone.id())
				return MessageValueType.kNone;

			System.out.println("Unknown Value type: "+theValueType);
			Thread.dumpStack();
			System.exit(1);
			return null;
		}

		public static String parsePayLoad(String payLoadString) {
                        int firstEqualPos=payLoadString.indexOf("=");
                        String payLoad=payLoadString.substring(firstEqualPos+1);


			return payLoad;
		}
}
