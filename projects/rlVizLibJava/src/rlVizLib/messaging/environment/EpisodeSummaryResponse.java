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
package rlVizLib.messaging.environment;

import java.util.StringTokenizer;
import java.util.Vector;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;

public class EpisodeSummaryResponse extends AbstractResponse {

    Vector<String> theEpisodeSummaries = new Vector<String>();

    //If the client didn't support it we could just make one of these

    EpisodeSummaryResponse() {
    }

    EpisodeSummaryResponse(Vector<String> theEpisodeSummaries) {
        this.theEpisodeSummaries = theEpisodeSummaries;
    }

    public EpisodeSummaryResponse(String responseMessage) throws NotAnRLVizMessageException {
        GenericMessage theGenericResponse = new GenericMessage(responseMessage);
        String thePayLoad = theGenericResponse.getPayLoad();

        StringTokenizer T = new StringTokenizer(thePayLoad, ":");

        int numElements = Integer.parseInt(T.nextToken());

        for (int i = 0; i < numElements; i++) {
			String thisToken=T.nextToken();
			theEpisodeSummaries.add(thisToken);
        }
    }

    @Override
	public String makeStringResponse() {
        StringBuffer thePayLoadBuffer = new StringBuffer();

        if (theEpisodeSummaries == null) {
            thePayLoadBuffer.append("0:");
        } else {
            thePayLoadBuffer.append(theEpisodeSummaries.size());

            thePayLoadBuffer.append(":");

            for (String theSummary : theEpisodeSummaries) {
                thePayLoadBuffer.append(theSummary);
                thePayLoadBuffer.append(":");
            }
        }


        String theResponse = AbstractMessage.makeMessage(MessageUser.kBenchmark.id(),
                MessageUser.kEnv.id(),
                EnvMessageType.kEnvResponse.id(),
                MessageValueType.kStringList.id(),
                thePayLoadBuffer.toString());

        return theResponse;

    }

    public Vector<String> getTheSummaries() {
        return theEpisodeSummaries;
    }
};