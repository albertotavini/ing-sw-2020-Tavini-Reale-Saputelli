package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.utils.ColorAnsi;
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

    private HashMap<IdentityCardOfPlayer, MenuFsmServerSingleClientHandler> correlationMap = new HashMap<>();

    //riceve la socket del creatore e l'aggiunge alla lista il nome della lobby è in upperCase
    public Lobby(String nameLobby, String lobbyCreator, int lobbyCapacity, MenuFsmServerSingleClientHandler creatorFsm) throws IOException {

        this.isPublic = true;
        this.nameLobby = nameLobby.toUpperCase();
        this.lobbyCapacity = lobbyCapacity;
        this.lobbyCreator = lobbyCreator;
        addFsmClientHandlerToList(creatorFsm);

    }

    //aggiunge giocatori alla lobby e se la lobby arriva al numero prefissato di giocatori fa partire la partita
    //ritorna true se si è raggiunto il numero giusto di giocatori, altrimenti ritorna false
    public boolean addFsmClientHandlerToList(MenuFsmServerSingleClientHandler fsm) throws IOException {

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

    private void informPlayerHasJoined(IdentityCardOfPlayer identity) throws IOException {

        //il meno uno serve a non mandare il messaggio al creatore la prima volta
        for(MenuFsmServerSingleClientHandler m : correlationMap.values()) {

            if(m.getCurrentServerState() instanceof ServerWaitingInLobbyState) {
                String message = ColorAnsi.YELLOW +"\nNumber of players actually connected: " +numberOfPlayersActuallyConnected +ColorAnsi.RESET +" " +ColorAnsi.RED +identity.getPlayerName() +ColorAnsi.RESET;
                ConnectionManager.sendObject(WaitingInLobbyMessage.newWaitingInLobbyMessageStandard(TypeOfMessage.WaitingInLobbyPlayerJoined, message), m.SocketobjectOutputStream);
            }

        }



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


        for (MenuFsmServerSingleClientHandler m : correlationMap.values()) {

            if (m.getCurrentServerState() instanceof ServerWaitingInLobbyState) {
                //risveglio i thread in attesa
                ((ServerWaitingInLobbyState) m.getCurrentServerState()).notifyWaitInLobby();

                try {
                    //uso il costruttore vuoto per mandare un messaggio di state completed
                    //messaggio di waiting in lobby completed
                    ConnectionManager.sendObject(new WaitingInLobbyMessage(), m.SocketobjectOutputStream);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        //creating an array list of players and remote views
        for(IdentityCardOfPlayer identityPlayer : correlationMap.keySet()) {

            Player player = new Player(identityPlayer.getPlayerName(), identityPlayer.getDateOfBirthday());
            lobbyListPlayer.add(player);

            InGameConnection playerConnection = null;

            if (correlationMap.get(identityPlayer).getCurrentServerState() instanceof ServerInGameState) {
                playerConnection = ((ServerInGameState) correlationMap.get(identityPlayer).getCurrentServerState()).getInGameConnection();
                remoteViewList.add(new RemoteView(player, playerConnection));
            }
        }


        Model game = new Model(lobbyListPlayer);
        Controller controller = new Controller(game);


        for(RemoteView rv : remoteViewList) {
            game.addObserver(rv);
            rv.addObserver(controller);
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
        return  "nameLobby='" + nameLobby + '\'' +
                ", lobbyCapacity=" + lobbyCapacity +
                ", numberOfPlayersActuallyConnected=" + numberOfPlayersActuallyConnected +
                ", lobbyCreator='" + lobbyCreator + '\'' +
                '}';
    }

}

class PublicLobby extends Lobby implements Runnable {

    public PublicLobby(String nameLobby, String lobbyCreator, int lobbyCapacity, MenuFsmServerSingleClientHandler creatorFsm) throws IOException {
        super(nameLobby, lobbyCreator, lobbyCapacity, creatorFsm);
    }
}

class PrivateLobby extends Lobby implements Runnable {


    private final String lobbyPassword;


    public PrivateLobby(String nameLobby, String lobbyCreator, String lobbyPassword, int lobbyCapacity, MenuFsmServerSingleClientHandler creatorFsm) throws IOException {
        super(nameLobby, lobbyCreator, lobbyCapacity, creatorFsm);
        this.lobbyPassword = lobbyPassword;
    }

    public boolean isTheRightPassword(String lobbyPassword){
        return lobbyPassword.equals(this.lobbyPassword);
    }

}
