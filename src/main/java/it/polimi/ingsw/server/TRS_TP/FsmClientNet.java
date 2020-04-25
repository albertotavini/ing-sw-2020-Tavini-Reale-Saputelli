package it.polimi.ingsw.server.TRS_TP;


import it.polimi.ingsw.server.model.Date;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class FsmClientNet {

    private ClientState currentClientState;
    final ObjectOutputStream SocketobjectOutputStream;
    final ObjectInputStream SocketobjectInputStream;

    private String playerName;
    private Date playerBirthday;

    private final Socket serverSocket;

    public FsmClientNet(Socket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
        this.SocketobjectOutputStream = new ObjectOutputStream(serverSocket.getOutputStream());
        this.SocketobjectInputStream = new ObjectInputStream(serverSocket.getInputStream());
        this.currentClientState = new ClientSetIdentityState(SocketobjectOutputStream, SocketobjectInputStream, serverSocket, this);
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

    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    private final Socket serverSocket;
    private final FsmClientNet fsmContext;



    public ClientSetIdentityState(ObjectOutputStream oos, ObjectInputStream ois, Socket serverSocket, FsmClientNet fsmContext) {
        this.oos = oos;
        this.ois = ois;
        this.serverSocket = serverSocket;
        this.fsmContext = fsmContext;

    }


    @Override
    public void handleClientFsm() {

        this.communicateWithTheServer();
        //setto il prossimo stato
        fsmContext.setState(new ClientCreateOrParticipateState(oos, ois, serverSocket, fsmContext));
    }

    @Override
    public void communicateWithTheServer() {

        boolean canContinue = false;

        do{

            try {

                //Invio un messaggio con all'interno il nome scelto e il compleanno
                SetNameMessage setNameMessageQuestion = new SetNameMessage(fsmContext.getPlayerName(), fsmContext.getPlayerBirthday());
                oos.writeObject(setNameMessageQuestion);
                oos.flush();


                //ricevo la risposta dal server
                SetNameMessage setNameMessageAnswer = (SetNameMessage) ois.readObject();


                if(setNameMessageAnswer.typeOfMessage.equals(TypeOfMessage.Fail)){
                    ClientViewAdapter.printMessage(setNameMessageAnswer.errorMessage);
                    String newName = ClientViewAdapter.askForName();
                    //cambio il nome nel contesto della fsm
                    fsmContext.setPlayerName(newName);
                    canContinue = false;
                }


                if(setNameMessageAnswer.typeOfMessage.equals(TypeOfMessage.SetNameStateCompleted)){
                    //invio un messaggio di success
                    ClientViewAdapter.printMessage("Ho completato la fase di set del nome");
                    canContinue = true;
                }



            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }while(!canContinue);
    }
}


class ClientCreateOrParticipateState implements ClientState {



    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    private final Socket serverSocket;
    private final FsmClientNet fsmContext;



    public ClientCreateOrParticipateState(ObjectOutputStream oos, ObjectInputStream ois, Socket serverSocket, FsmClientNet fsmContext) {
        this.oos = oos;
        this.ois = ois;
        this.serverSocket = serverSocket;
        this.fsmContext = fsmContext;

    }



    @Override
    public void handleClientFsm() {

        this.communicateWithTheServer();
        //setto il prossimo stato
        fsmContext.setState(new ClientFinalState(oos, ois, serverSocket, fsmContext));

    }

    @Override
    public void communicateWithTheServer() {


        boolean canContinue = false;

        do{

            try {

                //chiedo al giocatore se vuole creare o partecipare ad una lobby
                boolean wantsToCreate = ClientViewAdapter.askIfPlayerWantsToCreate();


                if( wantsToCreate ) {
                    //ho chiesto le info necessarie al client e mi ha risposto, posso inviare i dati al server
                    MenuMessages menuMessage = ClientViewAdapter.askForInfoToCreateLobby();
                    oos.writeObject(menuMessage);
                    oos.flush();

                }

                //il client ha deciso di partecipare ad una lobby
                else {

                    //chiedo se vuole partecipare ad una lobby pubblica o privata
                    boolean wantsLobbyPublic = ClientViewAdapter.askIfWantsToParticipateLobbyPublic();
                    //chiedo informazioni sulla lobby in questione
                    MenuMessages menuMessage = ClientViewAdapter.askForInfoToParticipateLobby(wantsLobbyPublic);
                    oos.writeObject(menuMessage);
                    oos.flush();
                }


                //ricevo la risposta dal server
                MenuMessages serverAnswer = (MenuMessages) ois.readObject();


                if(serverAnswer.typeOfMessage.equals(TypeOfMessage.Fail)){
                    //non vado avanti
                    ClientViewAdapter.printMessage(serverAnswer.errorMessage);
                    canContinue = false;
                }


                if(serverAnswer.typeOfMessage.equals(TypeOfMessage.CreateOrParticipateStateCompleted)){
                    //invio un messaggio di success
                    ClientViewAdapter.printMessage(serverAnswer.errorMessage);
                    canContinue = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }while(!canContinue);


    }
}


class ClientFinalState implements ClientState {

    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    private final Socket serverSocket;
    private final FsmClientNet fsmContext;



    public ClientFinalState(ObjectOutputStream oos, ObjectInputStream ois, Socket serverSocket, FsmClientNet fsmContext) {
        this.oos = oos;
        this.ois = ois;
        this.serverSocket = serverSocket;
        this.fsmContext = fsmContext;

    }

    @Override
    public void handleClientFsm() {

        System.out.println("sono un puzzofiato");
    }

    @Override
    public void communicateWithTheServer() {

    }
}



