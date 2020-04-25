package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.observers.ObservableVC;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//ricordarsi della gestione degli errori
public class InGameConnection extends ObservableVC<PlayerMove> implements Runnable{

        private Socket socket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private String name;
        private boolean openedConnection = true;


        public InGameConnection(Socket socket, ObjectOutputStream oos, ObjectInputStream ois){
            this.socket = socket;
            this.oos = oos;
            this.ois = ois;
        }

        private synchronized boolean isConnected(){ return openedConnection; }

        public void sendModelMessage(ModelMessage modelMessage){
            try {
                oos.writeObject(modelMessage);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //la useremo per mandare a tutti nello stesso momento la board aggiornata
        /*public void asyncSend(final String message){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendModelMessage(message);
                }
            }).start();
        }*/

        public synchronized void closeInGameConnection(){
            sendModelMessage(new ModelMessage(ModelMessageType.ConnectionClosed, "Connection closed from server side."));
            try{
                socket.close();
            }catch (IOException e){
                System.err.println(e.getMessage());
            }
            openedConnection = false;
        }

        private void close(){
            closeInGameConnection();
            System.out.println("Deregistering client...");
            System.out.println("Done!");
        }

        @Override
        public void run() {
            try{
                while(isConnected()){
                    PlayerMove playerMove = (PlayerMove) ois.readObject();
                    notify(playerMove);
                }
            } catch(IOException | ClassNotFoundException e){
                System.err.println(e.getMessage());
            } finally {
                close();
            }
        }
    }
