package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.model.Date;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientMain {

    private static String playerName;
    private static Date playerBirthday;

    public static Scanner scannerIn = new Scanner(System.in);


    public static void main(String[] args) {

        SocketChannel sChannel = null;

        try {

            sChannel = SocketChannel.open();

        sChannel.configureBlocking(true);
        String guiOrCli = null;


        UserInterface ui;


        do{

            System.out.printf("Preferisci gui o cli? g/c\n");
            guiOrCli = scannerIn.nextLine();
            guiOrCli = guiOrCli.toUpperCase();

        }while(!(guiOrCli.equals("G") || guiOrCli.equals("C")));

        if(guiOrCli.equals("g") || guiOrCli.equals("G")) ui = new Gui();
        else ui = new Cli();


        ClientViewAdapter.setTypeInterface(ui);


        if (sChannel.connect(new InetSocketAddress("localhost", 6700))) {

            FsmClientNet fsmClientNet = new FsmClientNet(sChannel.socket());

            fsmClientNet.run();

        }

        System.out.println("Connessione chiusa");


        } catch (IOException e) {
            System.out.println("Upsi, mi son disconnesso");
            e.printStackTrace();
        }



    }



    public static String getPlayerName(){
        return playerName;
    }
    public static Date getPlayerBirthday(){ return playerBirthday; }


    public static void setPlayerBirthday(Date playerBirthday) {
        ClientMain.playerBirthday = playerBirthday;
    }
    public static void setPlayerName(String playerName) {
        ClientMain.playerName = playerName;
    }
}