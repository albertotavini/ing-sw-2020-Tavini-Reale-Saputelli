package it.polimi.ingsw.server.TRS_TP;

public class ServerMain {


    public static void main(String[] args) {

        //potrebbe leggere le porte e il numero di server da creare da un file config o boh, da args o altro
        int portAccept = 6700;
        int portPingAndError = 6701;

        Thread serverThread;

        try {

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