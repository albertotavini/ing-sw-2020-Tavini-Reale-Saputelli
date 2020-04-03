package it.polimi.ingsw.server;


import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.lang.Math;
import java.util.zip.DataFormatException;


public class BoardTest {

    //I use this method to generate random numbers to test boxes inside the board
    public int generateInsideDimension(){
        Random random = new Random();
        return random.nextInt(4);
    }

    //I use this method to generate random numbers to test boxes outside the board
    public int generateOutsideDimension() {
        //60 could be a good range
        int max = 30;
        int min = -30;
        int range = max - min;

        int returningValue;
        //I strongly want the box outside the board
        do {
            returningValue = (int) (Math.random() * range) + min;
        } while (returningValue >= 0 && returningValue <= 4);
            return returningValue;
        }

    //here tests start
    @Test
    public void drawBoardTest() {
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

        Player player1 = new Player("Giulio", 22, 12, 1990);
        Worker workerA = new Worker(player1, "Green", "A");

        Player playerB = new Player("Marco", 22, 12, 1985);
        Worker workerB = new Worker(playerB, "Red", "A");

        Player playerC = new Player("Franco", 22, 12, 1980);
        Worker workerC = new Worker(player1, "Yellow", "A");

        //getting random boxes to put the workers
        int rowA = generateInsideDimension();
        int columnA = generateInsideDimension();
        board.placeWorker(workerA, rowA, columnA);
        int rowB = generateInsideDimension();
        int columnB = generateInsideDimension();
        board.placeWorker(workerB, rowB, columnB);
        int rowC = generateInsideDimension();
        int columnC = generateInsideDimension();
        board.placeWorker(workerC, rowC, columnC);

        //where players put the workers
        Box placingBoxA = board.getBox(rowA, columnA);
        Box placingBoxB = board.getBox(rowB, columnB);
        Box placingBoxC = board.getBox(rowC, columnC);

        //are the occupiers of the placing boxes the same workers we defined?
        assertEquals(placingBoxA.getOccupier(), workerA);
        assertEquals(placingBoxB.getOccupier(), workerB);
        assertEquals(placingBoxC.getOccupier(), workerC);

        //the only boxes which are not null are the placingBoxes
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                if (i==rowA && j==columnA || i==rowB && j==columnB || i==rowC && j==columnC)
                    assertNotNull( board.getBox(i, j).getOccupier() );
                else
                    assertNull( board.getBox(i, j).getOccupier() );
            }
        }
    }


    @Test
    public void inBoundariesTest (){
        Board board = Board.instance();

        //getting a random box in the board
        int insideRow = generateInsideDimension();
        int insideColumn = generateInsideDimension();

        //testing board's boxes
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                assertTrue(board.inBoundaries(insideRow, insideColumn));
            }
        }

        //testing 10 boxes outside the board
        for (int k=0; k<10; k++) {
            int outsideRow = generateOutsideDimension();
            int outsideColumn = generateOutsideDimension();

            assertFalse(board.inBoundaries(outsideRow, outsideColumn));
        }
    }

    @Test
    public void BoxIsNearTest () {
        Board board = Board.instance();

        //getting a random box in the board
        int insideRow = generateInsideDimension();
        int insideColumn = generateInsideDimension();


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                if (Math.abs(insideRow - i) == 1 || insideRow - i == 0)
                    if (Math.abs(insideColumn - j) == 1 || insideColumn - j == 0)
                        if (insideRow == i && insideColumn == j)
                            assertFalse(board.boxIsNear(insideRow, insideColumn, i, j));
                        else
                            assertTrue(board.boxIsNear(insideRow, insideColumn, i, j));
            }
        }

        //testing 10 boxes outside the board
        for (int k=0; k<10; k++) {
            int outsideRow = generateOutsideDimension();
            int outsideColumn = generateOutsideDimension();

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    assertFalse(board.boxIsNear(outsideRow, outsideColumn, i, j));
                }
            }
        }
    }

    @Test
    public void isNearbySpaceFreeTest () throws DataFormatException {
        Board board = Board.instance();

        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, "G", "A" );
        Worker workerB1 = new Worker( player1, "G", "B" );

        Player player2 = new Player("Luca", 3, 4, 2001);
        Worker workerA2 = new Worker( player2, "R", "A" );
        Worker workerB2 = new Worker( player2, "R", "B" );

        board.placeWorker(workerA1, 0, 2);
        board.placeWorker(workerB1, 0, 3);

        board.increaseLevel(0, 1);
        board.increaseLevel(0, 1);
        // (0,1) has level 2
        board.increaseLevel(1, 2);
        board.increaseLevel(1, 2);
        // (1,2) has level 2
        board.increaseLevel(1, 1);
        board.increaseLevel(1, 1);
        // (1,1) has level 2
        board.increaseLevel(1, 3);
        // (1,3) has level 1

        board.drawBoard();

        assertTrue(board.isNearbySpaceFree(0,2)); //rn (0,2) is free to move
        assertTrue(board.isNearbySpaceFree(0, 3)); //rn (0,3) is free to move
        board.increaseLevel(1, 3);
        //(1,3) has level 2

        board.drawBoard();

        //but now (0,2) is blocked!
        assertFalse(board.isNearbySpaceFree(0,2));

        board.placeWorker(workerB2,4,4);

        board.increaseLevel(3, 3);
        board.increaseLevel(3, 3);
        // (3,3) has level 2
        board.increaseLevel(3, 4);
        board.increaseLevel(3, 4);
        // (3,4) has level 2
        board.increaseLevel(4, 3);
        board.increaseLevel(4, 3);
        // (4,3) has level 2

        board.drawBoard();

        //check the corner worker
        assertFalse(board.isNearbySpaceFree(4,4));
    }

    @Test
    public void increaseLevelTest() {
        Board board = Board.instance();

        board.increaseLevel(0, 2);
        board.increaseLevel(0, 2);
        board.increaseLevel(0, 2);
        board.increaseLevel(0, 2);
        // (0,2) has level 4
        board.increaseLevel(0, 2);
        //the tower is complete

        board.drawBoard();

        board.increaseLevel(1, 3); //ok
        board.increaseLevel(2, 4); //ok
        board.increaseLevel(0, 5); //outside the board
        board.increaseLevel(0, 6); //outside the board
        board.drawBoard();
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