package it.polimi.ingsw.bothsides;

import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.PingAndErrorMessage;

import java.io.*;
import java.net.Socket;

/**
 * this class is meant to facilitate the usage of the output and input object stream
 */
public class ConnectionManager {

    private ConnectionManager(){
        //hiding the default constructor
    }


    /**
     * this method sends an object on oos plus calls reset() and flush for proper usage of the class
     *
     * @param message object to be sent
     * @param oos on which we will send
     * @param <T> generic type to send all type of objects
     * @throws IOException because exception are handled outside these calls, because they generally mean channel has been corrupted
     */
    public static <T> void sendObject(T message, ObjectOutputStream oos) throws IOException {
            oos.reset();
            oos.writeObject(message);
            oos.flush();

    }

    /**
     * this method simply returns the object that last was placed on the ois passed as argument
     *
     * @param ois where we receive from
     * @return object that has been received
     * @throws IOException  because exceptions are handled outside these calls, because they generally mean channel has been corrupted
     * @throws ClassNotFoundException because exceptions are handled outside these calls, because they generally mean channel has been corrupted
     */
    public static Object receiveStandardObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

        return ois.readObject();
    }


    /**
     * this method is simply used to close the connection channel
     *
     * @param message to be sent
     * @param socket to be closed
     * @param <T> generic type to send all stuff
     * @throws IOException because exceptions are handled outside these calls, because they generally mean channel has been corrupted
     */
    public static synchronized <T> void closeConnection (T message, Socket socket) throws IOException {

        sendObject(message, new ObjectOutputStream(socket.getOutputStream()));
        socket.close();

    }



}
