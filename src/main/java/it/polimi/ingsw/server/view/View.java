package it.polimi.ingsw.server.view;

import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

/**
 * parent class for remoteView
 * it will be updated by model and will update controller
 */
public abstract class View extends Observable <PlayerMove> implements Observer<BoardPhotography> {
    private Player player;
    ModelMessage currentModelMessage;

    /**
     * constructor simply sets the player
     * @param player associated
     */
    View(Player player) {
        this.player = player;
    }

    Player getPlayer() {
        return player;
    }

    /**
     * method to send updates to client, reimplemented in boardview
     *
     * @param boardPhotography status of the board
     * @param modelMessage the modelmessage
     */
    protected abstract void showBoard(BoardPhotography boardPhotography, ModelMessage modelMessage);

    /**
     * called when model notifies changes, reimplemented in remoteview
     * @param boardPhotography status of the board
     * @param obj the modelmessage
     */
    @Override
    public void update (BoardPhotography boardPhotography, Object obj){
        if (obj instanceof ModelMessage) {
            showBoard(boardPhotography, (ModelMessage) obj);
        }
    }

    /**
     * this method will add the Player object to the playermove
     * this is done on server so that the client cannot interfere with the operation
     * and then will notify controller of the move received
     *
     * @param playerMove received by corresponding client
     *
     */
    void handleInput(PlayerMove playerMove) {
                playerMove.setPlayer(player);
                notify(playerMove, null);

    }


}
