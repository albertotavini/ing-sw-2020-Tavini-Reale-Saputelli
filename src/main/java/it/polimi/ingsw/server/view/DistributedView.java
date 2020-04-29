package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.observers.ObservableVC;
import it.polimi.ingsw.server.observers.ObserverMV;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

public abstract class DistributedView extends ObservableVC <PlayerMove> implements ObserverMV<Board> {
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

    protected abstract void showBoard(Board board, String string);

    @Override
    public void update (Board board, ModelMessage modelMessage){
        showBoard(board, modelMessage.getMessage());
    }

    boolean handleInput(PlayerMove playerMove) {
            try {
                playerMove.setPlayer(player);
                notify(playerMove);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
    }


}
