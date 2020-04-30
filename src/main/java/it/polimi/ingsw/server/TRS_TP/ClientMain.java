package it.polimi.ingsw.server.TRS_TP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientMain {



    public static ExecutorService clientExecutor = Executors.newCachedThreadPool();

    public static Scanner scannerIn = new Scanner(System.in);

    public static void main(String[] args) {

        SocketChannel normalChannel = null;



        try {

            normalChannel = SocketChannel.open();
            SocketChannel errorChannel = SocketChannel.open();
            normalChannel.configureBlocking(true);
            errorChannel.configureBlocking(true);

        String guiOrCli = null;


        MenuUserInterface menuUi;
        InGameUserInterface inGameUi;


        do{

            System.out.printf("Preferisci gui o cli? g/c\n");
            guiOrCli = scannerIn.nextLine();
            guiOrCli = guiOrCli.toUpperCase();

        }while(!(guiOrCli.equals("G") || guiOrCli.equals("C")));

        if(guiOrCli.equals("g") || guiOrCli.equals("G"))
        {

            menuUi = new MenuGui();
            inGameUi = new InGameGui();

        }

        //l'utente ha scelto di usare la cli
        else{

            menuUi = new MenuCli();
            inGameUi = new InGameCli();

        }


        ClientViewAdapter.setTypeInterface(menuUi, inGameUi);

        clientExecutor.submit(new ClientPingAndErrorThread(errorChannel));


        if (normalChannel.connect(new InetSocketAddress("localhost", 6700))) {

            MenuFsmClientNet menuFsmClientNet = new MenuFsmClientNet(normalChannel.socket());

            menuFsmClientNet.run();

        }


        if(!clientExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) clientExecutor.shutdownNow();

        System.out.println("Connessione chiusa");


        } catch (IOException | InterruptedException e) {
            System.out.println("Upsi, mi son disconnesso");
            e.printStackTrace();
        }



    }

}