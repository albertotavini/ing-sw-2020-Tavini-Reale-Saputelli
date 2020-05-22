package it.polimi.ingsw.bothsides.onlinemessages.playermove;
import it.polimi.ingsw.server.model.Player;

import java.io.Serializable;

public class PlayerMove implements Serializable {


    //message tags
    private final PlayerMoveType type;

    private final ConfirmationEnum confirm;
    private final int row;
    private final int column;
    private final String genericMessage;
    //this element is not final because it is not set on the client but on the corresponding remote view on the server
    private Player player;

    public PlayerMove(int row, int column, Player player) {
        this.type = PlayerMoveType.COORD;
        this.row = row;
        this.column = column;
        this.player = player;
        this.genericMessage = null;
        this.confirm = ConfirmationEnum.NOTDEF;
    }

    public PlayerMove(String string, Player player) {
        this.type = PlayerMoveType.GOD_NAME;
        this.player = player;
        this.column = 0;
        this.row = 0;
        this.genericMessage = string;
        this.confirm = ConfirmationEnum.NOTDEF;
    }

    public PlayerMove(ConfirmationEnum confirmation, Player player) {
        this.type = PlayerMoveType.CONFIRM;
        this.player = player;
        this.column = 0;
        this.row = 0;
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getGenericMessage() {
        return genericMessage;
    }

    public ConfirmationEnum getConfirmation () {
        return confirm;
    }


    public PlayerMoveType getType() {
        return type;
    }

    @Override
    public String toString() {
       if (type == PlayerMoveType.GOD_NAME) return "tipo Godname, contenuto "+getGenericMessage();
       else if (type == PlayerMoveType.CONFIRM) return "tipo Confirm, contenuto "+getConfirmation();
       else return "tipo coord, contenuto "+getRow()+","+getColumn();
    }
}
