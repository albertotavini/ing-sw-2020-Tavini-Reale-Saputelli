package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;


/**
 * this message is the one sent while the player is waiting in the lobby
 */
public class WaitingInLobbyMessage extends SetupMessage implements Serializable {

    private final String nameOfPlayer;


    private WaitingInLobbyMessage(TypeOfSetupMessage typeOfSetupMessage, String nameOfPlayer) {
        super(typeOfSetupMessage);
        this.nameOfPlayer = nameOfPlayer;
    }
    public static WaitingInLobbyMessage newWaitingInLobbyMessageStandard(TypeOfSetupMessage typeOfSetupMessage, String nameOfPlayer){
        return new WaitingInLobbyMessage(typeOfSetupMessage, nameOfPlayer);
    }

    public WaitingInLobbyMessage(String errorMessage){
        super(TypeOfSetupMessage.WAITING_IN_LOBBY_DISCONNECTED, errorMessage);
        this.nameOfPlayer = null;
    }

    public WaitingInLobbyMessage(){
        super(TypeOfSetupMessage.WAITING_IN_LOBBY_STATE_COMPLETED);
        this.nameOfPlayer = null;

    }
    public static WaitingInLobbyMessage newWaitingInLobbyMessageStateCompleted(){
        return new WaitingInLobbyMessage();
    }


    public String getNameOfPlayer() {
        return nameOfPlayer;
    }
}
