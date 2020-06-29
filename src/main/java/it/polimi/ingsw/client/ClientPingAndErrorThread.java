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

public class ClientPingAndErrorThread implements Runnable{

    private final SocketChannel errorChannel;

    private final String nameClient;


    private boolean isActive = false;
    private boolean canClose = false;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;


    public ClientPingAndErrorThread(SocketChannel errorChannel, String nameClient){

        this.errorChannel = errorChannel;
        this.nameClient = nameClient;
    }

    public void closePingConnection() throws IOException {

        if( isActive ) {

           PingAndErrorMessage closingMessage = new PingAndErrorMessage(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_CLOSING, "CLOSING CONNECTION");
           ConnectionManager.sendObject(closingMessage, oos);

       }
    }


    @Override
    public void run() {

        isActive = true;

        try {

            if (errorChannel.connect(new InetSocketAddress(ClientMain.serverIpAddress, ClientMain.serverPortError))) {

                oos = new ObjectOutputStream(this.errorChannel.socket().getOutputStream());
                ois = new ObjectInputStream(this.errorChannel.socket().getInputStream());

                PingAndErrorMessage messageReceived;
                PingAndErrorMessage nameMessage;

                do {

                    messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(ois);
                    
                    switch (messageReceived.typeOfSetupMessage) {

                        case PING_AND_ERROR_MESSAGE_PING:

                            nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);
                            ConnectionManager.sendObject(nameMessage, oos);

                            break;

                        case WAITING_IN_LOBBY_DISCONNECTED:
                            LogPrinter.printOnLog(Global.LOBBYDISCONNECTED);
                            isActive = false;
                            canClose = true;
                            break;



                        case PING_AND_ERROR_MESSAGE_CLOSING_ACK:
                            canClose = true;
                            isActive = false;
                            break;




                        default:
                            break;

                    }





                } while (isActive || !canClose);

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
