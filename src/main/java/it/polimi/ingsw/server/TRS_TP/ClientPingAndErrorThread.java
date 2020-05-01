package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.utils.ColorAnsi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Calendar;

public class ClientPingAndErrorThread implements Runnable{

    private final SocketChannel errorChannel;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;


    private boolean isActive = true;


    public ClientPingAndErrorThread(SocketChannel errorChannel){

        this.errorChannel = errorChannel;


    }
    public void setInactive(){
        this.isActive = false;
    }


    @Override
    public void run() {

        try {

            if (errorChannel.connect(new InetSocketAddress("localhost", 6701))) {

                this.oos = new ObjectOutputStream(this.errorChannel.socket().getOutputStream());
                this.ois = new ObjectInputStream(this.errorChannel.socket().getInputStream());

                PingAndErrorMessage messageReceived;

                do {

                    messageReceived = (PingAndErrorMessage) ConnectionManager.receiveObject(ois);


                    switch (messageReceived.typeOfMessage) {

                        case PingAndErrorMessagePing :
                            ClientViewAdapter.printMessage(ColorAnsi.RED +"Ping  " +ColorAnsi.YELLOW +Calendar.getInstance().getTime().toString() +ColorAnsi.RESET);
                            break;

                        case WaitingInLobbyDisconnected :
                            //da vedere
                            break;

                    }

                } while (isActive);

            }


        }catch(ConnectException ex){

            ClientViewAdapter.printMessage("Non sono riuscito a connettermi al server per ping ed errori");

        } catch (Exception e) {

            ClientViewAdapter.printMessage(ColorAnsi.RED +"\n\nSomething wrong happened, closing the application" +ColorAnsi.RESET);
            System.exit(-1);

        }
    }
}
