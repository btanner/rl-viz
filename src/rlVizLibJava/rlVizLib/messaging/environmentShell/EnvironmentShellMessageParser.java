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
package rlVizLib.messaging.environmentShell;

import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.GenericMessageParser;
import rlVizLib.messaging.NotAnRLVizMessageException;

public class EnvironmentShellMessageParser extends GenericMessageParser{

	public static EnvironmentShellMessages parseMessage(String theMessage) throws NotAnRLVizMessageException{
		GenericMessage theGenericMessage=new GenericMessage(theMessage);
		return EnvironmentShellMessageParser.makeMessage(theGenericMessage);
	}

	public static EnvironmentShellMessages makeMessage(GenericMessage theGenericMessage) {
		int cmdId=theGenericMessage.getTheMessageType();
		if(cmdId==EnvShellMessageType.kEnvShellListQuery.id()) 				return new EnvShellListRequest(theGenericMessage);
		if(cmdId==EnvShellMessageType.kEnvShellLoad.id()) 				return new EnvShellLoadRequest(theGenericMessage);
		if(cmdId==EnvShellMessageType.kEnvShellUnLoad.id()) 				return new EnvShellUnLoadRequest(theGenericMessage);
                if(cmdId==EnvShellMessageType.kEnvShellRefresh.id()) 				return new EnvShellRefreshRequest(theGenericMessage);

		System.out.println("EnvironmentShellMessageParser - unknown query type: "+cmdId);
		Thread.dumpStack();
		System.exit(1);
		return null;
	}
}
