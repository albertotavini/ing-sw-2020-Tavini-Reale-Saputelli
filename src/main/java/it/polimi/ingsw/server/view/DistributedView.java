package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.BoardPhotography;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

public abstract class DistributedView extends Observable <PlayerMove> implements Observer<BoardPhotography> {
    private Player player;
    private ConfirmationEnum confirmation;
    private boolean done = false;
    ModelMessage currentModelMessage;

    DistributedView(Player player) {
        this.player = player;
    }

    protected Player getPlayer() {
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
