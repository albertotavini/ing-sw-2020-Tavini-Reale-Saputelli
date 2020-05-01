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


public class ServerConnection {

    private static ServerSocket socketAccept;
    private static ServerSocket socketPingAndError;
    private static boolean isActive = true;
    public static Scanner in = new Scanner(System.in);
    //pool di thread ad uso e consumo del server per creare fsmSingleClientHandler, WorkStealingPool migliora il parallelismo
    public static ExecutorService serverExecutor = Executors.newWorkStealingPool();


    public ServerConnection(int portAccept, int portPingAndError) throws IOException {

        this.socketAccept = new ServerSocket(portAccept);
        this.socketPingAndError = new ServerSocket(portPingAndError);

    }


    //metodo che fa semplicemente partire il server
    public void runServer() throws IOException, InterruptedException {

        //mando in esecuzione il thread che gestisce la cli del server
        Thread serverCliThread = new Thread(new ServerCliInterfaceThread());
        serverCliThread.start();

        //mando in esecuzione il thread che gestisce le accept
        Thread serverAcceptThread = new Thread(new ServerAcceptThread());
        serverAcceptThread.start();


        Thread pingAndErrorThread = new Thread(new ServerPingAndErrorAcceptThread());
        pingAndErrorThread.start();

        //perchè non una wait?
        while(isActive)
        {

            Thread.currentThread().sleep(5000);

        }


        return;


    }
    //da vedere la gestione errori
    public static void stopServer() {

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

        return ListLobbyPublic.hasPlayerAlreadyCreatedALobbyPublic(nameCreator) || ListLobbyPrivate.hasPlayerAlreadyCreatedALobbyPrivate(nameCreator);

    }
    //da vedere meglio
    public static void connectionError(Socket clientSocket) throws IOException {

        clientSocket.close();



    }

    //i tre thread principali del server

    //funge da terminale del server: può anche servire per "spegnere" il server
    static class ServerCliInterfaceThread implements Runnable {


        @Override
        public void run() {

            String commandInput = null;

            System.out.println("\n\n\n" +ColorAnsi.RED +AsciiArt.santorini4 +ColorAnsi.RESET);
            System.out.println("\nType h for help");

            do{

                System.out.printf(ColorAnsi.RED +"\nTerminal Active > " +ColorAnsi.RESET);

                commandInput = ServerConnection.in.nextLine();
                String regexInput = "^([phc]) ?(-?[\\w]?[\\w]?[\\w]?)$";

                Pattern patternInput = Pattern.compile(regexInput, Pattern.CASE_INSENSITIVE);
                Matcher matcherInput = patternInput.matcher(commandInput);

                if(matcherInput.find()){

                    switch(matcherInput.group(1).toUpperCase()) {

                        case "P":

                            switch (matcherInput.group(2)) {

                                case "-si":
                                    System.out.println("Insert the sequence:");
                                    ServerCliInterfaceThread.printIdentitiesContaining(ServerConnection.in.nextLine());
                                    break;

                                case "-ai":
                                    System.out.println("**********All identities**********");
                                    ServerCliInterfaceThread.printAllIdentities();
                                    break;

                                case "-prl":
                                    System.out.println("**********Private lobbies**********");
                                    ServerCliInterfaceThread.printPrivateLobbies();
                                    break;

                                case "-pul":
                                    System.out.println("**********Public lobbies**********");
                                    ServerCliInterfaceThread.printPublicLobbies();
                                    break;



                                case "-col":
                                    System.out.println("**********Lobby containing**********");
                                    System.out.println("Insert the sequence:");
                                    ServerCliInterfaceThread.printLobbyContaining(ServerConnection.in.nextLine());
                                    break;

                                case "-al":
                                    System.out.println("**********Private lobbies**********");
                                    ServerCliInterfaceThread.printPrivateLobbies();
                                    System.out.println("**********Public lobbies**********");
                                    ServerCliInterfaceThread.printPublicLobbies();
                                    break;


                                default :
                                    System.out.println("Wrong options!!! Type h for help");

                            }
                            break;


                        case "C":

                            //risveglio il processo principale
                            ServerConnection.isActive = false;
                            ServerConnection.stopServer();
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

            return;


        }

        public static void printPrivateLobbies() {

            int numberOfPrint = 0;
            for(PrivateLobby p : ListLobbyPrivate.list_lobbiesPrivate){
                System.out.println(p.toString());
                numberOfPrint++;

            }

            if(numberOfPrint == 0) System.out.println("Nessuna lobby");


        }

        public static void printPublicLobbies() {

            int numberOfPrint = 0;

            for(PublicLobby p : ListLobbyPublic.list_lobbiesPublic){
                System.out.println(p.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println("Nessuna lobby");

        }

        public static void printAllIdentities() {
            int numberOfPrint = 0;
            for(IdentityCardOfPlayer identityCardOfPlayer : ListIdentities.list_player){
                System.out.println(identityCardOfPlayer.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println("Nessuna lobby");


        }

        public static void printLobbyContaining(String sequence) {

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

        public static void printIdentitiesContaining(String charSeq) {

            int numberOfMatch = 0;

            for(IdentityCardOfPlayer identityCardOfPlayer : ListIdentities.list_player){
                if(identityCardOfPlayer.getPlayerName().contains(charSeq)){
                    System.out.println(identityCardOfPlayer.toString());
                    numberOfMatch++; } }

            if(numberOfMatch == 0) System.out.println("Nessun match con la sequenza inserita");


        }




    }
    //thread del server che gestisce l'accept dei client sul canale di comunicazione standard
    static class ServerAcceptThread implements Runnable {

        @Override
        public void run() {

            do {

                Socket socket = null;

                try {


                    socket = ServerConnection.socketAccept.accept();
                    serverExecutor.submit(new MenuFsmServerSingleClientHandler(socket, PlayerUniqueCode.getUniquePlayerCode()));


                } catch (Exception e) {
                    ServerConnection.stopServer();
                }


            }while(isActive);


        }
    }
    //thread del server che gestisce l'accept dei client sul canale di comunicazione per il ping e gli errori
    static class ServerPingAndErrorAcceptThread implements Runnable {

        @Override
        public void run() {

            do {

                Socket socketPingAndError = null;

                try {


                    socketPingAndError = ServerConnection.socketPingAndError.accept();
                    serverExecutor.submit(new AsyncronousPingAndErrorHandler(socketPingAndError));



                } catch (Exception e) {


                    //da vedere

                }


            }while(isActive);

        }
    }


    //inner class che gestisce la generazione del playerUniqueCode
    static class PlayerUniqueCode {

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

        public static IdentityCardOfPlayer retrievePlayerIdentity(String uniquePlayerCode){

            for(IdentityCardOfPlayer n : list_player){
                if(n.getUniquePlayerCode().equals(uniquePlayerCode)) return n;}

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










