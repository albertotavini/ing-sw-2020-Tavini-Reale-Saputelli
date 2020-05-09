package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.utils.Configuration;

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

            System.out.println("Upsi, qualcosa è andato storto\n");
            ex.printStackTrace();
            System.exit(-1);
        }



    }


}