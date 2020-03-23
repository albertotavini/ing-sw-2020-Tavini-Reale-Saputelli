package it.polimi.ingsw.model;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.zip.DataFormatException;


public class BoardTest {

    @Test
    public void drawBoard() {
        Board b = Board.instance();
        b.drawBoard();
    }

    @Test
    public void BoardConstructionTest() {
        Board board = Board.instance();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                assertNotNull( board );
                assertNotNull( board.getBox(i,j) );
                assertTrue( board.inBoundaries(i,j) );
            }
        }
    }

    @Test
    public void placeWorkerTest() throws DataFormatException {
        Board board = Board.instance();
        Player playerGiulio = new Player("Giulio", 22, 12, 1990);
        Worker worker = new Worker(playerGiulio, "Red", "A");

        //getting a random box to put the worker
        Random random = new Random();
        int row = random.nextInt(4);
        int column = random.nextInt(4);
        board.placeWorker(worker, row, column);

        //where the player put the worker
        Box placingBox = board.getBox(row, column);

        //is the occupier of the placing box the same worker we defined?
        assertEquals(placingBox.getOccupier(), worker);

        /*//c'è bisogno o no di un metodo che testi esplicitamente che il getOccupier della classe Box non sia nullo o è solo ridondante?
        assertNotNull( board.getBox(row, column).getOccupier() ); */
    }

    @Test
    public void increaseLevelTest() {
        //it depends on our way to manage pieces, I'm just creating the method
    }

    /*@Test
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

    }*/

}