package it.polimi.ingsw.server.model.piece;

public abstract class Piece {

    private int level;

    public int getLevel(){
        return this.level;
    }

    public Piece (int level){
        this.level = level;
    }

    public void setLevel(int level){
        this.level = level;
    }

}

