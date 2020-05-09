package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.TRS_TP.InGameConnection;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.BoardPhotography;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.server.utils.ColorAnsi;
import it.polimi.ingsw.server.utils.LogPrinter;
import it.polimi.ingsw.server.view.PlayerMove.InGameServerMessage;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

public class RemoteView extends DistributedView {

    private InGameConnection inGameConnection;

    private class MessageReceiver implements Observer<PlayerMove> {
        @Override
        public void update (PlayerMove playerMove, Object obj) {
            
            LogPrinter.printOnLog("\nReceived : "+ playerMove.toString() +" da "+getPlayer().getName());
            //il controllo potrebbe essere superfluo
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
        //meglio stampare i giocatori a inizio partita
    }

    @Override
    protected void showBoard(BoardPhotography boardPhotography, ModelMessage modelMessage) {
        inGameConnection.sendModelMessage(new InGameServerMessage(boardPhotography, modelMessage));

    }

    @Override
    public void update(BoardPhotography boardPhotography, Object obj ) {
        if (obj instanceof ModelMessage){
            showBoard(boardPhotography, (ModelMessage) obj);

            currentModelMessage = (ModelMessage) obj;
        }


    }
}
