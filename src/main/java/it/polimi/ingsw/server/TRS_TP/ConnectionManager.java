package it.polimi.ingsw.server.TRS_TP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionManager {

    public static <T> void sendObject(T message, ObjectOutputStream oos) throws IOException {

        oos.reset();
        oos.writeObject(message);
        oos.flush();

    }

    public static Object receiveObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
       return ois.readObject();
    }

    public static synchronized <T> void closeConnection (T message, Socket socket) throws IOException {

        sendObject(message, new ObjectOutputStream(socket.getOutputStream()));
        socket.close();

    }


    public void asyncSend(final Object message, ObjectOutputStream oos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendObject(message, oos);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



}
