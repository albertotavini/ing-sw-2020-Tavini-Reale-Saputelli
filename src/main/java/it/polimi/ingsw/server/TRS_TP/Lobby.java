package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.utils.Global;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;

import java.util.ArrayList;

public abstract class Lobby implements Runnable {

    private final boolean isPublic;
    private final String nameLobby;
    private final int lobbyCapacity;
    private final String lobbyCreator;
    private int numberOfPlayersActuallyConnected = 0;


    private MenuFsmServerSingleClientHandler[] fsmClientHandlerList;



    //riceve la socket del creatore e l'aggiunge alla lista il nome della lobby è in upperCase
    public Lobby(String nameLobby, String lobbyCreator, int lobbyCapacity, MenuFsmServerSingleClientHandler creatorFsm) {

        this.isPublic = true;
        this.nameLobby = nameLobby.toUpperCase();
        this.lobbyCapacity = lobbyCapacity;
        this.lobbyCreator = lobbyCreator;
        fsmClientHandlerList = new MenuFsmServerSingleClientHandler[lobbyCapacity];
        addFsmClientHandlerToList(creatorFsm);
    }

    //aggiunge giocatori alla lobby e se la lobby arriva al numero prefissato di giocatori fa partire la partita
    //ritorna true se si è raggiunto il numero giusto di giocatori, altrimenti ritorna false
    public boolean addFsmClientHandlerToList(MenuFsmServerSingleClientHandler fsm) {

        synchronized (fsmClientHandlerList) {
            if (numberOfPlayersActuallyConnected < lobbyCapacity) {
                fsmClientHandlerList[numberOfPlayersActuallyConnected] = fsm;
                numberOfPlayersActuallyConnected++;

                return true;
            }
            //ritorna false o true, in teoria dovrebbe ritornare true, però sarebbe fonte di confusione
            else return false;

        }

    }

    public boolean isLobbyNowComplete() {
        synchronized (fsmClientHandlerList){
            if(lobbyCapacity == numberOfPlayersActuallyConnected) return true;
            else return false;
        }
    }

    //fa partire il gioco vero e proprio
    @Override
    public void run() {

        System.out.println("\n\nFUNZIONO: adesso dovrei far partire il gioco\n\n");

        ArrayList<Player> lobbyList = new ArrayList<>();
        ArrayList<RemoteView> remoteViewList = new ArrayList<>();

        //creating an array list of players and remote views
        for(MenuFsmServerSingleClientHandler n : fsmClientHandlerList) {

            IdentityCardOfPlayer identityPlayer = ServerConnection.retrievePlayerIdentity(n.getUniquePlayerCode());

            Player player = new Player(identityPlayer.getPlayerName(), identityPlayer.getDateOfBirthday());

            lobbyList.add(player);
            //remoteViewList.add(new RemoteView(player));

        }

        Model game = new Model(lobbyList);
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

    public int getLobbyCapacity() {
        return lobbyCapacity;
    }

    public int getNumberOfPlayersActuallyConnected() {
        return numberOfPlayersActuallyConnected;
    }

    public synchronized boolean isLobbyFull(){
        return lobbyCapacity > numberOfPlayersActuallyConnected;
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

    public PublicLobby(String nameLobby, String lobbyCreator, int lobbyCapacity, MenuFsmServerSingleClientHandler creatorFsm) {
        super(nameLobby, lobbyCreator, lobbyCapacity, creatorFsm);
    }
}

class PrivateLobby extends Lobby implements Runnable {


    private final String lobbyPassword;


    public PrivateLobby(String nameLobby, String lobbyCreator, String lobbyPassword, int lobbyCapacity, MenuFsmServerSingleClientHandler creatorFsm) {
        super(nameLobby, lobbyCreator, lobbyCapacity, creatorFsm);
        this.lobbyPassword = lobbyPassword;
    }

    public boolean isTheRightPassword(String lobbyPassword){
        return lobbyPassword.equals(this.lobbyPassword);
    }

}
