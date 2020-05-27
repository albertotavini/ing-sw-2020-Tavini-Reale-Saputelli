package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.ConnectionManager;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.PingAndErrorMessage;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.TypeOfSetupMessage;
import it.polimi.ingsw.bothsides.utils.ColorAnsi;
import it.polimi.ingsw.bothsides.utils.LogPrinter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientPingAndErrorThread implements Runnable{

    private final SocketChannel errorChannel;

    private final String nameClient;


    private boolean isActive = true;


    public ClientPingAndErrorThread(SocketChannel errorChannel, String nameClient){

        this.errorChannel = errorChannel;
        this.nameClient = nameClient;
    }
    public void setInactive(){
        this.isActive = false;
    }


    @Override
    public void run() {

        try {

            if (errorChannel.connect(new InetSocketAddress("localhost", 6701))) {

                ObjectOutputStream oos = new ObjectOutputStream(this.errorChannel.socket().getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(this.errorChannel.socket().getInputStream());

                PingAndErrorMessage messageReceived;

                do {

                    messageReceived = (PingAndErrorMessage) ConnectionManager.receiveStandardObject(ois);


                    switch (messageReceived.typeOfSetupMessage) {

                        case PING_AND_ERROR_MESSAGE_PING:
                            break;

                        case WAITING_IN_LOBBY_DISCONNECTED:
                            LogPrinter.printOnLog("----Lobby Disconnected----");
                            break;

                        default:
                            break;

                    }


                    PingAndErrorMessage nameMessage = PingAndErrorMessage.newPingAndErrorMessageStandard(TypeOfSetupMessage.PING_AND_ERROR_MESSAGE_PING, nameClient);

                    ConnectionManager.sendObject(nameMessage, oos);


                } while (isActive);

            }


        }catch(ConnectException ex){

            ClientViewAdapter.printMenuMessage("Non sono riuscito a connettermi al server per ping ed errori");

        } catch (Exception e) {

            ClientViewAdapter.printMenuMessage(ColorAnsi.RED +"\n\nSomething wrong happened, closing the application" +ColorAnsi.RESET);
            System.exit(-1);

        }
    }
}
