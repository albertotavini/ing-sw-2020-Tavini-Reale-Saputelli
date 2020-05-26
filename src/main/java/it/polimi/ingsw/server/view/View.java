package it.polimi.ingsw.server.view;

import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

public abstract class View extends Observable <PlayerMove> implements Observer<BoardPhotography> {
    private Player player;
    ModelMessage currentModelMessage;

    View(Player player) {
        this.player = player;
    }

    Player getPlayer() {
        return player;
    }

    protected abstract void showBoard(BoardPhotography boardPhotography, ModelMessage modelMessage);

    @Override
    public void update (BoardPhotography boardPhotography, Object obj){
        if (obj instanceof ModelMessage) {
            showBoard(boardPhotography, (ModelMessage) obj);
        }
    }

    boolean handleInput(PlayerMove playerMove) {
            try {
                playerMove.setPlayer(player);
                notify(playerMove, null);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
    }


}
