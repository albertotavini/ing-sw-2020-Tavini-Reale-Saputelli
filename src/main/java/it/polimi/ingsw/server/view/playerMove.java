package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Player;

import javax.security.auth.callback.ConfirmationCallback;

public class playerMove {

    private int row;
    private int column;
    private Player player;
    private String genericMessage;
    private ConfirmationEnum confirm;

    public playerMove(int row, int column, Player player) {
        this.row = row;
        this.column = column;
        this.player = player;
        this.genericMessage = "nothing interesting here";
        this.confirm = ConfirmationEnum.NotDef;
    }

    public playerMove(String string, Player player) {
        this.player = player;
        this.column = 7;
        this.row = 7;
        this.genericMessage = string;
        this.confirm = ConfirmationEnum.NotDef;
    }

    public playerMove (ConfirmationEnum confirmation, Player player) {
        this.player = player;
        this.column = 7;
        this.row = 7;
        this.genericMessage = "nothing interesting here";
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



}
