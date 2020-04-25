package it.polimi.ingsw.server.view.playerMove;

import it.polimi.ingsw.server.model.Player;

public class playerMove {

    private int row;
    private int column;
    private Player player;
    private String genericMessage;
    private ConfirmationEnum confirm;
    private playerMoveType type;

    public playerMove(int row, int column, Player player) {
        this.type = playerMoveType.Coord;
        this.row = row;
        this.column = column;
        this.player = player;
        this.genericMessage = null;
        this.confirm = ConfirmationEnum.NotDef;
    }

    public playerMove(String string, Player player) {
        this.type = playerMoveType.GodName;
        this.player = player;
        this.column = 0;
        this.row = 0;
        this.genericMessage = string;
        this.confirm = ConfirmationEnum.NotDef;
    }

    public playerMove (ConfirmationEnum confirmation, Player player) {
        this.type = playerMoveType.Confirm;
        this.player = player;
        this.genericMessage = null;
        this.confirm = confirmation;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Player getPlayer() {
        return player;
    }

    public String getGenericMessage() {
        return genericMessage;
    }

    public ConfirmationEnum getConfirmation () {
        return confirm;
    }

    public void setGenericMessage(String genericMessage) {
        this.genericMessage = genericMessage;
    }

    public playerMoveType getType() {
        return type;
    }
}
