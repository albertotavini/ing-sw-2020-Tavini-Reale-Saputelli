package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.transmissionprotocol.InGameConnection;
import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.bothsides.utils.LogPrinter;
import it.polimi.ingsw.bothsides.onlinemessages.InGameServerMessage;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

/**
 * this class is ment to perform the view's role while dialoguing with a client
 * it hosts the inGameConncection, the object that receives playermoves and sends InGameServerMessages via TCP
 *
 *
 */
public class RemoteView extends View {

    private InGameConnection inGameConnection;

    /**
     * this class observes InGameConnection and utilises the playermove it signals
     * to controller through parent class' handleInput method
     * it also prints on a log all the playermoves it receives
     */
    private class MessageReceiver implements Observer<PlayerMove> {
        @Override
        public void update (PlayerMove playerMove, Object obj) {
            LogPrinter.printOnLog("\nReceived : "+ playerMove.toString() +" da "+getPlayer().getName());
            handleInput(playerMove);
        }
    }


    /**
     * it calls super() and then sets the message receiver as observer of InGameConnection
     * @param player whose playermoves this will be handling
     * @param inGameConnection corresponding to player's serverFSM
     */
    public RemoteView(Player player, InGameConnection inGameConnection) {
        //dovremmo passare anche una lista degli avversari
        super(player);
        this.inGameConnection = inGameConnection;
        inGameConnection.addObserver(new MessageReceiver());
        //ci si vuole un async send del nome degli avversari (?)
        //meglio stampare i giocatori a inizio partita
    }

    /**
     *utilises inGameConnection to send to the client the latest info on the game
     *
     * @param boardPhotography status of the board
     * @param modelMessage    the modelmessage
     */
    @Override
    protected void showBoard(BoardPhotography boardPhotography, ModelMessage modelMessage) {
        inGameConnection.sendInGameServerMessage(new InGameServerMessage(boardPhotography, modelMessage));

    }

    /**
     * it receives the info on the game from model and sends it to client though showBoardMethod
     * @param boardPhotography status of the board
     * @param obj              the modelmessage
     */
    @Override
    public void update(BoardPhotography boardPhotography, Object obj ) {
        if (obj instanceof ModelMessage){
            showBoard(boardPhotography, (ModelMessage) obj);

            currentModelMessage = (ModelMessage) obj;
        }


    }
}
