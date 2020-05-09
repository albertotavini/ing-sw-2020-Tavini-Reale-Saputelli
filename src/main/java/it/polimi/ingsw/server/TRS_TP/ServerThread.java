package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.utils.ColorAnsi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServerThread implements Runnable {

    private ServerSocket socketAccept;
    private ServerSocket socketPingAndError;
    private boolean isActive = true;
    public Scanner in = new Scanner(System.in);
    //pool di thread ad uso e consumo del server per creare fsmSingleClientHandler e Lobby;
    public static ExecutorService serverExecutor = Executors.newCachedThreadPool();


    public ServerThread(int portAccept, int portPingAndError) throws IOException {

        this.socketAccept = new ServerSocket(portAccept);
        this.socketPingAndError = new ServerSocket(portPingAndError);

    }


    //metodo che fa semplicemente partire il server
    @Override
    public void run() {


        //mando in esecuzione il thread che gestisce la cli del server
        Thread serverCliThread = new Thread(new ServerCliInterfaceThread(this));
        serverCliThread.start();

        //mando in esecuzione il thread che gestisce le accept
        Thread serverAcceptThread = new Thread(new ServerAcceptThread(this));
        serverAcceptThread.start();


        Thread pingAndErrorThread = new Thread(new ServerPingAndErrorAcceptThread(this));
        pingAndErrorThread.start();



        while(isActive) {

            serverThreadWait();

        }


    }
    //da vedere la gestione errori
    public void stopServer() {

        serverExecutor.shutdown();

        try {

            if(!serverExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) serverExecutor.shutdownNow();

            socketAccept.close();
            socketPingAndError.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    //ci dice se un giocatore ha già creato una lobby
    public static boolean hasPlayerAlreadyCreatedALobby(String nameCreator) {

        return (!ListLobbyPublic.hasPlayerAlreadyCreatedALobbyPublic(nameCreator) && !ListLobbyPrivate.hasPlayerAlreadyCreatedALobbyPrivate(nameCreator) && !ListLobbyCasual.hasPlayerAlreadyCreatedALobbyCasual(nameCreator));

    }

    //da vedere meglio
    public void connectionError(Socket clientSocket) throws IOException {

        clientSocket.close();



    }


    private synchronized void serverThreadWait() {

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private synchronized void serverThreadNotify() {

        notify();

    }


    //i tre sotto-thread principali di ServerThread

    //funge da terminale del server: può anche servire per "spegnere" il server
    private class ServerCliInterfaceThread implements Runnable {

        private ServerThread serverThreadReference;


        public ServerCliInterfaceThread(ServerThread serverThreadReference){

            this.serverThreadReference = serverThreadReference;

        }


        @Override
        public void run() {

            String commandInput = null;

            System.out.println("\n\n\n" +ColorAnsi.RED +AsciiArt.santorini4 +ColorAnsi.RESET);
            System.out.println("\nType h for help");

            do{

                System.out.printf(ColorAnsi.RED +"\nTerminal Active > " +ColorAnsi.RESET);

                commandInput = serverThreadReference.in.nextLine();
                String regexInput = "^([phc]) ?(-?[\\w]?[\\w]?[\\w]?)$";

                Pattern patternInput = Pattern.compile(regexInput, Pattern.CASE_INSENSITIVE);
                Matcher matcherInput = patternInput.matcher(commandInput);

                if(matcherInput.find()){

                    switch(matcherInput.group(1).toUpperCase()) {

                        case "P":

                            switch (matcherInput.group(2)) {

                                case "-si":
                                    System.out.println("Insert the sequence:");
                                    printIdentitiesContaining(serverThreadReference.in.nextLine());
                                    break;

                                case "-ai":
                                    System.out.println("**********All identities**********");
                                    printAllIdentities();
                                    break;

                                case "-prl":
                                    System.out.println("**********Private lobbies**********");
                                    printPrivateLobbies();
                                    break;

                                case "-pul":
                                    System.out.println("**********Public lobbies**********");
                                    printPublicLobbies();
                                    break;

                                case "-cal":
                                    System.out.println("**********Casual lobbies**********");
                                    printCasualLobbies();
                                    break;


                                case "-col":
                                    System.out.println("**********Lobby containing**********");
                                    System.out.println("Insert the sequence:");
                                    printLobbyContaining(serverThreadReference.in.nextLine());
                                    break;

                                case "-al":
                                    System.out.println("**********Private lobbies**********");
                                    printPrivateLobbies();
                                    System.out.println("**********Public lobbies**********");
                                    printPublicLobbies();
                                    System.out.println("**********Casual lobbies**********");
                                    printCasualLobbies();
                                    break;


                                default :
                                    System.out.println("Wrong options!!! Type h for help");

                            }
                            break;


                        case "C":

                            //risveglio il processo principale
                            serverThreadReference.isActive = false;
                            serverThreadReference.stopServer();
                            serverThreadReference.serverThreadNotify();
                            break;


                        case "H":

                            String help = ColorAnsi.YELLOW +"                     COMANDI" +ColorAnsi.RESET
                                    +"\nComando h : printa tutti i comandi disponibili"
                                    +"\nComando c : chiude il server e tutte le connessioni attive"
                                    +"\nComando p : esegue una print diversa in base alle opzioni:"
                                    +ColorAnsi.YELLOW
                                    +"\n        **********Lobby commands:"
                                    +ColorAnsi.RESET
                                    +"\n        pul : stampa soltanto le lobby pubbliche"
                                    +"\n        prl : stampa soltanto le lobby private"
                                    +"\n        cal : stampa soltanto le lobby casual"
                                    +"\n        al : stampa tutte le lobby"
                                    +"\n        col : stampa lobby contenenti una sequenza inserita dall'utente"
                                    +ColorAnsi.YELLOW
                                    +"\n        **********Identities commands:"
                                    +ColorAnsi.RESET
                                    +"\n        ai : stampa tutte le identities"
                                    +"\n        si : stampa le identità che contengono una sequenza inserita dall'utente";

                            System.out.println(help);
                            break;


                    }



                }

                else System.out.println("Incorrect command, try again");

            }while(isActive);


            System.out.println(ColorAnsi.YELLOW +"Server closed" +ColorAnsi.RESET);




        }

        public void printPrivateLobbies() {

            int numberOfPrint = 0;
            for(PrivateLobby p : ListLobbyPrivate.list_lobbiesPrivate){
                System.out.println(p.toString());
                numberOfPrint++;

            }

            if(numberOfPrint == 0) System.out.println("Nessuna lobby");


        }

        public void printPublicLobbies() {

            int numberOfPrint = 0;

            for(PublicLobby p : ListLobbyPublic.list_lobbiesPublic){
                System.out.println(p.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println("Nessuna lobby");

        }

        public void printCasualLobbies() {

            int numberOfPrint = 0;

            for(CasualLobby p : ListLobbyCasual.list_lobbiesCasual){
                System.out.println(p.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println("Nessuna lobby");

        }

        public void printAllIdentities() {
            int numberOfPrint = 0;
            for(IdentityCardOfPlayer identityCardOfPlayer : ListIdentities.list_player){
                System.out.println(identityCardOfPlayer.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println("Nessuna lobby");


        }

        public void printLobbyContaining(String sequence) {

            int numberOfMatches = 0;

            sequence = sequence.toUpperCase();

            for(PublicLobby lobby : ListLobbyPublic.list_lobbiesPublic){
                if(lobby.getNameLobby().contains(sequence)){
                    System.out.println("Pubblica " +lobby.toString());
                    numberOfMatches++; } }

            for(PrivateLobby lobby : ListLobbyPrivate.list_lobbiesPrivate){
                if(lobby.getNameLobby().contains(sequence)){
                    System.out.println("Privata " +lobby.toString());
                    numberOfMatches++; } }

            if(numberOfMatches == 0) System.out.println("Nessun match con la sequenza inserita");



        }

        public void printIdentitiesContaining(String charSeq) {

            int numberOfMatch = 0;

            for(IdentityCardOfPlayer identityCardOfPlayer : ListIdentities.list_player){
                if(identityCardOfPlayer.getPlayerName().contains(charSeq)){
                    System.out.println(identityCardOfPlayer.toString());
                    numberOfMatch++; } }

            if(numberOfMatch == 0) System.out.println("Nessun match con la sequenza inserita");


        }




    }
    //thread del server che gestisce l'accept dei client sul canale di comunicazione standard
    private class ServerAcceptThread implements Runnable {

        ServerThread serverThreadReference;

        public ServerAcceptThread(ServerThread serverThreadReference){

            this.serverThreadReference = serverThreadReference;
        }


        @Override
        public void run() {

            Socket socket = null;

            do {


                try {


                    socket = serverThreadReference.socketAccept.accept();
                    serverExecutor.submit(new MenuFsmServerSingleClientHandler(socket, PlayerUniqueCode.getUniquePlayerCode()));


                } catch (Exception e) {
                    serverThreadReference.stopServer();
                }


            }while(serverThreadReference.isActive);


        }
    }
    //thread del server che gestisce l'accept dei client sul canale di comunicazione per il ping e gli errori
    private class ServerPingAndErrorAcceptThread implements Runnable {

        private ServerThread serverThreadReference;

        public ServerPingAndErrorAcceptThread(ServerThread serverThreadReference){
            this.serverThreadReference = serverThreadReference;

        }

        @Override
        public void run() {


            Socket socketError = null;

            do {



                try {


                    socketError = serverThreadReference.socketPingAndError.accept();
                    serverExecutor.submit(new AsyncronousPingAndErrorHandler(socketError));



                } catch (Exception e) {


                    //da vedere

                }


            }while(serverThreadReference.isActive);

        }
    }



    //in comune tra tutti i ServerThread!!!! (anche su porte diverse)
    //inner class che gestisce la generazione del playerUniqueCode
    private static class PlayerUniqueCode {

        //numero utile per generare lo unique code
        private static short cyclicNumberForUniqueCode = 0;

        //metodo helper che restituisce un playerUniqueCode
        public static String getUniquePlayerCode() {

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





    }
    //inner class che gestisce le lobby private
    static class ListLobbyPrivate {

        //array list delle lobby pubbliche attualmente presenti sul server
        private static ArrayList<PrivateLobby> list_lobbiesPrivate = new ArrayList<>();

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


        public static boolean hasPlayerAlreadyCreatedALobbyPrivate(String nameCreator){

            synchronized (list_lobbiesPrivate){
                for(Lobby l : list_lobbiesPrivate){
                    if(l.getLobbyCreator().equals(nameCreator)){
                        return true;
                    }
                }

                return false;

                }
        }




    }
    //inner class che gestisce le lobby pubbliche
    static class ListLobbyPublic {

        //array list delle lobby pubbliche attualmente presenti sul server
        private static ArrayList<PublicLobby> list_lobbiesPublic = new ArrayList<>();


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


        public static boolean hasPlayerAlreadyCreatedALobbyPublic(String nameCreator){

            synchronized (list_lobbiesPublic){
                for(Lobby l : list_lobbiesPublic){
                    if(l.getLobbyCreator().equals(nameCreator)){
                        return true;
                    }
                }

                return false;

            }
        }


    }
    //inner class che gestisce le lobby pubbliche
    static class ListLobbyCasual {

        //array list delle lobby pubbliche attualmente presenti sul server
        private static ArrayList<CasualLobby> list_lobbiesCasual = new ArrayList<>();


        //aggiunge la lobby all'arraylist delle lobby casual
        public static boolean addToListLobbyCasual(CasualLobby lobby) {


            synchronized (list_lobbiesCasual) {

                list_lobbiesCasual.add(lobby);
                return true;

            }
        }

        public static ArrayList<CasualLobby> getList_lobbiesCasual() {
            return list_lobbiesCasual;
        }

        public static boolean hasPlayerAlreadyCreatedALobbyCasual(String nameCreator){

            synchronized (list_lobbiesCasual){
                for(Lobby l : list_lobbiesCasual){
                    if(l.getLobbyCreator().equals(nameCreator)){
                        return true;
                    }
                }

                return false;

            }
        }


    }
    //inner class che gestisce la lista delle identità
    static class ListIdentities {

        //array list delle identità dei player attualmente presenti sul server
        private static ArrayList<IdentityCardOfPlayer> list_player= new ArrayList<>();

        //aggiunge il giocatore alla lista delle identità e restituisce vero se l'operazione è andata a buon fine
        public static boolean addPlayerToListIdentities(IdentityCardOfPlayer playerIdentity) {
            synchronized(list_player){
                for(IdentityCardOfPlayer n : list_player){
                    if(playerIdentity.getPlayerName().equals(n.getPlayerName())) return false;
                }

                list_player.add(playerIdentity);}
            return true;

        }

        public static IdentityCardOfPlayer retrievePlayerIdentity(String uniquePlayerCode) {

            for(IdentityCardOfPlayer n : list_player){
                if(n.getUniquePlayerCode().equals(uniquePlayerCode)) return n;}

            return null;




        }

        public static String retrievePlayerName(String uniquePlayerCode) {
            for(IdentityCardOfPlayer n : list_player){
                if(n.getUniquePlayerCode().equals(uniquePlayerCode)) return n.getPlayerName();}

            return null;

        }

        public static void removePlayerFromListIdentities(String namePlayer){

            synchronized (list_player){
                for(int i = 0; i < list_player.size(); i++) {
                    if (namePlayer.equals(list_player.get(i).getPlayerName())) list_player.remove(i);
                } }
        }

    }


}










