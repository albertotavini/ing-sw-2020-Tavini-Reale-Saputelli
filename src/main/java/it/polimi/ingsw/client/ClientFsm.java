package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.*;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMoveType;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.*;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AsynchronousCloseException;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static it.polimi.ingsw.client.ClientMain.clientExecutor;



public class ClientFsm {

    private ClientState currentClientState;
    private final ObjectOutputStream standardOos;
    private final ObjectInputStream standardOis;

    private ObjectOutputStream chatOos = null;
    private ObjectInputStream chatOis = null;

    private String playerName;
    private Date playerBirthday;

    private final Socket serverSocket;
    private ClientPingAndErrorThread pingAndErrorThread = null;
    private ClientChatThread chatThread = null;

    ClientFsm(Socket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        this.standardOos = new ObjectOutputStream(serverSocket.getOutputStream());
        this.standardOis = new ObjectInputStream(serverSocket.getInputStream());

        this.currentClientState = new ClientSetIdentityState(this);
        this.playerName = ClientViewAdapter.askForName();
        this.playerBirthday = ClientViewAdapter.askForDate();
    }

    void setState(ClientState nextServerState) {
        currentClientState = nextServerState;
    }

    private void handleClientFsm() {
        currentClientState.handleClientFsm();
    }


    String getPlayerName(){
        return playerName;
    }
    Date getPlayerBirthday(){ return playerBirthday;}
    void setPlayerName(String playerName){
        this.playerName = playerName;
    }
    ObjectInputStream getOis() {
        return standardOis;
    }
    ObjectOutputStream getOos() {
        return standardOos;
    }

    ObjectInputStream getChatOis() {
        return chatOis;
    }

    ObjectOutputStream getChatOos() {
        return chatOos;
    }

    Socket getServerSocket() {
        return serverSocket;
    }
    ClientPingAndErrorThread getPingAndErrorThread() {
        return pingAndErrorThread;
    }
    ClientChatThread getChatThread(){return chatThread;}
    void setPingAndErrorThread(ClientPingAndErrorThread pingAndErrorThread) {
        this.pingAndErrorThread = pingAndErrorThread;
    }
    void setChatThread(ClientChatThread chatThread){
        this.chatThread = chatThread;
    }

    void setChatOis(ObjectInputStream chatOis) {
        this.chatOis = chatOis;
    }
    void setChatOos(ObjectOutputStream chatOos) {
        this.chatOos = chatOos;
    }

    void sendChatMessage(PlayerMove chatMessage) {

        try {

            ConnectionManager.sendObject(chatMessage, chatOos);
            System.out.println("Sono nel ClientFsm e sto inviando il messaggio: " +chatMessage);

        } catch (IOException e) {

            LogPrinter.printOnLog(Global.CHATERROR);
            LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            Thread.currentThread().interrupt();

        }

    }





    public void run() {

        do{

            this.handleClientFsm();

            //finchè non sono arrivato all'ultimo stato non mi fermo
        }while(  !(this.currentClientState instanceof ClientEndState)  );



        //*********************codice dell'ultimo stato
        this.handleClientFsm();


    }


}

/**
 * this interface is used to build an implementation of the state pattern that helps the client navigate through the different parts of the interaction with the
 *server
 *
 */

interface ClientState {
    void handleClientFsm();
    void communicateWithTheServer();
}


/**
 * this class is the first state of the machine, which is used for configuring the name and birthdate of the player
 * we move to the next one only when the info of the player has been accepted from the server
 */
class ClientSetIdentityState implements ClientState {

    private final ClientFsm fsmContext;

    /**
     * @param fsmContext the client fsm that will be assigned
     */
    public ClientSetIdentityState(ClientFsm fsmContext) {
        this.fsmContext = fsmContext;
    }

    private void askingNewName(SetNameMessage setNameMessageAnswer){
        ClientViewAdapter.printMenuMessage(setNameMessageAnswer.errorMessage);
        String newName = ClientViewAdapter.askForName();

        //cambio il nome nel contesto della fsm
        fsmContext.setPlayerName(newName);
    }


    /**
     * this method will simply call the comunicate, and when that method ends, it will move to the subsequent state
     */
    @Override
    public void handleClientFsm() {

        this.communicateWithTheServer();
        //setto il prossimo stato
        fsmContext.setState(new ClientCreateOrParticipateState(fsmContext));
    }

    /**
     * this method will send to the server the name and date the player inserted, and if the name has already been chosen the server
     * will comunicate it and the client will ask the player to chose another one, until the server accepts and the set of the
     * identity has been concluded
     */
    @Override
    public void communicateWithTheServer() {

        boolean canContinue = false;
        Object objReceived;

        do{

            try {

                //Invio un messaggio con all'interno il nome scelto e il compleanno
                ConnectionManager.sendObject(SetNameMessage.newSetNameMessageComplete(fsmContext.getPlayerName(), fsmContext.getPlayerBirthday()), fsmContext.getOos());

                do{//check that the object that arrives is correct

                    objReceived = ConnectionManager.receiveStandardObject(fsmContext.getOis());

                }while(!(objReceived  instanceof SetNameMessage));
                //ricevo la risposta dal server

                SetNameMessage setNameMessageAnswer = (SetNameMessage) objReceived;

                if(setNameMessageAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.FAIL)){

                    askingNewName(setNameMessageAnswer);
                    canContinue = false;

                }


                if(setNameMessageAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.SET_NAME_STATE_COMPLETED)){
                    //invio un messaggio di success
                    ClientViewAdapter.printMenuMessage(Global.IDENTITYSET);
                    canContinue = true;
                }



            } catch (IOException | ClassNotFoundException e) {
                LogPrinter.printOnLog(Global.IDENTITYSTATECLIENTERROR);
                LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
                e.printStackTrace();

            }

        } while(!canContinue);
    }

}


/**
 * in this state the player decides if he wants to create a new lobby or join one
 */
class ClientCreateOrParticipateState implements ClientState {

    private final ClientFsm fsmContext;

    private boolean jumpToInGameState = false;

    ClientCreateOrParticipateState(ClientFsm fsmContext) {
        this.fsmContext = fsmContext;
    }

    private void creatingLobby(ClientFsm fsmContext) throws IOException {
        MenuMessage menuMessage = ClientViewAdapter.askForInfoToCreateLobby(fsmContext.getPlayerName());
        ConnectionManager.sendObject(menuMessage, fsmContext.getOos());
    }

    private void joiningLobby(boolean wantsLobbyPublic, ClientFsm fsmContext) throws IOException {
        MenuMessage menuMessage = ClientViewAdapter.askForInfoToParticipateLobby(wantsLobbyPublic, fsmContext.getPlayerName());
        ConnectionManager.sendObject(menuMessage, fsmContext.getOos());
    }


    /**
     * this method will generate the ping and error handler if it hasn't been done already, and also create the thread that allows the chat to work
     * then will call the comunicatewithTheServer and after it is finished will jump to next state, which can eiter be waitinginLobby
     * or directly InGame
     */
    @Override
    public void handleClientFsm() {


        //fa partire il thread che gestisce i ping
        if(fsmContext.getPingAndErrorThread() == null) {

            ClientPingAndErrorThread clientPingAndErrorThread = new ClientPingAndErrorThread(ClientMain.getErrorChannel(), fsmContext.getPlayerName());
            fsmContext.setPingAndErrorThread(clientPingAndErrorThread);
            clientExecutor.submit(clientPingAndErrorThread);

        }



        ClientChatThread clientChatThread = new ClientChatThread(ClientMain.getChatChannel1(), ClientMain.getChatChannel2() ,fsmContext.getPlayerName(), fsmContext);
        fsmContext.setChatThread(clientChatThread);
        clientExecutor.submit(clientChatThread);



        this.communicateWithTheServer();
        //setto il prossimo stato

        if(!jumpToInGameState)fsmContext.setState(new ClientWaitingInLobbyState(fsmContext));

        if(jumpToInGameState)fsmContext.setState(new ClientInGameState(fsmContext));

    }

    /**
     * this method will call al the methods that take input from the player on whether he wants to create or join a lobby, and will
     * wait response from the server, which will obviously deny joining a lobby that doesn't exist or creating a lobby with the same name
     * of another one already present
     */
    @Override
    public void communicateWithTheServer() {


        boolean canContinue = false;
        Object objReceived;

        do{

            try {

                //chiedo al giocatore se vuole creare o partecipare ad una lobby
                boolean wantsToCreate = ClientViewAdapter.askBooleanQuestion(Global.DOYOUWANTTOCREATEANEWLOBBY);


                if( wantsToCreate ) {

                    //ho chiesto le info necessarie al client e mi ha risposto, posso inviare i dati al server
                    creatingLobby(fsmContext);

                }

                //il client ha deciso di partecipare ad una lobby
                else {

                    //chiedo se vuole partecipare ad una lobby pubblica o privata
                    boolean wantsLobbyPublic = ClientViewAdapter.askBooleanQuestion(Global.DOYOUWANTTOJOINAPUBLICLOBBY);

                    //chiedo informazioni sulla lobby in questione
                    joiningLobby(wantsLobbyPublic, fsmContext);

                }

                do{

                    objReceived = ConnectionManager.receiveStandardObject(fsmContext.getOis());

                    LogPrinter.printOnLog(objReceived.getClass().getName() +" " +objReceived.toString());

                }while(!(objReceived instanceof MenuMessage));



                //ricevo la risposta dal server
                MenuMessage serverAnswer = (MenuMessage) objReceived;


                if(serverAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.FAIL)){
                    //non vado avanti
                    ClientViewAdapter.printMenuMessage(serverAnswer.errorMessage);
                    jumpToInGameState = false;
                    canContinue = false;
                }


                if(serverAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.CREATE_OR_PARTICIPATE_STATE_COMPLETED)){
                    //invio un messaggio di success
                    ClientViewAdapter.printMenuMessage(serverAnswer.errorMessage);
                    jumpToInGameState = false;
                    canContinue = true;
                }


                if(serverAnswer.typeOfSetupMessage == TypeOfSetupMessage.CHOOSE_PARTECIPATE_CAN_JUMP_TO_IN_GAME_STATE){
                    //il client ha completato la lobby e può passare direttamente all'ingame state
                    jumpToInGameState = true;
                    ClientViewAdapter.printMenuMessage(Global.TIMETOPLAY);
                    canContinue = true;

                }



            } catch (IOException | ClassNotFoundException e) {
                LogPrinter.printOnLog(Global.CHOOSEORCREATESTATECLIENTERROR);
                LogPrinter.printOnLog(e.toString());
                e.printStackTrace();
            }

        }while(!canContinue);

    }

}


/**
 * this state is used to wait in the lobby until the number of required players is met and the game can finally begin
 */
class ClientWaitingInLobbyState implements ClientState {

    private final ClientFsm fsmContext;
    private boolean hasToWait = true;


    public ClientWaitingInLobbyState(ClientFsm fsmContext) {
        this.fsmContext = fsmContext;

    }


    /**
     * tyhis method will call the corresponding comunicate and when it finishes to run it will move the fsm to InGameState
     */
    @Override
    public void handleClientFsm() {

        this.communicateWithTheServer();
        //setto il prossimo stato
        this.hasToWait = false;
        fsmContext.setState(new ClientInGameState(fsmContext));

    }

    /**
     * this method will call the waitingInLobbyCompanion on CLI or the waitInLobby window of the menuGUI until the server
     * informs that the game can begin
     */
    @Override
    public void communicateWithTheServer() {

        boolean canContinueToInGameState = false;
        Object objReceived;

        if( !ClientViewAdapter.isMenuInterfaceAGui() )clientExecutor.submit(new CliWaitingCompanion());

        else MenuGui.setWaitInLobby();

        do{

            try {

                do{//check that the object that arrives is correct

                objReceived = ConnectionManager.receiveStandardObject(fsmContext.getOis());

            }while(!(objReceived  instanceof WaitingInLobbyMessage));

                WaitingInLobbyMessage waitingInLobbyMessage = (WaitingInLobbyMessage)  objReceived;

                switch (waitingInLobbyMessage.typeOfSetupMessage) {


                    case WAITING_IN_LOBBY_STATE_COMPLETED:
                        ClientViewAdapter.printMenuMessage(Global.LOBBYFULL);
                        canContinueToInGameState = true;
                        break;


                    case WAITING_IN_LOBBY_PLAYER_DISCONNECTED:

                        ClientViewAdapter.printMenuMessage(waitingInLobbyMessage.getNameOfPlayer() + Global.HASDISCONNECTEDFROMLOBBY);
                        canContinueToInGameState = false;
                        break;


                    case WAITING_IN_LOBBY_PLAYER_JOINED:

                        ClientViewAdapter.printMenuMessage(waitingInLobbyMessage.getNameOfPlayer() + Global.HASJOINEDTHELOBBY);
                        canContinueToInGameState = false;
                        break;


                    default:
                        canContinueToInGameState = false;
                        break;


                }



            } catch(SocketException | AsynchronousCloseException e){
                LogPrinter.printOnLog(Global.CLIENTCLOSED);
                LogPrinter.printOnLog(Global.WAITINGSTATECLIENTERROR);
                LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
                e.printStackTrace();

                try {
                    fsmContext.getServerSocket().close();
                } catch (IOException ex) {
                    LogPrinter.printOnLog(Global.FAILEDTOCLOSESOCKET);
                    LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
                    e.printStackTrace();
                }

            } catch (IOException | ClassNotFoundException e) {
                LogPrinter.printOnLog(Global.WAITINGSTATECLIENTERROR);
                LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }


        }while(!canContinueToInGameState);


    }



    private class CliWaitingCompanion implements Runnable{

        /**
         * a little companion for the CLI that will print # while the player is waiting in the lobby
         */
        @Override
        public void run() {

            ClientViewAdapter.printMenuMessage(Global.WAITINGINLOBBY);

            do {

                System.out.printf("%s", Global.HASHTAG);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    LogPrinter.printOnLog(Global.WAITINGSTATECLIENTERROR);
                    LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

            }while(hasToWait);
        }
    }




}


/**
 * this state will just deal with the actual in game dialogue with the server, catching updates from model and
 * 
 */
class ClientInGameState implements ClientState {

    private final ClientFsm fsmContext;
    private boolean canContinueToChoiceRestartState;


    public ClientInGameState(ClientFsm fsmContext) {

        this.fsmContext = fsmContext;
        this.canContinueToChoiceRestartState = false;

    }


    @Override
    public void handleClientFsm() {

        ClientViewAdapter.printInGameMessage(Global.TIMETOPLAY );
        //avvio la transizione da menu a ingame gui
        ClientViewAdapter.fromMenuToInGameGui();
        this.communicateWithTheServer();
        ClientViewAdapter.fromInGameGuiToMenu();
        //setto il prossimo stato
        fsmContext.setState(new ClientChoiceNewGameState(fsmContext));


    }

    @Override
    public void communicateWithTheServer() {

        try{

            Thread inGameIoHandler = new Thread(new InGameIoHandler());
            inGameIoHandler.start();
            inGameIoHandler.join();


        } catch(InterruptedException | NoSuchElementException e){
            LogPrinter.printOnLog(Global.CLIENTCLOSED);
            LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }


    /**
     * this class is meant to intercept the packages arriving from server during the game and process them depending on
     * the type of modelmessage received
     * if of no interests, tells its user to wait for the situation to evolve
     *
     */
    private class InGameIoHandler implements Runnable {

        @Override
        public void run() {

            Thread chatMessageHandler = new Thread(new ChatMessageHandler());
            chatMessageHandler.start();

            try {

                BoardPhotography boardPhotography = null;
                ModelMessage modelMessage = null;

                while (!canContinueToChoiceRestartState) {

                    Object inputObject = ConnectionManager.receiveStandardObject(fsmContext.getOis());

                    if (inputObject instanceof InGameServerMessage) {

                        boardPhotography = ((InGameServerMessage) inputObject).getBoardPhotography();

                        modelMessage = ((InGameServerMessage) inputObject).getModelMessage();

                        if (boardPhotography != null) {

                            ClientViewAdapter.updateBoard(boardPhotography);
                        }

                        //fundamental so that if there is no new error the panel disappears
                        ClientViewAdapter.printSecondaryInGameMessage(Global.BACKSLASHN);

                        if ( packetFilter(modelMessage) ) {

                            processModelMessage(modelMessage);

                        }

                        else {

                            handleModelMessageBlocking(new ModelMessage(ModelMessageType.WAIT, Global.SPACE));

                        }

                    }

                }

            } catch (Exception e){
                LogPrinter.printOnLog(Global.READSERVERMESSAGEFAILED);
                LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        /**
         * controls if the modelMessage has info of any interest for this client
         *
         * @param modelMessage received from server
         * @return true if this client has interesting in processing this model message
         */
        private boolean packetFilter(ModelMessage modelMessage) {

            return modelMessage != null && (modelMessage.getModelMessageType().equals(ModelMessageType.GODHASBEENCHOSEN) ||
                    modelMessage.getModelMessageType().equals(ModelMessageType.GAMEOVER) ||
                    modelMessage.getModelMessageType().equals(ModelMessageType.DISCONNECTED) ||
                    modelMessage.getModelMessageType().equals(ModelMessageType.CHAT_MESSAGE) ||
                    modelMessage.getCurrentPlayer().equals(fsmContext.getPlayerName()));
        }

        /**
         * this method controls if there is need to print an error message
         * if the chat needs to be refreshed
         * and finally calls the handle blocking or not blocking depending on necessity
         *
         * @param modelMessage that has arrived from server
         * @throws IOException that is endled by run of the InGameIOHandler
         */
        private void processModelMessage (ModelMessage modelMessage ) throws IOException {

            if(modelMessage.getModelError() != ModelError.NONE) {

                ClientViewAdapter.printSecondaryInGameMessage(Global.NOTALLOWED +modelMessage.getModelError().toString()+Global.BACKSLASHN);
            }

            if(modelMessage.getModelMessageType() != ModelMessageType.CHAT_MESSAGE){

                ClientViewAdapter.printInGameMessage(modelMessage.getMessage());
            }


            handleModelMessageBlocking(modelMessage);

        }

        private void handleModelMessageBlocking(ModelMessage modelMessage) throws IOException {

            switch (modelMessage.getModelMessageType()) {


                case CHAT_MESSAGE:
                    ClientViewAdapter.refreshChat(modelMessage.getMessage());
                    break;

                case CONFIRMATION:
                    //invio il messaggio con la stringa relativa
                    PlayerMove playerMoveConfirmation = ClientViewAdapter.askForInGameConfirmation(modelMessage.getMessage());
                    ConnectionManager.sendObject(playerMoveConfirmation, fsmContext.getOos());
                    break;


                case GODNAME:
                    //invio il messaggio con la stringa relativa
                    PlayerMove playerMoveGodName = ClientViewAdapter.askForGodName(modelMessage.getMessage());
                    ConnectionManager.sendObject(playerMoveGodName, fsmContext.getOos());
                    break;


                case COORDINATES:
                    //invio il messaggio con la stringa relativa
                    PlayerMove playerMoveCoordinates = ClientViewAdapter.askForCoordinates(modelMessage.getMessage());
                    ConnectionManager.sendObject(playerMoveCoordinates, fsmContext.getOos());
                    break;

                case GODHASBEENCHOSEN:
                    //the second argument is true if this client is the one of the player that now chose the god
                    ClientViewAdapter.showChosenGods(modelMessage, modelMessage.getCurrentPlayer().equals(fsmContext.getPlayerName()));
                    break;

                case WAIT:
                    ClientViewAdapter.printInGameMessage(Global.WAITYOURTURN);
                    break;

                case DISCONNECTED:
                    ClientViewAdapter.printInGameMessage(Global.YOUHAVEBEENDISCONNECTED);
                    //ClientMain.closeConnectionChannels();
                    ConnectionManager.sendObject(PlayerMove.buildKillerPlayerMove(PlayerMoveType.KILL_IN_GAME_CONNECTION_GAMEOVER), fsmContext.getOos());
                    canContinueToChoiceRestartState = true;
                    break;

                case YOULOST:
                    ClientViewAdapter.printInGameMessage("YOU LOST");
                    ConnectionManager.sendObject(PlayerMove.buildKillerPlayerMove(PlayerMoveType.KILL_IN_GAME_CONNECTION_YOU_LOST), fsmContext.getOos());
                    canContinueToChoiceRestartState = true;
                    break;


                case GAMEOVER:
                    ClientViewAdapter.printInGameMessage(Global.GAMEOVER);
                    ConnectionManager.sendObject(PlayerMove.buildKillerPlayerMove(PlayerMoveType.KILL_IN_GAME_CONNECTION_GAMEOVER), fsmContext.getOos());
                    canContinueToChoiceRestartState = true;
                    break;

                default:
                    ClientViewAdapter.printInGameMessage(Global.INCORRECTPLAYERMOVE);
                    break;
            }


        }



        private class ChatMessageHandler implements Runnable {


            @Override
            public void run() {

                try {

                    while (!canContinueToChoiceRestartState) {

                        Object inputObject = ConnectionManager.receiveStandardObject(fsmContext.getChatOis());



                        if (inputObject instanceof InGameServerMessage) {

                            processModelMessage(((InGameServerMessage) inputObject).getModelMessage());

                            System.out.println("Ho ricevuto un messaggio: " +((InGameServerMessage) inputObject).getModelMessage());

                        }

                    }

                } catch (Exception e){
                    LogPrinter.printOnLog(Global.READSERVERMESSAGEFAILED);
                    LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
                }


            }
        }











    }






}


class ClientChoiceNewGameState implements ClientState {

    private final ClientFsm fsmContext;
    private boolean wantsToContinue = false;


    ClientChoiceNewGameState(ClientFsm fsmContext) {
        this.fsmContext = fsmContext;

    }

    @Override
    public void handleClientFsm() {


        this.communicateWithTheServer();

        if(wantsToContinue){

            ClientViewAdapter.resetGuiForNewMatch();
            fsmContext.setState(new ClientCreateOrParticipateState(fsmContext));


        }

        else fsmContext.setState(new ClientEndState(fsmContext));

    }

    @Override
    public void communicateWithTheServer() {

        FinalStateMessage finalAnswer = null;

        try {

            Object obj;

            do {

                obj = ConnectionManager.receiveStandardObject(fsmContext.getOis());

            }while(! (obj instanceof FinalStateMessage));

            finalAnswer = (FinalStateMessage) obj;


            boolean wantsToRestart = ClientViewAdapter.askBooleanQuestion(Global.DOYOUWANTTORESTART);


            if(wantsToRestart){

                wantsToContinue = true;
                finalAnswer = FinalStateMessage.newFinalStateMessageAnswer(true);
                ConnectionManager.sendObject(finalAnswer, fsmContext.getOos());

            }

            else{

                wantsToContinue = false;
                finalAnswer = FinalStateMessage.newFinalStateMessageAnswer(false);
                ConnectionManager.sendObject(finalAnswer, fsmContext.getOos());

            }


        } catch (Exception e) {

            e.printStackTrace();
            ClientViewAdapter.printMenuMessage(Global.FATALERRORINFINALSTATE);
            System.exit(-1);

        }


    }

}


class ClientEndState implements ClientState {

    private final ClientFsm fsmContext;

    ClientEndState(ClientFsm fsmContext) {
        this.fsmContext = fsmContext;
    }


    @Override
    public void handleClientFsm() {

        if(fsmContext.getPingAndErrorThread() != null){

            try {
                fsmContext.getPingAndErrorThread().closePingConnection();
            } catch (IOException e) {
                LogPrinter.printOnLog(Global.ERRORINENDSTATE);
                LogPrinter.printOnLog(Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }

        }


    }

    @Override
    public void communicateWithTheServer() {
        //here code not needed

    }

}
