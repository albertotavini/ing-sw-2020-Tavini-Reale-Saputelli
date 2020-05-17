package it.polimi.ingsw.bothsides.onlinemessages.playermove;
import it.polimi.ingsw.server.model.Player;

import java.io.Serializable;

public class PlayerMove implements Serializable {


    //message tags
    private PlayerMoveType type;
    private ConfirmationEnum confirm;

    private int row;
    private int column;
    private Player player;
    private String genericMessage;


    public PlayerMove(int row, int column, Player player) {
        this.type = PlayerMoveType.Coord;
        this.row = row;
        this.column = column;
        this.player = player;
        this.genericMessage = null;
        this.confirm = ConfirmationEnum.NotDef;
    }

    public PlayerMove(String string, Player player) {
        this.type = PlayerMoveType.GodName;
        this.player = player;
        this.column = 0;
        this.row = 0;
        this.genericMessage = string;
        this.confirm = ConfirmationEnum.NotDef;
    }

    public PlayerMove(ConfirmationEnum confirmation, Player player) {
        this.type = PlayerMoveType.Confirm;
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

    public void setGenericMessage(String genericMessage) {
        this.genericMessage = genericMessage;
    }

    public PlayerMoveType getType() {
        return type;
    }

    @Override
    public String toString() {
       if (type == PlayerMoveType.GodName) return "tipo Godname, contenuto "+getGenericMessage();
       else if (type == PlayerMoveType.Confirm) return "tipo Confirm, contenuto "+getConfirmation();
       else return "tipo coord, contenuto "+getRow()+","+getColumn();
    }
}