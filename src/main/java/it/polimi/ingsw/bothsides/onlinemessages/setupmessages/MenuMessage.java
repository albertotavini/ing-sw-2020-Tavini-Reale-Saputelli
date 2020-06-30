package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;

/**
 * this messages are the ones exchanged in the phase of selecting or creating a lobby
 * here constructors for different types of this message are private
 * and there is a corresponding public method to build each type with ease
 * and to make code more legible
 */
public class MenuMessage extends SetupMessage implements Serializable {

    //da modificare il costruttore quando l'input è sbagliato


    private final String lobbyName;
    private final int numberOfPlayers;
    private final String lobbyPassword;
    private final String myName;
    private final boolean isPublic;

    private MenuMessage(String lobbyName, int numberOfPlayers, String lobbyPassword, String myName) {

        super(TypeOfSetupMessage.CHOOSE_CREATE_LOBBY_PRIVATE);
        this.lobbyName = lobbyName;
        this.lobbyPassword = lobbyPassword;
        this.myName = myName;
        this.numberOfPlayers = numberOfPlayers;
        this.isPublic = false;
    }
    public static MenuMessage newMenuMessageCreatePrivate(String lobbyName, int numberOfPlayers, String lobbyPassword, String myName){

        return new MenuMessage(lobbyName, numberOfPlayers, lobbyPassword, myName);
    }

    private MenuMessage(String lobbyName, int numberOfPlayers, String myName) {

        super(TypeOfSetupMessage.CHOOSE_CREATE_LOBBY_PUBLIC);
        this.lobbyName = lobbyName;
        this.lobbyPassword = null;
        this.myName = myName;
        this.numberOfPlayers = numberOfPlayers;
        this.isPublic = true;
    }
    public static MenuMessage newMenuMessageCreatePublic(String lobbyName, int numberOfPlayers, String myName){

        return new MenuMessage(lobbyName, numberOfPlayers, myName);
    }

    private MenuMessage(String lobbyName, String lobbyPassword, String myName) {

        super(TypeOfSetupMessage.CHOOSE_PARTECIPATE_LOBBY_PRIVATE);
        this.lobbyName = lobbyName;
        this.lobbyPassword = lobbyPassword;
        this.myName = myName;
        this.numberOfPlayers = 0;
        this.isPublic = false;

    }
    public static MenuMessage newMenuMessagePartPrivate(String lobbyName, String lobbyPassword, String myName){

        return new MenuMessage(lobbyName, lobbyPassword, myName);

    }

    private MenuMessage(String lobbyName, String myName) {

        super(TypeOfSetupMessage.CHOOSE_PARTECIPATE_LOBBY_PUBLIC);
        this.lobbyName = lobbyName;
        this.lobbyPassword = null;
        this.myName = myName;
        this.numberOfPlayers = 0;
        this.isPublic = false;

    }

    public static MenuMessage newMenuMessagePartPublic(String lobbyName, String myName){

        return new MenuMessage(lobbyName, myName);

    }

    private MenuMessage(String myName, int numberOfPlayers) {

        super(TypeOfSetupMessage.CHOOSE_LOBBY_CASUAL);
        this.lobbyName = null;
        this.lobbyPassword = null;
        this.myName = myName;
        this.numberOfPlayers = numberOfPlayers;
        this.isPublic = false;

    }
    public static MenuMessage newMenuMessageCasual(String myName, int numberOfPlayers){

        return new MenuMessage(myName, numberOfPlayers);

    }

    private MenuMessage(TypeOfSetupMessage typeOfSetupMessage, String errorMessage) {
        super(typeOfSetupMessage, errorMessage);
        this.lobbyName = null;
        this.myName = null;
        this.lobbyPassword = null;
        this.numberOfPlayers = 0;
        this.isPublic = false;
    }
    public static MenuMessage newMenuMessageAffirmation(TypeOfSetupMessage typeOfSetupMessage, String errorMessage){

        return new MenuMessage(typeOfSetupMessage, errorMessage);

    }




    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getLobbyPassword() {
        return lobbyPassword;
    }

    public String getMyName() {
        return myName;
    }




}
