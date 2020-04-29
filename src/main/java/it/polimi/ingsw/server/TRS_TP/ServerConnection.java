package it.polimi.ingsw.server.TRS_TP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServerConnection {

    static int port;

    public static Scanner in = new Scanner(System.in);

    //pool di thread ad uso e consumo del server per creare fsmSingleClientHandler
    public static ExecutorService executor = Executors.newCachedThreadPool();

    public ServerConnection(int port){
        this.port = port;
    }

    //metodo che fa semplicemente partire il server
    public void runServer() {

        ServerSocket ssocket;

        //mando in esecuzione il thread che gestisce la cli del server
        executor.submit(new ServerCliInterface());

        try {

            ssocket = new ServerSocket(port);

            while(true)
            {

                Socket socket = ssocket.accept();
                //System.out.println("A player joined the server");
                executor.submit(new MenuFsmServerSingleClientHandler(socket, PlayerUniqueCode.getUniquePlayerCode()));

            }

        }catch (IOException e){
            //da vedere
        }

    }

    //ci dice se un giocatore ha già creato una lobby
    public static boolean hasPlayerAlreadyCreatedALobby(String nameCreator) {

        return ListLobbyPublic.hasPlayerAlreadyCreatedALobbyPublic(nameCreator) || ListLobbyPrivate.hasPlayerAlreadyCreatedALobbyPrivate(nameCreator);

    }


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

    static class ServerCliInterface implements Runnable {

        @Override
        public void run() {

            String commandInput = null;

            System.out.println("\n\n\n" +AsciiArt.santorini4);
            System.out.println("Server listening on port: " +port);
            System.out.println("\nType h for help");

            do{
                System.out.printf("\nTerminal Active > ");

                commandInput = ServerConnection.in.nextLine();
                //String regexData = "-\\w -\\w$";
                String regexInput = "^([ph]) ?(-?[\\w]?[\\w]?[\\w]?)$";

                Pattern patternInput = Pattern.compile(regexInput, Pattern.CASE_INSENSITIVE);
                Matcher matcherInput = patternInput.matcher(commandInput);

                if(matcherInput.find()){

                    switch(matcherInput.group(1).toUpperCase()) {

                        case "P":

                            switch (matcherInput.group(2)) {

                                case "-si":
                                    System.out.println("Insert the sequence:");
                                    ServerCliInterface.printIdentitiesContaining(ServerConnection.in.nextLine());
                                    break;

                                case "-ai":
                                    System.out.println("**********All identities**********");
                                    ServerCliInterface.printAllIdentities();
                                    break;

                                case "-prl":
                                    System.out.println("**********Private lobbies**********");
                                    ServerCliInterface.printPrivateLobbies();
                                    break;

                                case "-pul":
                                    System.out.println("**********Public lobbies**********");
                                    ServerCliInterface.printPublicLobbies();
                                    break;



                                case "-col":
                                    System.out.println("**********Lobby containing**********");
                                    System.out.println("Insert the sequence:");
                                    ServerCliInterface.printLobbyContaining(ServerConnection.in.nextLine());
                                    break;

                                case "-al":
                                    System.out.println("**********Private lobbies**********");
                                    ServerCliInterface.printPrivateLobbies();
                                    System.out.println("**********Public lobbies**********");
                                    ServerCliInterface.printPublicLobbies();
                                    break;


                                default :
                                    System.out.println("Wrong options!!! Type h for help");

                                }
                            break;


                        case "H":
                            String help = "Comando h : printa tutti i comandi disponibili"
                                    +"\nComando p : esegue una print diversa in base alle opzioni:"
                                    +"\n        **********Lobby commands:"
                                    +"\n        pul : stampa soltanto le lobby pubbliche"
                                    +"\n        prl : stampa soltanto le lobby private"
                                    +"\n        al : stampa tutte le lobby"
                                    +"\n        col : stampa lobby contenenti una sequenza inserita dall'utente"
                                    +"\n        **********Identities commands:"
                                    +"\n        ai : stampa tutte le identities"
                                    +"\n        si : stampa le identità che contengono una sequenza inserita dall'utente";

                            System.out.println(help);
                            break;


                    }



                    }

                else System.out.println("Incorrect command, try again");

            }while(true);


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


    }










