package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.TRS_TP.InGameConnection;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ObserverVC;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

public class RemoteView extends DistributedView {

    private InGameConnection inGameConnection;

    private class MessageReceiver implements ObserverVC<PlayerMove> {
        //stringa o playermove?
        @Override
        public void update (PlayerMove playerMove) {
            //System.out.println("Received : " + message);
            if(!handleInput(playerMove)) {
                //su connection va fatto un asyncSend di errore
            }
        }
    }


    public RemoteView(Player player, InGameConnection inGameConnection) {
        //dovremmo passare anche una lista degli avversari
        super(player);
        this.inGameConnection = inGameConnection;
        inGameConnection.addObserver(new MessageReceiver());
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
