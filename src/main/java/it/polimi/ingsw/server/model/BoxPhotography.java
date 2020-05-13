package it.polimi.ingsw.server.model;

import java.io.Serializable;

public class BoxPhotography implements Serializable {

    //class is tested in BoardTest

    private final int row;
    private final int column;
    private final int level;
    private final boolean domed;

    //workers parameter
    private final boolean occupied;
    private final Color color;

    BoxPhotography(int row, int column){
        this.row = row;
        this.column = column;
        this.level = 0;
        this.domed = false;
        this.occupied = false;
        this.color = null;
    }



    BoxPhotography(int row, int column, int level, boolean domed, boolean occupied, Color color) {
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

    boolean isDomed() {
        return domed;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        if ((!isOccupied()) && (getLevel()==0)) {
            return "-- --";
        }
        else if ((isOccupied()) && (getLevel()==0)) {
            return getColor().abbrev()+"- --";
        }
        else if ((!isOccupied()) && (getLevel()>0) && (!isDomed())) {
            return "-- "+getLevel()+"-";
        }
        else if ((!isOccupied()) && (getLevel()>0) && (isDomed())) {
            return "-- "+getLevel()+"*";
        }
        else if (isOccupied() && getLevel()>0 && (isDomed())) {
            return getColor().abbrev()+"- "+getLevel()+"*";
        }
        else if (isOccupied() && getLevel()>0 && (!isDomed())) {
            return getColor().abbrev()+"- "+getLevel()+"-";
        }
        else return "err";
    }

    @Override
    public boolean equals (Object object) {
        if (! (object instanceof BoxPhotography) ) {return false;}
        if (this.getRow() != ((BoxPhotography) object).getRow())  {return false;}
        if (this.getColumn() != ((BoxPhotography) object).getColumn()) {return false;}
        if (this.getLevel() != ((BoxPhotography) object).getLevel()) {return false;}
        if(this.isOccupied() != ((BoxPhotography) object).isOccupied()) {return false;}
        if (this.getColor() != ((BoxPhotography) object).getColor()) {return false;}
        return this.isDomed() == ((BoxPhotography) object).isDomed();
    }
}
