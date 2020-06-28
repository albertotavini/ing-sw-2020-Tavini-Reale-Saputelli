package it.polimi.ingsw.server.transmissionprotocol;

import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.InGameServerMessage;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * this class is the one that deals with the reception of playermoves from client and sends InGameServerMessages to the client
 * it is instantiated in serverFSM and passed as a parameter to the RemoteView of that player
 *
 */
//ricordarsi della gestione degli errori
public class InGameConnection extends Observable<PlayerMove> implements Runnable {


        private ObjectOutputStream standardOos;
        private ObjectInputStream standardOis;

        private ObjectOutputStream chatOos;
        private ObjectInputStream chatOis;


        private String uniquePlayerCode;
        private final ServerFsm fsmContext;
        private final ServerInGameState serverInGameState;

        boolean openedConnection = true;


    /**
     * the constructor is strictly related to the corresponding ServerFSM
     *
     * @param uniquePlayerCode inherited from ServerFSM
     * @param standardOos inherited from ServerFSM
     * @param standardOis inherited from ServerFSM
     * @param fsmContext status of the ServerFSM
     */
        public InGameConnection(String uniquePlayerCode, ObjectOutputStream standardOos, ObjectInputStream standardOis, ServerFsm fsmContext, ServerInGameState serverInGameState) {

            this.serverInGameState = serverInGameState;
            this.uniquePlayerCode = uniquePlayerCode;
            this.standardOos = standardOos;
            this.standardOis = standardOis;
            this.fsmContext = fsmContext;

            this.chatOos = fsmContext.getChatOos();
            this.chatOis = fsmContext.getChatOis();

        }

        private String getUniquePlayerCode() {
        return uniquePlayerCode;
    }

    /**
     * this method is used to send the BoardPhotograpy and the ModelMessage every time update is called in remoteview
     * exception are printed on a specific Log
     *
     * @param inGameServerMessage passed from the remote view
     */
    public void sendInGameServerMessage(InGameServerMessage inGameServerMessage) {
        try {

            ConnectionManager.sendObject(inGameServerMessage, standardOos);
            System.out.println("Messaggio inviato dal server: " +inGameServerMessage.toString());

        } catch (IOException e) {
            LogPrinter.printOnLog(Global.INGAMECONNECTIONWASNTABLETOSENDMODELMESSAGE);
            LogPrinter.printOnLog(e.toString());

        }
    }

    private void setInGameHasLost() {

        serverInGameState.setHasLost(true);
    }



    /**
     * this method, while openedConnection is true continues to read playermoves from the ObjectInputStream
     * and usually notifies them to observers (the MessageReceiver of the RemoteView
     * if the type of the playermove is CHAT_MESSAGE it is processed as it should be
     * if the type is between the two KILLER the InGameConnection will kill itself and if necessary also the lobby
     *
     * if an exception is given by irregular behaviour of the OIS (for example if the player closes the game)
     * the connection ends and other players are disconnected as corresponding ServerFSM calls killLobby
     *
     * informations on the exceptions launched is saved on a log file
     */
        @Override
        public void run() {

            LogPrinter.printOnLog(Global.JUSTRUNINGAMECONNECTIONOFTHEFOLLOWINGPLAYER +ServerThread.ListIdentities.retrievePlayerName(getUniquePlayerCode()));

            try{

                Thread chatReceiver = new Thread(new ChatReceiver());

                chatReceiver.start();



                while(openedConnection) {


                    Object obj = standardOis.readObject();

                    System.out.println("Ho ricevuto un oggetto nella standard in game connection " +obj.toString());

                    if (obj instanceof PlayerMove) {

                        PlayerMove playerMove = (PlayerMove) obj;

                        if (playerMove.getType() == PlayerMoveType.KILL_IN_GAME_CONNECTION_GAMEOVER) {

                            openedConnection = false;
                        }

                        else if (playerMove.getType() == PlayerMoveType.KILL_IN_GAME_CONNECTION_YOU_LOST) {

                            openedConnection = false;
                            setInGameHasLost();

                        }

                        else notify(playerMove, null);


                    }
                }

            } catch(Exception e){

               ServerFsm fsm = ServerThread.getFsmByUniqueCode(uniquePlayerCode);

                try {

                    fsm.getAssignedLobby().killLobby();

                } catch (IOException ex) {
                    LogPrinter.printOnLog(Global.ITDIDNTKILLTHELOBBYINGAMECONNECTION);
                    LogPrinter.printOnLog(e.toString());
                }

                LogPrinter.printOnLog(Global.INGAMECONNECTIONFAILEDTORECEIVEPLAYERMOVE);
                LogPrinter.printOnLog(e.toString());

            }

        }

        private class ChatReceiver implements Runnable{


            @Override
            public void run() {

                try{

                    while(openedConnection) {


                        Object obj = chatOis.readObject();

                        System.out.println("Ho ricevuto un messaggio nella in game conn della chat " +obj.toString());

                        if (obj instanceof PlayerMove) {

                            PlayerMove playerMove = (PlayerMove) obj;

                            fsmContext.getAssignedLobby().getLobbyChat().addMessage(playerMove.getGenericMessage());


                        }
                    }

                } catch(Exception e){

                    LogPrinter.printOnLog(Global.INGAMECONNECTIONFAILEDTORECEIVEPLAYERMOVE);
                    LogPrinter.printOnLog(e.toString());

                }

            }


        }



    }
