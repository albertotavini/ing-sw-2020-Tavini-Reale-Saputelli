package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.utils.Configuration;
import it.polimi.ingsw.server.utils.LogPrinter;

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

            LogPrinter.printOnLog("\n----Something wrong happened in SeerverMain----");
            LogPrinter.printOnLog(ex.toString());
            System.exit(-1);

        }



    }


}