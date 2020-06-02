package it.polimi.ingsw.server.transmissionprotocol;


import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.WaitingInLobbyMessage;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.bothsides.onlinemessages.InGameServerMessage;
import it.polimi.ingsw.server.view.RemoteView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public abstract class Lobby implements Runnable {

    private final boolean isPublic;
    private final String nameLobby;
    private final int lobbyCapacity;
    private final String lobbyCreator;
    private int numberOfPlayersActuallyConnected = 0;
    private final LobbyChat lobbyChat = new LobbyChat();

    private HashMap<IdentityCardOfPlayer, ServerFsm> correlationMap = new HashMap<>();

    //riceve la socket del creatore e l'aggiunge alla lista il nome della lobby è in upperCase
    public Lobby(String nameLobby, String lobbyCreator, int lobbyCapacity, ServerFsm creatorFsm) throws IOException {

        this.isPublic = true;
        this.nameLobby = nameLobby.toUpperCase();
        this.lobbyCapacity = lobbyCapacity;
        this.lobbyCreator = lobbyCreator;
        addFsmClientHandlerToList(creatorFsm);

    }

    //aggiunge giocatori alla lobby e se la lobby arriva al numero prefissato di giocatori fa partire la partita
    //ritorna true se si è raggiunto il numero giusto di giocatori, altrimenti ritorna false
    public boolean addFsmClientHandlerToList(ServerFsm fsm) throws IOException {

        synchronized (correlationMap) {

            if (numberOfPlayersActuallyConnected < lobbyCapacity) {

                IdentityCardOfPlayer identity = ServerThread.ListIdentities.retrievePlayerIdentity(fsm.getUniquePlayerCode());
                correlationMap.put(identity, fsm);
                numberOfPlayersActuallyConnected++;
                informPlayerHasJoined(identity);
                return true;
            }

            else return false;

        }

    }

    public void removeFsmClientHandlerFromList(IdentityCardOfPlayer identity) throws IOException {

        synchronized (correlationMap) {

                correlationMap.remove(identity);
                numberOfPlayersActuallyConnected--;
                informPlayerHasDisconnected(identity);

                if(numberOfPlayersActuallyConnected == 0) killLobby();

        }

    }

    private void informPlayerHasJoined(IdentityCardOfPlayer identity) throws IOException {

        //il meno uno serve a non mandare il messaggio al creatore la prima volta
        for(ServerFsm m : correlationMap.values()) {

            if(m.getCurrentServerState() instanceof ServerWaitingInLobbyState) {
                String message = Global.NUMBEROFPLAYERSACTUALLYCONNECTED +numberOfPlayersActuallyConnected +" " +identity.getPlayerName();
                ConnectionManager.sendObject(WaitingInLobbyMessage.newWaitingInLobbyMessageStandard(TypeOfSetupMessage.WAITING_IN_LOBBY_PLAYER_JOINED, message), m.getOos());
            }

        }



    }

    private void informPlayerHasDisconnected(IdentityCardOfPlayer identity) throws IOException {

        //il meno uno serve a non mandare il messaggio al creatore la prima volta
        for(ServerFsm m : correlationMap.values()) {

            if(m.getCurrentServerState() instanceof ServerWaitingInLobbyState) {
                String message = Global.NUMBEROFPLAYERSACTUALLYCONNECTED +numberOfPlayersActuallyConnected +" " +identity.getPlayerName();
                ConnectionManager.sendObject(WaitingInLobbyMessage.newWaitingInLobbyMessageStandard(TypeOfSetupMessage.WAITING_IN_LOBBY_PLAYER_DISCONNECTED, message), m.getOos());
            }

        }



    }

    public synchronized void killLobby() throws IOException{

        ServerFsm[] arrayFsm = correlationMap.values().toArray(new ServerFsm[0]);
        ServerFsm m = null;

        for(int i = 0; i < arrayFsm.length; i++) {

            m = arrayFsm[i];

            if(m != null && m.getCurrentServerState() instanceof ServerInGameState) {
                String message = Global.LOBBYDISCONNECTED;
                try {

                    ModelMessage disconnectedMessage = new ModelMessage(ModelMessageType.DISCONNECTED, ModelError.NONE, message, " " );
                    ConnectionManager.sendObject(new InGameServerMessage(null, disconnectedMessage), m.getOos());

                }catch(IOException ex){
                    LogPrinter.printOnLog(Global.ONEOFTHECLIENTSDIDNOTRECEIVETHEKILLLOBBYMESSAGE);
                }
            }

        }


        if(this instanceof PublicLobby){ ServerThread.ListLobbyPublic.deleteLobbyPublic((PublicLobby) this);}

        if(this instanceof PrivateLobby){ ServerThread.ListLobbyPrivate.deleteLobbyPrivate((PrivateLobby) this);}

        if(this instanceof CasualLobby){ServerThread.ListLobbyCasual.deleteLobbyCasual((CasualLobby) this);}


        Thread.currentThread().interrupt();


    }


    public boolean isLobbyNowComplete() {

        synchronized (correlationMap) {

            return (lobbyCapacity == numberOfPlayersActuallyConnected);

        }
    }


    public LobbyChat getLobbyChat() {
        return lobbyChat;
    }

    //fa partire il gioco vero e proprio
    @Override
    public void run() {

        ArrayList<Player> lobbyListPlayer = new ArrayList<>();
        ArrayList<RemoteView> remoteViewList = new ArrayList<>();

        int numberOfplayerAwake = 0;

        for (ServerFsm m : correlationMap.values()) {

            if (m.getCurrentServerState() instanceof ServerWaitingInLobbyState) {
                //risveglio i thread in attesa
                ((ServerWaitingInLobbyState) m.getCurrentServerState()).notifyWaitInLobby();

                LogPrinter.printOnLog(Global.IMINLOBBYRUNANDIJUSTWOKEUPFROMWAITINGSTATE + ServerThread.ListIdentities.retrievePlayerName(m.getUniquePlayerCode()));

                try {
                    //uso il costruttore vuoto per mandare un messaggio di state completed
                    //messaggio di waiting in lobby completed
                    ConnectionManager.sendObject(new WaitingInLobbyMessage(), m.getOos());

                } catch (Exception e) {
                    LogPrinter.printOnLog(Global.LOBBYFAILEDTOWAKEPLAYERSFROMWAITINGSTATE);
                    LogPrinter.printOnLog(e.toString());
                }
            }
        }


        while(numberOfplayerAwake != lobbyCapacity){

            for(ServerFsm m : correlationMap.values()) {
                if(m.getCurrentServerState() instanceof ServerInGameState && ((ServerInGameState) m.getCurrentServerState()).isWaiting()){
                    ((ServerInGameState) m.getCurrentServerState()).notifyInGameState();
                    LogPrinter.printOnLog(Global.IMINLOBBYRUNANDIJUSTWOKEUPFROMWAITINGAMESTATE +ServerThread.ListIdentities.retrievePlayerName(m.getUniquePlayerCode()));
                    numberOfplayerAwake++;
                }
            }
        }



        //creating an array list of players and remote views
        for(IdentityCardOfPlayer identityPlayer : correlationMap.keySet()) {

            Player player = new Player(identityPlayer.getPlayerName(), identityPlayer.getDateOfBirthday());
            lobbyListPlayer.add(player);

            if (correlationMap.get(identityPlayer).getCurrentServerState() instanceof ServerInGameState) {
                remoteViewList.add(new RemoteView(player, ((ServerInGameState) correlationMap.get(identityPlayer).getCurrentServerState()).getInGameConnection()));
            }
        }


        Model game = new Model(lobbyListPlayer);


        Controller controller = new Controller(game);

        for(RemoteView rv : remoteViewList) {
            game.addObserver(rv);
            rv.addObserver(controller);
        }


        controller.initialMessage();


    }





    class LobbyChat {

        public synchronized void addMessage(String message) {

            ServerFsm[] arrayFsm = correlationMap.values().toArray(new ServerFsm[0]);
            ServerFsm m = null;

            ModelMessage chatMessage = null;

            for(int i = 0; i < arrayFsm.length; i++) {

                m = arrayFsm[i];

                if(m != null && m.getCurrentServerState() instanceof ServerInGameState) {

                    try {

                        chatMessage = new ModelMessage(ModelMessageType.CHAT_MESSAGE, message);
                        ConnectionManager.sendObject(new InGameServerMessage(null, chatMessage ), m.getOos());

                    }catch(IOException ex){
                        LogPrinter.printOnLog(Global.ONEOFTHECLIENTSDIDNOTRECEIVETHECHATMESSAGE);
                    }
                }

            }



        }

    }





    public boolean isPublic() {
        return isPublic;
    }

    public String getNameLobby() {
        return nameLobby;
    }

    public String getLobbyCreator() {
        return lobbyCreator;
    }

    @Override
    public String toString() {
        return  Global.NAMELOBBY + nameLobby + Global.BACKSLASH +
                Global.LOBBYCAPACITY + lobbyCapacity +
                Global.NUMBEROFPLAYERSACTUALLYCONNECTEDFORTOSTRING + numberOfPlayersActuallyConnected +
                Global.LOBBYCREATOR + lobbyCreator + Global.BACKSLASH +
                Global.CLOSINGBRACE;
    }



}

class PublicLobby extends Lobby implements Runnable {

    public PublicLobby(String nameLobby, String lobbyCreator, int lobbyCapacity, ServerFsm creatorFsm) throws IOException {
        super(nameLobby, lobbyCreator, lobbyCapacity, creatorFsm);
    }
}

class PrivateLobby extends Lobby implements Runnable {


    private final String lobbyPassword;


    public PrivateLobby(String nameLobby, String lobbyCreator, String lobbyPassword, int lobbyCapacity, ServerFsm creatorFsm) throws IOException {
        super(nameLobby, lobbyCreator, lobbyCapacity, creatorFsm);
        this.lobbyPassword = lobbyPassword;
    }

    public boolean isTheRightPassword(String lobbyPassword){
        return lobbyPassword.equals(this.lobbyPassword);
    }

}

class CasualLobby extends Lobby implements Runnable {

    public CasualLobby(String lobbyCreator, int lobbyCapacity, ServerFsm creatorFsm) throws IOException {
        super(Global.CASUALLOBBY, lobbyCreator, lobbyCapacity, creatorFsm);

    }
}
