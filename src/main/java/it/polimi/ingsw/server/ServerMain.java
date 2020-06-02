package it.polimi.ingsw.server;

import it.polimi.ingsw.bothsides.utils.Configuration;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.server.transmissionprotocol.ServerThread;

public class ServerMain {


    public static void main(String[] args) {


        Configuration configuration = new Configuration();

        Thread serverThread;

        try {

            int portAccept = configuration.getPortAccept();
            int portPingAndError = configuration.getPortPingAndError();

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