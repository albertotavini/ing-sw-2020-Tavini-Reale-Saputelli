package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.PingAndErrorMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.utils.Global;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientChatThread implements Runnable{

    private final SocketChannel chatChannel1;
    private final SocketChannel chatChannel2;
    private final String nameClient;
    private final ClientFsm fsmContext;

    private ObjectOutputStream oosChannel1;
    private ObjectInputStream oisChannel2;


    public ClientChatThread(SocketChannel chatChannel1, SocketChannel chatChannel2, String nameClient, ClientFsm fsmContext){

        this.chatChannel1 = chatChannel1;
        this.chatChannel2 = chatChannel2;
        this.nameClient = nameClient;
        this.fsmContext = fsmContext;
    }


    @Override
    public void run() {


        try {

            if (chatChannel1.connect(new InetSocketAddress(ClientMain.serverIpAddress, ClientMain.serverPortChat1))) {

                oosChannel1 = new ObjectOutputStream(this.chatChannel1.socket().getOutputStream());
                ObjectInputStream receiveChannel1 = new ObjectInputStream(this.chatChannel1.socket().getInputStream());

                PingAndErrorMessage messageReceived;
                PingAndErrorMessage nameMessage;

                messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(receiveChannel1);

                nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);
                ConnectionManager.sendObject(nameMessage, oosChannel1);

                fsmContext.setChatOos(oosChannel1);

            }


            if (chatChannel2.connect(new InetSocketAddress(ClientMain.serverIpAddress, ClientMain.serverPortChat2))) {

                ObjectOutputStream sendChannel2 = new ObjectOutputStream(this.chatChannel2.socket().getOutputStream());
                oisChannel2 = new ObjectInputStream(this.chatChannel2.socket().getInputStream());

                PingAndErrorMessage messageReceived;
                PingAndErrorMessage nameMessage;

                messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(oisChannel2);

                nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);
                ConnectionManager.sendObject(nameMessage, sendChannel2);

                fsmContext.setChatOis(oisChannel2);

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
