package it.polimi.ingsw.server.transmissionprotocol;


import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.SetNameMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.bothsides.utils.LogPrinter;

import java.io.*;
import java.net.Socket;

//contesto della macchina a stati
public class ServerFsm implements Runnable {

    //lo stato presente della macchina a stati
    private ServerState currentServerState;
    private Socket clientSocket;
    //la lobby assegnata al client (se ancora non ha nessuna lobby assegnata è null)
    private Lobby assignedLobby;
    //un codice univoco per ogni client
    private String uniquePlayerCode;
    //li ho creati per non dovere fare apri e chiudi ogni volta nei singoli stati
    private ObjectOutputStream socketobjectOutputStream;
    private ObjectInputStream socketobjectInputStream;


    private boolean isEverythingOk = true;


    public ServerFsm(Socket clientSocket, String uniquePlayerCode) {

        try {

            this.clientSocket = clientSocket;
            this.uniquePlayerCode = uniquePlayerCode;
            this.assignedLobby = null;
            this.socketobjectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.socketobjectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.currentServerState = new ServerSetIdentityState(this);

        }catch (Exception ex){

            LogPrinter.printOnLog(Global.FSMNOTFUNCTIONING);
            Thread.currentThread().interrupt();

        }

    }


    //gestione della macchina a stati
    void setState(ServerState nextServerState) {

        LogPrinter.printOnLog("\nFrom " +nameState(currentServerState) +" i pass to " +nameState(nextServerState) +" for the player " +ServerThread.ListIdentities.retrievePlayerIdentity(getUniquePlayerCode()).getPlayerName());
        currentServerState = nextServerState;
    }

    public void handleServerFsm() {
        currentServerState.handleServerFsm();
    }


    //getter e setter


    public boolean isEverythingOk() {
        return isEverythingOk;
    }

    public void setEverythingOkFalse() {
        isEverythingOk = false;
    }

    public void setAssignedLobby(Lobby assignedLobby) {
        this.assignedLobby = assignedLobby;
    }

    public Lobby getAssignedLobby() {
        return assignedLobby;
    }

    public String getUniquePlayerCode(){
        return this.uniquePlayerCode;
    }

    public ServerState getCurrentServerState() {
        return currentServerState;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public ObjectOutputStream getOos() {
        return socketobjectOutputStream;
    }

    public ObjectInputStream getOis() {
        return socketobjectInputStream;
    }

    @Override
    public void run() {

        do{

            this.handleServerFsm();

            //finchè non sono arrivato all'ultimo stato non mi fermo
        }while(  !(this.currentServerState instanceof ServerFinalState) );


        //executes the final state's code
        this.handleServerFsm();

    }


    @Override
    public String toString() {
        return "MenuFsmServerSingleClientHandler{" +
                "currentServerState=" + currentServerState +
                ", clientSocket=" + clientSocket +
                ", assignedLobby=" + assignedLobby +
                ", uniquePlayerCode='" + uniquePlayerCode + '\'' +
                ", SocketobjectOutputStream=" + socketobjectOutputStream +
                ", SocketobjectInputStream=" + socketobjectInputStream +
                '}';
    }


    public String nameState(ServerState serverState) {

        if(serverState instanceof ServerSetIdentityState) return Global.SERVERSETIDENTITYSTATE ;
        else if(serverState instanceof CreateOrPartecipateState) return Global.SERVERCREATEORPARTECIPATESTATE;
        else if(serverState instanceof ServerInGameState) return Global.SERVERINGAMESTATE;
        else if(serverState instanceof ServerWaitingInLobbyState) return Global.SERVERWAITINGINLOBBYSTATE;
        else if(serverState instanceof ServerFinalState) return Global.SERVERFINALSTATE ;

        else return Global.INCONSISTENTSTATE;

    }

}


interface ServerState {

    void handleServerFsm();
    void communicateWithTheClient();

}


class ServerSetIdentityState implements ServerState {

    private final ServerFsm fsmContext;

    public ServerSetIdentityState(ServerFsm fsmContext) {
        this.fsmContext = fsmContext;
    }


    private IdentityCardOfPlayer creatingPlayerIdentity(String namePlayer, Date date){
        return new IdentityCardOfPlayer(namePlayer, date, fsmContext.getUniquePlayerCode());
    }

    private void sendingSuccessMessage(TypeOfSetupMessage typeOfSetupMessage, String message) throws IOException {
        SetNameMessage successAnswer = SetNameMessage.newSetNameMessageAffirmation(typeOfSetupMessage, message);
        ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
    }

    private void sendingFailureMessage(TypeOfSetupMessage typeOfSetupMessage, String message) throws IOException {
        SetNameMessage failureAnswer = SetNameMessage.newSetNameMessageAffirmation(typeOfSetupMessage, message);
        ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
    }


    @Override
    public void handleServerFsm() {

        this.communicateWithTheClient();
        //setto il prossimo stato
        ServerState nextState = new CreateOrPartecipateState(fsmContext);
        fsmContext.setState(nextState);

    }

    @Override
    public void communicateWithTheClient() {

        boolean canContinueToCreateOrParticipate = false;


        //fase di set del nome
        while( !canContinueToCreateOrParticipate && fsmContext.isEverythingOk()){

            try {

                //ottiene il nome e la data di compleanno del giocatore
                SetNameMessage setNameMessage = (SetNameMessage) ConnectionManager.receiveStandardObject(fsmContext.getOis());

                if ( setNameMessage.typeOfSetupMessage == TypeOfSetupMessage.SET_PLAYER_NAME_AND_BIRTHDAY) {

                    //creo l'identity del giocatore, passando come parametro nome e data di nascita
                    IdentityCardOfPlayer identityCardOfPlayer = creatingPlayerIdentity(setNameMessage.getPlayerName(), setNameMessage.getDateOfBirthday());

                    //add to player list fa il controllo per vedere se esiste già un player con con quel nome e restituisce un true
                    //se il nome è stato effitivamente aggiunto
                    if (ServerThread.ListIdentities.addPlayerToListIdentities(identityCardOfPlayer)) {

                        //creo il messaggio che certifica il successo nel creare l'identità del player
                        sendingSuccessMessage(TypeOfSetupMessage.SET_NAME_STATE_COMPLETED, Global.IDENTITYSET);
                        canContinueToCreateOrParticipate = true;

                    }

                    //il nome è già stato scelto
                    else{

                        sendingFailureMessage(TypeOfSetupMessage.FAIL, Global.NAMEALREADYCHOSEN);
                        canContinueToCreateOrParticipate = false;
                    }

                }

            } catch (Exception e) {

                LogPrinter.printOnLog( Global.WHOIS +fsmContext.getUniquePlayerCode() + Global.DISCONNECTEDIN + Global.SERVERSETIDENTITYSTATE);
                LogPrinter.printOnLog(e.toString());
                ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());
                fsmContext.setEverythingOkFalse();
                Thread.currentThread().interrupt();

            }

        }

    }

}


class CreateOrPartecipateState implements ServerState {

    private final ServerFsm fsmContext;

    //serve ad evitare che si vada nello stato di waiting in lobby se la lobby arriva a riempirsi
    private boolean lobbyFull = false;

    public CreateOrPartecipateState(ServerFsm fsmContext) {
        this.fsmContext = fsmContext;
    }

    private void sendingSuccessMessage(TypeOfSetupMessage typeOfSetupMessage, String message) throws IOException {
        MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(typeOfSetupMessage, message);
        ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
    }

    private void sendingFailureMessage(TypeOfSetupMessage typeOfSetupMessage, String message) throws IOException {
        MenuMessage failureAnswer = MenuMessage.newMenuMessageAffirmation(typeOfSetupMessage, message);
        ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
    }

    private boolean executeCreateLobbyPrivate(String nameLobby, String creator, String lobbyPassword, int lobbyCapacity) throws IOException {
        PrivateLobby assignedPrivateLobby = new PrivateLobby(nameLobby, creator, lobbyPassword, lobbyCapacity, fsmContext);

        //controllo attraverso il valore di ritorno di addtolistlobby se il nome lobby è stato già scelto, poi se il player ha già creato una lobby attiva
        //utilizzo la cortocircuitazione data dall'and, invertite le due condizioni non farebbero mai entrare nel'if!!!!!! (valutare se fare due if annidati)
        if (ServerThread.playerHasNotCreatedALobby(creator) && ServerThread.ListLobbyPrivate.addToListLobbyPrivate(assignedPrivateLobby)) {

            fsmContext.setAssignedLobby(assignedPrivateLobby);

            sendingSuccessMessage(TypeOfSetupMessage.CREATE_OR_PARTICIPATE_STATE_COMPLETED, Global.PRIVATELOBBYCREATED);
            return true;

        } else {

            sendingFailureMessage(TypeOfSetupMessage.FAIL, Global.NAMEALREADYCHOSEN);
            return false;

        }

    }

    private boolean executeCreateLobbyPublic(String nameLobby, String creator, int lobbyCapacity) throws IOException {


        //uso il costruttore di lobby pubbliche
        PublicLobby assignedPublicLobby = new PublicLobby(nameLobby, creator, lobbyCapacity, fsmContext);

        //controllo se il nome è stato già scelto atraverso il return value di add to list lobby o se il player ha già creato una lobby attiva
        if (ServerThread.playerHasNotCreatedALobby(creator) && ServerThread.ListLobbyPublic.addToListLobbyPublic(assignedPublicLobby)) {

            fsmContext.setAssignedLobby(assignedPublicLobby);

            sendingSuccessMessage(TypeOfSetupMessage.CREATE_OR_PARTICIPATE_STATE_COMPLETED, Global.PUBLICLOBBYCREATED);
            return true;

        } else {

            sendingFailureMessage(TypeOfSetupMessage.FAIL, Global.NAMEALREADYCHOSEN);
            return false;

        }

    }

    private boolean executePartecipateLobbyPrivate(String nameLobby, String lobbyPassword) throws IOException {


        boolean internalCanContinue = false;

        //vede se la lobby esiste e se ha posti liberi e se la password è quella corretta
        PrivateLobby chosenLobby = ServerThread.ListLobbyPrivate.findLobbyPrivate(nameLobby);

        if (chosenLobby != null) {

            if (chosenLobby.isTheRightPassword(lobbyPassword) && chosenLobby.addFsmClientHandlerToList(fsmContext)) {

                fsmContext.setAssignedLobby(chosenLobby);

                //vedo se la lobby ha raggiunto il numero giusto di giocatori
                //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                if (chosenLobby.isLobbyNowComplete()) {
                    sendingSuccessMessage(TypeOfSetupMessage.CHOOSE_PARTECIPATE_CAN_JUMP_TO_IN_GAME_STATE, Global.COMPLETEDLOBBY);
                    lobbyFull = true;
                }

                //sono riuscito ad entrare nella lobby, ma non è ancora completa
                else {

                    sendingSuccessMessage(TypeOfSetupMessage.CREATE_OR_PARTICIPATE_STATE_COMPLETED, Global.ADDEDTOLOBBY);
                    lobbyFull = false;
                }

                internalCanContinue = true;
            }

            //la password è scorretta
            else {

                sendingFailureMessage(TypeOfSetupMessage.FAIL, Global.INCORRECTPASSWORD);
                internalCanContinue = false;

            }


        }

        //la lobby non esiste oppure è piena
        else {

            sendingFailureMessage(TypeOfSetupMessage.FAIL, Global.LOBBYNOTAVAILABLE);
            internalCanContinue = false;

        }

        return internalCanContinue;

    }

    private boolean executePartecipateLobbyPublic(String nameLobby) throws IOException {


        boolean internalCanContinue = false;

        //vede se la lobby esiste e se ha posti liberi e se la password è quella corretta
        Lobby chosenLobbyPublic = ServerThread.ListLobbyPublic.findLobbyPublic(nameLobby);

        if (chosenLobbyPublic != null && chosenLobbyPublic.addFsmClientHandlerToList(fsmContext)) {

            fsmContext.setAssignedLobby(chosenLobbyPublic);

            //vedo se la lobby ha raggiunto il numrto giusto di giocatori
            //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
            if (chosenLobbyPublic.isLobbyNowComplete()) {
                sendingSuccessMessage(TypeOfSetupMessage.CHOOSE_PARTECIPATE_CAN_JUMP_TO_IN_GAME_STATE, Global.COMPLETEDLOBBY);
                lobbyFull = true;
            } else {
                sendingSuccessMessage(TypeOfSetupMessage.CREATE_OR_PARTICIPATE_STATE_COMPLETED, Global.ADDEDTOLOBBY);
                lobbyFull = false;
            }

            internalCanContinue = true;
        }

        //la lobby non esiste oppure è piena
        else {

            sendingFailureMessage(TypeOfSetupMessage.FAIL, Global.LOBBYNOTAVAILABLE);
            internalCanContinue = false;

        }
        return internalCanContinue;

    }

    private boolean executeChooseLobbyCasual (String creator, int lobbyCapacity) throws IOException {

        boolean internalCanContinue = false;


        CasualLobby c = null;

        for (int i = 0; i < ServerThread.ListLobbyCasual.getListLobbiesCasual().size() && !internalCanContinue ; i++) {

            c = ServerThread.ListLobbyCasual.getListLobbiesCasual().get(i);

            if (c.addFsmClientHandlerToList(fsmContext)) {

                fsmContext.setAssignedLobby(c);

                //vedo se la lobby ha raggiunto il numrto giusto di giocatori
                //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                if (c.isLobbyNowComplete()) {
                    sendingSuccessMessage(TypeOfSetupMessage.CHOOSE_PARTECIPATE_CAN_JUMP_TO_IN_GAME_STATE, Global.COMPLETEDLOBBY);
                    lobbyFull = true;
                } else {

                    sendingSuccessMessage(TypeOfSetupMessage.CREATE_OR_PARTICIPATE_STATE_COMPLETED, Global.ADDEDTOLOBBY);
                    lobbyFull = false;
                }

                internalCanContinue = true;

            }
        }

        //couldn't find any lobby so it creates one
        if (!internalCanContinue ) {

            //uso il costruttore di lobby casuali
            CasualLobby assignedCasualLobby = new CasualLobby(creator, lobbyCapacity, fsmContext);

            if (ServerThread.playerHasNotCreatedALobby(creator) && ServerThread.ListLobbyCasual.addToListLobbyCasual(assignedCasualLobby)) {

                fsmContext.setAssignedLobby(assignedCasualLobby);
                sendingSuccessMessage(TypeOfSetupMessage.CREATE_OR_PARTICIPATE_STATE_COMPLETED, Global.CASUALLOBBYCREATED);
                internalCanContinue = true;
            }

        }

        return internalCanContinue;
    }

    @Override
    public void handleServerFsm() {

        this.communicateWithTheClient();

        //setto il prossimo stato

        //se la lobby non è full aspetto in lobby
        if( !lobbyFull ) fsmContext.setState(new ServerWaitingInLobbyState(fsmContext));
        else{

            fsmContext.setState(new ServerInGameState(fsmContext));
            Thread lobbyThread = new Thread(fsmContext.getAssignedLobby());
            lobbyThread.start();

        }

    }


    @Override
    public void communicateWithTheClient() {

        boolean canContinue = false;

        String nameLobby;
        String lobbyPassword;
        String creator;
        int lobbyCapacity;

        while ( !canContinue && fsmContext.isEverythingOk()){

            try {
                //ottiene la volontà del giocatore: se si vuole creare una lobby o partecipare ad una esistente
                MenuMessage menuMessage = (MenuMessage) ConnectionManager.receiveStandardObject(fsmContext.getOis());

                nameLobby = menuMessage.getLobbyName();
                lobbyPassword = menuMessage.getLobbyPassword();
                creator = menuMessage.getMyName();
                lobbyCapacity = menuMessage.getNumberOfPlayers();


                switch (menuMessage.typeOfSetupMessage) {

                    case CHOOSE_CREATE_LOBBY_PRIVATE: {

                        canContinue = executeCreateLobbyPrivate(nameLobby, creator, lobbyPassword, lobbyCapacity);

                        break;
                    }

                    case CHOOSE_CREATE_LOBBY_PUBLIC: {

                        canContinue = executeCreateLobbyPublic(nameLobby, creator, lobbyCapacity);

                        break;
                    }

                    case CHOOSE_PARTECIPATE_LOBBY_PRIVATE: {

                        canContinue = executePartecipateLobbyPrivate(nameLobby, lobbyPassword);

                        break;
                    }

                    case CHOOSE_PARTECIPATE_LOBBY_PUBLIC: {

                        canContinue = executePartecipateLobbyPublic(nameLobby);

                        break;
                    }

                    case CHOOSE_LOBBY_CASUAL: {

                        canContinue = executeChooseLobbyCasual(creator, lobbyCapacity);

                        break;
                    }

                    default:
                        LogPrinter.printOnLog(Global.INCORRECTLOBBYOPTIONS);
                        break;

                }


            }catch (Exception e) {

                LogPrinter.printOnLog(Global.WHOIS +fsmContext.getUniquePlayerCode() +Global.DISCONNECTEDIN + Global.SERVERCREATEORPARTECIPATESTATE);
                LogPrinter.printOnLog(e.toString());
                ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());

                if(fsmContext.getAssignedLobby() != null){

                    try {

                        fsmContext.getAssignedLobby().removeFsmClientHandlerFromList(ServerThread.ListIdentities.retrievePlayerIdentity(fsmContext.getUniquePlayerCode()));

                    } catch (IOException ex) {
                        LogPrinter.printOnLog(Global.COULDNOTREMOVEFROMLOBBY);
                        LogPrinter.printOnLog(e.toString());
                    }
                }

                fsmContext.setEverythingOkFalse();
                Thread.currentThread().interrupt();


            }

        }

    }

}


class ServerWaitingInLobbyState implements ServerState {

    private final ServerFsm fsmContext;

    public ServerWaitingInLobbyState(ServerFsm fsmContext) {
        this.fsmContext = fsmContext;
    }

    @Override
    public void handleServerFsm() {

        this.communicateWithTheClient();
        //setto il prossimo stato
        ServerState nextState = new ServerInGameState(fsmContext);
        fsmContext.setState(nextState);

    }

    @Override
    public void communicateWithTheClient() {

        try {
            waitInLobby();
        } catch (Exception e) {

            LogPrinter.printOnLog(Global.WHOIS +fsmContext.getUniquePlayerCode() +Global.DISCONNECTEDIN + Global.SERVERWAITINGINLOBBYSTATE);
            LogPrinter.printOnLog(e.toString());
            ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());

            if(fsmContext.getAssignedLobby() != null){

                try {

                    fsmContext.getAssignedLobby().removeFsmClientHandlerFromList(ServerThread.ListIdentities.retrievePlayerIdentity(fsmContext.getUniquePlayerCode()));

                } catch (IOException ex) {
                    LogPrinter.printOnLog(Global.COULDNOTREMOVEFROMLOBBY);
                    LogPrinter.printOnLog(e.toString());
                }
            }

            fsmContext.setEverythingOkFalse();
            Thread.currentThread().interrupt();
        }

    }


    public synchronized void waitInLobby() throws InterruptedException {

            wait();

    }
    public synchronized void notifyWaitInLobby(){

        notifyAll();

    }



}


class ServerInGameState implements ServerState {

    private final ServerFsm fsmContext;
    private final InGameConnection inGameConnection;
    private boolean isWaiting = false;

    public ServerInGameState(ServerFsm fsmContext) {
        this.fsmContext = fsmContext;
        this.inGameConnection = new InGameConnection(fsmContext.getClientSocket(), fsmContext.getUniquePlayerCode(), fsmContext.getOos(), fsmContext.getOis(), fsmContext);
    }

    public InGameConnection getInGameConnection() {
        return inGameConnection;
    }


    @Override
    public void handleServerFsm() {

        this.communicateWithTheClient();
        //setto il prossimo stato
        fsmContext.setState(new ServerFinalState());


    }

    @Override
    public void communicateWithTheClient() {

        Thread inGameConnectionThread = new Thread(inGameConnection);

            try {

                LogPrinter.printOnLog(Global.WAITINGINGAMESTATE +ServerThread.ListIdentities.retrievePlayerName(fsmContext.getUniquePlayerCode()));

                waitInGame();


            } catch(Exception e)
            {

                LogPrinter.printOnLog(Global.WHOIS +fsmContext.getUniquePlayerCode() +Global.DISCONNECTEDIN+ Global.SERVERINGAMESTATE);
                LogPrinter.printOnLog(e.toString());
                ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());

                if(fsmContext.getAssignedLobby() != null){

                    try {

                        fsmContext.getAssignedLobby().removeFsmClientHandlerFromList(ServerThread.ListIdentities.retrievePlayerIdentity(fsmContext.getUniquePlayerCode()));
                        fsmContext.getAssignedLobby().killLobby();

                    } catch (IOException ex) {
                        LogPrinter.printOnLog(Global.COULDNOTREMOVEFROMLOBBY);
                        LogPrinter.printOnLog(e.toString());
                    }
                }

                fsmContext.setEverythingOkFalse();
                Thread.currentThread().interrupt();
            }

            try {

                inGameConnectionThread.start();

                inGameConnectionThread.join();

            }catch(Exception ex ){


                try {

                    fsmContext.getAssignedLobby().killLobby();

                } catch (IOException e) {
                    LogPrinter.printOnLog(Global.FSMDIDNOTKILLLOBBY);
                    LogPrinter.printOnLog(e.toString());
                }


            }finally {

                try {

                    fsmContext.getAssignedLobby().killLobby();

                } catch (IOException e) {
                    LogPrinter.printOnLog(Global.FSMDIDNOTKILLLOBBY);
                    LogPrinter.printOnLog(e.toString());
                }

            }


    }


    public synchronized void waitInGame() throws InterruptedException {

        isWaiting = true;
        wait();

    }

    public synchronized void notifyInGameState() {

        isWaiting = false;
        notifyAll();


    }

    public boolean isWaiting() {
        return isWaiting;
    }
}


class ServerFinalState implements ServerState {
    @Override
    public void handleServerFsm() {
        //method needs to be finished

    }

    @Override
    public void communicateWithTheClient() {
        //method needs to be finished
    }
}