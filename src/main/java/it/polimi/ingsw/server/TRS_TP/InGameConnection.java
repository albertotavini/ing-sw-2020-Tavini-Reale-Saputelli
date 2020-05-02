package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.observers.ObservableVC;
import it.polimi.ingsw.server.view.PlayerMove.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//ricordarsi della gestione degli errori
public class InGameConnection extends ObservableVC<PlayerMove> implements Runnable{

        private Socket socket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private String uniquePlayerCode;
        private boolean openedConnection = false;


        public InGameConnection(Socket socket, String uniquePlayerCode, ObjectOutputStream oos, ObjectInputStream ois){
            this.socket = socket;
            this.uniquePlayerCode = uniquePlayerCode;
            this.oos = oos;
            this.ois = ois;
        }

        private synchronized boolean isConnected(){ return openedConnection; }

        public Socket getSocket() {
        return socket;
    }

        public String getUniquePlayerCode() {
        return uniquePlayerCode;
    }

    public void sendModelMessage(InGameServerMessage inGameServerMessage){
        try {
            oos.writeObject(inGameServerMessage);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void closeInGameConnection(){
        sendModelMessage(new InGameServerMessage(null, new ModelMessage(ModelMessageType.ConnectionClosed, "Connection closed from server side.")));
        try{
            socket.close();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        openedConnection = false;
    }

        @Override
        public void run() {
            openedConnection = true;
            System.out.println("ho fatto partire la inGame di"+ ServerConnection.ListIdentities.retrievePlayerIdentity(getUniquePlayerCode()).getPlayerName());

            try{
                while(isConnected()){
                    PlayerMove playerMove = (PlayerMove) ois.readObject();
                    notify(playerMove);
                }
            } catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
                System.out.println("la inGameConnection ha quittato");
            } finally {
                //
                closeInGameConnection();
            }
        }
    }
