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
        while (true) {
            try {
                notify(playerMove);
                /*if (currentModelMessage.getModelMessageType() == ModelMessageType.NeedsCoordinates) {
                    if (s.length() == 3 && s.charAt(1) == ',') {
                        String[] inputs = s.split(",");

                        int row, column;
                        row = Integer.parseInt(inputs[0]);
                        column = Integer.parseInt(inputs[1]);
                        PlayerMove message = new PlayerMove(row, column, this.player);

                        notify(message);
                    }
                } else if (currentModelMessage.getModelMessageType() == ModelMessageType.NeedsGodName) {//if it is needed to send a confirmation to the will of activating god's powers, or select god's
                    PlayerMove message = new PlayerMove(s, this.player);
                    notify(message);
                } else if (currentModelMessage.getModelMessageType() == ModelMessageType.NeedsConfirmation) {
                    if (s.toUpperCase().equals("YES")) {
                        confirmation = ConfirmationEnum.Yes;
                        PlayerMove message = new PlayerMove(confirmation, this.player);
                        notify(message);
                    } else if (s.toUpperCase().equals("NO")) {
                        confirmation = ConfirmationEnum.No;
                        PlayerMove message = new PlayerMove(confirmation, this.player);
                        notify(message);
                    }
                } else if (currentModelMessage.getModelMessageType() == ModelMessageType.GameOver) {
                    done = true;
                }

                return true;
                */
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }


}
