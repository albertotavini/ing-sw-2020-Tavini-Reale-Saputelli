package it.polimi.ingsw.server.TRS_TP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerConnection {

    final int port;
    //numero utile per generare lo unique code
    private static short cyclicNumberForUniqueCode = 0;
    //pool di thread ad uso e consumo del server per creare fsmSingleClientHandler
    public static ExecutorService executor = Executors.newCachedThreadPool();
    //array list delle lobby pubbliche attualmente presenti sul server
    private static ArrayList<PublicLobby> list_lobbiesPublic = new ArrayList<>();
    //array list delle lobby pubbliche attualmente presenti sul server
    private static ArrayList<PrivateLobby> list_lobbiesPrivate = new ArrayList<>();
    //array list delle identità dei player attualmente presenti sul server
    private static ArrayList<IdentityCardOfPlayer> list_player= new ArrayList<>();

    public ServerConnection(int port){
        this.port = port;
    }

    //metodo che fa semplicemente partire il server
    public void runServer() {

        ServerSocket ssocket;

        try {

            ssocket = new ServerSocket(port);
            System.out.println("Server listening on port: " +port);

            while(true)
            {

                Socket socket = ssocket.accept();
                System.out.println("A player joined the server");
                executor.submit(new FsmServerSingleClientHandler(socket, getUniquePlayerCode()));

            }

        }catch (IOException e){
            /*da vedere*/
        }

    }

    //************************************************************************************************************************
    //I metodi sono sincronizzati con le rispettive liste (contengono blocchi sincronizzati)
    //************************************************************************************************************************

    //metodo helper che restituisce un playerUniqueCode
    private static String getUniquePlayerCode() {

        String dataAccesso = Calendar.getInstance().getTime().toString();
        String delimiter = "###";
        if(cyclicNumberForUniqueCode < 32000){
            cyclicNumberForUniqueCode++;
            return dataAccesso +delimiter +cyclicNumberForUniqueCode;
        }

        else{
            cyclicNumberForUniqueCode = 0;
            return dataAccesso +delimiter +cyclicNumberForUniqueCode;

        }


    }

    //aggiunge il giocatore alla lista delle identità e restituisce vero se l'operazione è andata a buon fine
    public static boolean addPlayerToListIdentities(IdentityCardOfPlayer playerIdentity) {
        synchronized(list_player){
        for(IdentityCardOfPlayer n : list_player){
            if(playerIdentity.getPlayerName().equals(n.getPlayerName())) return false;
        }

        list_player.add(playerIdentity);}
        System.out.println("\nNew identity: " +playerIdentity.toString());
        return true;

    }

    public static void removePlayerFromListIdentities(String namePlayer){

        synchronized (list_player){
        for(int i = 0; i < list_player.size(); i++) {
            if (namePlayer.equals(list_player.get(i).getPlayerName())) list_player.remove(i);
        } }
    }

    //aggiunge la lobby all'arraylist delle lobby pubbliche
    public static boolean addToListLobbyPublic(PublicLobby lobby) {

        String nomeLobbyPublic = lobby.getNameLobby().toUpperCase();

        synchronized (list_lobbiesPublic) {
            for(Lobby l : list_lobbiesPublic){
                if(l.getNameLobby().equals(nomeLobbyPublic)){
                    return false;
                    }
                }

            list_lobbiesPublic.add(lobby);

            return true;
        }
    }

    //il nome della lobby viene messo in automatico in upperCase
    public static PublicLobby findLobbyPublic(String nameLobby) {
        nameLobby = nameLobby.toUpperCase();

        synchronized (list_lobbiesPublic){
        for(PublicLobby l : list_lobbiesPublic){
            if(l.getNameLobby().equals(nameLobby)){
                return l;
            }}
        }

        return null;



    }

    //aggiunge la lobby all'arraylist delle lobby private
    public static boolean addToListLobbyPrivate(PrivateLobby lobby) {

        String nomeLobby = lobby.getNameLobby().toUpperCase();

        synchronized (list_lobbiesPrivate) {
            for(Lobby l : list_lobbiesPrivate){
                if(l.getNameLobby().equals(nomeLobby)){
                    return false;
                }
            }

            list_lobbiesPrivate.add(lobby);

            return true;
        }
    }

    //il nome della lobby viene messo in automatico in upperCase
    public static PrivateLobby findLobbyPrivate(String nameLobby) {
        nameLobby = nameLobby.toUpperCase();

        synchronized (list_lobbiesPrivate){
            for(PrivateLobby l : list_lobbiesPrivate){
                if(l.getNameLobby().equals(nameLobby)){
                    return l;
                }}
        }

        return null;



    }

    //ci dice se un giocatore ha già creato una lobby
    public static boolean hasPlayerAlreadyCreatedALobby(String nameCreator) {

        for(Lobby l : list_lobbiesPublic){
            if(l.getLobbyCreator().equals(nameCreator)){
                return true;
            }
        }

        for(Lobby l : list_lobbiesPrivate){
            if(l.getLobbyCreator().equals(nameCreator)){
                return true;
            }
        }

        return false;

    }

}
