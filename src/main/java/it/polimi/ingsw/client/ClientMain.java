package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.utils.ColorAnsi;
import it.polimi.ingsw.bothsides.utils.Global;


import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMain {

    private static SocketChannel normalChannel;
    private static SocketChannel errorChannel;
    private static SocketChannel chatChannel1;
    private static SocketChannel chatChannel2;

    public static final Scanner scannerIn = new Scanner(System.in);
    public static final ExecutorService clientExecutor = Executors.newCachedThreadPool();

    public static MenuUserInterface menuUi = null;
    public static InGameUserInterface inGameUi = null;

    public static String serverIpAddress;
    public static int serverPortStandard = 6700;
    public static int serverPortError = 6701;
    public static int serverPortChat1 = 6702;
    public static int serverPortChat2 = 6703;


    public static void main(String[] args) {




        //printa un messaggio di benvenuto per la cli
        if(args[0].equals("c")) printWelcome();


        try {

            //apre il canale di connessione standard
            openConnectionChannels();

            //gestisce la scelta tra gui e cli da parte dell'utente
            setTypeOfUserInterface(args[0]);

            serverIpAddress = args[1];

            if(!verifyIpAddress(serverIpAddress)) {

                ClientViewAdapter.printMenuMessage("Wrong ip address, closing the application");
                System.exit(-1);

            }

            //fa partire la connessione standard e la macchina a stati che gestisce il gioco e la comunicazione standard
            initiateStandardCommunication();

            clientExecutor.shutdown();
            if (!clientExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) clientExecutor.shutdownNow();


            ClientViewAdapter.printMenuMessage(Global.CONNECTIONWASCLOSEDSUCCESSFULLY);

            System.exit(0);


        }catch(ConnectException ex){

            ClientViewAdapter.printMenuMessage(Global.ICOULDNOTCONNECTTOTHESERVERFORSTANDARDCONNECTION);
            System.exit(-1);

        } catch (Exception e) {
            ClientViewAdapter.printMenuMessage(Global.OPSIDISCONNECTED);
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }



    }



    //metodo che printa il welcome al gioco
    private static void printWelcome() {

        System.out.println(ColorAnsi.YELLOW +"\n\n\n\n\n\n\n\n" + Global.WELCOMETO +ColorAnsi.RESET);

        System.out.println("\n\n\n" + Global.SANTORINI);
        System.out.println(ColorAnsi.YELLOW + Global.ATRSPRODUCTION +"\n\n" +ColorAnsi.RESET);


    }
    //metodo che gestisce la scelta della user interface: gui o cli
    private static void setTypeOfUserInterface(String guiOrCli) {

        guiOrCli = guiOrCli.toUpperCase();

        if (guiOrCli.equals("G")) {

            /*try {
                UIManager.setLookAndFeel( new FlatDarculaLaf());
            } catch( Exception ex ) {
                System.err.println(Global.FAILEDTOINITIALIZELAF);
            }*/

            menuUi = new MenuGui();
            inGameUi = new InGameGui();
        }

        //l'utente ha scelto di usare la cli
        else {

            menuUi = new MenuCli();
            inGameUi = new InGameCli();

        }


        ClientViewAdapter.setTypeInterface(menuUi, inGameUi);

    }

    private static boolean verifyIpAddress(String ip){

        String regexIp =  "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        Pattern ipPattern = Pattern.compile(regexIp);
        Matcher matcherIp = ipPattern.matcher(ip);

        if(matcherIp.find()) {

            return true;
        }

        else return false;
    }

    //metodo che apre i canali di connessione standard e per errori e ping
    private static void openConnectionChannels() throws IOException {

        normalChannel = SocketChannel.open();
        errorChannel = SocketChannel.open();
        chatChannel1 = SocketChannel.open();
        chatChannel2 = SocketChannel.open();

        normalChannel.configureBlocking(true);
        errorChannel.configureBlocking(true);
        chatChannel1.configureBlocking(true);
        chatChannel2.configureBlocking(true);


    }

    public static void closeConnectionChannels() throws IOException {

        normalChannel.close();
        errorChannel.close();
        chatChannel1.close();
        chatChannel2.close();

    }

    //fa partire la comunicazione standard del gioco
    private static void initiateStandardCommunication() throws IOException {

        if (normalChannel.connect(new InetSocketAddress(serverIpAddress, serverPortStandard))) {

            ClientFsm clientFsm = new ClientFsm(normalChannel.socket());
            ClientViewAdapter.setClientFsm(clientFsm);
            clientFsm.run();

        }

    }

    public static SocketChannel getErrorChannel() {
        return errorChannel;

    }

    public static SocketChannel getChatChannel1(){return chatChannel1;}

    public static SocketChannel getChatChannel2(){return chatChannel2;}



}