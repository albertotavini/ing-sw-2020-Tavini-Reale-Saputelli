package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.PingAndErrorMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.utils.LogPrinter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientChatThread implements Runnable{

    private final SocketChannel chatChannel;
    private final String nameClient;
    private final ClientFsm fsmContext;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;


    public ClientChatThread(SocketChannel chatChannel, String nameClient, ClientFsm fsmContext){

        this.chatChannel = chatChannel;
        this.nameClient = nameClient;
        this.fsmContext = fsmContext;
    }


    @Override
    public void run() {


        try {

            if (chatChannel.connect(new InetSocketAddress(ClientMain.serverIpAddress, ClientMain.serverPortChat))) {

                oos = new ObjectOutputStream(this.chatChannel.socket().getOutputStream());
                ois = new ObjectInputStream(this.chatChannel.socket().getInputStream());

                PingAndErrorMessage messageReceived;
                PingAndErrorMessage nameMessage;


                messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(ois);

                System.out.println("Ho ricevuto un messaggio sulla socket della chat");



                nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);
                ConnectionManager.sendObject(nameMessage, oos);



                fsmContext.setChatOis(ois);
                fsmContext.setChatOos(oos);

                System.out.println("Ho settato gli stream per la chat sul client");



            }


        }catch(ConnectException ex){

            ex.printStackTrace();
            ClientViewAdapter.printMenuMessage(Global.ICOULDNOTCONNECTOTHESERVERDUETOPINGANDERRORS);

        } catch (Exception e) {

            e.printStackTrace();
            ClientViewAdapter.printMenuMessage(Global.SOMETHINGWRONGHAPPENEDCLOSINGTHEAPPLICATION);
            System.exit(-1);

        }
    }
}
