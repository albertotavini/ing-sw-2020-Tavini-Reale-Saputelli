package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.utils.ColorAnsi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class AsyncronousPingAndErrorHandler implements Runnable{


    private final Socket clientSocket;
    private boolean isActive;

    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    public AsyncronousPingAndErrorHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.ois = new ObjectInputStream(clientSocket.getInputStream());
        this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
        this.isActive = true;
    }

    public void setInactive(){
        this.isActive = false;
    }


    @Override
    public void run() {

        PingAndErrorMessage pingMessage = new PingAndErrorMessage(TypeOfMessage.PingAndErrorMessagePing, "Ping");

        do {

            try {

                Thread.sleep(1000);
                ConnectionManager.sendObject(pingMessage, this.oos);

            } catch (Exception e) {
                System.out.println(ColorAnsi.YELLOW +"Ho cannato nel PingHandler" +ColorAnsi.RESET);
                e.printStackTrace();
            }

        }while (isActive);

    }
}
