package it.polimi.ingsw.server.transmissionprotocol;

import it.polimi.ingsw.bothsides.onlinemessages.InGameServerMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

//ricordarsi della gestione degli errori
public class InGameConnection extends Observable<PlayerMove> implements Runnable{

        private Socket socket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private String uniquePlayerCode;
        private boolean openedConnection = false;
        private final ServerFsm fsmContext;


        public InGameConnection(Socket socket, String uniquePlayerCode, ObjectOutputStream oos, ObjectInputStream ois, ServerFsm fsmContext){
            this.socket = socket;
            this.uniquePlayerCode = uniquePlayerCode;
            this.oos = oos;
            this.ois = ois;
            this.fsmContext = fsmContext;
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
            LogPrinter.printOnLog("----In GameConnection wasn't able to send ModelMessage");
            LogPrinter.printOnLog(e.toString());

        }
    }


    public synchronized void closeInGameConnection(){
        sendModelMessage(new InGameServerMessage(null, new ModelMessage(ModelMessageType.DISCONNECTED, "Connection closed from server side.")));
        try{
            socket.close();
        }catch (IOException e){
            LogPrinter.printOnLog("----In GameConnection failed to close");
            LogPrinter.printOnLog(e.toString());
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

                    if(playerMove.getType() == PlayerMoveType.CHAT_MESSAGE){


                        System.out.println("Sono nella inGame e ho ricevuto il messaggio: " +playerMove.getGenericMessage());

                        fsmContext.getAssignedLobby().getLobbyChat().addMessage(playerMove.getGenericMessage());

                    }

                    else notify(playerMove, null);


                }


            } catch(Exception e){

               ServerFsm fsm = ServerThread.getFsmByUniqueCode(uniquePlayerCode);

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
