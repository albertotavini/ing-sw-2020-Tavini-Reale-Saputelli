package it.polimi.ingsw.server.model.piece;

/**
 * This class represents one of the two types of pieces.
 * Blocks are the type of pieces used to compose towers on all the levels except on the highest one.
 */
public class Block extends Piece {
    public Block(int level) {
        super(level);
    }
}
