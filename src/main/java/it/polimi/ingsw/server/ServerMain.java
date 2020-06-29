package it.polimi.ingsw.server;

import it.polimi.ingsw.bothsides.utils.Configuration;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.server.transmissionprotocol.ServerThread;

public class ServerMain {


    private static String serverLogPath;

    public static String getServerLogPath() {
        return serverLogPath;
    }


    public static void main(String[] args) {

        serverLogPath = args[0];

        Thread serverThread;
        int portAccept;
        int portPingAndError;

        try {

             //portAccept = Configuration.getPortAccept();
            portAccept = 50000;
            portPingAndError = 50001;
            // portPingAndError = Configuration.getPortPingAndError();

            serverThread = new Thread(new ServerThread(portAccept, portPingAndError));
            serverThread.start();
            serverThread.join();

        }catch(Exception ex){

            LogPrinter.printOnLog(Global.SOMETHINGWRONGHAPPENEDINSERVERMAIN);
            LogPrinter.printOnLog(ex.toString());
            System.exit(-1);

        }



    }


}