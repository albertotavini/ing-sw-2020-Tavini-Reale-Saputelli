package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.server.utils.ColorAnsi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

//contesto della macchina a stati
public class MenuFsmServerSingleClientHandler implements Runnable {

    //lo stato presente della macchina a stati
    private ServerState currentServerState;
    private final Socket clientSocket;
    //la lobby assegnata al client (se ancora non ha nessuna lobby assegnata è null)
    private Lobby assignedLobby;
    //un codice univoco per ogni client
    private final String uniquePlayerCode;
    //li ho creati per non dovere fare apri e chiudi ogni volta nei singoli stati
    final ObjectOutputStream SocketobjectOutputStream;
    final ObjectInputStream SocketobjectInputStream;


    public MenuFsmServerSingleClientHandler(Socket clientSocket, String uniquePlayerCode) throws IOException {
        this.clientSocket = clientSocket;
        this.uniquePlayerCode = uniquePlayerCode;
        this.assignedLobby = null;
        this.SocketobjectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        this.SocketobjectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        this.currentServerState = new ServerSetIdentityState(this);
    }


    //gestione della macchina a stati
    void setState(ServerState nextServerState) {
        System.out.println("Da " +nameState(currentServerState) +" passo a " +nameState(nextServerState) +" per " +ColorAnsi.RED +ServerThread.ListIdentities.retrievePlayerIdentity(getUniquePlayerCode()).getPlayerName() +ColorAnsi.RESET);
        currentServerState = nextServerState;
    }

    public void handleServerFsm() {
        currentServerState.handleServerFsm();
    }


    //getter e setter
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



        //altre cose da vedere....



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

        if(serverState instanceof ServerSetIdentityState) return ColorAnsi.YELLOW +"ServerSetIdentityState" +ColorAnsi.RESET;
        if(serverState instanceof CreateOrPartecipateState) return ColorAnsi.YELLOW +"CreateOrPartecipateState" +ColorAnsi.RESET;
        if(serverState instanceof ServerInGameState) return ColorAnsi.YELLOW +"ServerInGameState" +ColorAnsi.RESET;
        if(serverState instanceof ServerWaitingInLobbyState) return ColorAnsi.YELLOW +"ServerWaitingInLobbyState" +ColorAnsi.RESET;
        if(serverState instanceof ServerFinalState) return ColorAnsi.YELLOW +"ServerFinalState" +ColorAnsi.RESET;

        else return ColorAnsi.YELLOW +"Wrong state: ERRORR" +ColorAnsi.RESET;

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
        while(!canContinueToCreateOrParticipate) {

            try {

                //ottiene il nome e la data di compleanno del giocatore
                SetNameMessage setNameMessage = (SetNameMessage) ConnectionManager.receiveObject(fsmContext.getOis());

                if (setNameMessage.typeOfMessage.equals(TypeOfMessage.SetPlayerNameAndBirthday)) {

                    //creo l'identity del giocatore
                    String namePlayer = setNameMessage.getPlayerName();
                    Date date = setNameMessage.getDateOfBirthday();
                    IdentityCardOfPlayer identityCardOfPlayer = new IdentityCardOfPlayer(namePlayer, date, fsmContext.getUniquePlayerCode());

                    //add to player list fa il controllo per vedere se esiste già un player con con quel nome e restituisce un true
                    //se il nome è stato effitivamente aggiunto
                    if (ServerThread.ListIdentities.addPlayerToListIdentities(identityCardOfPlayer)) {

                        //creo il messaggio che certifica il successo nel creare l'identità del player
                        SetNameMessage successAnswer = SetNameMessage.newSetNameMessageAffirmation(TypeOfMessage.SetNameStateCompleted, "Ho aggiunto con successo l'identità");
                        ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                        canContinueToCreateOrParticipate = true;
                    }

                    //il nome è già stato scelto
                    else {

                        SetNameMessage failureAnswer = SetNameMessage.newSetNameMessageAffirmation(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile avere due player con lo stesso nome");
                        ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
                        canContinueToCreateOrParticipate = false;


                    }


                }


            }catch(SocketException ex){

                ServerThread.serverExecutor.shutdown();


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            ServerThread.serverExecutor.shutdown();}


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


        while (!canContinue) {

            try {

                //ottiene la volontà del giocatore: se si vuole creare una lobby o partecipare ad una esistente
                MenuMessage menuMessage = (MenuMessage) ConnectionManager.receiveObject(fsmContext.getOis());

                String nameLobbyCasual = null;
                String lobbyPassword = null;
                String clientName = null;
                int lobbyCapacity;


                switch (menuMessage.typeOfMessage) {

                    case ChooseCreateLobbyPrivate: {

                        lobbyCapacity = menuMessage.getNumberOfPlayers();
                        nameLobbyCasual = menuMessage.getLobbyName();
                        lobbyPassword = menuMessage.getLobbyPassword();
                        String creator = menuMessage.getMyName();

                        PrivateLobby assignedPrivateLobby = new PrivateLobby(nameLobbyCasual, creator, lobbyPassword, lobbyCapacity, fsmContext);

                        //controllo attraverso il valore di ritorno di addtolistlobby se il nome lobby è stato già scelto, poi se il player ha già creato una lobby attiva
                        //utilizzo la cortocircuitazione data dall'and, invertite le due condizioni non farebbero mai entrare nel'if!!!!!! (valutare se fare due if annidati)
                        if (ServerThread.hasPlayerAlreadyCreatedALobby(creator) && ServerThread.ListLobbyPrivate.addToListLobbyPrivate(assignedPrivateLobby)) {

                            //gli passo la socket del creatore e lui nel costruttore di lobby l'aggiunge alla socketlist nella lobby
                            fsmContext.setAssignedLobby(assignedPrivateLobby);
                            ServerThread.ListLobbyPrivate.addToListLobbyPrivate(assignedPrivateLobby);
                            MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.CreateOrParticipateStateCompleted, "Ho creato con successo la lobby");
                            ConnectionManager.sendObject(successAnswer, fsmContext.getOos());

                            canContinue = true;

                        } else {

                            MenuMessage failureAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile creare due lobby con lo stesso nome");
                            ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
                            canContinue = false;

                        }

                        break;}


                    case ChooseCreateLobbyPublic: {

                        lobbyCapacity = menuMessage.getNumberOfPlayers();
                        nameLobbyCasual = menuMessage.getLobbyName();
                        String creatorPublic = menuMessage.getMyName();
                        //uso il costruttore di lobby pubbliche
                        PublicLobby assignedPublicLobby = new PublicLobby(nameLobbyCasual, creatorPublic, lobbyCapacity, fsmContext);

                        //controllo se il nome è stato già scelto atraverso il return value di add to list lobby o se il player ha già creato una lobby attiva
                        if (ServerThread.hasPlayerAlreadyCreatedALobby(creatorPublic) && ServerThread.ListLobbyPublic.addToListLobbyPublic(assignedPublicLobby)) {

                            fsmContext.setAssignedLobby(assignedPublicLobby);
                            MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.CreateOrParticipateStateCompleted, "Ho creato con successo la lobby pubblica");

                            ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                            canContinue = true;

                        } else {

                            MenuMessage failureAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile creare due lobby con lo stesso nome");
                            ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
                            canContinue = false;

                        }

                        break;}


                    case ChoosePartecipateLobbyPrivate: {

                        nameLobbyCasual = menuMessage.getLobbyName();
                        lobbyPassword = menuMessage.getLobbyPassword();

                        //vede se la lobby esiste e se ha posti liberi e se la password è quella corretta
                        PrivateLobby chosenLobby = ServerThread.ListLobbyPrivate.findLobbyPrivate(nameLobbyCasual);


                        if (chosenLobby != null) {

                            if (chosenLobby.isTheRightPassword(lobbyPassword) && chosenLobby.addFsmClientHandlerToList(fsmContext)) {

                                fsmContext.setAssignedLobby(chosenLobby);

                                //vedo se la lobby ha raggiunto il numero giusto di giocatori
                                //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                                if (chosenLobby.isLobbyNowComplete())
                                {
                                    MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.ChoosePartecipateCanJumpToInGameState, "Hai completato la lobby");
                                    ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                                    lobbyFull = true;
                                }

                                //sono riuscito ad entrare nella lobby, ma non è ancora completa
                                else{

                                    MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.CreateOrParticipateStateCompleted, "Sei stato aggiunto con successo alla lobby");
                                    ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                                    lobbyFull = false;
                                }

                                canContinue = true;

                            }

                            //la password è scorretta
                            else {

                                MenuMessage failureAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.Fail, "La password inserita per la lobby non è corretta");
                                ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
                                canContinue = false;

                            }


                        }

                        //la lobby non esiste oppure è piena
                        else {

                            MenuMessage failureAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.Fail, "La lobby non esiste oppure è piena");
                            ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
                            canContinue = false;

                        }

                        break;}


                    case ChoosePartecipateLobbyPublic: {

                        nameLobbyCasual = menuMessage.getLobbyName();

                        //vede se la lobby esiste e se ha posti liberi e se la password è quella corretta
                        Lobby chosenLobbyPublic = ServerThread.ListLobbyPublic.findLobbyPublic(nameLobbyCasual);


                        if (chosenLobbyPublic != null && chosenLobbyPublic.addFsmClientHandlerToList(fsmContext)) {


                            fsmContext.setAssignedLobby(chosenLobbyPublic);


                            //vedo se la lobby ha raggiunto il numrto giusto di giocatori
                            //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                            if (chosenLobbyPublic.isLobbyNowComplete()) {
                                MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.ChoosePartecipateCanJumpToInGameState, "Hai completato la lobby, il gioco può partire");
                                ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                                lobbyFull = true;
                            }

                            else{

                                MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.CreateOrParticipateStateCompleted, "Sei stato aggiunto con successo alla lobby");
                                ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                                lobbyFull = false;
                            }

                            canContinue = true;

                        }

                        //la lobby non esiste oppure è piena
                        else {

                            MenuMessage failureAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.Fail, "La lobby pubblica non esiste oppure è piena");
                            ConnectionManager.sendObject(failureAnswer, fsmContext.getOos());
                            canContinue = false;

                        }

                        break;}


                    case ChooseLobbyCasual: {

                        Lobby c;

                        for(int i = 0; i < ServerThread.ListLobbyCasual.getList_lobbiesCasual().size() && canContinue == false; i++){

                            c = ServerThread.ListLobbyCasual.getList_lobbiesCasual().get(i);

                            if (c.addFsmClientHandlerToList(fsmContext)) {


                                fsmContext.setAssignedLobby(c);


                                //vedo se la lobby ha raggiunto il numrto giusto di giocatori
                                //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                                if (c.isLobbyNowComplete()) {
                                    MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.ChoosePartecipateCanJumpToInGameState, "Hai completato la lobby, il gioco può partire");
                                    ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                                    lobbyFull = true;
                                }

                                else{

                                    MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.CreateOrParticipateStateCompleted, "Sei stato aggiunto con successo alla lobby");
                                    ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                                    lobbyFull = false;
                                }

                                canContinue = true;

                            }}

                            //la lobby non esiste oppure è piena
                            if(canContinue == false){

                                lobbyCapacity = menuMessage.getNumberOfPlayers();
                                String creatorCasual = menuMessage.getMyName();
                                //uso il costruttore di lobby pubbliche
                                CasualLobby assignedCasualLobby = new CasualLobby(creatorCasual, lobbyCapacity, fsmContext);

                                if (ServerThread.hasPlayerAlreadyCreatedALobby(creatorCasual) && ServerThread.ListLobbyCasual.addToListLobbyCasual(assignedCasualLobby)) {

                                    fsmContext.setAssignedLobby(assignedCasualLobby);
                                    MenuMessage successAnswer = MenuMessage.newMenuMessageAffirmation(TypeOfMessage.CreateOrParticipateStateCompleted, "Ho creato con successo la lobby casual");

                                    ConnectionManager.sendObject(successAnswer, fsmContext.getOos());
                                    canContinue = true;

                                }


                                else canContinue =  false;

                            }

                        break;}


                }



            }catch(SocketException ex){

                ServerThread.serverExecutor.shutdown();

            }


             catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
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

       waitInLobby();

    }


    public synchronized void waitInLobby(){
        try {
            wait();
        } catch (Exception e) {
            System.out.println("sono nella wait in lobby e ho cannato");
            e.printStackTrace();
        }
    }
    public synchronized void notifyWaitInLobby(){

        notify();

    }



}


class ServerInGameState implements ServerState {

    private final MenuFsmServerSingleClientHandler fsmContext;
    private InGameConnection inGameConnection;

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

            try {

                Thread inGameConnectionThread = new Thread(inGameConnection);


                System.out.println("Sono in game state e sto aspettando sono: "
                        +ColorAnsi.RED +ServerThread.ListIdentities.retrievePlayerName(fsmContext.getUniquePlayerCode()) +ColorAnsi.RESET);

                waitInGame();

                inGameConnectionThread.start();

                inGameConnectionThread.join();

            }
            catch(Exception e) {

                System.out.println("something went wrong while catching playermoves, sono nell'ingame state");
                e.printStackTrace();

            }









    }


    public synchronized void waitInGame() throws InterruptedException {

        wait();
    }

    public synchronized void notifyInGameState(){

        notify();

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