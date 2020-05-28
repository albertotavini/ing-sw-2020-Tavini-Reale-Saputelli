package it.polimi.ingsw.bothsides;

import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.PingAndErrorMessage;

import java.io.*;
import java.net.Socket;

public class ConnectionManager {

    private ConnectionManager(){
        //hiding the default constructor
    }


    public static <T> void sendObject(T message, ObjectOutputStream oos) throws IOException {

            oos.reset();
            oos.writeObject(message);
            oos.flush();

    }

    public static Object receiveStandardObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

        return ois.readObject();
    }


    public static synchronized <T> void closeConnection (T message, Socket socket) throws IOException {

        sendObject(message, new ObjectOutputStream(socket.getOutputStream()));
        socket.close();

    }


    public static void sendString(String message, PrintWriter writer){

        writer.println(message);
        writer.flush();

    }


}
