package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.utils.ColorAnsi;
import it.polimi.ingsw.server.view.RemoteView;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;




public abstract class Lobby implements Runnable {

    private final boolean isPublic;
    private final String nameLobby;
    private final int lobbyCapacity;
    private final String lobbyCreator;
    private int numberOfPlayersActuallyConnected = 0;

    private ArrayList<IdentityCardOfPlayer> listIdentities = new ArrayList<>();
    private MenuFsmServerSingleClientHandler[] fsmClientHandlerList;


    //riceve la socket del creatore e l'aggiunge alla lista il nome della lobby è in upperCase
    public Lobby(String nameLobby, String lobbyCreator, int lobbyCapacity, MenuFsmServerSingleClientHandler creatorFsm) throws IOException {

        this.isPublic = true;
        this.nameLobby = nameLobby.toUpperCase();
        this.lobbyCapacity = lobbyCapacity;
        this.lobbyCreator = lobbyCreator;
        fsmClientHandlerList = new MenuFsmServerSingleClientHandler[lobbyCapacity];
        addFsmClientHandlerToList(creatorFsm);

    }

    //aggiunge giocatori alla lobby e se la lobby arriva al numero prefissato di giocatori fa partire la partita
    //ritorna true se si è raggiunto il numero giusto di giocatori, altrimenti ritorna false
    public boolean addFsmClientHandlerToList(MenuFsmServerSingleClientHandler fsm) throws IOException {

        synchronized (fsmClientHandlerList) {
            if (numberOfPlayersActuallyConnected < lobbyCapacity) {

                IdentityCardOfPlayer identity = ServerConnection.ListIdentities.retrievePlayerIdentity(fsm.getUniquePlayerCode());
                listIdentities.add(identity);


                fsmClientHandlerList[numberOfPlayersActuallyConnected] = fsm;
                numberOfPlayersActuallyConnected++;

                //il meno uno serve a non mandare il messaggio al creatore la prima volta
                for(int i = 0; i < numberOfPlayersActuallyConnected - 1; i++) {

                    if(fsmClientHandlerList[i].getCurrentServerState() instanceof ServerWaitingInLobbyState)
                    {
                        String message = ColorAnsi.YELLOW +"\nNumber of players actually connected: " +numberOfPlayersActuallyConnected +ColorAnsi.RESET +" " +ColorAnsi.RED +identity.getPlayerName() +ColorAnsi.RESET;
                        ConnectionManager.sendObject(new WaitingInLobbyMessages(TypeOfMessage.WaitingInLobbyPlayerJoined, message), fsmClientHandlerList[i].SocketobjectOutputStream);
                    }
                }

                return true;
            }
            //ritorna false o true, in teoria dovrebbe ritornare true, però sarebbe fonte di confusione
            else return false;

        }

    }

    public boolean isLobbyNowComplete() throws IOException {
        synchronized (fsmClientHandlerList) {
            if (lobbyCapacity == numberOfPlayersActuallyConnected) {

                for (int i = 0; i < numberOfPlayersActuallyConnected; i++) {

                    if (fsmClientHandlerList[i].getCurrentServerState() instanceof ServerWaitingInLobbyState) {
                        //uso il costruttore vuoto per mandare un messaggio di state completed
                        ConnectionManager.sendObject(new WaitingInLobbyMessages(), fsmClientHandlerList[i].SocketobjectOutputStream);
                    }
                }

                return true;


            } else return false;

        }
    }

    //fa partire il gioco vero e proprio
    @Override
    public void run() {

        ArrayList<Player> lobbyList = new ArrayList<>();
        ArrayList<RemoteView> remoteViewList = new ArrayList<>();

        //creating an array list of players and remote views
        for(IdentityCardOfPlayer identityPlayer : listIdentities) {

            Player player = new Player(identityPlayer.getPlayerName(), identityPlayer.getDateOfBirthday());
            lobbyList.add(player);

            for(MenuFsmServerSingleClientHandler m : fsmClientHandlerList){
                InGameConnection playerConnection;
                if (m.getUniquePlayerCode().equals( identityPlayer.getUniquePlayerCode() )){

                    Socket playerSocket = m.getClientSocket();
                    playerConnection = new InGameConnection(playerSocket, m.getUniquePlayerCode(), m.getOos(), m.getOis());
                    remoteViewList.add(new RemoteView(player, playerConnection));
                }

            }

        }

        Model game = new Model(lobbyList);
        Controller controller = new Controller(game);

        for(RemoteView rv : remoteViewList) {
            game.addObserver(rv);
            rv.addObserver(controller);
        }

        //controller.getModel().informView();

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
