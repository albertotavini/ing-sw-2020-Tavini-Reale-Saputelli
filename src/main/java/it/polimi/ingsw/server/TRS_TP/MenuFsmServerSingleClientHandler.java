package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.server.utils.LogPrinter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//contesto della macchina a stati
public class MenuFsmServerSingleClientHandler implements Runnable {

    //lo stato presente della macchina a stati
    private ServerState currentServerState;
    private Socket clientSocket;
    //la lobby assegnata al client (se ancora non ha nessuna lobby assegnata è null)
    private Lobby assignedLobby;
    //un codice univoco per ogni client
    private String uniquePlayerCode;
    //li ho creati per non dovere fare apri e chiudi ogni volta nei singoli stati
    private ObjectOutputStream SocketobjectOutputStream;
    private ObjectInputStream SocketobjectInputStream;


    private boolean isEverythingOk = true;


    public MenuFsmServerSingleClientHandler(Socket clientSocket, String uniquePlayerCode) {

        try {

            this.clientSocket = clientSocket;
            this.uniquePlayerCode = uniquePlayerCode;
            this.assignedLobby = null;
            this.SocketobjectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.SocketobjectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.currentServerState = new ServerSetIdentityState(this);

        }catch (Exception ex){

            LogPrinter.printOnLog("----FsmServer couldn't start----");
            Thread.currentThread().interrupt();

        }

    }


    //gestione della macchina a stati
    void setState(ServerState nextServerState) {

        LogPrinter.printOnLog("\nDa " +nameState(currentServerState) +" passo a " +nameState(nextServerState) +" per " +ServerThread.ListIdentities.retrievePlayerIdentity(getUniquePlayerCode()).getPlayerName());
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
        return SocketobjectOutputStream;
    }

    public ObjectInputStream getOis() {
        return SocketobjectInputStream;
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
                ", SocketobjectOutputStream=" + SocketobjectOutputStream +
                ", SocketobjectInputStream=" + SocketobjectInputStream +
                '}';
    }


    public String nameState(ServerState serverState) {

        if(serverState instanceof ServerSetIdentityState) return "ServerSetIdentityState";
        if(serverState instanceof CreateOrPartecipateState) return "CreateOrPartecipateState";
        if(serverState instanceof ServerInGameState) return "ServerInGameState";
        if(serverState instanceof ServerWaitingInLobbyState) return "ServerWaitingInLobbyState";
        if(serverState instanceof ServerFinalState) return "ServerFinalState";

        else return "Wrong state: ERRORR";

    }

}


interface ServerState {

    void handleServerFsm();
    void communicateWithTheClient();

}


class ServerSetIdentityState implements ServerState {

    private final MenuFsmServerSingleClientHandler fsmContext;

    public ServerSetIdentityState(MenuFsmServerSingleClientHandler fsmContext) {
        this.fsmContext = fsmContext;
    }


    private IdentityCardOfPlayer creatingPlayerIdentity(String namePlayer, Date date){
        return new IdentityCardOfPlayer(namePlayer, date, fsmContext.getUniquePlayerCode());
    }

    private void sendingSuccessMessage(TypeOfMessage typeOfMessage, String message) throws IOException {
        SetNameMessage successAnswer = SetNameMessage.newSetNameMessageAffirmation(typeOfMessage, message);
        ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
    }

    private void sendingFailureMessage(TypeOfMessage typeOfMessage, String message) throws IOException {
        SetNameMessage failureAnswer = SetNameMessage.newSetNameMessageAffirmation(typeOfMessage, message);
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
                SetNameMessage setNameMessage = (SetNameMessage) ConnectionManager.receiveObject(fsmContext.getOis());

                if ( setNameMessage.typeOfMessage == TypeOfMessage.SetPlayerNameAndBirthday ) {

                    //creo l'identity del giocatore, passando come parametro nome e data di nascita
                    IdentityCardOfPlayer identityCardOfPlayer = creatingPlayerIdentity(setNameMessage.getPlayerName(), setNameMessage.getDateOfBirthday());

                    //add to player list fa il controllo per vedere se esiste già un player con con quel nome e restituisce un true
                    //se il nome è stato effitivamente aggiunto
                    if (ServerThread.ListIdentities.addPlayerToListIdentities(identityCardOfPlayer)) {

                        //creo il messaggio che certifica il successo nel creare l'identità del player
                        sendingSuccessMessage(TypeOfMessage.SetNameStateCompleted, "Ho aggiunto con successo l'identità");
                        canContinueToCreateOrParticipate = true;

                    }

                    //il nome è già stato scelto
                    else{

                        sendingFailureMessage(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile avere due player con lo stesso nome");
                        canContinueToCreateOrParticipate = false;
                    }

                }

            } catch (Exception e) {

                LogPrinter.printOnLog("\n----Player " +fsmContext.getUniquePlayerCode() +" has disconnected in SetIdentity state----");
                LogPrinter.printOnLog("\n" +e.toString());
                ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());
                fsmContext.setEverythingOkFalse();
                Thread.currentThread().interrupt();

            }

        }

    }

}


class CreateOrPartecipateState implements ServerState {

    private final MenuFsmServerSingleClientHandler fsmContext;

    //serve ad evitare che si vada nello stato di waiting in lobby se la lobby arriva a riempirsi
    private boolean lobbyFull = false;

    public CreateOrPartecipateState(MenuFsmServerSingleClientHandler fsmContext) {
        this.fsmContext = fsmContext;
    }

    private void sendingSuccessMessage(TypeOfMessage typeOfMessage, String message) throws IOException {
        MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(typeOfMessage, message);
        ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
    }

    private void sendingFailureMessage(TypeOfMessage typeOfMessage, String message) throws IOException {
        MenuMessage failureAnswer = MenuMessage.newMenuMessageAffirmation(typeOfMessage, message);
        ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
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
                MenuMessage menuMessage = (MenuMessage) ConnectionManager.receiveObject(fsmContext.getOis());

                nameLobby = menuMessage.getLobbyName();
                lobbyPassword = menuMessage.getLobbyPassword();
                creator = menuMessage.getMyName();
                lobbyCapacity = menuMessage.getNumberOfPlayers();


                switch (menuMessage.typeOfMessage) {

                    case ChooseCreateLobbyPrivate: {

                        PrivateLobby assignedPrivateLobby = new PrivateLobby(nameLobby, creator, lobbyPassword, lobbyCapacity, fsmContext);

                        //controllo attraverso il valore di ritorno di addtolistlobby se il nome lobby è stato già scelto, poi se il player ha già creato una lobby attiva
                        //utilizzo la cortocircuitazione data dall'and, invertite le due condizioni non farebbero mai entrare nel'if!!!!!! (valutare se fare due if annidati)
                        if (ServerThread.playerHasNotCreatedALobby(creator) && ServerThread.ListLobbyPrivate.addToListLobbyPrivate(assignedPrivateLobby)) {

                            fsmContext.setAssignedLobby(assignedPrivateLobby);

                            sendingSuccessMessage(TypeOfMessage.CreateOrParticipateStateCompleted, "Ho creato con successo la lobby privata");
                            canContinue = true;

                        } else {

                            sendingFailureMessage(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile creare due lobby con lo stesso nome");
                            canContinue = false;

                        }

                        break;

                    }


                    case ChooseCreateLobbyPublic: {

                        //uso il costruttore di lobby pubbliche
                        PublicLobby assignedPublicLobby = new PublicLobby(nameLobby, creator, lobbyCapacity, fsmContext);

                        //controllo se il nome è stato già scelto atraverso il return value di add to list lobby o se il player ha già creato una lobby attiva
                        if (ServerThread.playerHasNotCreatedALobby(creator) && ServerThread.ListLobbyPublic.addToListLobbyPublic(assignedPublicLobby)) {

                            fsmContext.setAssignedLobby(assignedPublicLobby);

                            sendingSuccessMessage(TypeOfMessage.CreateOrParticipateStateCompleted, "Ho creato con successo la lobby pubblica");
                            canContinue = true;

                        } else {

                            sendingFailureMessage(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile creare due lobby con lo stesso nome");
                            canContinue = false;

                        }

                        break;
                    }


                    case ChoosePartecipateLobbyPrivate: {

                        //vede se la lobby esiste e se ha posti liberi e se la password è quella corretta
                        PrivateLobby chosenLobby = ServerThread.ListLobbyPrivate.findLobbyPrivate(nameLobby);

                        if (chosenLobby != null) {

                            if (chosenLobby.isTheRightPassword(lobbyPassword) && chosenLobby.addFsmClientHandlerToList(fsmContext)) {

                                fsmContext.setAssignedLobby(chosenLobby);

                                //vedo se la lobby ha raggiunto il numero giusto di giocatori
                                //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                                if (chosenLobby.isLobbyNowComplete()) {
                                    sendingSuccessMessage(TypeOfMessage.ChoosePartecipateCanJumpToInGameState, "Hai completato la lobby");
                                    lobbyFull = true;
                                }

                                //sono riuscito ad entrare nella lobby, ma non è ancora completa
                                else {

                                    sendingSuccessMessage(TypeOfMessage.CreateOrParticipateStateCompleted, "Sei stato aggiunto con successo alla lobby");
                                    lobbyFull = false;
                                }

                                canContinue = true;
                            }

                            //la password è scorretta
                            else {

                                sendingFailureMessage(TypeOfMessage.Fail, "La password inserita per la lobby non è corretta");
                                canContinue = false;

                            }


                        }

                        //la lobby non esiste oppure è piena
                        else {

                            sendingFailureMessage(TypeOfMessage.Fail, "La lobby non esiste oppure è piena");
                            canContinue = false;

                        }

                        break;

                    }


                    case ChoosePartecipateLobbyPublic: {

                        //vede se la lobby esiste e se ha posti liberi e se la password è quella corretta
                        Lobby chosenLobbyPublic = ServerThread.ListLobbyPublic.findLobbyPublic(nameLobby);

                        if (chosenLobbyPublic != null && chosenLobbyPublic.addFsmClientHandlerToList(fsmContext)) {

                            fsmContext.setAssignedLobby(chosenLobbyPublic);

                            //vedo se la lobby ha raggiunto il numrto giusto di giocatori
                            //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                            if (chosenLobbyPublic.isLobbyNowComplete()) {
                                sendingSuccessMessage(TypeOfMessage.ChoosePartecipateCanJumpToInGameState, "Hai completato la lobby, il gioco può partire");
                                lobbyFull = true;
                            } else {
                                sendingSuccessMessage(TypeOfMessage.CreateOrParticipateStateCompleted, "Sei stato aggiunto con successo alla lobby");
                                lobbyFull = false;
                            }

                            canContinue = true;
                        }

                        //la lobby non esiste oppure è piena
                        else {

                            sendingFailureMessage(TypeOfMessage.Fail, "La lobby pubblica non esiste oppure è piena");
                            canContinue = false;

                        }

                        break;
                    }


                    case ChooseLobbyCasual: {

                        CasualLobby c = null;

                        for (int i = 0; i < ServerThread.ListLobbyCasual.getListLobbiesCasual().size() && canContinue == false; i++) {

                            c = ServerThread.ListLobbyCasual.getListLobbiesCasual().get(i);

                            if (c.addFsmClientHandlerToList(fsmContext)) {

                                fsmContext.setAssignedLobby(c);

                                //vedo se la lobby ha raggiunto il numrto giusto di giocatori
                                //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                                if (c.isLobbyNowComplete()) {
                                    sendingSuccessMessage(TypeOfMessage.ChoosePartecipateCanJumpToInGameState, "Hai completato la lobby, il gioco può partire");
                                    lobbyFull = true;
                                } else {

                                    sendingSuccessMessage(TypeOfMessage.CreateOrParticipateStateCompleted, "Sei stato aggiunto con successo alla lobby");
                                    lobbyFull = false;
                                }

                                canContinue = true;

                            }
                        }

                        //couldn't find any lobby so it creates one
                        if (canContinue == false) {

                            //uso il costruttore di lobby casuali
                            CasualLobby assignedCasualLobby = new CasualLobby(creator, lobbyCapacity, fsmContext);

                            if (ServerThread.playerHasNotCreatedALobby(creator) && ServerThread.ListLobbyCasual.addToListLobbyCasual(assignedCasualLobby)) {

                                fsmContext.setAssignedLobby(assignedCasualLobby);
                                sendingSuccessMessage(TypeOfMessage.CreateOrParticipateStateCompleted, "Ho creato con successo la lobby casual");
                                canContinue = true;
                            }

                        }

                        break;
                    }

                }


            }catch (Exception e) {

                LogPrinter.printOnLog("\n----Player " +fsmContext.getUniquePlayerCode() +" has disconnected in CreateOrPartecipate state----");
                LogPrinter.printOnLog("\n" +e.toString());
                ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());

                if(fsmContext.getAssignedLobby() != null){

                    try {

                        fsmContext.getAssignedLobby().removeFsmClientHandlerFromList(ServerThread.ListIdentities.retrievePlayerIdentity(fsmContext.getUniquePlayerCode()));

                    } catch (IOException ex) {
                        LogPrinter.printOnLog("\n----Couldn't remove player from assigned lobby----");
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

    private final MenuFsmServerSingleClientHandler fsmContext;

    public ServerWaitingInLobbyState(MenuFsmServerSingleClientHandler fsmContext) {
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

            LogPrinter.printOnLog("\n----Player " +fsmContext.getUniquePlayerCode() +" has disconnected in WaitingInLobby state----");
            LogPrinter.printOnLog("\n" +e.toString());
            ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());

            if(fsmContext.getAssignedLobby() != null){

                try {

                    fsmContext.getAssignedLobby().removeFsmClientHandlerFromList(ServerThread.ListIdentities.retrievePlayerIdentity(fsmContext.getUniquePlayerCode()));

                } catch (IOException ex) {
                    LogPrinter.printOnLog("\n----Couldn't remove player from assigned lobby----");
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

    private final MenuFsmServerSingleClientHandler fsmContext;
    private final InGameConnection inGameConnection;
    private boolean isWaiting = false;

    public ServerInGameState(MenuFsmServerSingleClientHandler fsmContext) {
        this.fsmContext = fsmContext;
        this.inGameConnection = new InGameConnection(fsmContext.getClientSocket(), fsmContext.getUniquePlayerCode(), fsmContext.getOos(), fsmContext.getOis());
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

                LogPrinter.printOnLog("\nSono in game state e sto aspettando sono: " +ServerThread.ListIdentities.retrievePlayerName(fsmContext.getUniquePlayerCode()));

                waitInGame();


            } catch(Exception e)
            {

                LogPrinter.printOnLog("\n----Player " +fsmContext.getUniquePlayerCode() +" has disconnected in InGame state----");
                LogPrinter.printOnLog("\n" +e.toString());
                ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());

                if(fsmContext.getAssignedLobby() != null){

                    try {

                        fsmContext.getAssignedLobby().removeFsmClientHandlerFromList(ServerThread.ListIdentities.retrievePlayerIdentity(fsmContext.getUniquePlayerCode()));
                        fsmContext.getAssignedLobby().killLobby();

                    } catch (IOException ex) {
                        LogPrinter.printOnLog("\n----Couldn't remove player from assigned lobby----");
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
                    e.printStackTrace();
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

    }

    @Override
    public void communicateWithTheClient() {

    }
}