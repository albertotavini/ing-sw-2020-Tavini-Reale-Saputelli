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

public class ChatHandler implements Runnable{

    private final Socket clientSocket;
    private boolean isActive = true;

    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    private String namePlayer;
    private boolean hasNameBeenSet = false;

    public ChatHandler(Socket chatSocket) throws IOException {

        this.clientSocket = chatSocket;
        this.ois = new ObjectInputStream(chatSocket.getInputStream());
        this.oos = new ObjectOutputStream(chatSocket.getOutputStream());
    }


    @Override
    public void run() {

        PingAndErrorMessage pingMessage = new PingAndErrorMessage(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, Global.PING);


            try {

                ConnectionManager.sendObject(pingMessage, this.oos);
                PingAndErrorMessage answer = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(ois);


                namePlayer = answer.errorMessage;
                hasNameBeenSet = true;


            } catch (Exception e)
            {

                isActive = false;
                LogPrinter.printOnLog("Something went wrong in the chat handler");
                LogPrinter.printOnLog(Global.BACKSLASHN + Arrays.toString(e.getStackTrace()));


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


            }






        String uniquePlayerCode = ServerThread.ListIdentities.retrievePlayerIdentityByName(namePlayer).getUniquePlayerCode();

        ServerFsm fsmContext = ServerThread.getFsmByUniqueCode(uniquePlayerCode);

        fsmContext.setChatOos(oos);
        fsmContext.setChatOis(ois);



    }


}
