package it.polimi.ingsw.client;

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

    static final Scanner scannerIn = new Scanner(System.in);
    static final ExecutorService clientExecutor = Executors.newCachedThreadPool();

    static MenuUserInterface menuUi = null;
    static InGameUserInterface inGameUi = null;

    static String serverIpAddress;
    static int serverPortStandard = 6700;
    static int serverPortError = 6701;
    static int serverPortChat1 = 6702;
    static int serverPortChat2 = 6703;


    public static void main(String[] args) {



        if(!verifyArguments(args)) {

            System.err.printf("Wrong arguments! Closing the application");
            System.exit(-1);


        }


        //prints a message on the cli
        if(args[0].equals("c")) printWelcome();


        try {

            openConnectionChannels();

            setTypeOfUserInterface(args[0]);

            serverIpAddress = args[1];

            if(!verifyIpAddress(serverIpAddress)) {

                ClientViewAdapter.printMenuMessage("Wrong ip address, closing the application");
                System.exit(-1);

            }

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
            // e.printStackTrace()
        }



    }




    private static void printWelcome() {

        System.out.println("\n\n\n\n\n\n\n\n" + Global.WELCOMETO);

        System.out.println("\n\n\n" + Global.SANTORINI);
        System.out.println(Global.ATRSPRODUCTION +"\n\n");


    }

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


    private static boolean verifyArguments(String[] args){

        if(args.length != 2){ return false; }

        if(!(args[0].equals("c") || args[0].equals("g"))) {
            System.err.printf("\nInvalid gui or cli parameter\n");
            return false; }

        if(!verifyIpAddress(args[1])){
            System.err.printf("\nInvalid ip\n");
            return false; }

        else return true;





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

    private static void initiateStandardCommunication() throws IOException {

        if (normalChannel.connect(new InetSocketAddress(serverIpAddress, serverPortStandard))) {

            ClientFsm clientFsm = new ClientFsm(normalChannel.socket());
            ClientViewAdapter.setClientFsm(clientFsm);
            clientFsm.run();

        }

    }

    static SocketChannel getErrorChannel() {
        return errorChannel;

    }

    static SocketChannel getChatChannel1(){return chatChannel1;}

    static SocketChannel getChatChannel2(){return chatChannel2;}



}