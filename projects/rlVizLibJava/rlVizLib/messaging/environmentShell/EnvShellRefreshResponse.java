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

import java.util.StringTokenizer;

import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
public class EnvShellRefreshResponse extends AbstractResponse{
//	Constructor when the Shell is responding to the load request
	boolean theResult;
        
        public boolean getTheResult(){
            return theResult;
        }
	public EnvShellRefreshResponse(boolean theResult){
		this.theResult=theResult;
	}

//	Constructor when the benchmark is interpreting the returned response
	public EnvShellRefreshResponse(String theMessage) throws NotAnRLVizMessageException {
		GenericMessage theGenericResponse=new GenericMessage(theMessage);

		String thePayLoadString=theGenericResponse.getPayLoad();

		StringTokenizer obsTokenizer = new StringTokenizer(thePayLoadString, ":");

                String loadResult=obsTokenizer.nextToken();
		String loadMessage=obsTokenizer.nextToken();
                theResult=true;
		if(!loadResult.equals("SUCCESS")){
                        theResult=false;
			System.err.println("Didn't refresh environment list for reason: "+loadMessage);
		}
	}

	@Override
	public String makeStringResponse() {
		String thePayLoadString="";

		if(theResult)
			thePayLoadString+="SUCCESS:No Message";
		else
			thePayLoadString+="FAILURE:No Message";

		String theResponse=AbstractMessage.makeMessage(
				MessageUser.kBenchmark.id(),
				MessageUser.kEnvShell.id(),
				EnvShellMessageType.kEnvShellRefresh.id(),
				MessageValueType.kStringList.id(),
				thePayLoadString);

		return theResponse;
		}


};