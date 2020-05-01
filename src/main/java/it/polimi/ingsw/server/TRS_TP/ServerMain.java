package it.polimi.ingsw.server.TRS_TP;

public class ServerMain {

    public static void main(String[] args) {

        int portAccept = 6700;
        int portPingAndError = 6701;


        try {


            new ServerConnection(portAccept, portPingAndError).runServer();


        }catch(Exception ex){
            System.out.println("Upsi, qualcosa è andato storto");
            System.exit(-1);
        }


        return;

    }
}