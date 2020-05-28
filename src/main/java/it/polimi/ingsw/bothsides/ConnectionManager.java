package it.polimi.ingsw.bothsides;

import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.PingAndErrorMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        Object obj;

        do{

            obj = ois.readObject();


        }while (obj instanceof PingAndErrorMessage);


        return obj;
    }

    public static Object receiveErrorObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

        Object obj;

        do{

            obj = ois.readObject();


        }while ( !(obj instanceof PingAndErrorMessage ) );


        return obj;
    }

    public static synchronized <T> void closeConnection (T message, Socket socket) throws IOException {

        sendObject(message, new ObjectOutputStream(socket.getOutputStream()));
        socket.close();

    }


}
