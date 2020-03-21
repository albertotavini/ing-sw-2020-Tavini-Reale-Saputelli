package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.zip.DataFormatException;

class BoardTest {

    @Test
    void drawBoard() {
        Board b = Board.instance();
        b.drawBoard();
    }

    @Test
    void placeWorker() {
        Board b = Board.instance();
        Player p1 = null;
        try { p1 = new Player("Marco", 2, 2,2000);}
        catch (DataFormatException e) {e.printStackTrace();}
        Worker w1 = new Worker( p1, "Y", "A" );
        Worker w2 = new Worker( p1, "Y", "B" );
        b.placeWorker(w1, 1, 3);
        b.placeWorker(w2, 2, 0);

        b.drawBoard();
    }

    @Test
    void increaseLevel() {
        Board b = Board.instance();
        b.increaseLevel(0, 0);
        b.increaseLevel(0, 0);
        b.increaseLevel(2, 3);
        b.drawBoard();

    }

}