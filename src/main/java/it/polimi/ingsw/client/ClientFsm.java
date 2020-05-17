package it.polimi.ingsw.client;


import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.*;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.SetNameMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.WaitingInLobbyMessage;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.server.model.BoardPhotography;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        ClientViewAdapter.printMessage(setNameMessageAnswer.errorMessage);
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
                SetNameMessage setNameMessageAnswer = (SetNameMessage) ConnectionManager.receiveObject(fsmContext.getOis());

                if(setNameMessageAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.Fail)){

                    askingNewName(setNameMessageAnswer);
                    canContinue = false;

                }


                if(setNameMessageAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.SetNameStateCompleted)){
                    //invio un messaggio di success
                    ClientViewAdapter.printMessage("Ho completato la fase di set del nome");
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
                MenuMessage serverAnswer = (MenuMessage) ConnectionManager.receiveObject(fsmContext.getOis());


                if(serverAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.Fail)){
                    //non vado avanti
                    ClientViewAdapter.printMessage(serverAnswer.errorMessage);
                    jumpToInGameState = false;
                    canContinue = false;
                }


                if(serverAnswer.typeOfSetupMessage.equals(TypeOfSetupMessage.CreateOrParticipateStateCompleted)){
                    //invio un messaggio di success
                    ClientViewAdapter.printMessage(serverAnswer.errorMessage);
                    jumpToInGameState = false;
                    canContinue = true;
                }


                if(serverAnswer.typeOfSetupMessage == TypeOfSetupMessage.ChoosePartecipateCanJumpToInGameState){
                    //il client ha completato la lobby e può passare direttamente all'ingame state
                    jumpToInGameState = true;
                    ClientViewAdapter.printMessage("Salto direttamente all'in game state");
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

        clientExecutor.submit(new WaitingCompanion());

        do{

            try {

                WaitingInLobbyMessage waitingInLobbyMessage = (WaitingInLobbyMessage) ConnectionManager.receiveObject(fsmContext.getOis());

                switch (waitingInLobbyMessage.typeOfSetupMessage) {


                    case WaitingInLobbyStateCompleted:
                        ClientViewAdapter.printMessage("The lobby is full, now you can start playing!");
                        canContinueToInGameState = true;
                        break;


                    case WaitingInLobbyPlayerDisconnected:

                        ClientViewAdapter.printMessage(waitingInLobbyMessage.getNameOfPlayer() + " has disconnected from the lobby");
                        canContinueToInGameState = false;
                        break;


                    case WaitingInLobbyPlayerJoined:

                        ClientViewAdapter.printMessage(waitingInLobbyMessage.getNameOfPlayer() + " has joined the lobby");
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



    private class WaitingCompanion implements Runnable{

        @Override
        public void run() {

            ClientViewAdapter.printMessage("Waiting in lobby");

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

        ClientViewAdapter.printMessage(ColorAnsi.RED +"\n\n\nNOW IT'S TIME TO PLAY\n" +ColorAnsi.RESET);
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

                    Object inputObject = ConnectionManager.receiveObject(fsmContext.getOis());

                    if (inputObject instanceof InGameServerMessage) {

                        boardPhotography = ((InGameServerMessage) inputObject).getBoardPhotography();
                        modelMessage = ((InGameServerMessage) inputObject).getModelMessage();

                        if (boardPhotography != null) {

                            ClientViewAdapter.updateBoard(boardPhotography);
                        }


                        if ( packetFilter(modelMessage) ) {

                            ClientViewAdapter.printMessage(modelMessage.getMessage());

                            //SIMO la mia idea sarebbe di mettere qua un else che se il messaggio non è per loro, mette currentModelMessage a tipo WAIT
                            //ho gia aggiunto l'enumerazione, ragionaci tu

                            handleModelMessage(modelMessage);

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

            return modelMessage != null && (modelMessage.isBroadcast() || modelMessage.getReceivingPlayer().equals(fsmContext.getPlayerName()));



        }

        private void handleModelMessage(ModelMessage modelMessage) throws IOException {

            switch (modelMessage.getModelMessageType()) {

                case DISCONNECTED:
                    ClientViewAdapter.printMessage("You have been disconnected");
                    ClientMain.closeConnectionChannels();
                    canContinueToFinalState = true;
                    break;


                case GAMEOVER:
                    canContinueToFinalState = true;
                    break;


                case NEEDSCONFIRMATION:
                    //invio il messaggio con la stringa relativa
                    PlayerMove playerMoveConfirmation = ClientViewAdapter.askForInGameConfirmation(modelMessage.getMessage());
                    ConnectionManager.sendObject(playerMoveConfirmation, fsmContext.getOos());
                    break;


                case NEEDSGODNAME:
                    //invio il messaggio con la stringa relativa
                    PlayerMove playerMoveGodName = ClientViewAdapter.askForGodName(modelMessage.getMessage());
                    ConnectionManager.sendObject(playerMoveGodName, fsmContext.getOos());
                    break;


                case NEEDSCOORDINATES:
                    //invio il messaggio con la stringa relativa
                    PlayerMove playerMoveCoordinates = ClientViewAdapter.askForCoordinates(modelMessage.getMessage());
                    ConnectionManager.sendObject(playerMoveCoordinates, fsmContext.getOos());
                    break;

                case WAIT:
                    ClientViewAdapter.printMessage(" a' sptta");
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
