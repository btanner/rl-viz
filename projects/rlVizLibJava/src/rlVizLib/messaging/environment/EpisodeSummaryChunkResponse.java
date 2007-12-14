/*
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
package rlVizLib.messaging.environment;

import java.util.StringTokenizer;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;

/**
 * This is the workhorse that brings the functionality to the {@link EpisodeSummaryRequest} message.
 * It transports a substring from the log file from the environment to the experiment program.
 * @author Brian Tanner
 */
class EpisodeSummaryChunkResponse extends AbstractResponse {

    String theLogData;

    /**
     * Not storing amount requested inside anymore but if I take it out of constructor
     * then I have a conflict with the constructor that builds object from messsage string
     * @param theLogString
     * @param amountRequested
     */public EpisodeSummaryChunkResponse(String theLogString, int amountRequested) {
        this.theLogData = theLogString;
    }
    
        public EpisodeSummaryChunkResponse(String responseMessage) throws NotAnRLVizMessageException {
        GenericMessage theGenericResponse = new GenericMessage(responseMessage);
        String thePayLoad = theGenericResponse.getPayLoad();

        this.theLogData=thePayLoad;
    }


    int getAmountReceived() {
        return theLogData.length();
    }

    String getLogData() {
        return theLogData;
    }

    @Override
    public String makeStringResponse() {
        StringBuffer thePayLoadBuffer = new StringBuffer();
        if (theLogData != null) {
            thePayLoadBuffer.append(theLogData);
        }


        String theResponse = AbstractMessage.makeMessage(MessageUser.kBenchmark.id(),
                MessageUser.kEnv.id(),
                EnvMessageType.kEnvResponse.id(),
                MessageValueType.kStringList.id(),
                thePayLoadBuffer.toString());

        return theResponse;
    }
}
