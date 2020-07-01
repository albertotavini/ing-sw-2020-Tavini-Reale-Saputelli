package it.polimi.ingsw.server.transmissionprotocol;

import it.polimi.ingsw.bothsides.utils.AsciiArt;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.server.ServerMain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * this is the principal thread on which the server runs, handles lobbies and interacts with clients
 */
public class ServerThread implements Runnable {

    private final ServerSocket socketAccept;
    private final ServerSocket socketPingAndError;
    private final ServerSocket socketAcceptChat1;
    private final ServerSocket socketAcceptChat2;


    private boolean isActive = true;
    public final Scanner in = new Scanner(System.in);
    public static final ExecutorService serverExecutor = Executors.newCachedThreadPool();

    private static HashMap<String, ServerFsm> uniquePlayerToFsm = new HashMap<>();


    public ServerThread(int portAccept, int portPingAndError, int portChat1, int portChat2) throws IOException {

        this.socketAccept = new ServerSocket(portAccept);
        this.socketPingAndError = new ServerSocket(portPingAndError);
        this.socketAcceptChat1 = new ServerSocket(portChat1);
        this.socketAcceptChat2 = new ServerSocket(portChat2);

    }


    /**
     * simply runs the threads that deal with the interface of the server and the ones that deal with the accepts of the clients
     */
    @Override
    public void run() {


        //calls the thread that controls the cli
        Thread serverCliThread = new Thread(new ServerCliInterfaceThread(this));
        serverCliThread.start();

        //calls the thread that deals with accepts of the thread
        Thread serverAcceptThread = new Thread(new ServerAcceptThread(this));
        serverAcceptThread.start();


        Thread pingAndErrorThread = new Thread(new ServerPingAndErrorAcceptThread(this));
        pingAndErrorThread.start();

        Thread chatAccept1Thread = new Thread(new ServerChat1AcceptThread(this));
        chatAccept1Thread.start();

        Thread chatAccept2Thread = new Thread(new ServerChat2AcceptThread(this));
        chatAccept2Thread.start();


        try {

            serverCliThread.join();
            serverAcceptThread.join();
            pingAndErrorThread.join();
            chatAccept1Thread.join();
            chatAccept2Thread.join();

        } catch (InterruptedException e) {
            LogPrinter.printOnLog(Global.SERVERTHREADRUNFAILED);
            LogPrinter.printOnLog(e.toString());
            Thread.currentThread().interrupt();
            System.exit(-1);
        }


    }

    /**
     * method to kill the server when needed
     */
    public void stopServer() {

        serverExecutor.shutdown();

        try {

            if(!serverExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) serverExecutor.shutdownNow();

            socketAccept.close();
            socketPingAndError.close();
            socketAcceptChat1.close();

        } catch (Exception e) {
            LogPrinter.printOnLog(Global.ERRORWHILETERMINATINGSERVEREXECUTOR);
            LogPrinter.printOnLog(e.toString());
        }
    }


    /**
     * @param nameCreator of the one we want to know about
     * @return true if the player whose name is passed did not create any lobby
     */
    public static boolean playerHasNotCreatedALobby(String nameCreator) {

        return (!ListLobbyPublic.hasPlayerAlreadyCreatedALobbyPublic(nameCreator) && !ListLobbyPrivate.hasPlayerAlreadyCreatedALobbyPrivate(nameCreator) && !ListLobbyCasual.hasPlayerAlreadyCreatedALobbyCasual(nameCreator));

    }



    //class that allows the Server to be interfaced and visualize info about active lobbies and connected players
    private class ServerCliInterfaceThread implements Runnable {

        private ServerThread serverThreadReference;

        public ServerCliInterfaceThread(ServerThread serverThreadReference){

            this.serverThreadReference = serverThreadReference;

        }

        @Override
        public void run() {

            String commandInput = null;

            System.out.println("\n\n\n"+ AsciiArt.SANTORINI_4 );
            System.out.println(Global.TYPEHFORHELP );

            if (ServerMain.isNeededToPrintLog()) {
                System.out.println("\nYour path for the server log: "  +ServerMain.getServerLogPath() );
            }


            do{

                System.out.printf("%s", Global.TERMINALACTIVE);

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
                            if (ServerMain.isNeededToPrintLog()) {
                                LogPrinter.printLogOnFile();
                            }
                            serverThreadReference.isActive = false;
                            serverThreadReference.stopServer();
                            System.exit(0);
                            break;


                        case "H":

                            String help = "                     OPTIONS"
                                    +"\nOption h : prints all availables options"
                                    +"\nOption c : closes the server and all the active connections"
                                    +"\nOption p : performs a different print depending on the options:"
                                    +"\n        **********Lobby commands:"
                                    +"\n        pul : prints just public lobbies"
                                    +"\n        prl : prints just private lobbies"
                                    +"\n        cal : prints just casual lobbies"
                                    +"\n        al : prints all the lobbies"
                                    +"\n        col : prints all the lobbies whose name contains an inserted string"
                                    +"\n        **********Identities commands:"
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


            System.out.println(Global.SERVERCLOSED);




        }

        private void printPrivateLobbies() {

            int numberOfPrint = 0;
            for(PrivateLobby p : ListLobbyPrivate.listLobbiesPrivate){
                System.out.println(p.toString());
                numberOfPrint++;

            }

            if(numberOfPrint == 0) System.out.println(Global.THEREISNOLOBBY);


        }

        private void printPublicLobbies() {

            int numberOfPrint = 0;

            for(PublicLobby p : ListLobbyPublic.listLobbiesPublic){
                System.out.println(p.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println(Global.THEREISNOLOBBY);

        }

        private void printCasualLobbies() {

            int numberOfPrint = 0;

            for(CasualLobby p : ListLobbyCasual.listLobbiesCasual){
                System.out.println(p.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println(Global.THEREISNOLOBBY);

        }

        private void printAllIdentities() {
            int numberOfPrint = 0;
            for(IdentityCardOfPlayer identityCardOfPlayer : ListIdentities.listPlayer){
                System.out.println(identityCardOfPlayer.toString());
                numberOfPrint++;
            }
            if(numberOfPrint == 0) System.out.println("There is no identity");


        }

        private void printLobbyContaining(String sequence) {

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

        private void printIdentitiesContaining(String charSeq) {

            int numberOfMatch = 0;

            for(IdentityCardOfPlayer identityCardOfPlayer : ListIdentities.listPlayer){
                if(identityCardOfPlayer.getPlayerName().contains(charSeq)){
                    System.out.println(identityCardOfPlayer.toString());
                    numberOfMatch++; } }

            if(numberOfMatch == 0) System.out.println(Global.THEREISNOMATCHWITHTHEINSERTEDSEQUENCE);


        }




    }

    /**
     * thread of the server that deals with accept of the client on the standard comunication channel
     */
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

    /**
     * thread of the server that deals with accept of clients on the channel dedicated to ping and errors
     */
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

    /**
     * thread that deals with accept of clients on the first channel dedicated to chat messages for the GUI
     */
    private class ServerChat1AcceptThread implements Runnable {

        private ServerThread serverThreadReference;

        public ServerChat1AcceptThread(ServerThread serverThreadReference){
            this.serverThreadReference = serverThreadReference;

        }

        @Override
        public void run() {


            Socket socketChat1 = null;

            do {



                try {


                    socketChat1 = serverThreadReference.socketAcceptChat1.accept();
                    serverExecutor.submit(new ChatHandler(socketChat1, true));



                } catch (Exception e) {

                    serverThreadReference.stopServer();
                    serverThreadReference.isActive = false;

                }


            }while(serverThreadReference.isActive);

        }
    }

    /**
     * thread that deals with accept of clients on the second channel dedicated to chat messages for the GUI
     */
    private class ServerChat2AcceptThread implements Runnable {

        private ServerThread serverThreadReference;

        public ServerChat2AcceptThread(ServerThread serverThreadReference){
            this.serverThreadReference = serverThreadReference;

        }

        @Override
        public void run() {


            Socket socketChat = null;

            do {



                try {


                    socketChat = serverThreadReference.socketAcceptChat2.accept();
                    serverExecutor.submit(new ChatHandler(socketChat, false));



                } catch (Exception e) {

                    serverThreadReference.stopServer();
                    serverThreadReference.isActive = false;

                }


            }while(serverThreadReference.isActive);

        }
    }

    /**
     * @param uniqueCode of the players
     * @return serverFSM associated to that player
     */
    static ServerFsm getFsmByUniqueCode(String uniqueCode){

        synchronized (uniquePlayerToFsm){

            return uniquePlayerToFsm.get(uniqueCode);

        }


    }


    /**
     * inner class that deals with the creation of the uniquePlayerCodes
     */
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

    /**
     * inner class that manages private lobbies
     */
    static class ListLobbyPrivate {

        private ListLobbyPrivate(){
            //hiding the default constructor
        }

        private static ArrayList<PrivateLobby> listLobbiesPrivate = new ArrayList<>();

        static boolean addToListLobbyPrivate(PrivateLobby lobby) {

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

        static PrivateLobby findLobbyPrivate(String nameLobby) {
            nameLobby = nameLobby.toUpperCase();

            synchronized (listLobbiesPrivate){
                for(PrivateLobby l : listLobbiesPrivate){
                    if(l.getNameLobby().equals(nameLobby)){
                        return l;
                    }}
            }

            return null;



        }


        static boolean hasPlayerAlreadyCreatedALobbyPrivate(String nameCreator){

            synchronized (listLobbiesPrivate){
                for(Lobby l : listLobbiesPrivate){
                    if(l.getLobbyCreator().equals(nameCreator)){
                        return true;
                    }
                }

                return false;

                }
        }

        static void deleteLobbyPrivate(PrivateLobby lobby){

            synchronized (listLobbiesPrivate){

                for(int i = 0; i < listLobbiesPrivate.size(); i++){

                    if(listLobbiesPrivate.get(i).getNameLobby().equals(lobby.getNameLobby())){
                        listLobbiesPrivate.remove(i);
                    }


                }


            }


        }




    }
    /**
     * inner class that manages public lobbies
     */
    static class ListLobbyPublic {

        private ListLobbyPublic(){
            //hiding the default constructor
        }

        private static ArrayList<PublicLobby> listLobbiesPublic = new ArrayList<>();

        static boolean addToListLobbyPublic(PublicLobby lobby) {

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

        static PublicLobby findLobbyPublic(String nameLobby) {
            nameLobby = nameLobby.toUpperCase();

            synchronized (listLobbiesPublic){
                for(PublicLobby l : listLobbiesPublic){
                    if(l.getNameLobby().equals(nameLobby)){
                        return l;
                    }}
            }

            return null;



        }

        static void deleteLobbyPublic(PublicLobby lobby){

            synchronized (listLobbiesPublic){

                for(int i = 0; i < listLobbiesPublic.size(); i++){

                    if(listLobbiesPublic.get(i).getNameLobby().equals(lobby.getNameLobby())){
                        listLobbiesPublic.remove(i);
                    }


                }


            }


        }



        static boolean hasPlayerAlreadyCreatedALobbyPublic(String nameCreator){

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
    /**
     * inner class that manages casual lobbies
     */
    static class ListLobbyCasual {

        private ListLobbyCasual(){
            //hiding the default constructor
        }

        private static ArrayList<CasualLobby> listLobbiesCasual = new ArrayList<>();


        static boolean addToListLobbyCasual(CasualLobby lobby) {


            synchronized (listLobbiesCasual) {

                listLobbiesCasual.add(lobby);
                return true;

            }
        }

        static List<CasualLobby> getListLobbiesCasual() {
            return listLobbiesCasual;
        }

        static void deleteLobbyCasual(CasualLobby lobby){

            synchronized (listLobbiesCasual){

                for(int i = 0; i < listLobbiesCasual.size(); i++){

                    if(listLobbiesCasual.get(i).getNameLobby().equals(lobby.getNameLobby())){
                        listLobbiesCasual.remove(i);
                    }


                }


            }


        }


        static boolean hasPlayerAlreadyCreatedALobbyCasual(String nameCreator){

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
    /**
     * inner class that manages the player's identities
     */
    static class ListIdentities {

        private ListIdentities(){
            //hiding the default constructor
        }

        private static ArrayList<IdentityCardOfPlayer> listPlayer = new ArrayList<>();

        static boolean addPlayerToListIdentities(IdentityCardOfPlayer playerIdentity) {
            synchronized(listPlayer){
                for(IdentityCardOfPlayer n : listPlayer){
                    if(playerIdentity.getPlayerName().equals(n.getPlayerName())) return false;
                }

                listPlayer.add(playerIdentity);}
            return true;

        }

        static IdentityCardOfPlayer retrievePlayerIdentity(String uniquePlayerCode) {

            for(IdentityCardOfPlayer n : listPlayer){
                if(n.getUniquePlayerCode().equals(uniquePlayerCode)) return n;}

            return null;




        }

        static IdentityCardOfPlayer retrievePlayerIdentityByName(String namePlayer) {

            for(IdentityCardOfPlayer n : listPlayer){
                if(n.getPlayerName().equals(namePlayer)) return n;}

            return null;




        }

        static String retrievePlayerName(String uniquePlayerCode) {
            for(IdentityCardOfPlayer n : listPlayer){
                if(n.getUniquePlayerCode().equals(uniquePlayerCode)) return n.getPlayerName();}

            return null;

        }

        static void removePlayerFromListIdentities(String uniquePlayerCode) {

            synchronized (listPlayer){
                for(int i = 0; i < listPlayer.size(); i++) {
                    if (uniquePlayerCode.equals(listPlayer.get(i).getUniquePlayerCode())) listPlayer.remove(i);
                } }
        }

    }


}










