package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import it.polimi.ingsw.server.model.Date;

import java.io.Serializable;

//messaggio che si invia nel primo stato
public class SetNameMessage extends SetupMessage implements Serializable {

    private final String playerName;
    private final Date dateOfBirthday;

    //costruttore completo
    private SetNameMessage(String playerName, Date dateOfBirthday){
        super(TypeOfSetupMessage.SetPlayerNameAndBirthday);
        this.playerName = playerName;
        this.dateOfBirthday = dateOfBirthday;

    }
    public static SetNameMessage newSetNameMessageComplete(String playerName, Date dateOfBirthday) {

        return new SetNameMessage(playerName, dateOfBirthday);
    }

    //costruttore per la risposta positiva o negativa
    private SetNameMessage(TypeOfSetupMessage typeOfSetupMessage, String errorMessage) {
        super(typeOfSetupMessage, errorMessage);
        this.playerName = null;
        this.dateOfBirthday = null;
    }
    public static SetNameMessage newSetNameMessageAffirmation(TypeOfSetupMessage typeOfSetupMessage, String errorMessage) {

        return new SetNameMessage(typeOfSetupMessage, errorMessage);
    }


    public String getPlayerName() {
        return playerName;
    }
    public Date getDateOfBirthday() {
        return dateOfBirthday;
    }
}
