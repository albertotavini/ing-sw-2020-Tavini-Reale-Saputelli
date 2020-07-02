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


/**
 * this is the parent class that represents a lobby, when full its run() method will instantiate Model, Controller and Remoteviews
 * and associate Observers so that the game can begin
 */
public abstract class Lobby implements Runnable {

    private final boolean isPublic;
    private final String nameLobby;
    private final int lobbyCapacity;
    private final String lobbyCreator;
    private int numberOfPlayersActuallyConnected = 0;
    private boolean isActive = false;
    private final LobbyChat lobbyChat = new LobbyChat();

    private HashMap<IdentityCardOfPlayer, ServerFsm> correlationMap = new HashMap<>();

    public Lobby(String nameLobby, String lobbyCreator, int lobbyCapacity, ServerFsm creatorFsm) throws IOException {

        this.isPublic = true;
        this.nameLobby = nameLobby.toUpperCase();
        this.lobbyCapacity = lobbyCapacity;
        this.lobbyCreator = lobbyCreator;
        addFsmClientHandlerToList(creatorFsm);

    }

    /**
     * adds the player to the lobby and if it becomes full it starts the match
     *
     * @param fsm of the player that needs to be added
     * @return true when the last player has been added and game can start
     * @throws IOException to be handled outside of this, the informPlayerHasJoined uses connection manager and that could
     * throw the exception
     */
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

    /**
     *
     * while lobby isn't full it can remove a player who disconnected so that space is freed and others can join to
     * complete the lobby and start a match
     *
     * @param identity of the player to be removed
     * @throws IOException can be thrown by usage of connectionManager inside
     */
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

    public boolean isActive() {
        return isActive;
    }

    /**
     * fundamental method that is called when one player disconnects, it closes the lobby where the game is being held
     * and tells the other players who are partecipating that something went wrong and they might join a new one
     *
     * @throws IOException can be thrown by usage of connectionManager inside
     */
    public synchronized void killLobby() throws IOException{


        if(isActive) {


            ServerFsm[] arrayFsm = correlationMap.values().toArray(new ServerFsm[0]);
            ServerFsm m = null;

            for (int i = 0; i < arrayFsm.length; i++) {

                m = arrayFsm[i];

                if (m != null && m.getCurrentServerState() instanceof ServerInGameState) {
                    String message = Global.LOBBYDISCONNECTED;
                    try {

                        ModelMessage disconnectedMessage = new ModelMessage(ModelMessageType.DISCONNECTED, ModelError.NONE, message, " ");
                        ConnectionManager.sendObject(new InGameServerMessage(null, disconnectedMessage), m.getOos());

                    } catch (IOException ex) {
                        LogPrinter.printOnLog(Global.ONEOFTHECLIENTSDIDNOTRECEIVETHEKILLLOBBYMESSAGE);
                    }
                }

            }


            if (this instanceof PublicLobby) {
                ServerThread.ListLobbyPublic.deleteLobbyPublic((PublicLobby) this);
            }

            if (this instanceof PrivateLobby) {
                ServerThread.ListLobbyPrivate.deleteLobbyPrivate((PrivateLobby) this);
            }

            if (this instanceof CasualLobby) {
                ServerThread.ListLobbyCasual.deleteLobbyCasual((CasualLobby) this);
            }

            isActive = false;
        }


    }


    /**
     * @return true if the player has a number of partecipants corresponding to its capacity
     */
    public boolean isLobbyNowComplete() {

        synchronized (correlationMap) {

            return (lobbyCapacity == numberOfPlayersActuallyConnected);

        }
    }


    public LobbyChat getLobbyChat() {
        return lobbyChat;
    }


    /**
     * this is probably the most important method of the whole project
     * clients who are waiting in WaitingInLobbyState or InGameState on their FSM are told the game is beginning and all switch to "active"
     * InGameState
     * here model, controller and remoteview are created, observers are set and InGameConnections are assigned so that
     * everything during the game can properly work
     * after that controller sends an initial message and the game begins
     */
    @Override
    public void run() {

        this.isActive = true;


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
                        ConnectionManager.sendObject(new InGameServerMessage(null, chatMessage ), m.getChatOos());

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

/**
 * child class that is used for Public lobbies
 */
class PublicLobby extends Lobby implements Runnable {

    public PublicLobby(String nameLobby, String lobbyCreator, int lobbyCapacity, ServerFsm creatorFsm) throws IOException {
        super(nameLobby, lobbyCreator, lobbyCapacity, creatorFsm);
    }
}

/**
 * child class that is used for private lobbies
 */
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


/**
 * child class that is used for casual lobbies
 */
class CasualLobby extends Lobby implements Runnable {

    public CasualLobby(String lobbyCreator, int lobbyCapacity, ServerFsm creatorFsm) throws IOException {
        super(Global.CASUALLOBBY, lobbyCreator, lobbyCapacity, creatorFsm);

    }
}
