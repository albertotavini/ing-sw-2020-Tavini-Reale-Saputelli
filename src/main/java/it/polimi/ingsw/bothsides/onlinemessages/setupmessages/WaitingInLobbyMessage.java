package it.polimi.ingsw.bothsides.onlinemessages.setupmessages;

import java.io.Serializable;

//messaggio che si invia nello stato in cui si attende in lobby
public class WaitingInLobbyMessage extends SetupMessage implements Serializable {

    //nome del player che si è appena connesso o disconnesso
    private final String nameOfPlayer;


    //costruttore per messaggi del tipoWaitingInLobbyPlayerJoined, WaitingInLobbyPlayerDisconnected
    private WaitingInLobbyMessage(TypeOfSetupMessage typeOfSetupMessage, String nameOfPlayer) {
        super(typeOfSetupMessage);
        this.nameOfPlayer = nameOfPlayer;
    }
    public static WaitingInLobbyMessage newWaitingInLobbyMessageStandard(TypeOfSetupMessage typeOfSetupMessage, String nameOfPlayer){
        return new WaitingInLobbyMessage(typeOfSetupMessage, nameOfPlayer);
    }

    public WaitingInLobbyMessage(String errorMessage){
        super(TypeOfSetupMessage.WaitingInLobbyDisconnected, errorMessage);
        this.nameOfPlayer = null;
    }

    //costruttore per inviarwe messaggio di state completed
    public WaitingInLobbyMessage(){
        super(TypeOfSetupMessage.WaitingInLobbyStateCompleted);
        this.nameOfPlayer = null;

    }
    public static WaitingInLobbyMessage newWaitingInLobbyMessageStateCompleted(){
        return new WaitingInLobbyMessage();
    }


    public String getNameOfPlayer() {
        return nameOfPlayer;
    }
}
