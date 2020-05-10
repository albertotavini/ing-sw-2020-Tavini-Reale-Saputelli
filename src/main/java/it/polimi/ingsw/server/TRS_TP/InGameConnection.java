package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.server.utils.ColorAnsi;
import it.polimi.ingsw.server.utils.LogPrinter;
import it.polimi.ingsw.server.view.PlayerMove.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//ricordarsi della gestione degli errori
public class InGameConnection extends Observable<PlayerMove> implements Runnable{

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
        sendModelMessage(new InGameServerMessage(null, new ModelMessage(ModelMessageType.Disconnected, "Connection closed from server side.")));
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

            LogPrinter.printOnLog("\nHo fatto partire la inGameConnection di " +ServerThread.ListIdentities.retrievePlayerName(getUniquePlayerCode()));

            try{

                while(isConnected()){

                    PlayerMove playerMove = (PlayerMove) ois.readObject();
                    notify(playerMove, null);
                }
            } catch(Exception e){

               MenuFsmServerSingleClientHandler fsm = ServerThread.getFsmByUniqueCode(uniquePlayerCode);

                try {

                    fsm.getAssignedLobby().killLobby();

                } catch (IOException ex) {
                    LogPrinter.printOnLog("\n----It didn't kill the lobby in Ingame connection----");
                }

                LogPrinter.printOnLog("\n----InGameConnection failed to receive player move----");
                LogPrinter.printOnLog(e.toString());

            } finally {
                //
                closeInGameConnection();
            }
        }
    }
