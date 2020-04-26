package it.polimi.ingsw.server.model;

import java.io.Serializable;

public class BoardPhotography implements Serializable {









}



class BoxPhotography implements Serializable {

    private final int row;
    private final int column;
    private final int level;
    private final boolean domed;

    //workers parameter
    private final boolean occupied;
    private final Color color;



    public BoxPhotography(int row, int column, int level, boolean domed, boolean occupied, Color color) {
        this.row = row;
        this.column = column;
        this.level = level;
        this.domed = domed;
        this.occupied = occupied;
        this.color = color;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getLevel() {
        return level;
    }

    public boolean isDomed() {
        return domed;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Color getColor() {
        return color;
    }

}
