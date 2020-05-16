package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;

//messaggio che si invia e si riceve in maniera asincrona rispetto agli altri
public class PingAndErrorMessage extends SetupMessage implements Serializable {

    public PingAndErrorMessage(TypeOfSetupMessage typeOfSetupMessage, String errorMessage) {
        super(typeOfSetupMessage, errorMessage);
    }

    public static PingAndErrorMessage newPingAndErrorMessageStandard(TypeOfSetupMessage typeOfSetupMessage, String errorMessage){

        return new PingAndErrorMessage(typeOfSetupMessage, errorMessage);
    }
}
