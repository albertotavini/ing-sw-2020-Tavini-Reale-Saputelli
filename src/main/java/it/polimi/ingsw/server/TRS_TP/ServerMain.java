package it.polimi.ingsw.server.TRS_TP;

public class ServerMain {

    public static void main(String[] args) {

        int port = 6700;

        ServerConnection serverConnection = new ServerConnection (port);

        try {
            serverConnection.runServer();
        }catch(Exception ex){
            System.out.println("Upsi, qualcosa è andato storto");
        }



    }
}