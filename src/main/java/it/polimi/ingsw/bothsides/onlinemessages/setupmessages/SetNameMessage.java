package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import it.polimi.ingsw.server.model.Date;

import java.io.Serializable;

/**
 * this kind of message is used to comunicate the name and birthdate of the player to server
 *
 * also here constructors are private and there are static methods to build
 * different types of this message
 * and to make code or more legible
 */
public class SetNameMessage extends SetupMessage implements Serializable {

    private final String playerName;
    private final Date dateOfBirthday;

    private SetNameMessage(String playerName, Date dateOfBirthday){
        super(TypeOfSetupMessage.SET_PLAYER_NAME_AND_BIRTHDAY);
        this.playerName = playerName;
        this.dateOfBirthday = dateOfBirthday;

    }
    public static SetNameMessage newSetNameMessageComplete(String playerName, Date dateOfBirthday) {

        return new SetNameMessage(playerName, dateOfBirthday);
    }

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
