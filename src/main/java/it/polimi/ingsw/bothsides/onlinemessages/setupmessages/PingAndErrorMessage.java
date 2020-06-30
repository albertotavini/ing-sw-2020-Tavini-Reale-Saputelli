package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;

/**
 * this is the type of message that is sent from PingAndErrorHandler to control that the client is still
 * connected to the server
 */
public class PingAndErrorMessage extends SetupMessage implements Serializable {

    public PingAndErrorMessage(TypeOfSetupMessage typeOfSetupMessage, String errorMessage) {
        super(typeOfSetupMessage, errorMessage);
    }

    public static PingAndErrorMessage newPingAndErrorMessageStandard(TypeOfSetupMessage typeOfSetupMessage, String errorMessage){

        return new PingAndErrorMessage(typeOfSetupMessage, errorMessage);
    }
}
