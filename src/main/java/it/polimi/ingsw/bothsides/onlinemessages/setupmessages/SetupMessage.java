package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;


/**
 * this is the archetype of message that is exchanged between client and server
 * all types of messages in this package will inherit from this
 */
public abstract class SetupMessage implements Serializable {


    public final TypeOfSetupMessage typeOfSetupMessage;
    public final String errorMessage;

    SetupMessage(TypeOfSetupMessage typeOfSetupMessage, String errorMessage) {
        this.typeOfSetupMessage = typeOfSetupMessage;

        this.errorMessage = errorMessage;
    }

    SetupMessage(TypeOfSetupMessage typeOfSetupMessage) {

        this.errorMessage = null;
        this.typeOfSetupMessage = typeOfSetupMessage;

    }

}



