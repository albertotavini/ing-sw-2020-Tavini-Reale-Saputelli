package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.observers.ObservableVC;
import it.polimi.ingsw.server.observers.ObserverVC;

public class RemoteView extends DistributedView {


   // private Connection connection;



    private class MessageReceiver implements ObserverVC<String> {
        //stringa o playermove?
        @Override
        public void update (String message) {
            System.out.println("Received : " + message);
            if(!handleInput(message)) {
                //su connection va fatto un asyncSend di errore
            }
        }
    }



    public RemoteView(Player player/*, Connection c*/) {
        //dovremmo passare anche una lista degli avversari
        super(player);
        //this.connection = c;
        //c.addObserver(new MessageReceiver());
        //ci si vuole un async send del nome degli avversari (?)
    }

    @Override
    protected void showBoard(Board board, String string) {
        //manda la board tramite connection

    }

    @Override
    public void update(Board board, ModelMessage modelMessage) {
        showBoard(board, modelMessage.getMessage());

        //qua va spedita la board o la sua rappresentazione al clien
        //e stampato anche il modelMessage.getMessage()

        currentModelMessage = modelMessage;

    }
}
