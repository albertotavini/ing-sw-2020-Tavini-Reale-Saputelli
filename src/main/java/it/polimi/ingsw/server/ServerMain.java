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


        if(args[0] != null){

            serverLogPath = args[0];

        }

        else{

            System.err.printf("\nWrong Arguments! Closing the application\n");
            System.exit(-1);
        }



        Thread serverThread;
        int portAccept;
        int portPingAndError;
        int portChat1;
        int portChat2;

        try {

             portAccept = Configuration.getPortAccept();
             portPingAndError = Configuration.getPortPingAndError();
             portChat1 = 6702;
             portChat2 = 6703;


            serverThread = new Thread(new ServerThread(portAccept, portPingAndError, portChat1, portChat2));
            serverThread.start();
            serverThread.join();

        }catch(Exception ex){

            LogPrinter.printOnLog(Global.SOMETHINGWRONGHAPPENEDINSERVERMAIN);
            LogPrinter.printOnLog(ex.toString());
            System.exit(-1);

        }



    }
    


}