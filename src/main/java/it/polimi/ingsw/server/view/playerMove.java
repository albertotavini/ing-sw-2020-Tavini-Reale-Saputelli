package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Player;

public class playerMove {

    private int row;
    private int column;
    private Player player;

    public playerMove(int row, int column, Player player) {
        this.row = row;
        this.column = column;
        this.player = player;
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
}
