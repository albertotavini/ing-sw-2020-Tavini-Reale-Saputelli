package it.polimi.ingsw.server.transmissionprotocol;

import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.PingAndErrorMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;


/**
 * this thread is meant to deal with the ping between client and server on server side. It controls that the connection is up and informs
 * one when the other interrupts comunications
 */
public class AsyncronousPingAndErrorHandler implements Runnable {


    private final Socket clientSocket;
    private boolean isActive = true;

    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    private String namePlayer;
    private boolean hasNameBeenSet = false;

    public AsyncronousPingAndErrorHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.ois = new ObjectInputStream(clientSocket.getInputStream());
        this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
    }


    /**
     * sends a ping message every second to ensure that connection is still on with the client
     */
    @Override
    public void run() {

        PingAndErrorMessage pingMessage = new PingAndErrorMessage(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, Global.PING);

        do {

            try {

                Thread.sleep(1000);
                ConnectionManager.sendObject(pingMessage, this.oos);
                PingAndErrorMessage answer = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(ois);

                if( !hasNameBeenSet ){
                    namePlayer = answer.errorMessage;
                    hasNameBeenSet = true;
                }


                if(answer.typeOfSetupMessage == TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_CLOSING){

                    PingAndErrorMessage closingAck = new PingAndErrorMessage(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_CLOSING_ACK, "Closing ack");
                    ConnectionManager.sendObject(closingAck, oos);
                    isActive = false;


                }


            } catch (Exception e)
            {

                //e.printStackTrace();

                isActive = false;
                LogPrinter.printOnLog(Global.SOMETHINGWENTWRONGINTHEPINGHANDLER);
                LogPrinter.printOnLog(Global.BACKSLASHN +Arrays.toString(e.getStackTrace()));


                String uniquePlayerCode = ServerThread.ListIdentities.retrievePlayerIdentityByName(namePlayer).getUniquePlayerCode();

                ServerFsm fsmContext = ServerThread.getFsmByUniqueCode(uniquePlayerCode);


                if(fsmContext.getAssignedLobby() != null){

                    try {

                        fsmContext.getAssignedLobby().removeFsmClientHandlerFromList(ServerThread.ListIdentities.retrievePlayerIdentity(fsmContext.getUniquePlayerCode()));

                    } catch (IOException ex) {
                        LogPrinter.printOnLog(Global.COULDNOTREMOVEFROMLOBBY);
                        LogPrinter.printOnLog(e.toString());
                    }
                }

                fsmContext.setEverythingOkFalse();
                ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());


                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    LogPrinter.printOnLog(Global.ASYNCRONOUSPINGHANDLERWASNOTABLETOCLOSETHECONNECTION);
                    Thread.currentThread().interrupt();
                }


                Thread.currentThread().interrupt();


            }



        }while (isActive);


        String uniquePlayerCode = ServerThread.ListIdentities.retrievePlayerIdentityByName(namePlayer).getUniquePlayerCode();

        ServerFsm fsmContext = ServerThread.getFsmByUniqueCode(uniquePlayerCode);

        if(fsmContext.getAssignedLobby() != null) {

            try {

                fsmContext.getAssignedLobby().removeFsmClientHandlerFromList(ServerThread.ListIdentities.retrievePlayerIdentity(fsmContext.getUniquePlayerCode()));

            } catch (IOException ex) {
                LogPrinter.printOnLog(Global.COULDNOTREMOVEFROMLOBBY);
                LogPrinter.printOnLog(ex.toString());
            }
        }

        ServerThread.ListIdentities.removePlayerFromListIdentities(fsmContext.getUniquePlayerCode());

        try {
            clientSocket.close();
        } catch (IOException ex) {
            LogPrinter.printOnLog(Global.ASYNCRONOUSPINGHANDLERWASNOTABLETOCLOSETHECONNECTION);
            Thread.currentThread().interrupt();
        }



    }


}
