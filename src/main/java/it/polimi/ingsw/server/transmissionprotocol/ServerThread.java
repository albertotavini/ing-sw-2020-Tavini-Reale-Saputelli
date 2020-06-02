package it.polimi.ingsw.server.transmissionprotocol;

import it.polimi.ingsw.bothsides.utils.AsciiArt;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServerThread implements Runnable {

    private final ServerSocket socketAccept;
    private final ServerSocket socketPingAndError;

    private boolean isActive = true;
    public final Scanner in = new Scanner(System.in);
    public static final ExecutorService serverExecutor = Executors.newCachedThreadPool();

    private static HashMap<String, ServerFsm> uniquePlayerToFsm = new HashMap<>();


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


        try {

            serverCliThread.join();
            serverAcceptThread.join();
            pingAndErrorThread.join();

        } catch (InterruptedException e) {
            LogPrinter.printOnLog(Global.SERVERTHREADRUNFAILED);
            LogPrinter.printOnLog(e.toString());
            Thread.currentThread().interrupt();
            System.exit(-1);
        }


    }
    //da vedere la gestione errori
    public void stopServer() {

        serverExecutor.shutdown();

        try {

            if(!serverExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) serverExecutor.shutdownNow();

            socketAccept.close();
            socketPingAndError.close();

        } catch (Exception e) {
            LogPrinter.printOnLog(Global.ERRORWHILETERMINATINGSERVEREXECUTOR);
            LogPrinter.printOnLog(e.toString());
        }
    }


    //ci dice se un giocatore non ha già creato una lobby
    public static boolean playerHasNotCreatedALobby(String nameCreator) {

        return (!ListLobbyPublic.hasPlayerAlreadyCreatedALobbyPublic(nameCreator) && !ListLobbyPrivate.hasPlayerAlreadyCreatedALobbyPrivate(nameCreator) && !ListLobbyCasual.hasPlayerAlreadyCreatedALobbyCasual(nameCreator));

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

            System.out.println("\n\n\n" +ColorAnsi.RED + AsciiArt.SANTORINI_4 +ColorAnsi.RESET);
            System.out.println(Global.TYPEHFORHELP);

            do{

                System.out.printf("%s%s%s", ColorAnsi.RED, Global.TERMINALACTIVE, ColorAnsi.RESET);

                commandInput = serverThreadReference.in.nextLine();
                String regexInput = "^([phc]) ?(-?[\\w]?[\\w]?[\\w]?)$";

                Pattern patternInput = Pattern.compile(regexInput, Pattern.CASE_INSENSITIVE);
                Matcher matcherInput = patternInput.matcher(commandInput);

                if(matcherInput.find()){

                    switch(matcherInput.group(1).toUpperCase()) {

                        case "P":

                            switch (matcherInput.group(2)) {

                                case "-si":
                                    System.out.println(Global.INSERTTHESEQUENCE);
                                    printIdentitiesContaining(serverThreadReference.in.nextLine());
                                    break;

                                case "-ai":
                                    System.out.println(Global.ALLIDENTITIES);
                                    printAllIdentities();
                                    break;

                                case "-prl":
                                    System.out.println(Global.ALLPRIVATELOBBIES);
                                    printPrivateLobbies();
                                    break;

                                case "-pul":
                                    System.out.println(Global.ALLPUBLICLOBBIES);
                                    printPublicLobbies();
                                    break;

                                case "-cal":
                                    System.out.println(Global.ALLCASUALLOBBIES);
                                    printCasualLobbies();
                                    break;


                                case "-col":
                                    System.out.println(Global.LOBBYCONTAINING);
                                    System.out.println(Global.INSERTTHESEQUENCE);
                                    printLobbyContaining(serverThreadReference.in.nextLine());
                                    break;

                                case "-al":
                                    System.out.println(Global.ALLPRIVATELOBBIES);
                                    printPrivateLobbies();
                                    System.out.println(Global.ALLPUBLICLOBBIES);
                                    printPublicLobbies();
                                    System.out.println(Global.ALLCASUALLOBBIES);
                                    printCasualLobbies();
                                    break;


                                default :
                                    System.out.println(Global.WRONGOPTIONS);

                            }
                            break;


                        case "C":

                            //risveglio il processo principale
                            LogPrinter.printLogOnFile();
                            serverThreadReference.isActive = false;
                            serverThreadReference.stopServer();
                            break;


                        case "H":

                            String help = ColorAnsi.YELLOW +"                     OPTIONS" +ColorAnsi.RESET
                                    +"\nOption h : prints all availables options"
                                    +"\nOption c : closes the server and all the active connections"
                                    +"\nOption p : performs a different print depending on the options:"
                                    +ColorAnsi.YELLOW
                                    +"\n        **********Lobby commands:"
                                    +ColorAnsi.RESET
                                    +"\n        pul : prints just public lobbies"
                                    +"\n        prl : prints just private lobbies"
                                    +"\n        cal : prints just casual lobbies"
                                    +"\n        al : prints all the lobbies"
                                    +"\n        col : prints all the lobbies whose name contains an inserted string"
                                    +ColorAnsi.YELLOW
                                    +"\n        **********Identities commands:"
                                    +ColorAnsi.RESET
                                    +"\n        ai : prints all the identities"
                                    +"\n        si : prints all the identities whose name contains an inserted string";

                            System.out.println(help);
                            break;




                        default:
                            break;

                    }



                }

                else System.out.println(Global.INCORRECTCOMMAND);

            }while(isActive);


            System.out.println(ColorAnsi.YELLOW + Global.SERVERCLOSED +ColorAnsi.RESET);




        }

        public void printPrivateLobbies() {

            int numberOfPrint = 0;
            for(PrivateLobby p : ListLobbyPrivate.listLobbiesPrivate){
                System.out.println(p.toString());
                numberOfPrint++;

            }

            if(numberOfPrint == 0) System.out.println(Global.THEREISNOLOBBY);


        }

        public void printPublicLobbies() {

            int numberOfPrint = 0;

            for(PublicLobby p : ListLobbyPublic.listLobbiesPublic){
                System.out.println(p.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println(Global.THEREISNOLOBBY);

        }

        public void printCasualLobbies() {

            int numberOfPrint = 0;

            for(CasualLobby p : ListLobbyCasual.listLobbiesCasual){
                System.out.println(p.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println(Global.THEREISNOLOBBY);

        }

        public void printAllIdentities() {
            int numberOfPrint = 0;
            for(IdentityCardOfPlayer identityCardOfPlayer : ListIdentities.listPlayer){
                System.out.println(identityCardOfPlayer.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println(Global.THEREISNOLOBBY);


        }

        public void printLobbyContaining(String sequence) {

            int numberOfMatches = 0;

            sequence = sequence.toUpperCase();

            for(PublicLobby lobby : ListLobbyPublic.listLobbiesPublic){
                if(lobby.getNameLobby().contains(sequence)){
                    System.out.println("Public: " +lobby.toString());
                    numberOfMatches++; } }

            for(PrivateLobby lobby : ListLobbyPrivate.listLobbiesPrivate){
                if(lobby.getNameLobby().contains(sequence)){
                    System.out.println("Private: " +lobby.toString());
                    numberOfMatches++; } }

            if(numberOfMatches == 0) System.out.println(Global.THEREISNOMATCHWITHTHEINSERTEDSEQUENCE);



        }

        public void printIdentitiesContaining(String charSeq) {

            int numberOfMatch = 0;

            for(IdentityCardOfPlayer identityCardOfPlayer : ListIdentities.listPlayer){
                if(identityCardOfPlayer.getPlayerName().contains(charSeq)){
                    System.out.println(identityCardOfPlayer.toString());
                    numberOfMatch++; } }

            if(numberOfMatch == 0) System.out.println(Global.THEREISNOMATCHWITHTHEINSERTEDSEQUENCE);


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

            ServerFsm serverFsm = null;
            String uniquePlayerCode = null;

            do {


                try {


                    socket = serverThreadReference.socketAccept.accept();

                    uniquePlayerCode = PlayerUniqueCode.getUniquePlayerCode();
                    serverFsm = new ServerFsm(socket, uniquePlayerCode);
                    uniquePlayerToFsm.put(uniquePlayerCode, serverFsm);
                    serverExecutor.submit(serverFsm);


                } catch (Exception e) {

                    serverThreadReference.stopServer();
                    serverThreadReference.isActive = false;

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

                    serverThreadReference.stopServer();
                    serverThreadReference.isActive = false;

                }


            }while(serverThreadReference.isActive);

        }
    }

    public static ServerFsm getFsmByUniqueCode(String uniqueCode){

        synchronized (uniquePlayerToFsm){

            return uniquePlayerToFsm.get(uniqueCode);

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

        private ListLobbyPrivate(){
            //hiding the default constructor
        }

        //array list delle lobby private attualmente presenti sul server
        private static ArrayList<PrivateLobby> listLobbiesPrivate = new ArrayList<>();

        //aggiunge la lobby all'arraylist delle lobby private
        public static boolean addToListLobbyPrivate(PrivateLobby lobby) {

            String nomeLobby = lobby.getNameLobby().toUpperCase();

            synchronized (listLobbiesPrivate) {
                for(Lobby l : listLobbiesPrivate){
                    if(l.getNameLobby().equals(nomeLobby)){
                        return false;
                    }
                }

                listLobbiesPrivate.add(lobby);

                return true;
            }
        }

        //il nome della lobby viene messo in automatico in upperCase
        public static PrivateLobby findLobbyPrivate(String nameLobby) {
            nameLobby = nameLobby.toUpperCase();

            synchronized (listLobbiesPrivate){
                for(PrivateLobby l : listLobbiesPrivate){
                    if(l.getNameLobby().equals(nameLobby)){
                        return l;
                    }}
            }

            return null;



        }


        public static boolean hasPlayerAlreadyCreatedALobbyPrivate(String nameCreator){

            synchronized (listLobbiesPrivate){
                for(Lobby l : listLobbiesPrivate){
                    if(l.getLobbyCreator().equals(nameCreator)){
                        return true;
                    }
                }

                return false;

                }
        }

        public static void deleteLobbyPrivate(PrivateLobby lobby){

            synchronized (listLobbiesPrivate){

                for(int i = 0; i < listLobbiesPrivate.size(); i++){

                    if(listLobbiesPrivate.get(i).getNameLobby().equals(lobby.getNameLobby())){
                        listLobbiesPrivate.remove(i);
                    }


                }


            }


        }




    }
    //inner class che gestisce le lobby pubbliche
    static class ListLobbyPublic {

        private ListLobbyPublic(){
            //hiding the default constructor
        }

        //array list delle lobby pubbliche attualmente presenti sul server
        private static ArrayList<PublicLobby> listLobbiesPublic = new ArrayList<>();


        //aggiunge la lobby all'arraylist delle lobby pubbliche
        public static boolean addToListLobbyPublic(PublicLobby lobby) {

            String nomeLobbyPublic = lobby.getNameLobby().toUpperCase();

            synchronized (listLobbiesPublic) {
                for(Lobby l : listLobbiesPublic){
                    if(l.getNameLobby().equals(nomeLobbyPublic)){
                        return false;
                    }
                }

                listLobbiesPublic.add(lobby);

                return true;
            }
        }

        //il nome della lobby viene messo in automatico in upperCase
        public static PublicLobby findLobbyPublic(String nameLobby) {
            nameLobby = nameLobby.toUpperCase();

            synchronized (listLobbiesPublic){
                for(PublicLobby l : listLobbiesPublic){
                    if(l.getNameLobby().equals(nameLobby)){
                        return l;
                    }}
            }

            return null;



        }

        public static void deleteLobbyPublic(PublicLobby lobby){

            synchronized (listLobbiesPublic){

                for(int i = 0; i < listLobbiesPublic.size(); i++){

                    if(listLobbiesPublic.get(i).getNameLobby().equals(lobby.getNameLobby())){
                        listLobbiesPublic.remove(i);
                    }


                }


            }


        }



        public static boolean hasPlayerAlreadyCreatedALobbyPublic(String nameCreator){

            synchronized (listLobbiesPublic){
                for(Lobby l : listLobbiesPublic){
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

        private ListLobbyCasual(){
            //hiding the default constructor
        }

        //array list delle lobby casual attualmente presenti sul server
        private static ArrayList<CasualLobby> listLobbiesCasual = new ArrayList<>();


        //aggiunge la lobby all'arraylist delle lobby casual
        public static boolean addToListLobbyCasual(CasualLobby lobby) {


            synchronized (listLobbiesCasual) {

                listLobbiesCasual.add(lobby);
                return true;

            }
        }

        public static List<CasualLobby> getListLobbiesCasual() {
            return listLobbiesCasual;
        }

        public static void deleteLobbyCasual(CasualLobby lobby){

            synchronized (listLobbiesCasual){

                for(int i = 0; i < listLobbiesCasual.size(); i++){

                    if(listLobbiesCasual.get(i).getNameLobby().equals(lobby.getNameLobby())){
                        listLobbiesCasual.remove(i);
                    }


                }


            }


        }


        public static boolean hasPlayerAlreadyCreatedALobbyCasual(String nameCreator){

            synchronized (listLobbiesCasual){
                for(Lobby l : listLobbiesCasual){
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

        private ListIdentities(){
            //hiding the default constructor
        }

        //array list delle identità dei player attualmente presenti sul server
        private static ArrayList<IdentityCardOfPlayer> listPlayer = new ArrayList<>();

        //aggiunge il giocatore alla lista delle identità e restituisce vero se l'operazione è andata a buon fine
        public static boolean addPlayerToListIdentities(IdentityCardOfPlayer playerIdentity) {
            synchronized(listPlayer){
                for(IdentityCardOfPlayer n : listPlayer){
                    if(playerIdentity.getPlayerName().equals(n.getPlayerName())) return false;
                }

                listPlayer.add(playerIdentity);}
            return true;

        }

        public static IdentityCardOfPlayer retrievePlayerIdentity(String uniquePlayerCode) {

            for(IdentityCardOfPlayer n : listPlayer){
                if(n.getUniquePlayerCode().equals(uniquePlayerCode)) return n;}

            return null;




        }

        public static IdentityCardOfPlayer retrievePlayerIdentityByName(String namePlayer) {

            for(IdentityCardOfPlayer n : listPlayer){
                if(n.getPlayerName().equals(namePlayer)) return n;}

            return null;




        }

        public static String retrievePlayerName(String uniquePlayerCode) {
            for(IdentityCardOfPlayer n : listPlayer){
                if(n.getUniquePlayerCode().equals(uniquePlayerCode)) return n.getPlayerName();}

            return null;

        }

        public static void removePlayerFromListIdentities(String uniquePlayerCode) {

            synchronized (listPlayer){
                for(int i = 0; i < listPlayer.size(); i++) {
                    if (uniquePlayerCode.equals(listPlayer.get(i).getUniquePlayerCode())) listPlayer.remove(i);
                } }
        }

    }


}










