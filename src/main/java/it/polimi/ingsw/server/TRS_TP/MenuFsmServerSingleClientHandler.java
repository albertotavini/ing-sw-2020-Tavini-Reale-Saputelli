package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.Date;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;


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
        this.currentServerState = new ServerSetIdentityState(SocketobjectOutputStream, SocketobjectInputStream, clientSocket, this);
    }

    void setState(ServerState nextServerState) {
        currentServerState = nextServerState;
    }

    public void handleServerFsm() {
        currentServerState.handleServerFsm();
    }

    public void setAssignedLobby(Lobby assignedLobby) {
        this.assignedLobby = assignedLobby;
    }

    public String getUniquePlayerCode(){
        return this.uniquePlayerCode;
    }

    public ServerState getCurrentServerState() {
        return currentServerState;
    }

    @Override
    public void run() {

        do{

            this.handleServerFsm();

            //finchè non sono arrivato all'ultimo stato non mi fermo
        }while(  !(this.currentServerState instanceof ServerFinalState) );



        //altre cose da vedere....



    }

}


interface ServerState {

    void handleServerFsm();
    void communicateWithTheClient();

}


class ServerSetIdentityState implements ServerState {

    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    private final Socket clientSocket;
    private final MenuFsmServerSingleClientHandler fsmContext;



    public ServerSetIdentityState(ObjectOutputStream oos, ObjectInputStream ois, Socket clientSocket, MenuFsmServerSingleClientHandler fsmContext) {
        this.oos = oos;
        this.ois = ois;
        this.clientSocket = clientSocket;
        this.fsmContext = fsmContext;
    }



    @Override
    public void handleServerFsm() {

        this.communicateWithTheClient();
        //setto il prossimo stato
        ServerState nextState = new CreateOrPartecipateState(oos, ois, clientSocket, fsmContext);
        fsmContext.setState(nextState);

    }

    @Override
    public void communicateWithTheClient() {

        boolean canContinueToCreateOrParticipate = false;

        //fase di set del nome
        while(!canContinueToCreateOrParticipate) {

            try {

                //ottiene il nome e la data di compleanno del giocatore
                SetNameMessage setNameMessage = (SetNameMessage) ois.readObject();

                if (setNameMessage.typeOfMessage.equals(TypeOfMessage.SetPlayerNameAndBirthday)) {

                    //creo l'identity del giocatore
                    String namePlayer = setNameMessage.getPlayerName();
                    Date date = setNameMessage.getDateOfBirthday();
                    IdentityCardOfPlayer identityCardOfPlayer = new IdentityCardOfPlayer(namePlayer, date, fsmContext.getUniquePlayerCode());

                    //add to player list fa il controllo per vedere se esiste già un player con con quel nome e restituisce un true
                    //se il nome è stato effitivamente aggiunto
                    if (ServerConnection.ListIdentities.addPlayerToListIdentities(identityCardOfPlayer)) {

                        //creo il messaggio che certifica il successo nel creare l'identità del player
                        SetNameMessage successAnswer = new SetNameMessage(TypeOfMessage.SetNameStateCompleted, "Ho aggiunto con successo l'identità");

                        //ho settato il nome del client nel contesto della fsm
                        oos.writeObject(successAnswer);
                        oos.flush();
                        canContinueToCreateOrParticipate = true;
                    }

                    //il nome è già stato scelto
                    else {

                        SetNameMessage failureAnswer = new SetNameMessage(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile avere due player con lo stesso nome");
                        oos.writeObject(failureAnswer);
                        oos.flush();
                        canContinueToCreateOrParticipate = false;


                    }


                }


            }catch(SocketException ex){

                ServerConnection.executor.shutdown();


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            ServerConnection.executor.shutdown();}


        }


    }




}


class CreateOrPartecipateState implements ServerState {

    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    private final Socket clientSocket;
    private final MenuFsmServerSingleClientHandler fsmContext;

    //serve ad evitare che si vada nello stato di waiting in lobby se la lobby arriva a riempirsi
    private boolean lobbyFull = false;


    public CreateOrPartecipateState(ObjectOutputStream oos, ObjectInputStream ois, Socket clientSocket, MenuFsmServerSingleClientHandler fsmContext) {
        this.oos = oos;
        this.ois = ois;
        this.clientSocket = clientSocket;
        this.fsmContext = fsmContext;
    }


    @Override
    public void handleServerFsm() {

        this.communicateWithTheClient();
        //setto il prossimo stato

        //se la lobby non è full aspetto in lobby
        if( !lobbyFull ) fsmContext.setState(new ServerWaitingInLobbyState(ois, oos, clientSocket, fsmContext));
        else fsmContext.setState(new ServerFinalState());

    }


    @Override
    public void communicateWithTheClient() {


        boolean canContinue = false;

        while (!canContinue) {

            try {

                //ottiene la volontà del giocatore: se si vuole creare una lobby o partecipare ad una esistente
                MenuMessages menuMessage = (MenuMessages) ConnectionManager.receiveObject(ois);

                String nameLobby = null;
                String lobbyPassword = null;
                String clientName = null;


                switch (menuMessage.typeOfMessage) {

                    case ChooseCreateLobbyPrivate:

                        int lobbyCapacity = menuMessage.getNumberOfPlayers();
                        nameLobby = menuMessage.getLobbyName();
                        lobbyPassword = menuMessage.getLobbyPassword();
                        String creator = menuMessage.getMyName();

                        PrivateLobby assignedPrivateLobby = new PrivateLobby(nameLobby, creator, lobbyPassword, lobbyCapacity, fsmContext);

                        //controllo attraverso il valore di ritorno di addtolistlobby se il nome lobby è stato già scelto, poi se il player ha già creato una lobby attiva
                        //utilizzo la cortocircuitazione data dall'and, invertite le due condizioni non farebbero mai entrare nel'if!!!!!! (valutare se fare due if annidati)
                        if (!ServerConnection.hasPlayerAlreadyCreatedALobby(creator) && ServerConnection.ListLobbyPrivate.addToListLobbyPrivate(assignedPrivateLobby)) {

                            //gli passo la socket del creatore e lui nel costruttore di lobby l'aggiunge alla socketlist nella lobby
                            fsmContext.setAssignedLobby(assignedPrivateLobby);
                            ServerConnection.ListLobbyPrivate.addToListLobbyPrivate(assignedPrivateLobby);
                            MenuMessages successAnswer = new MenuMessages(TypeOfMessage.CreateOrParticipateStateCompleted, "Ho creato con successo la lobby");

                            oos.writeObject(successAnswer);
                            oos.flush();
                            canContinue = true;

                        } else {

                            MenuMessages failureAnswer = new MenuMessages(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile creare due lobby con lo stesso nome");
                            oos.writeObject(failureAnswer);
                            oos.flush();
                            canContinue = false;

                        }

                        break;


                    case ChooseCreateLobbyPublic:

                        lobbyCapacity = menuMessage.getNumberOfPlayers();
                        nameLobby = menuMessage.getLobbyName();
                        String creatorPublic = menuMessage.getMyName();
                        //uso il costruttore di lobby pubbliche
                        PublicLobby assignedPublicLobby = new PublicLobby(nameLobby, creatorPublic, lobbyCapacity, fsmContext);

                        //controllo se il nome è stato già scelto atraverso il return value di add to list lobby o se il player ha già creato una lobby attiva
                        if (!ServerConnection.hasPlayerAlreadyCreatedALobby(creatorPublic) && ServerConnection.ListLobbyPublic.addToListLobbyPublic(assignedPublicLobby)) {

                            fsmContext.setAssignedLobby(assignedPublicLobby);
                            MenuMessages successAnswer = new MenuMessages(TypeOfMessage.CreateOrParticipateStateCompleted, "Ho creato con successo la lobby pubblica");

                            oos.writeObject(successAnswer);
                            oos.flush();
                            canContinue = true;

                        } else {

                            MenuMessages failureAnswer = new MenuMessages(TypeOfMessage.Fail, "Il nome è stato già scelto, non è possibile creare due lobby con lo stesso nome");
                            oos.writeObject(failureAnswer);
                            oos.flush();
                            canContinue = false;

                        }

                        break;


                    case ChoosePartecipateLobbyPrivate:

                        nameLobby = menuMessage.getLobbyName();
                        lobbyPassword = menuMessage.getLobbyPassword();

                        //vede se la lobby esiste e se ha posti liberi e se la password è quella corretta
                        PrivateLobby chosenLobby = ServerConnection.ListLobbyPrivate.findLobbyPrivate(nameLobby);


                        if (chosenLobby != null) {

                            if (chosenLobby.isTheRightPassword(lobbyPassword) && chosenLobby.addFsmClientHandlerToList(fsmContext)) {

                                fsmContext.setAssignedLobby(chosenLobby);
                                MenuMessages successAnswer = new MenuMessages(TypeOfMessage.CreateOrParticipateStateCompleted, "Sei stato aggiunto con successo alla lobby");
                                oos.writeObject(successAnswer);
                                oos.flush();

                                //vedo se la lobby ha raggiunto il numero giusto di giocatori
                                //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                                if (chosenLobby.isLobbyNowComplete())
                                {
                                    ServerConnection.executor.submit(chosenLobby);
                                    lobbyFull = true;
                                }

                                else lobbyFull = false;

                                canContinue = true;

                            }

                            //la password è scorretta
                            else {

                                MenuMessages failureAnswer = new MenuMessages(TypeOfMessage.Fail, "La password inserita per la lobby non è corretta");
                                oos.writeObject(failureAnswer);
                                oos.flush();
                                canContinue = false;

                            }


                        }

                        //la lobby non esiste oppure è piena
                        else {

                            MenuMessages failureAnswer = new MenuMessages(TypeOfMessage.Fail, "La lobby non esiste oppure è piena");
                            oos.writeObject(failureAnswer);
                            oos.flush();
                            canContinue = false;

                        }

                        break;


                    case ChoosePartecipateLobbyPublic:

                        nameLobby = menuMessage.getLobbyName();

                        //vede se la lobby esiste e se ha posti liberi e se la password è quella corretta
                        Lobby chosenLobbyPublic = ServerConnection.ListLobbyPublic.findLobbyPublic(nameLobby);


                        if (chosenLobbyPublic != null && chosenLobbyPublic.addFsmClientHandlerToList(fsmContext)) {


                            fsmContext.setAssignedLobby(chosenLobbyPublic);
                            MenuMessages successAnswer = new MenuMessages(TypeOfMessage.CreateOrParticipateStateCompleted, "Sei stato aggiunto con successo alla lobby");
                            oos.writeObject(successAnswer);
                            oos.flush();

                            //vedo se la lobby ha raggiunto il numrto giusto di giocatori
                            //attivo il thread lobby solo quando ho tutti i giocatori, prima non mi interessa
                            if (chosenLobbyPublic.isLobbyNowComplete()) {
                                ServerConnection.executor.submit(chosenLobbyPublic);
                                lobbyFull = true;
                            }

                            else lobbyFull = false;

                            canContinue = true;

                        }

                        //la lobby non esiste oppure è piena
                        else {

                            MenuMessages failureAnswer = new MenuMessages(TypeOfMessage.Fail, "La lobby pubblica non esiste oppure è piena");
                            oos.writeObject(failureAnswer);
                            oos.flush();
                            canContinue = false;

                        }

                        break;


                }



            }catch(SocketException ex){

                ServerConnection.executor.shutdown();

            }


             catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            }

    }


}


class ServerWaitingInLobbyState implements ServerState {


    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    private final Socket clientSocket;
    private final MenuFsmServerSingleClientHandler fsmContext;

    public ServerWaitingInLobbyState(ObjectInputStream ois, ObjectOutputStream oos, Socket clientSocket, MenuFsmServerSingleClientHandler fsmContext) {
        this.ois = ois;
        this.oos = oos;
        this.clientSocket = clientSocket;
        this.fsmContext = fsmContext;
    }


    @Override
    public void handleServerFsm() {

        this.communicateWithTheClient();
        //setto il prossimo stato
        fsmContext.setState(new ServerFinalState());


    }

    @Override
    public void communicateWithTheClient() {


        boolean canContinueToInGameState = false;

        do {

            try {


                WaitingInLobbyMessages message = (WaitingInLobbyMessages) ConnectionManager.receiveObject(ois);


            } catch (SocketException e) {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        } while (!canContinueToInGameState);


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