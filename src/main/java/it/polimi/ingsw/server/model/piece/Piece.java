package it.polimi.ingsw.server.model.piece;

/**
 * This class represents the pieces which compose the towers on the game board.
 * It has just one attribute, which represents the level of the tower.
 * There are two types of pieces: blocks or domes.
 */
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

