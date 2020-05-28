package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.*;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.SetNameMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.WaitingInLobbyMessage;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AsynchronousCloseException;
import java.util.NoSuchElementException;

import static it.polimi.ingsw.client.ClientMain.clientExecutor;


public class ClientFsm {

    private ClientState currentClientState;
    private final ObjectOutputStream socketobjectOutputStream;
    private final ObjectInputStream socketobjectInputStream;

    private String playerName;
    private Date playerBirthday;

    private final Socket serverSocket;

    public ClientFsm(Socket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        this.socketobjectOutputStream = new ObjectOutputStream(serverSocket.getOutputStream());
        this.socketobjectInputStream = new ObjectInputStream(serverSocket.getInputStream());

        this.currentClientState = new ClientSetIdentityState(this);
        this.playerName = ClientViewAdapter.askForName();
        this.playerBirthday = ClientViewAdapter.askForDate();
    }

    public void setState(ClientState nextServerState) {
        currentClientState = nextServerState;
    }

    public void handleClientFsm() {
        currentClientState.handleClientFsm();
    }


    public String getPlayerName(){
        return playerName;
    }
    public Date getPlayerBirthday(){ return playerBirthday;}
    public void setPlayerName(String playerName){
        this.playerName = playerName;
    }
    public ObjectInputStream getOis() {
        return socketobjectInputStream;
    }
    public ObjectOutputStream getOos() {
        return socketobjectOutputStream;
    }
    public Socket getServerSocket() {
        return serverSocket;
    }





    public void run() {

        do{

            this.handleClientFsm();


            //finchè non sono arrivato all'ultimo stato non mi fermo
        }while(  !(this.currentClientState instanceof ClientFinalState)  );



        //*********************codice dell'ultimo stato


    }


}


interface ClientState {
    void handleClientFsm();
    void communicateWithTheServer();
}


class ClientSetIdentityState implements ClientState {

    private final ClientFsm fsmContext;

    public ClientSetIdentityState(ClientFsm fsmContext) {
        this.fsmContext = fsmContext;
    }

    private void askingNewName(SetNameMessage setNameMessageAnswer){
        ClientViewAdapter.printMenuMessage(setNameMessageAnswer.errorMessage);
        String newName = ClientViewAdapter.askForName();
        //cambio il nome nel contesto della fsm
        fsmContext.setPlayerName(newName);
    }


    @Override
    public void handleClientFsm() {

        this.communicateWithTheServer();
        //setto il prossimo stato
        fsmContext.setState(new ClientCreateOrParticipateState(fsmContext));
    }

    @Override
    public void communicateWithTheServer() {

        boolean canContinue = false;

        do{

            try {

                //Invio un messaggio con all'interno il nome scelto e il compleanno
                ConnectionManager.sendObject(SetNameMessage.newSetNameMessageComplete(fsmContext.getPlayerName(), fsmContext.getPlayerBirthday()), fsmContext.getOos());

                //ricevo la risposta dal server
                SetNameMessage setNameMessageAnswer = (SetNameMessage) ConnectionManager.receiveStandardObject(fsmContext.getOis());

                if(setNameMessageAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.FAIL)){

                    askingNewName(setNameMessageAnswer);
                    canContinue = false;

                }


                if(setNameMessageAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.SET_NAME_STATE_COMPLETED)){
                    //invio un messaggio di success
                    ClientViewAdapter.printMenuMessage("Ho completato la fase di set del nome");
                    canContinue = true;
                }



            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } while(!canContinue);
    }

}


class ClientCreateOrParticipateState implements ClientState {

    private final ClientFsm fsmContext;

    private boolean jumpToInGameState = false;

    public ClientCreateOrParticipateState(ClientFsm fsmContext) {
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


    @Override
    public void handleClientFsm() {


        //fa partire il thread che gestisce i ping
        clientExecutor.submit(new ClientPingAndErrorThread(ClientMain.getErrorChannel(), fsmContext.getPlayerName()));


        this.communicateWithTheServer();
        //setto il prossimo stato

        if(!jumpToInGameState)fsmContext.setState(new ClientWaitingInLobbyState(fsmContext));

        if(jumpToInGameState)fsmContext.setState(new ClientInGameState(fsmContext));

    }

    @Override
    public void communicateWithTheServer() {


        boolean canContinue = false;

        do{

            try {

                //chiedo al giocatore se vuole creare o partecipare ad una lobby
                boolean wantsToCreate = ClientViewAdapter.askBooleanQuestion("Vuoi creare una lobby? y/n");


                if( wantsToCreate ) {

                    //ho chiesto le info necessarie al client e mi ha risposto, posso inviare i dati al server
                    creatingLobby(fsmContext);

                }

                //il client ha deciso di partecipare ad una lobby
                else {

                    //chiedo se vuole partecipare ad una lobby pubblica o privata
                    boolean wantsLobbyPublic = ClientViewAdapter.askBooleanQuestion("Vuoi partecipare ad una lobby pubblica? y/n");

                    //chiedo informazioni sulla lobby in questione
                    joiningLobby(wantsLobbyPublic, fsmContext);

                }

                //ricevo la risposta dal server
                MenuMessage serverAnswer = (MenuMessage) ConnectionManager.receiveStandardObject(fsmContext.getOis());


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
                    ClientViewAdapter.printMenuMessage("Salto direttamente all'in game state");
                    canContinue = true;

                }



            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }while(!canContinue);

    }

}


class ClientWaitingInLobbyState implements ClientState {

    private final ClientFsm fsmContext;
    private boolean hasToWait = true;


    public ClientWaitingInLobbyState(ClientFsm fsmContext) {
        this.fsmContext = fsmContext;

    }


    @Override
    public void handleClientFsm() {

        this.communicateWithTheServer();
        //setto il prossimo stato
        this.hasToWait = false;
        fsmContext.setState(new ClientInGameState(fsmContext));

    }

    @Override
    public void communicateWithTheServer() {

        boolean canContinueToInGameState = false;

        if( !ClientViewAdapter.isMenuInterfaceAGui() )clientExecutor.submit(new CliWaitingCompanion());

        else MenuGui.setWaitInLobby();

        do{

            try {

                WaitingInLobbyMessage waitingInLobbyMessage = (WaitingInLobbyMessage) ConnectionManager.receiveStandardObject(fsmContext.getOis());

                switch (waitingInLobbyMessage.typeOfSetupMessage) {


                    case WAITING_IN_LOBBY_STATE_COMPLETED:
                        ClientViewAdapter.printMenuMessage("The lobby is full, now you can start playing!");
                        canContinueToInGameState = true;
                        break;


                    case WAITING_IN_LOBBY_PLAYER_DISCONNECTED:

                        ClientViewAdapter.printMenuMessage(waitingInLobbyMessage.getNameOfPlayer() + " has disconnected from the lobby");
                        canContinueToInGameState = false;
                        break;


                    case WAITING_IN_LOBBY_PLAYER_JOINED:

                        ClientViewAdapter.printMenuMessage(waitingInLobbyMessage.getNameOfPlayer() + " has joined the lobby");
                        canContinueToInGameState = false;
                        break;


                    default:
                        canContinueToInGameState = false;
                        break;


                }



            } catch(SocketException | AsynchronousCloseException e){

                try {
                    fsmContext.getServerSocket().close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }while(!canContinueToInGameState);


    }



    private class CliWaitingCompanion implements Runnable{

        @Override
        public void run() {

            ClientViewAdapter.printMenuMessage("Waiting in lobby");

            do {

                System.out.printf("%s", "#");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    LogPrinter.printOnLog(e.toString());
                    Thread.currentThread().interrupt();
                }

            }while(hasToWait);
        }
    }




}


class ClientInGameState implements ClientState {

    private final ClientFsm fsmContext;
    private boolean canContinueToFinalState;
    private ModelMessage currentModelMessage;


    public ClientInGameState(ClientFsm fsmContext) {

        this.fsmContext = fsmContext;
        this.canContinueToFinalState = false;
        this.currentModelMessage = null;

    }


    @Override
    public void handleClientFsm() {

        ClientViewAdapter.printInGameMessage(ColorAnsi.RED +"\n\n\nNOW IT'S TIME TO PLAY\n" +ColorAnsi.RESET);
        //avvio la transizione da menu a ingame gui
        ClientViewAdapter.fromMenuToInGameGui();
        this.communicateWithTheServer();
        //setto il prossimo stato
        fsmContext.setState(new ClientFinalState(fsmContext));


    }


    private class InGameIoHandler implements Runnable {

        @Override
        public void run() {

            try {

                BoardPhotography boardPhotography = null;
                ModelMessage modelMessage = null;

                while (!canContinueToFinalState) {

                    Object inputObject = ConnectionManager.receiveStandardObject(fsmContext.getOis());

                    if (inputObject instanceof InGameServerMessage) {

                        boardPhotography = ((InGameServerMessage) inputObject).getBoardPhotography();
                        modelMessage = ((InGameServerMessage) inputObject).getModelMessage();

                        if (boardPhotography != null) {

                            ClientViewAdapter.updateBoard(boardPhotography);
                        }

                        ClientViewAdapter.printSecondaryInGameMessage(" \n");

                        if ( packetFilter(modelMessage) ) {

                            if(modelMessage.getModelError() != ModelError.NONE) {
                                ClientViewAdapter.printSecondaryInGameMessage("NOT ALLOWED: "+modelMessage.getModelError().toString()+"\n");
                            }

                            ClientViewAdapter.printInGameMessage(modelMessage.getMessage());

                            handleModelMessage(modelMessage);

                        }
                        else {
                            handleModelMessage(new ModelMessage(ModelMessageType.WAIT, ""));
                        }

                    }

                }
            } catch (Exception e){
                System.out.println("L'oggetto ricevuto nell'async read non è valido");
                e.printStackTrace();
                //qua va portato a false canContinueToFinalState?
            }
        }

        private boolean packetFilter(ModelMessage modelMessage) {

            return modelMessage != null && (modelMessage.getModelMessageType().equals(ModelMessageType.GODHASBEENCHOSEN) ||
                    modelMessage.getModelMessageType().equals(ModelMessageType.GAMEOVER) ||
                    modelMessage.getModelMessageType().equals(ModelMessageType.DISCONNECTED) ||
                    modelMessage.getCurrentPlayer().equals(fsmContext.getPlayerName()));



        }

        private void handleModelMessage(ModelMessage modelMessage) throws IOException {

            switch (modelMessage.getModelMessageType()) {

                case DISCONNECTED:
                    ClientViewAdapter.printInGameMessage("You have been disconnected");
                    ClientMain.closeConnectionChannels();
                    canContinueToFinalState = true;
                    break;

                case YOULOST:

                case GAMEOVER:
                    canContinueToFinalState = true;
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
                    if (modelMessage.getCurrentPlayer().equals(fsmContext.getPlayerName())) {
                        ClientViewAdapter.showChosenGods(modelMessage, true);
                    }
                    else {
                        ClientViewAdapter.showChosenGods(modelMessage, false);
                    }
                     break;

                case WAIT:
                    ClientViewAdapter.printInGameMessage("\nNow wait for the other players to do complete their operations");
                    break;

                default:
                    System.out.println("the playermove's type is not specified correctly");
                    break;
            }


        }




    }


    @Override
    public void communicateWithTheServer() {

        try{

            Thread inGameIoHandler = new Thread(new InGameIoHandler());
            inGameIoHandler.start();
            inGameIoHandler.join();

        } catch(InterruptedException | NoSuchElementException e){
            System.out.println("Connection closed from the client side");
            //da vedere
            Thread.currentThread().interrupt();
        }
    }


}


class ClientFinalState implements ClientState {

    private final ClientFsm fsmContext;



    public ClientFinalState(ClientFsm fsmContext) {
        this.fsmContext = fsmContext;

    }

    @Override
    public void handleClientFsm() {

        //da vedere
    }

    @Override
    public void communicateWithTheServer() {

        //da vedere
    }
}
