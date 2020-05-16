package it.polimi.ingsw.server.transmissionprotocol;


import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.WaitingInLobbyMessage;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;
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
                String message = ColorAnsi.YELLOW +"\nNumber of players actually connected: " +numberOfPlayersActuallyConnected +ColorAnsi.RESET +" " +ColorAnsi.RED +identity.getPlayerName() +ColorAnsi.RESET;
                ConnectionManager.sendObject(WaitingInLobbyMessage.newWaitingInLobbyMessageStandard(TypeOfSetupMessage.WaitingInLobbyPlayerJoined, message), m.getOos());
            }

        }



    }

    private void informPlayerHasDisconnected(IdentityCardOfPlayer identity) throws IOException {

        //il meno uno serve a non mandare il messaggio al creatore la prima volta
        for(ServerFsm m : correlationMap.values()) {

            if(m.getCurrentServerState() instanceof ServerWaitingInLobbyState) {
                String message = ColorAnsi.YELLOW +"\nNumber of players actually connected: " +numberOfPlayersActuallyConnected +ColorAnsi.RESET +" " +ColorAnsi.RED +identity.getPlayerName() +ColorAnsi.RESET;
                ConnectionManager.sendObject(WaitingInLobbyMessage.newWaitingInLobbyMessageStandard(TypeOfSetupMessage.WaitingInLobbyPlayerDisconnected, message), m.getOos());
            }

        }



    }

    public synchronized void killLobby() throws IOException{


        for(ServerFsm m : correlationMap.values()) {

            if(m.getCurrentServerState() instanceof ServerInGameState) {
                String message = ColorAnsi.YELLOW +"\nLobby disconnected" +ColorAnsi.RESET;
                try {
                    ConnectionManager.sendObject(new InGameServerMessage(null, new ModelMessage(ModelMessageType.DISCONNECTED, message)), m.getOos());
                }catch(IOException ex){
                    LogPrinter.printOnLog("\n----One of the clients did not receive the kill lobby message");
                }
            }

        }


        if(this instanceof PublicLobby){ ServerThread.ListLobbyPublic.deleteLobbyPublic((PublicLobby) this);}

        if(this instanceof PrivateLobby){ ServerThread.ListLobbyPrivate.deleteLobbyPrivate((PrivateLobby) this);}

        if(this instanceof CasualLobby){ServerThread.ListLobbyCasual.deleteLobbyCasual((CasualLobby) this);}


        Thread.currentThread().interrupt();


    }


    public boolean isLobbyNowComplete() throws IOException {

        synchronized (correlationMap) {

            if (lobbyCapacity == numberOfPlayersActuallyConnected) { return true; }

            else return false;
        }
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

                LogPrinter.printOnLog("\nSono nella run della lobby e ho risvegliato dallo waiting state " +ServerThread.ListIdentities.retrievePlayerName(m.getUniquePlayerCode()));

                try {
                    //uso il costruttore vuoto per mandare un messaggio di state completed
                    //messaggio di waiting in lobby completed
                    ConnectionManager.sendObject(new WaitingInLobbyMessage(), m.getOos());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        while(numberOfplayerAwake != lobbyCapacity){

            for(ServerFsm m : correlationMap.values()) {
                if(m.getCurrentServerState() instanceof ServerInGameState && ((ServerInGameState) m.getCurrentServerState()).isWaiting()){
                    ((ServerInGameState) m.getCurrentServerState()).notifyInGameState();
                    LogPrinter.printOnLog("\nSono nella run della lobby e sto risvegliando dalla wait nel gameState " +ServerThread.ListIdentities.retrievePlayerName(m.getUniquePlayerCode()));
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
        return  "nameLobby='" + nameLobby + '\'' +
                ", lobbyCapacity=" + lobbyCapacity +
                ", numberOfPlayersActuallyConnected=" + numberOfPlayersActuallyConnected +
                ", lobbyCreator='" + lobbyCreator + '\'' +
                '}';
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
        super("CASUAL LOBBY", lobbyCreator, lobbyCapacity, creatorFsm);

    }
}
