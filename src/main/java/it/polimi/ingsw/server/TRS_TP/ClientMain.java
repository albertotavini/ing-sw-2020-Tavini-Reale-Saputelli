package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.model.Date;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMain {



    public static ExecutorService clientExecutor = Executors.newCachedThreadPool();

    public static Scanner scannerIn = new Scanner(System.in);

    public static void main(String[] args) {

        SocketChannel sChannel = null;

        try {

            sChannel = SocketChannel.open();

        sChannel.configureBlocking(true);
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


        if (sChannel.connect(new InetSocketAddress("localhost", 6700))) {

            MenuFsmClientNet menuFsmClientNet = new MenuFsmClientNet(sChannel.socket());

            menuFsmClientNet.run();

        }

        System.out.println("Connessione chiusa");


        } catch (IOException e) {
            System.out.println("Upsi, mi son disconnesso");
            e.printStackTrace();
        }



    }


}