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

/**
 * this class is the thread that is meant to deal with the chat of the GUI
 */
public class ClientChatThread implements Runnable{

    private SocketChannel chatChannel1;
    private SocketChannel chatChannel2;
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


    /**
     * the run opens the channels for comunication and activates the connections
     * sets oos and ois
     */
    @Override
    public void run() {



        try {

            if(chatChannel1.isConnected()){


                chatChannel1.close();

                chatChannel1 = SocketChannel.open();

                chatChannel1.connect(new InetSocketAddress(ClientMain.serverIpAddress, ClientMain.serverPortChat1));

                oosChannel1 = new ObjectOutputStream(this.chatChannel1.socket().getOutputStream());
                ObjectInputStream receiveChannel1 = new ObjectInputStream(this.chatChannel1.socket().getInputStream());

                PingAndErrorMessage messageReceived;
                PingAndErrorMessage nameMessage;

                messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(receiveChannel1);

                nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);
                ConnectionManager.sendObject(nameMessage, oosChannel1);

                fsmContext.setChatOos(oosChannel1);

            }

            else{

                if(!chatChannel1.isOpen()) chatChannel1 = SocketChannel.open();

                chatChannel1.connect(new InetSocketAddress(ClientMain.serverIpAddress, ClientMain.serverPortChat1));

                oosChannel1 = new ObjectOutputStream(this.chatChannel1.socket().getOutputStream());
                ObjectInputStream receiveChannel1 = new ObjectInputStream(this.chatChannel1.socket().getInputStream());

                PingAndErrorMessage messageReceived;
                PingAndErrorMessage nameMessage;

                messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(receiveChannel1);

                nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);
                ConnectionManager.sendObject(nameMessage, oosChannel1);

                fsmContext.setChatOos(oosChannel1);




            }


            if(chatChannel2.isConnected()){


                chatChannel2.close();

                chatChannel2 = SocketChannel.open();

                chatChannel2.connect(new InetSocketAddress(ClientMain.serverIpAddress, ClientMain.serverPortChat2));




                ObjectOutputStream sendChannel2 = new ObjectOutputStream(this.chatChannel2.socket().getOutputStream());
                oisChannel2 = new ObjectInputStream(this.chatChannel2.socket().getInputStream());

                PingAndErrorMessage messageReceived;
                PingAndErrorMessage nameMessage;

                messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(oisChannel2);

                nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);
                ConnectionManager.sendObject(nameMessage, sendChannel2);

                fsmContext.setChatOis(oisChannel2);


            }


            else {

                if(!chatChannel2.isOpen()) chatChannel2 = SocketChannel.open();


                chatChannel2.connect(new InetSocketAddress(ClientMain.serverIpAddress, ClientMain.serverPortChat2));

                ObjectOutputStream sendChannel2 = new ObjectOutputStream(this.chatChannel2.socket().getOutputStream());
                oisChannel2 = new ObjectInputStream(this.chatChannel2.socket().getInputStream());

                PingAndErrorMessage messageReceived;
                PingAndErrorMessage nameMessage;

                messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(oisChannel2);

                nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);
                ConnectionManager.sendObject(nameMessage, sendChannel2);

                fsmContext.setChatOis(oisChannel2);

            }



        } catch (Exception e) {

            Thread.currentThread().interrupt();
            //ClientViewAdapter.printMenuMessage(Global.SOMETHINGWRONGHAPPENEDCLOSINGTHEAPPLICATION)

        }
    }

}
