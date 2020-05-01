package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.utils.ColorAnsi;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientMain {

    private static MenuUserInterface menuUi;
    private static InGameUserInterface inGameUi;
    private static SocketChannel normalChannel;
    private static SocketChannel errorChannel;
    public static Scanner scannerIn = new Scanner(System.in);
    public static ExecutorService clientExecutor = Executors.newWorkStealingPool();


    public static void main(String[] args) {

        //printa un messaggio di benvenuto
        printWelcome();

        try {

            //apre il canale di connessione standard
            openConnectionChannels();

            //gestisce la scelta tra gui e cli da parte dell'utente
            setTypeOfUserInterface();

            //fa partire il thread che gestisce i ping
            clientExecutor.submit(new ClientPingAndErrorThread(errorChannel));

            //fa partire la connessione standard e la macchina a stati che gestisce il gioco e la comunicazione standard
            initiateStandardCommunication();


            clientExecutor.shutdown();
            if (!clientExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) clientExecutor.shutdownNow();


            ClientViewAdapter.printMessage("Connessione chiusa con sucesso");


        }catch(ConnectException ex){

            ClientViewAdapter.printMessage("Non sono riuscito a connettermi al server per la connessione standard");
            System.exit(-1);

        } catch (IOException | InterruptedException e) {
            System.out.println("Upsi, mi son disconnesso");
            e.printStackTrace();
        }



    }





    //metodo che printa il welcome al gioco
    private static void printWelcome() {

        System.out.println(ColorAnsi.YELLOW +"\n\n\n\n\n\n\n\nBenvenuto in:" +ColorAnsi.RESET);

        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n\n\n" + ColorAnsi.RED +AsciiArt.santorini4 +ColorAnsi.RESET);
        System.out.println(ColorAnsi.YELLOW +"A TRS production (excuse moi el dulce principe cranio creations)\n\n" +ColorAnsi.RESET);


    }
    //metodo che gestisce la scelta della user interface: gui o cli
    private static void setTypeOfUserInterface() {

        String guiOrCli = null;

        do {

            System.out.printf("Preferisci gui o cli? g/c\n");
            guiOrCli = scannerIn.nextLine();
            guiOrCli = guiOrCli.toUpperCase();

        } while (!(guiOrCli.equals("G") || guiOrCli.equals("C")));

        if (guiOrCli.equals("g") || guiOrCli.equals("G")) {

            menuUi = new MenuGui();
            inGameUi = new InGameGui();
        }

        //l'utente ha scelto di usare la cli
        else {

            menuUi = new MenuCli();
            inGameUi = new InGameCli();

        }


        ClientViewAdapter.setTypeInterface(menuUi, inGameUi);

        return;

    }
    //metodo che apre i canali di connessione standard e per errori e ping
    private static void openConnectionChannels() throws IOException {

        normalChannel = SocketChannel.open();
        errorChannel = SocketChannel.open();
        normalChannel.configureBlocking(true);
        errorChannel.configureBlocking(true);


    }
    //fa partire la comunicazione standard del gioco
    private static void initiateStandardCommunication() throws IOException {

        if (normalChannel.connect(new InetSocketAddress("localhost", 6700))) {

            MenuFsmClientNet menuFsmClientNet = new MenuFsmClientNet(normalChannel.socket());
            menuFsmClientNet.run();

        }

    }



}