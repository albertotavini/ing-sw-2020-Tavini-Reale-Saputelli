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
    private SetNameMessage(String playerName, Date dateOfBirthday){
        super(TypeOfMessage.SetPlayerNameAndBirthday);
        this.playerName = playerName;
        this.dateOfBirthday = dateOfBirthday;

    }
    public static SetNameMessage newSetNameMessageComplete(String playerName, Date dateOfBirthday) {

        return new SetNameMessage(playerName, dateOfBirthday);
    }

    //costruttore per la risposta positiva o negativa
    private SetNameMessage(TypeOfMessage typeOfMessage, String errorMessage) {
        super(typeOfMessage, errorMessage);
        this.playerName = null;
        this.dateOfBirthday = null;
    }
    public static SetNameMessage newSetNameMessageAffirmation(TypeOfMessage typeOfMessage, String errorMessage) {

        return new SetNameMessage(typeOfMessage, errorMessage);
    }


    public String getPlayerName() {
        return playerName;
    }
    public Date getDateOfBirthday() {
        return dateOfBirthday;
    }
}

//messaggio che si invia nello stato in cui si costruisce una lobby o si partecipa
class MenuMessage extends Message implements Serializable {

    //da modificare il costruttore quando l'input è sbagliato


    private final String lobbyName;
    private final int numberOfPlayers;
    private final String lobbyPassword;
    private final String myName;
    private final boolean isPublic;

    //costruttore standard per messaggi di create lobby private
    private MenuMessage(String lobbyName, int numberOfPlayers, String lobbyPassword, String myName) {

        super(TypeOfMessage.ChooseCreateLobbyPrivate);
        this.lobbyName = lobbyName;
        this.lobbyPassword = lobbyPassword;
        this.myName = myName;
        this.numberOfPlayers = numberOfPlayers;
        this.isPublic = false;
    }
    public static MenuMessage newMenuMessageCreatePrivate(String lobbyName, int numberOfPlayers, String lobbyPassword, String myName){

        return new MenuMessage(lobbyName, numberOfPlayers, lobbyPassword, myName);
    }

    //costruttore standard per messaggi di create lobby pubbliche
    private MenuMessage(String lobbyName, int numberOfPlayers, String myName) {

        super(TypeOfMessage.ChooseCreateLobbyPublic);
        this.lobbyName = lobbyName;
        this.lobbyPassword = null;
        this.myName = myName;
        this.numberOfPlayers = numberOfPlayers;
        this.isPublic = true;
    }
    public static MenuMessage newMenuMessageCreatePublic(String lobbyName, int numberOfPlayers, String myName){

        return new MenuMessage(lobbyName, numberOfPlayers, myName);
    }

    //costruttore standard per messaggi di participate con lobby privata
    private MenuMessage(String lobbyName, String lobbyPassword, String myName) {

        super(TypeOfMessage.ChoosePartecipateLobbyPrivate);
        this.lobbyName = lobbyName;
        this.lobbyPassword = lobbyPassword;
        this.myName = myName;
        this.numberOfPlayers = 0;
        this.isPublic = false;

    }
    public static MenuMessage newMenuMessagePartPrivate(String lobbyName, String lobbyPassword, String myName){

        return new MenuMessage(lobbyName, lobbyPassword, myName);

    }


    //costruttore standard per messaggi di participate con lobby pubblica
    private MenuMessage(String lobbyName, String myName) {

        super(TypeOfMessage.ChoosePartecipateLobbyPublic);
        this.lobbyName = lobbyName;
        this.lobbyPassword = null;
        this.myName = myName;
        this.numberOfPlayers = 0;
        this.isPublic = false;

    }
    public static MenuMessage newMenuMessagePartPublic(String lobbyName, String myName){

        return new MenuMessage(lobbyName, myName);

    }


    //costruttore semplice per comunicazioni tipo failure, success....
    private MenuMessage(TypeOfMessage typeOfMessage, String errorMessage) {
        super(typeOfMessage, errorMessage);
        this.lobbyName = null;
        this.myName = null;
        this.lobbyPassword = null;
        this.numberOfPlayers = 0;
        this.isPublic = false;
    }
    public static MenuMessage newMenuMessageAffirmation(TypeOfMessage typeOfMessage, String errorMessage){

        return new MenuMessage(typeOfMessage, errorMessage);

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
class WaitingInLobbyMessage extends Message implements Serializable {

    //nome del player che si è appena connesso o disconnesso
    private final String nameOfPlayer;


    //costruttore per messaggi del tipoWaitingInLobbyPlayerJoined, WaitingInLobbyPlayerDisconnected
    private WaitingInLobbyMessage(TypeOfMessage typeOfMessage, String nameOfPlayer) {
        super(typeOfMessage);
        this.nameOfPlayer = nameOfPlayer;
    }
    public static WaitingInLobbyMessage newWaitingInLobbyMessageStandard(TypeOfMessage typeOfMessage, String nameOfPlayer){
        return new WaitingInLobbyMessage(typeOfMessage, nameOfPlayer);
    }

    public WaitingInLobbyMessage(String errorMessage){
        super(TypeOfMessage.WaitingInLobbyDisconnected, errorMessage);
        this.nameOfPlayer = null;
    }

    //costruttore per inviarwe messaggio di state completed
    public WaitingInLobbyMessage(){
        super(TypeOfMessage.WaitingInLobbyStateCompleted);
        this.nameOfPlayer = null;

    }
    public static WaitingInLobbyMessage newWaitingInLobbyMessageStateCompleted(){
        return new WaitingInLobbyMessage();
    }


    public String getNameOfPlayer() {
        return nameOfPlayer;
    }
}

//messaggio che si invia e si riceve in maniera asincrona rispetto agli altri
class PingAndErrorMessage extends Message implements Serializable {

    public PingAndErrorMessage(TypeOfMessage typeOfMessage, String errorMessage) {
        super(typeOfMessage, errorMessage);
    }

    public static PingAndErrorMessage newPingAndErrorMessageStandard(TypeOfMessage typeOfMessage, String errorMessage){

        return new PingAndErrorMessage(typeOfMessage, errorMessage);
    }
}




