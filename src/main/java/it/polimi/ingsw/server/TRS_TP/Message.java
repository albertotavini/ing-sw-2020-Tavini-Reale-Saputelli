package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.model.Date;

import java.io.Serializable;


abstract class Message implements Serializable {


    public final TypeOfMessage typeOfMessage;
    public final String errorMessage;

    //costruttore per messaggi con errorText
    public Message(TypeOfMessage typeOfMessage, String errorMessage) {
        this.typeOfMessage = typeOfMessage;

        this.errorMessage = errorMessage;
    }

    public Message(TypeOfMessage typeOfMessage) {

        this.errorMessage = null;
        this.typeOfMessage = typeOfMessage;

    }

}


//la relazione tra messaggio scambiato e stato è univoca, un tipo di messaggio per ogni stato, il resto lo fanno i costruttori particolari


//messaggio che si invia nel primo stato
class SetNameMessage extends Message implements Serializable {

    private final String playerName;
    private final Date dateOfBirthday;

    //costruttore completo
    public SetNameMessage(String playerName, Date dateOfBirthday){
        super(TypeOfMessage.SetPlayerNameAndBirthday);
        this.playerName = playerName;
        this.dateOfBirthday = dateOfBirthday;

    }


    //costruttore per la risposta positiva o negativa
    public SetNameMessage(TypeOfMessage typeOfMessage, String errorMessage){
        super(typeOfMessage, errorMessage);
        this.playerName = null;
        this.dateOfBirthday = null;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Date getDateOfBirthday() {
        return dateOfBirthday;
    }
}

//messaggio che si invia nello stato in cui si costruisce una lobby o si partecipa
class MenuMessages extends Message implements Serializable {

    //da modificare il costruttore quando l'input è sbagliato


    private final String lobbyName;
    private final int numberOfPlayers;
    private final String lobbyPassword;
    private final String myName;
    private final boolean isPublic;

    //costruttore standard per messaggi di create lobby private
    public MenuMessages(String lobbyName, int numberOfPlayers, String lobbyPassword, String myName) {

        super(TypeOfMessage.ChooseCreateLobbyPrivate);
        this.lobbyName = lobbyName;
        this.lobbyPassword = lobbyPassword;
        this.myName = myName;
        this.numberOfPlayers = numberOfPlayers;
        this.isPublic = false;
    }

    //costruttore standard per messaggi di create lobby pubbliche
    public MenuMessages(String lobbyName, int numberOfPlayers, String myName) {

        super(TypeOfMessage.ChooseCreateLobbyPublic);
        this.lobbyName = lobbyName;
        this.lobbyPassword = null;
        this.myName = myName;
        this.numberOfPlayers = numberOfPlayers;
        this.isPublic = true;
    }

    //costruttore standard per messaggi di participate con lobby privata
    public MenuMessages(String lobbyName, String lobbyPassword, String myName) {

        super(TypeOfMessage.ChoosePartecipateLobbyPrivate);
        this.lobbyName = lobbyName;
        this.lobbyPassword = lobbyPassword;
        this.myName = myName;
        this.numberOfPlayers = 0;
        this.isPublic = false;

    }

    //costruttore standard per messaggi di participate con lobby pubblica
    public MenuMessages(String lobbyName, String myName) {

        super(TypeOfMessage.ChoosePartecipateLobbyPublic);
        this.lobbyName = lobbyName;
        this.lobbyPassword = null;
        this.myName = myName;
        this.numberOfPlayers = 0;
        this.isPublic = false;

    }

    //costruttore semplice per comunicazioni tipo failure, success....
    public MenuMessages(TypeOfMessage typeOfMessage, String errorMessage) {
        super(typeOfMessage, errorMessage);
        this.lobbyName = null;
        this.myName = null;
        this.lobbyPassword = null;
        this.numberOfPlayers = 0;
        this.isPublic = false;
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

//messaggio che si invia nello stato in cui si attende in lobby
class WaitingInLobbyMessages extends Message implements Serializable {

    //nome del player che si è appena connesso o disconnesso
    private final String nameOfPlayer;


    //costruttore per messaggi del tipoWaitingInLobbyPlayerJoined, WaitingInLobbyPlayerDisconnected
    public WaitingInLobbyMessages(TypeOfMessage typeOfMessage, String nameOfPlayer) {
        super(typeOfMessage);
        this.nameOfPlayer = nameOfPlayer;
    }

    public WaitingInLobbyMessages(String erroMessage){
        super(TypeOfMessage.WaitingInLobbyDisconnected, erroMessage);
        this.nameOfPlayer = null;
    }

    //costruttore per inviarwe messaggio di state completed
    public WaitingInLobbyMessages(){
        super(TypeOfMessage.WaitingInLobbyStateCompleted);
        this.nameOfPlayer = null;

    }


    public String getNameOfPlayer() {
        return nameOfPlayer;
    }
}




