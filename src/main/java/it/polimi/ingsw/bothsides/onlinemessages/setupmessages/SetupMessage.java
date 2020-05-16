package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;


public abstract class SetupMessage implements Serializable {


    public final TypeOfSetupMessage typeOfSetupMessage;
    public final String errorMessage;

    //costruttore per messaggi con errorText
    public SetupMessage(TypeOfSetupMessage typeOfSetupMessage, String errorMessage) {
        this.typeOfSetupMessage = typeOfSetupMessage;

        this.errorMessage = errorMessage;
    }

    public SetupMessage(TypeOfSetupMessage typeOfSetupMessage) {

        this.errorMessage = null;
        this.typeOfSetupMessage = typeOfSetupMessage;

    }

}


//la relazione tra messaggio scambiato e stato è univoca, un tipo di messaggio per ogni stato, il resto lo fanno i costruttori particolari




