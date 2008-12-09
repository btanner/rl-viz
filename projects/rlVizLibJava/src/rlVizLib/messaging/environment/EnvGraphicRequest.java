/*
Copyright 2007 Brian Tanner
brian@tannerpages.com
http://brian.tannerpages.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package rlVizLib.messaging.environment;

import java.awt.image.RenderedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.BinaryPayload;
import rlVizLib.messaging.interfaces.HasImageInterface;

public class EnvGraphicRequest extends EnvironmentMessages {

    public EnvGraphicRequest(GenericMessage theMessageObject) {
        super(theMessageObject);
    }

    public static Response Execute() {
        String theRequest = AbstractMessage.makeMessage(
                MessageUser.kEnv.id(),
                MessageUser.kBenchmark.id(),
                EnvMessageType.kEnvQueryVisualizerName.id(),
                MessageValueType.kNone.id(),
                "NULL");

        String responseMessage = RLGlue.RL_env_message(theRequest);

        Response theResponse;
        theResponse = new Response(responseMessage);
        return theResponse;
    }

    @Override
    public String handleAutomatically(EnvironmentInterface theEnvironment) {
        HasImageInterface castedEnv = (HasImageInterface) theEnvironment;
        Response theResponse = new Response(castedEnv.getImage());
        return theResponse.makeStringResponse();
    }

    @Override
    public boolean canHandleAutomatically(Object theEnvironment) {
        return (theEnvironment instanceof HasImageInterface);
    }

    public static class Response extends AbstractResponse {

        private RenderedImage theImage;

        public Response(RenderedImage theImage) {
            this.theImage = theImage;

        }

        public Response(String responseMessage) {
            try {
                GenericMessage theGenericResponse = new GenericMessage(responseMessage);

                DataInputStream DIS = BinaryPayload.getInputStreamFromPayload(responseMessage);
                theImage = ImageIO.read(DIS);
            } catch (IOException ex) {
                Logger.getLogger(EnvGraphicRequest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotAnRLVizMessageException ex) {
                Logger.getLogger(EnvGraphicRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public String makeStringResponse() {
            try {
                BinaryPayload P = new BinaryPayload();
                DataOutputStream DOS = P.getOutputStream();
                ImageIO.write(theImage, "PNG", DOS);
                String theEncodedImage = P.getAsEncodedString();
                String theResponse = AbstractMessage.makeMessage(MessageUser.kBenchmark.id(), MessageUser.kEnv.id(), EnvMessageType.kEnvResponse.id(), MessageValueType.kStringList.id(), theEncodedImage);

                return theResponse;
            } catch (IOException ex) {
                Logger.getLogger(EnvGraphicRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        public RenderedImage getImage() {
            return theImage;

        }
    };
}
