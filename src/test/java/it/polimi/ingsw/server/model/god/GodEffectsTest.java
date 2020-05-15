package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.utils.Global;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class GodEffectsTest {

    //support methods to build playermoves, they're built the same way in the view
    private static PlayerMove coord(int row, int column) throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        PlayerMove playermove = new PlayerMove(row, column, p1);
        playermove.setGenericMessage("nothing interesting here");
        return playermove;
    }
    private static PlayerMove confirmation(ConfirmationEnum confirmation) throws DataFormatException {
        //the playermoves are done like this becayse it's always player 1 to test godpowers
        Player p1 = new Player("Peppino", 1,12, 2000);
        return new PlayerMove(confirmation, p1);
    }


    //support method to clear the board
   private void clearBoardForFutureTests (Board board){
        for (int i=0; i<5; i++) {
            for (int j= 0; j < 5; j++) {

                //setting every box's level at 0
                while (board.getBox(i, j).getTower().size() != 0)
                    board.getBox(i, j).decreaseLevel();

                //removing every player
                if (board.getBox(i, j).getOccupier() != null)
                    board.getBox(i, j).setOccupier(null);

            }
        }
    }


    @Test
    void minotaurEffectOrizontalTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();

        //here i test the two possible usages of the effect on the same row successfully

        t1.placeWorker(board, coord(2, 2), "B");
        t2.placeWorker(board, coord(2, 3), "A");
        t1.selectWorker(board, coord(2, 2));
        t1.move(board, coord(2, 3));
        assertEquals(Color.GREEN, board.getBox(2, 3).getOccupier().getColour());
        assertEquals( Color.RED, board.getBox(2, 4).getOccupier().getColour());
        clearBoardForFutureTests(board);

        t1.placeWorker(board, coord(2, 3), "B");
        t2.placeWorker(board, coord(2, 2), "A");
        t1.selectWorker(board, coord(2, 3));
        t1.move(board, coord(2, 2));
        assertEquals(Color.GREEN, board.getBox(2, 2).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(2, 1).getOccupier().getColour());
        clearBoardForFutureTests(board);
    }
    @Test
    void minotaurEffectVerticalTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        //here i test the two possible usages of the effect on the same column successfully

        t1.placeWorker(board, coord(1, 3), "B");
        t2.placeWorker(board, coord(2, 3), "A");
        t1.selectWorker(board, coord(1, 3));
        t1.move(board, coord(2, 3));
        assertEquals(Color.GREEN,board.getBox(2, 3).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(3, 3).getOccupier().getColour());
        clearBoardForFutureTests(board);

        t1.placeWorker(board, coord(2, 3), "B");
        t2.placeWorker(board, coord(1, 3), "A");
        t1.selectWorker(board, coord(2, 3));
        t1.move(board, coord(1, 3));
        assertEquals(Color.GREEN, board.getBox(1, 3).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(0, 3).getOccupier().getColour());
        clearBoardForFutureTests(board);

    }
    @Test
    void minotaurEffectDiagonalTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();


        //cases of diagonal successful usage

        t1.placeWorker(board, coord(3, 3), "B");
        t2.placeWorker(board, coord(2, 2), "A");
        t1.selectWorker(board, coord(3, 3));
        t1.move(board, coord(2, 2));
        assertEquals(Color.GREEN, board.getBox(2, 2).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(1, 1).getOccupier().getColour());
        clearBoardForFutureTests(board);

        t1.placeWorker(board, coord(2, 2), "B");
        t2.placeWorker(board, coord(3, 3), "A");
        t1.selectWorker(board, coord(2, 2));
        t1.move(board, coord(3, 3));
        assertEquals(Color.GREEN,board.getBox(3, 3).getOccupier().getColour());
        assertEquals(Color.RED,board.getBox(4, 4).getOccupier().getColour() );
        clearBoardForFutureTests(board);

        t1.placeWorker(board, coord(2, 3), "B");
        t2.placeWorker(board, coord(3, 2), "A");
        t1.selectWorker(board, coord(2, 3));
        t1.move(board, coord(3, 2));
        assertEquals(Color.GREEN, board.getBox(3, 2).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(4, 1).getOccupier().getColour());
        clearBoardForFutureTests(board);

        t1.placeWorker(board, coord(4, 0), "B");
        t2.placeWorker(board, coord(3, 1), "A");
        t1.selectWorker(board, coord(4, 0));
        t1.move(board, coord(3, 1));
        assertEquals(Color.GREEN, board.getBox(3, 1).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(2, 2).getOccupier().getColour());
        clearBoardForFutureTests(board);


    }
    @Test
    void minotaurEffectObstacleTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();

        //cases with other workers or domes preventing the effect

        t1.placeWorker(board, coord(2, 3), "B");
        t2.placeWorker(board, coord(2, 2), "A");
        t1.selectWorker(board, coord(2, 3));
        board.getBox(2,1).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(2,1).increaseLevel();
        t1.move(board, coord(2, 2));
        assertEquals(Color.GREEN, board.getBox(2, 3).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(2, 2).getOccupier().getColour());
        clearBoardForFutureTests(board);

        t1.placeWorker(board, coord(2, 3), "B");
        t2.placeWorker(board, coord(2, 2), "A");
        t2.placeWorker(board, coord(2, 1), "B");
        t1.selectWorker(board, coord(2, 3));
        t1.move(board, coord(2, 2));
        assertEquals(Color.GREEN, board.getBox(2, 3).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(2, 2).getOccupier().getColour());
        clearBoardForFutureTests(board);

    }
    @Test
    void minotaurEffectOutOfBoardTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();

        //cases where the opponent worker would be sent out of the board
        //there is no need to test the whole border, because if the calculation of r3, c3 is always correct (as the SUCCESSFUL tests show)
        //there's no reason the InBoundaries method will fail to test if other variations of r3,c3 will cause problems


        t1.placeWorker(board, coord(1, 1), "B");
        t2.placeWorker(board, coord(0, 1), "A");
        t1.selectWorker(board, coord(1, 1));
        t1.move(board, coord(0, 1));
        assertEquals(Color.GREEN, board.getBox(1, 1).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(0, 1).getOccupier().getColour());
        clearBoardForFutureTests(board);

        t1.placeWorker(board, coord(1, 1), "B");
        t2.placeWorker(board, coord(0, 0), "A");
        board.drawBoard();
        t1.selectWorker(board, coord(1, 1));
        t1.move(board, coord(0, 0));
        board.drawBoard();
        assertEquals(Color.GREEN, board.getBox(1, 1).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(0, 0).getOccupier().getColour());
        clearBoardForFutureTests(board);
    }
    @Test
    void minotaurEffectYourOwnWorkerTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();



        //case where it is denied to send back one of your workers
            t1.placeWorker(board, coord(2, 2), "B");
            t1.placeWorker(board, coord(2, 1), "A");
            t2.placeWorker(board, coord(2, 3), "A");
            board.drawBoard();
            t1.selectWorker(board, coord(2,2));
            //the effect is denied for a box where another one of your workers is
            assertFalse(t1.move(board, coord(2,1)));
            //but again works correctly if asked a box where an opponent's worker is placed
            assertTrue(t1.move(board, coord(2,3)));
            board.drawBoard();
            clearBoardForFutureTests(board);

    }


    @Test
    void panEffectFrom2To0Test() throws  DataFormatException{
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();


        //considered that the effect of pan is a basicMove with another winning condition, i just verify that this part works
        t1.placeWorker(board, coord(2, 2), "B");
        t2.placeWorker(board, coord(2, 3), "A");
        t2.selectWorker(board, coord(2,3 ));
        t2.move(board , coord(2,4));
        t2.build(board, coord(3,4));
        t2.selectWorker(board, coord(2 ,4 ));
        t2.move(board , coord(3,4));
        t2.build(board, coord(2,4));
        t2.selectWorker(board, coord(3 ,4 ));
        t2.move(board , coord(2,4));
        t2.build(board, coord(3,4));
        t2.selectWorker(board, coord(2 ,4 ));
        t2.move(board , coord(3,4));
        t2.build(board, coord(2,4));
        t2.selectWorker(board, coord(3 ,4 ));
        assertFalse(t2.isWinner());
        t2.move(board , coord(3,3));
        assertTrue(t2.isWinner());
        clearBoardForFutureTests(board);
        t2.setWinner(false);


    }
    @Test
    void panEffectFrom3To0Test() throws  DataFormatException {
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn(p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn(p2, Color.RED, "pan");
        Board board = new Board();

        //considered that the effect of pan is a basicMove with another winning condition, i just verify that this part works
        t1.placeWorker(board, coord(2, 0), "B");
        t2.placeWorker(board, coord(2, 3), "A");
        t2.selectWorker(board, coord(2,3 ));
        t2.move(board , coord(2,2));
        t2.build(board, coord(3,2));
        t2.selectWorker(board, coord(2 ,2 ));
        t2.move(board , coord(3,2));
        t2.build(board, coord(2,2));
        t2.selectWorker(board, coord(3 ,2 ));
        t2.move(board , coord(2,2));
        t2.build(board, coord(3,2));
        t2.selectWorker(board, coord(2 ,2 ));
        t2.move(board , coord(3,2));
        t2.build(board, coord(2,2));
        t2.selectWorker(board, coord(3 ,2 ));
        t2.move(board , coord(2,2));
        t2.build(board, coord(3,2));
        t2.selectWorker(board, coord(2 ,2 ));
        t2.move(board , coord(3,2));
        t2.build(board, coord(2,2));
        //need to bring winner to false because by going to third level player wins
        t2.setWinner(false);
        assertFalse(t2.isWinner());
        t2.selectWorker(board, coord(3 ,2 ));
        board.drawBoard();
        t2.move(board , coord(2,1));
        board.drawBoard();
        assertTrue(t2.isWinner());
        clearBoardForFutureTests(board);
        t2.setWinner(false);


    }
    @Test
    void panEffectFrom3To1Test() throws  DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();


        //considered that the effect of pan is a basicMove with another winning condition, i just verify that this part works

            t1.placeWorker(board, coord(2, 0), "B");
            t2.placeWorker(board, coord(2, 3), "A");
            t2.selectWorker(board, coord(2,3 ));
            t2.move(board , coord(2,2));
            t2.build(board, coord(3,2));
            t2.selectWorker(board, coord(2 ,2 ));
            t2.move(board , coord(3,2));
            t2.build(board, coord(2,2));
            t2.selectWorker(board, coord(3 ,2 ));
            t2.move(board , coord(2,2));
            t2.build(board, coord(3,2));
            t2.selectWorker(board, coord(2 ,2 ));
            t2.move(board , coord(3,2));
            t2.build(board, coord(2,2));
            t2.selectWorker(board, coord(3 ,2 ));
            t2.move(board , coord(2,2));
            t2.build(board, coord(3,2));
            t2.selectWorker(board, coord(2 ,2 ));
            t2.move(board , coord(3,2));
            t2.build(board, coord(2,2));
            t2.selectWorker(board, coord(3 ,2 ));
            t2.move(board , coord(2,2));
            t2.build(board, coord(2,1));
            //need to bring winner to false because by going to third level player wins
            t2.setWinner(false);
            assertFalse(t2.isWinner());
            board.drawBoard();
            t2.selectWorker(board, coord(2 ,2 ));
            t2.move(board , coord(2,1));
            board.drawBoard();
            assertTrue(t2.isWinner());
            clearBoardForFutureTests(board);
            t2.setWinner(false);

        clearBoardForFutureTests(board);
    }


    @Test
    void athenaEffectFirstTest() throws DataFormatException {
        //here are tested cases where athena moves from 0 to 0 and from 0 to 1 and subsequent effect on opponent
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "athena");
        Turn t2 = new Turn (p2, Color.RED, "minotaur");
        Board board = new Board();



        t1.placeWorker(board, coord(2,3),  "A");
        t2.placeWorker(board, coord(1,1), "A");
        t2.placeWorker(board, coord(0,4), "B");
        board.getBox(2,2).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(1,2).increaseLevel();
        board.getBox(3,2).increaseLevel();
        board.getBox(1,3).increaseLevel();
        board.getBox(2,0).increaseLevel();
        board.getBox(0,2).increaseLevel();
        board.getBox(0,2).increaseLevel();
        //board.drawBoard();
        t1.selectWorker(board, coord(2,3));
        t1.move(board, coord(3,3));
        t1.build(board, coord(4,3));
        t2.selectWorker(board, coord(1,1));
        //the player with athena moved from level 0 to level 0, the other player can go to level 1
        assertTrue(t2.move(board, coord(1,2)));
        t2.build(board, coord(2,2));
        //board.drawBoard();
        t1.selectWorker(board, coord(3,3));
        t1.move(board, coord(3,2));
        t1.build(board, coord(3,1));
        t2.selectWorker(board, coord(1,2));
        //the player with athena moved from level 0 to level 1, the other player cannot go up to level 2
        assertFalse(t2.move(board, coord(2,2)));
        assertTrue(t2.move(board,coord(1,3)));
        t2.build(board, coord(1,2));
        board.drawBoard();


    }
    @Test
    void athenaEffectSecondTest() throws DataFormatException {
        //here are tested cases where athena moves from 1 to 1 and from 1 to 2 and subsequent effect on opponent
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "athena");
        Turn t2 = new Turn (p2, Color.RED, "minotaur");
        Board board = new Board();


        board.getBox(2,2).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(1,2).increaseLevel();
        board.getBox(3,2).increaseLevel();
        board.getBox(3,1).increaseLevel();
        board.getBox(1,3).increaseLevel();
        board.getBox(2,0).increaseLevel();
        board.getBox(0,2).increaseLevel();
        t1.placeWorker(board, coord(3,2),  "A");
        t2.placeWorker(board, coord(1,3), "A");
        t2.placeWorker(board, coord(0,4), "B");

        t1.selectWorker(board, coord(3,2));
        t1.move(board, coord(3,1));
        t1.build(board, coord(3,0));
        t2.selectWorker(board, coord(1,3));

        //the player with athena moved from level 1 to level 1, the other player can move from 1 to 2
        assertTrue(t2.move(board, coord(1,2)));
        t2.build(board, coord(2,2));
        board.drawBoard();
        t1.selectWorker(board, coord(3,1));
        t1.move(board, coord(2,1));
        board.drawBoard();
        t1.build(board, coord(2,0));
        t2.selectWorker(board, coord(1,2));
        //the player with athena moved from level 1 to level 2, the other player cannot move from 2 to 3
        assertFalse(t2.move(board, coord(2,2)));
        assertTrue(t2.move(board, coord(0,2)));
        t2.build(board, coord(1,2));
        board.drawBoard();

    }
    @Test
    void athenaEffectThirdTest() throws DataFormatException {
        //cases from 2 to 2 and from 2 to 3, and subsequent effect on opponent
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "athena");
        Turn t2 = new Turn (p2, Color.RED, "minotaur");
        Board board = new Board();


        t1.placeWorker(board, coord(2,1),  "A");
        t2.placeWorker(board, coord(0,2), "A");
        t2.placeWorker(board, coord(0,4), "B");
        board.getBox(2,2).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(1,2).increaseLevel();
        board.getBox(3,2).increaseLevel();
        board.getBox(3,1).increaseLevel();
        board.getBox(1,3).increaseLevel();
        board.getBox(2,0).increaseLevel();
        board.getBox(0,2).increaseLevel();
        board.getBox(2,2).increaseLevel();
        board.getBox(3,0).increaseLevel();
        board.getBox(2,0).increaseLevel();
        board.getBox(1,2).increaseLevel();
        board.drawBoard();
        
            t1.selectWorker(board, coord(2,1));
            t1.move(board, coord(2,0));
            t1.build(board, coord(2,1));
            board.drawBoard();
            t2.selectWorker(board, coord(0,2));
            //the player with athena moved from level 2 to level 2, the other player can move from 2 to 3 (IGNORING WINNER TOGGLE BECAUSE NOT RELEVANT)
            assertTrue(t2.move(board, coord(1,2)));
            t2.build(board, coord(0,2));
            board.drawBoard();
            t1.selectWorker(board, coord(2,0));
            t1.move(board, coord(2,1));
            t1.build(board, coord(1,0));
            t2.selectWorker(board, coord(0,4));
            //now the player with athena got from level 2 to level 3, the other worker from player 2, which was still at level 0, cannot go up to 1
            assertFalse(t2.move(board, coord(1,3)));
            assertTrue(t2.move(board, coord(1,4)));
            t2.build(board, coord(0,4));
            board.drawBoard();
            clearBoardForFutureTests(board);




    }


    @Test
    void apolloEffectFirstTest() throws DataFormatException {

        //considered that apollo's effect is a basicBuild with another control i only check his relevant cases

        //some cases of functioning usage
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "apollo");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(3,2),  "B");
        t2.placeWorker(board, coord(1,2), "A");
        t2.placeWorker(board, coord(3,4), "B");


        board.drawBoard();
        assertEquals(Color.GREEN, board.getBox(2,3).getOccupier().getColour());
        assertEquals(Color.RED, board.getBox(1,2).getOccupier().getColour());
        t1.selectWorker(board, coord(2,3));
        t1.move(board, coord(1,2));
        t1.build(board, coord(0,2));
        assertEquals(Color.RED, board.getBox(2,3).getOccupier().getColour());
        assertEquals(Color.GREEN, board.getBox(1,2).getOccupier().getColour());
        //as expected, the workers switched positions
        board.drawBoard();
        t2.selectWorker(board, coord(2,3));
        t2.move(board, coord(2,4));
        t2.build(board, coord(1,4));
        board.drawBoard();
        t1.selectWorker(board, coord(1,2));
        t1.move(board, coord(2,3));
        //now the player with apollo simply moved in an empty space and left an empty space behind
        assertEquals(Color.GREEN, board.getBox(2,3).getOccupier().getColour());
        assertNull(board.getBox(1,2).getOccupier());
        t1.build(board, coord(1,3));
        t2.selectWorker(board, coord(3,4));
        t2.move(board, coord(4,3));
        t2.build(board, coord(4,4));
        board.drawBoard();
        t1.selectWorker(board, coord(2,3));
        //it is denied to switch with one of your workers
        assertFalse(t1.move(board, coord(3,2)));
        //but the switch with an opponent is accepted
        assertTrue(t1.move(board, coord(2,4)));
        assertEquals(Color.RED, board.getBox(2,3).getOccupier().getColour());
        assertEquals(Color.GREEN, board.getBox(2,4).getOccupier().getColour());
        t1.build(board, coord(1,4));
        board.drawBoard();

    }
    @Test
    void apolloEffectSecondTest() throws DataFormatException {

        //considered that apollo's effect is a basicBuild with another control i only check his relevant cases

        //a couple cases from level which the opponent's worker would not normally be able to reach
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "apollo");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t2.placeWorker(board, coord(2,3),  "A");
        t2.placeWorker(board, coord(4,3),  "B");
        t1.placeWorker(board, coord(2,4), "A");
        t1.placeWorker(board, coord(3,2), "B");
        board.getBox(0,2).increaseLevel();
        board.getBox(1,4).increaseLevel();
        board.getBox(1,3).increaseLevel();
        board.getBox(4,4).increaseLevel();
        board.getBox(1,4).increaseLevel();


            board.getBox(1,1).increaseLevel();
            board.getBox(1,1).increaseLevel();
            board.getBox(1,1).increaseLevel();
            board.moveWorker(3,2,1,1);
            board.moveWorker(2,3, 1,2);
            board.drawBoard();
            t1.selectWorker(board, coord(1,1));
            assertEquals(Color.GREEN, board.getBox(1,1).getOccupier().getColour());
            assertEquals(Color.RED, board.getBox(1,2).getOccupier().getColour());
            t1.move(board, coord(1,2));
            assertEquals(Color.RED, board.getBox(1,1).getOccupier().getColour());
            assertEquals(Color.GREEN, board.getBox(1,2).getOccupier().getColour());
            board.drawBoard();
        clearBoardForFutureTests(board);

    }


    @Test
    void prometheusEffectNotConfirmingTest() throws DataFormatException {
        //case where the input confirmation message is wrong or the player sends coordinates

        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "prometheus");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();


        t1.selectWorker(board, coord (2,3));
        //sends something different from yes/no
        assertFalse(t1.move(board, confirmation(ConfirmationEnum.NotDef)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        //sends some coordinates
        assertFalse(t1.move(board, coord(2,2)));
        assertEquals(GodPart.ONE, t1.getGodPart());
    }
    @Test
    void prometheusEffectUsedOrNotUsedTest() throws DataFormatException {
        //cases where the effect is activated or not
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "prometheus");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        //t2.placeWorker(board, coord(1,2), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();



            t1.selectWorker(board, coord(2, 3));
            assertEquals(GodPart.ONE, t1.getGodPart());
            assertFalse(t1.move(board, confirmation(ConfirmationEnum.Yes)));
            assertEquals(GodPart.TWO, t1.getGodPart());
            assertFalse(t1.move(board, coord(1, 3)));
            assertEquals(GodPart.THREE, t1.getGodPart());
            //when accepted to use the power, the first coordinates given to the move tell where to build prior moving
            assertEquals(1, board.getBox(1, 3).getTower().size());
            assertNull(board.getBox(1, 3).getOccupier());
            board.drawBoard();
            //then if the player asks to move to where he built, he's denied to, because now he can't move up
            assertFalse(t1.move(board, coord(1, 3)));
            assertNull(board.getBox(1, 3).getOccupier());
            //now the move will return true after completing successfully the move part of the effect
            assertTrue(t1.move(board, coord(2, 2)));
            assertEquals( GodPart.ONE, t1.getGodPart());
            t1.build(board, coord(2, 1));
            board.drawBoard();
            //now a turn for the second player
            t2.selectWorker(board, coord(3, 4));
            t2.move(board, coord(3, 3));
            t2.build(board, coord(3, 2));
            board.drawBoard();
            t1.selectWorker(board, coord(4,1));
            //when saying no, the player will be able to conclude the move in one step, even going up
            assertFalse(t1.move(board, confirmation(ConfirmationEnum.No)));
            assertEquals(GodPart.FOUR, t1.getGodPart());
            assertTrue(t1.move(board, coord(3,2)));
            board.drawBoard();
            clearBoardForFutureTests(board);



    }


    @Test
    void artemisEffectNotConfirmingTest() throws DataFormatException {
        //case where the input confirmation message is wrong or the player sends coordinates

        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "artemis");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        //t2.placeWorker(board, coord(1,2), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();

        t1.selectWorker(board, coord (2,3));
        //sends something different from yes/no
        assertFalse(t1.move(board, confirmation(ConfirmationEnum.NotDef)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        //sends some coordinates
        assertFalse(t1.move(board, coord(2,2)));
        assertEquals(GodPart.ONE, t1.getGodPart());
    }
    @Test
    void artemisEffectUsedOrNotUsedTest() throws DataFormatException {
        //cases where the player chooses to use or not use the effect

        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "artemis");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        //t2.placeWorker(board, coord(1,2), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();



        //cases where the player chooses to use or not use the effect
            t1.selectWorker(board, coord(2,3));
            assertEquals(GodPart.ONE, t1.getGodPart());
            t1.move(board, confirmation(ConfirmationEnum.Yes));
            assertEquals(GodPart.TWO, t1.getGodPart());
            t1.move(board, coord(2,2));
            assertEquals(GodPart.THREE, t1.getGodPart() );
            board.drawBoard();
            //if i try to go back to the old position the move does not conclude and i'm still in the second
            assertFalse(t1.move(board, coord(2,3)));
            assertEquals(GodPart.THREE, t1.getGodPart());
            board.drawBoard();
            assertTrue(t1.move(board, coord(2,1)));
            assertEquals(GodPart.ONE, t1.getGodPart());
            t1.build(board, coord(2,0));
            board.drawBoard();
            //a turn for the second player
            t2.selectWorker(board, coord(3,4));
            t2.move(board, coord(4,4));
            t2.build(board, coord(3,4));
            //then a turn where the player with artemis chooses not tu use its effect
            assertEquals(GodPart.ONE, t1.getGodPart());
            assertFalse(t1.move(board, confirmation(ConfirmationEnum.No)));
            assertEquals(GodPart.FOUR, t1.getGodPart());
            //now the move will be completed with just on act
            assertTrue(t1.move(board, coord(2,0)));
            assertEquals(GodPart.ONE, t1.getGodPart());
            t1.build(board, coord(2,1));
            board.drawBoard();

        clearBoardForFutureTests(board);
    }
    @Test
    void artemisCanBeUsedTest() throws DataFormatException {
        //here i test the private parse method that checks if artemis' effect can be used

        Board board = new Board();
        Player playerA = new Player("Giulio", 22, 12, 1990);
        Worker workerA = new Worker(playerA, Color.GREEN, "A");
        Worker workerB = new Worker(playerA, Color.GREEN, "B");
        Worker workerC = new Worker(playerA, Color.GREEN, "C");
        clearBoardForFutureTests(board);
        Turn t1 = new Turn(playerA, Color.GREEN, "artemis");

        board.placeWorker(workerA, 1,2);
        board.placeWorker(workerB, 1,1);
        board.placeWorker(workerC, 2 ,2);
        board.getBox(0,0).placeDome();
        board.getBox(0,1).placeDome();
        board.getBox(0,2).placeDome();
        board.getBox(0,3).placeDome();
        board.getBox(2,1).placeDome();
        board.getBox(0,4).placeDome();
        board.getBox(2,3).placeDome();
        board.getBox(1,4).placeDome();
        board.getBox(2,4).placeDome();
        //in 1,2 it is already blocked
        t1.selectWorker(board, coord(1,2));
        t1.move(board, coord(1,2));
        assertEquals(GodPart.FOUR, t1.getGodPart());
        t1.setGodPart(GodPart.ONE);
        board.drawBoard();
        board.getBox(3,1).placeDome();
        board.getBox(3,3).placeDome();
        board.getBox(4,1).placeDome();
        board.getBox(4,3).placeDome();
        board.drawBoard();
        //while in 2,2 it is not
        t1.selectWorker(board, coord(2,2));
        t1.move(board, coord(2,2));
        assertEquals(GodPart.ONE, t1.getGodPart());
        //now i block also 2,2
        board.getBox(4,2).placeDome();
        t1.selectWorker(board, coord(2,2));
        t1.move(board, coord(2,2));
        assertEquals(GodPart.FOUR, t1.getGodPart());
        t1.setGodPart(GodPart.ONE);
        board.drawBoard();
        //1,1 was never blocked
        t1.selectWorker(board, coord(1,1));
        t1.move(board, coord(1,1));
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.getBox(2,3).decreaseLevel();
        //now i remove a dome and 1,2 and 2,2 could use the effect
        t1.selectWorker(board, coord(1,2));
        t1.move(board, coord(1,2));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.selectWorker(board, coord(2,2));
        t1.move(board, coord(2,2));
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();
        board.getBox(1,0).placeDome();
        board.getBox(2,0).placeDome();
        t1.selectWorker(board, coord(1,1));
        t1.move(board, coord(1,1));
        board.drawBoard();
        //1,2 and 2,2 will be blocked again
        board.getBox(1,3).placeDome();
        board.getBox(3,4).placeDome();
        board.getBox(3,2).placeDome();
        t1.selectWorker(board, coord(1,2));
        t1.move(board, coord(1,2));
        assertEquals(GodPart.FOUR, t1.getGodPart());
        t1.setGodPart(GodPart.ONE);
        t1.selectWorker(board, coord(2,2));
        t1.move(board, coord(2,2));
        assertEquals(GodPart.FOUR, t1.getGodPart());
        t1.setGodPart(GodPart.ONE);
        board.drawBoard();


        clearBoardForFutureTests(board);
    }


    @Test
    void atlasEffectNotConfirmingTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1,12, 2000);
        Turn t1 = new Turn (p1, Color.GREEN, "atlas");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");

        //case where the input confirmation message is wrong or the player sends coordinates
        t1.selectWorker(board, coord (2,3));
        //sends something different from yes/no
        assertFalse(t1.build(board, confirmation(ConfirmationEnum.NotDef)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        //sends some coordinates
        assertFalse(t1.build(board, coord(2,2)));
        //if the message is not yes/no, nothing happens and the state will remain GodStateOne
        assertEquals(GodPart.ONE, t1.getGodPart());

        clearBoardForFutureTests(board);
    }
    @Test
    void atlasEffectUsedTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1,12, 2000);
        Turn t1 = new Turn (p1, Color.GREEN, "atlas");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");

        //a turn where I activate Atlas'effect
        t1.selectWorker(board, coord(2,3));
        t1.move(board,coord(2,2));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.TWO, t1.getGodPart());
        t1.build(board, coord(2,3));
        //there will be a dome where the worker built
        assertEquals( 1, board.getBox(2,3).getTower().size() );
        assertTrue( board.getBox(2,3).isDomed() );
        //returning to GodStateOne
        assertEquals(GodPart.ONE, t1.getGodPart());

        clearBoardForFutureTests(board);
    }
    @Test
    void atlasEffectNotUsedTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1,12, 2000);
        Turn t1 = new Turn (p1, Color.GREEN, "atlas");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");

        //a turn where I don't activate Atlas'effect
        t1.selectWorker(board, coord(2,3));
        t1.move(board,coord(3,3));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.No));
        assertEquals(GodPart.THREE, t1.getGodPart());
        t1.build(board, coord(4,4));
        //there won't be a dome where the worker built
        assertFalse( board.getBox(4,4).isDomed() );
        assertEquals( 1, board.getBox(4,4).getTower().size());
        //returning to GodStateOne
        assertEquals(GodPart.ONE, t1.getGodPart());

        clearBoardForFutureTests(board);
    }


    @Test
    void hephaestusEffectNotConfirmingTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Hephaestus");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 3), "A");

        //case where the input confirmation message is wrong or the player sends coordinates
        t1.selectWorker(board, coord(2, 3));
        //sends something different from yes/no
        assertFalse(t1.build(board, confirmation(ConfirmationEnum.NotDef)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        //sends some coordinates
        assertFalse(t1.build(board, coord(2, 2)));
        assertEquals(GodPart.ONE, t1.getGodPart());

        clearBoardForFutureTests(board);
    }
    @Test
    void hephaestusEffectUsedTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Hephaestus");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 3), "A");

        //a turn where I activate Hephaestus' effect
        t1.selectWorker(board, coord(2, 3));
        t1.move(board, coord(2, 2));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.TWO, t1.getGodPart());
        //before building, (1,1) has level 0
        assertEquals(0, board.getBox(1, 1).getTower().size());
        t1.build(board, coord(1, 1));
        //after building, (1,1) has level 2, because of Hephaestus' effect
        assertEquals(2, board.getBox(1, 1).getTower().size());
        //returning to GodStateOne
        assertEquals(GodPart.ONE, t1.getGodPart());

        clearBoardForFutureTests(board);
    }
    @Test
    void hephaestusEffectNotUsedTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Hephaestus");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 2), "A");

        //a turn where I don't activate Hephaestus' effect
        t1.selectWorker(board, coord(2, 2));
        t1.move(board, coord(3, 3));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.No));
        assertEquals(GodPart.THREE, t1.getGodPart());
        //before building, (4,4) has level 0
        assertEquals(0, board.getBox(4, 4).getTower().size());
        t1.build(board, coord(4, 4));
        //after building, (4,4) has level 1
        assertEquals(1, board.getBox(4, 4).getTower().size());
        //returning to GodStateOne
        assertEquals(GodPart.ONE, t1.getGodPart());

        clearBoardForFutureTests(board);
    }
    @Test
    void hephaestusEffectSingleConstruction() throws DataFormatException{
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Hephaestus");
        Board board = new Board();
        t1.placeWorker(board, coord(3, 3), "A");

        //case with a single construction
        t1.selectWorker(board, coord(3, 3));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.Yes));

        assertEquals(0, board.getBox(2, 2).getTower().size());
        board.getBox(2, 2).increaseLevel();
        board.getBox(2, 2).increaseLevel();
        //before building, (2,2) has level 2, so I just build once
        assertEquals(2, board.getBox(2, 2).getTower().size());
        t1.build(board, coord(2, 2));
        //after building, (2,2) has level 3 instead of 4, even if I activated the effect, and it's not domed
        assertEquals(3, board.getBox(2, 2).getTower().size());
        assertFalse(board.getBox(2, 2).isDomed());
        //returning to GodStateOne
        assertEquals(GodPart.ONE, t1.getGodPart());

        clearBoardForFutureTests(board);
    }
    @Test
    void hephaestusEffectAlreadyLevel3Tower() throws DataFormatException{
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Hephaestus");
        Board board = new Board();
        t1.placeWorker(board, coord(3, 3), "A");

        //case where the box is already at level 3, I don't leave the player to build
        t1.selectWorker(board, coord(3, 3));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(0, board.getBox(2, 3).getTower().size());
        board.getBox(2, 3).increaseLevel();
        board.getBox(2, 3).increaseLevel();
        board.getBox(2, 3).increaseLevel();
        //before building, (2,3) has level 3
        assertEquals(3, board.getBox(2, 3).getTower().size());
        t1.build(board, coord(2, 3));
        //after building, (2,3) has level 3, like before
        assertEquals(3, board.getBox(2, 3).getTower().size());
        assertFalse(board.getBox(2, 3).isDomed());
        //he is still at GodStateTwo, he has still to build then
        assertEquals(GodPart.TWO, t1.getGodPart());

        clearBoardForFutureTests(board);
    }


    @Test
    void demeterEffectNotConfirmingTest() throws DataFormatException {
        //cases where the answer to the will to activate the effect isn't given in the right way
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "demeter");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        t2.placeWorker(board, coord(0,1), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();

        t1.selectWorker(board, coord (2,3));
        t1.move(board, coord(2,2));
        //sends something different from yes/no
        assertFalse(t1.build(board, confirmation(ConfirmationEnum.NotDef)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        //sends some coordinates
        assertFalse(t1.build(board, coord(2,1)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();
    }
    @Test
    void demeterEffectRefusedTest() throws DataFormatException {
        //a case where the player chooses not to activate te effect
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "demeter");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        t2.placeWorker(board, coord(0,1), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();

        t1.selectWorker(board, coord (2,3));
        t1.move(board, coord(2,2));

        //by saying no it sends to state Four where it simply make a basicbuild then resets godstate and returns true
        t1.build(board, confirmation(ConfirmationEnum.No));
        assertEquals(GodPart.FOUR, t1.getGodPart());
        assertTrue(t1.build(board, coord(2,1)));
        assertEquals(GodPart.ONE, t1.getGodPart());

    }
    @Test
    void demeterEffectUsedAndCheckedTest() throws DataFormatException {
        //here i use the effect and check that it won't allow me to build on the same spot

        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "demeter");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        t2.placeWorker(board, coord(0,1), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();

        t1.selectWorker(board, coord (2,3));
        t1.move(board, coord(2,2));
        t1.build(board, coord(2,1));
        //part where the effect is actually tested
        board.drawBoard();
        //a turn for t2 to respect the order there would be in the game
        t2.selectWorker(board, coord(0,1));
        t2.move(board, coord(0,2));
        t2.build(board, coord(0,1));
        board.drawBoard();

        //now the player with demeter accepts to use its effect
        t1.selectWorker(board, coord(2,2));
        t1.move(board,coord(3,2));
        board.drawBoard();
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.TWO, t1.getGodPart());
        t1.build(board, coord(2,1));
        assertEquals(GodPart.THREE, t1.getGodPart());
        board.drawBoard();

        //now if asked to do the second build on the same sport the build stays in state 3 and returns false
        assertFalse(t1.build(board, coord(2,1)));
        assertEquals(GodPart.THREE, t1.getGodPart());
        board.drawBoard();

        //while if asked to build on a different box, does the build and returns true after setting godState back to one
        assertTrue(t1.build(board, coord(2,2)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();

        clearBoardForFutureTests(board);

    }
    @Test
    void demeterCanBeUsedTest() throws DataFormatException {
        //here are tested various cases of the private parse method that prevents the effect's usage when not possible

        Board board = new Board();
        Player playerA = new Player("Giulio", 22, 12, 1990);
        Worker workerA = new Worker(playerA, Color.GREEN, "A");
        Worker workerB = new Worker(playerA, Color.GREEN, "B");
        Worker workerC = new Worker(playerA, Color.GREEN, "C");
        clearBoardForFutureTests(board);
        Turn t = new Turn (playerA, Color.GREEN, "demeter");


        board.placeWorker(workerA, 1,2);
        board.placeWorker(workerB, 1,1);
        board.placeWorker(workerC, 2 ,2);

        board.getBox(0,0).placeDome();
        board.getBox(0,1).placeDome();
        board.getBox(0,2).placeDome();
        board.getBox(0,3).placeDome();
        board.getBox(2,1).placeDome();
        board.getBox(0,4).placeDome();
        board.getBox(2,3).placeDome();
        board.getBox(1,4).placeDome();
        board.getBox(2,4).placeDome();
        board.drawBoard();
        //the worker in 1,2 cannot use the effect, the others can
        t.selectWorker(board, coord(1,2));
        t.build(board, coord(1,2));
        assertEquals(GodPart.FOUR, t.getGodPart());
        t.setGodPart(GodPart.ONE);
        t.selectWorker(board, coord(2,2));
        t.build(board, coord(2,2));
        assertEquals(GodPart.ONE, t.getGodPart());
        t.selectWorker(board, coord(1,1));
        t.build(board, coord(1,1));
        assertEquals(GodPart.ONE, t.getGodPart());
        board.getBox(3,3).placeDome();
        board.getBox(3,1).placeDome();
        board.getBox(3,2).placeDome();
        //i'll block the one in 2,2
        t.selectWorker(board, coord(2,2));
        t.build(board, coord(2,2));
        assertEquals(GodPart.FOUR, t.getGodPart());
        t.setGodPart(GodPart.ONE);
        board.getBox(3,0).placeDome();
        board.drawBoard();
        //leave just two free spaces to the one in 1,1
        t.selectWorker(board, coord(1,1));
        t.build(board, coord(1,1));
        assertEquals(GodPart.ONE, t.getGodPart());
        board.getBox(2,0).placeDome();
        //and then even him won't be able to use the effect
        t.selectWorker(board, coord(1,1));
        t.build(board, coord(1,1));
        assertEquals(GodPart.FOUR, t.getGodPart());
        t.setGodPart(GodPart.ONE);
        //then i free a position far from the worker in 1,2, still cannot use the effect
        board.getBox(0,4).decreaseLevel();
        t.selectWorker(board, coord(1,2));
        t.build(board, coord(1,2));
        assertEquals(GodPart.FOUR, t.getGodPart());
        t.setGodPart(GodPart.ONE);
        //then i free one near him, now he could use the effect
        board.getBox(0,3).decreaseLevel();
        t.selectWorker(board, coord(1,2));
        t.build(board, coord(1,2));
        assertEquals(GodPart.ONE, t.getGodPart());
        board.drawBoard();
    }


    @Test
    void tritonEffectContinueUntilObstacleTest() throws DataFormatException {
        //case where i say yes and go on until i find an obstacle and then leave the perimeter

        Player p1 = new Player("Peppino", 1, 12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn(p1, Color.GREEN, "triton");
        Turn t2 = new Turn(p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 3), "A");
        t1.placeWorker(board, coord(4, 1), "B");
        t2.placeWorker(board, coord(0, 1), "A");
        t2.placeWorker(board, coord(3, 4), "B");
        board.drawBoard();


        t1.selectWorker(board, coord(2,3));
        t1.move(board, coord(2,4));
        assertEquals(GodPart.TWO, t1.getGodPart());
        t1.move(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.move(board, coord(1,4));
        assertEquals(GodPart.TWO, t1.getGodPart());
        assertEquals(Color.GREEN, board.getBox(1,4).getOccupier().getColour());
        board.drawBoard();
        t1.move(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.move(board, coord(0,4));
        assertEquals(GodPart.TWO, t1.getGodPart());
        assertEquals(Color.GREEN, board.getBox(0,4).getOccupier().getColour());
        board.drawBoard();
        t1.move(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.move(board, coord(0,3));
        assertEquals(GodPart.TWO, t1.getGodPart());
        assertEquals(Color.GREEN, board.getBox(0,3).getOccupier().getColour());
        board.drawBoard();
        t1.move(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.move(board, coord(0,2));
        assertEquals(GodPart.TWO, t1.getGodPart());
        assertEquals(Color.GREEN, board.getBox(0,2).getOccupier().getColour());
        board.drawBoard();
        //now there is another worker on the track, so it leaves the perimeter and the method finally returns true
        t1.move(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.move(board, coord(0,1));
        assertEquals(GodPart.ONE, t1.getGodPart());
        assertTrue(t1.move(board, coord(1,2)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();
    }
    @Test
    void tritonEffectNotInPerimeterTest() throws DataFormatException {
        //case where i move but not in the perimeter, so confirmation is not even required and it immediately returns true

        Player p1 = new Player("Peppino", 1, 12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn(p1, Color.GREEN, "triton");
        Turn t2 = new Turn(p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(1, 2), "A");
        t1.placeWorker(board, coord(4, 1), "B");
        t2.placeWorker(board, coord(0, 1), "A");
        t2.placeWorker(board, coord(3, 4), "B");
        board.drawBoard();


        t1.selectWorker(board, coord(1,2));
        assertEquals(GodPart.ONE, t1.getGodPart());
        assertTrue(t1.move(board, coord(1,1)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();

    }
    @Test
    void tritonEffectYesAndNoTest() throws DataFormatException {
        //case where i say yes once and no the second

        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "triton");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(1,2),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        t2.placeWorker(board, coord(0,1), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();

            t1.selectWorker(board, coord(4,1));
            t1.move(board, coord(4,2));
            assertEquals(GodPart.TWO, t1.getGodPart());
            t1.move(board, confirmation(ConfirmationEnum.Yes));
            assertEquals(GodPart.ONE, t1.getGodPart());
            t1.move(board, coord(4,3));
            assertEquals(GodPart.TWO, t1.getGodPart());
            assertTrue(t1.move(board, confirmation(ConfirmationEnum.No)));
            board.drawBoard();


    }


    @Test
    void hestiaEffectAcceptUsageTest() throws DataFormatException {
        //case where i use the effect and correlated limit cases

        Player p1 = new Player("Peppino", 1, 12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn(p1, Color.GREEN, "hestia");
        Turn t2 = new Turn(p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 3), "A");
        t1.placeWorker(board, coord(4, 1), "B");
        t2.placeWorker(board, coord(0, 1), "A");
        t2.placeWorker(board, coord(3, 4), "B");
        board.drawBoard();

        t1.selectWorker(board, coord(4,1));
        t1.move(board, coord(3,1));
        //if i try to send a confirmation message in state one nothing changes
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.No));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, coord(4,1));
        assertEquals(1, board.getBox(4,1).getTower().size());
        assertEquals(GodPart.TWO, t1.getGodPart());
        board.drawBoard();
        //if i try to send other coordinates it doesn't step because it's waiting for confirmation
        t1.build(board, coord(3,2));
        assertEquals(GodPart.TWO, t1.getGodPart());
        //now i accept to use the effect
        t1.build(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.THREE, t1.getGodPart());
        //now if i try to build on the perimeter it won't allow me to do it
        t1.build(board, coord(4,2));
        t1.build(board, coord(4,1));
        t1.build(board, coord(4,0));
        t1.build(board, coord(3,0));
        // 4,1 is where i built before, therefore it's level 1
        assertEquals(1, board.getBox(4,1).getTower().size());
        assertEquals(0, board.getBox(4,2).getTower().size());
        assertEquals(0, board.getBox(4,0).getTower().size());
        assertEquals(0, board.getBox(3,0).getTower().size());
        assertEquals(GodPart.THREE, t1.getGodPart());
        board.drawBoard();
        //if i try to send a confirmation message in state three nothing changes
        assertEquals(GodPart.THREE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.No));
        assertEquals(GodPart.THREE, t1.getGodPart());
        //and when i chose the box not on the perimeter it works and the method concludes by returning true
        assertTrue(t1.build(board, coord(3,2)));
        assertEquals(1, board.getBox(3,2).getTower().size());
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();

    }
    @Test
    void hestiaEffectRefuseUsageTest() throws DataFormatException {
        //case where i refuse to use the effect and correlated limit cases
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "hestia");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(3,1),  "B");
        t2.placeWorker(board, coord(0,1), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.getBox(3,2).increaseLevel();
        board.getBox(3,2).increaseLevel();
        board.drawBoard();

            t1.selectWorker(board, coord(2,3));
            t1.move(board, coord(2,2));
            assertEquals(GodPart.ONE, t1.getGodPart());
            //i build the first time
            assertEquals(0, board.getBox(2,1).getTower().size());
            t1.build(board, coord(2,1));
            assertEquals(1, board.getBox(2,1).getTower().size());
            assertEquals(GodPart.TWO, t1.getGodPart());
            board.drawBoard();
            //waits for confirmation so if i give coords nothing happens
            t1.build(board, coord(2,3));
            board.drawBoard();
            assertEquals(0, board.getBox(2,3).getTower().size());
            assertEquals(GodPart.TWO, t1.getGodPart());
            //if i tell him no it will conclude and return true
            assertTrue(t1.build(board, confirmation(ConfirmationEnum.No)));
            assertEquals(GodPart.ONE, t1.getGodPart());
    }


    @Test
    void aresEffectNoBlocksTest() throws DataFormatException {
        //case where there are no blocks to be removed

        Player p1 = new Player("Peppino", 1, 12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn(p1, Color.GREEN, "ares");
        Turn t2 = new Turn(p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 3), "A");
        t1.placeWorker(board, coord(4, 1), "B");
        t2.placeWorker(board, coord(0, 1), "A");
        t2.placeWorker(board, coord(3, 4), "B");
        board.drawBoard();

        t1.selectWorker(board, coord (4,1));
        t1.move(board, coord(4,2));
        assertEquals(GodPart.ONE, t1.getGodPart());
        //being no block to remove the method returns true and concludes without passing to state 2
        assertTrue(t1.build(board, coord(4,1)));
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();
    }
    @Test
    void aresEffectAcceptUsageTest() throws DataFormatException {
        //cases where the effect is applied

        Player p1 = new Player("Peppino", 1, 12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn(p1, Color.GREEN, "ares");
        Turn t2 = new Turn(p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 3), "A");
        t1.placeWorker(board, coord(4, 2), "B");
        t2.placeWorker(board, coord(0, 1), "A");
        t2.placeWorker(board, coord(3, 4), "B");
        board.getBox(4,1).increaseLevel();
        board.drawBoard();

        //first i give the other player a turn so that he can build near the other worker
        t2.selectWorker(board, coord(3,4));
        t2.move(board, coord(3,3));
        t2.build(board, coord(3,4));
        board.drawBoard();
        //now i make the turn for the player with ares
        t1.selectWorker(board, coord(4,2));
        t1.move(board, coord(4,1));
        board.drawBoard();
        //if i try to send a confirmation nothing happens
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.ONE, t1.getGodPart());
        //now that there is a block to be removed it will allow passage to state 2
        t1.build(board, coord(4,2));
        assertEquals(GodPart.TWO, t1.getGodPart());
        board.drawBoard();
        //now it waits for a confirmation so it won't accept a pair of coordinates
        t1.build(board, coord(4,2));
        assertEquals(1, board.getBox(4,2).getTower().size());
        assertEquals(GodPart.TWO, t1.getGodPart());
        //if i say yes it will move to state 3, also the coordinates the turn references to will change to the unmoved worker
        assertEquals(2, t1.getCurrentRow());
        assertEquals(3, t1.getCurrentColumn());
        t1.build(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.THREE, t1.getGodPart());
        //now if i gave coordinates where nothing is built nothing happens
        assertFalse(t1.build(board, coord(2,4)));
        assertFalse(t1.build(board, coord(3,2)));
        assertEquals(GodPart.THREE, t1.getGodPart());
        //same thing if i place a dome and choose that box
        board.getBox(3,2).placeDome();
        assertFalse(t1.build(board, coord(3,2)));
        assertEquals(GodPart.THREE, t1.getGodPart());
        //same thing if the place is occupied by a worker
        assertFalse(t1.build(board, coord(3,3)));
        assertEquals(GodPart.THREE, t1.getGodPart());
        //now i chose the box with a block on it, it will return true and do what's it meant to
        assertEquals(1, board.getBox(3,4).getTower().size());
        assertTrue(t1.build(board, coord(3,4)));
        assertEquals(0, board.getBox(3,4).getTower().size());
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();

        //now one case with a 3 level tower, by inverting the roles of the two workers in the effect's usage
        board.getBox(4,0).increaseLevel();
        board.getBox(4,0).increaseLevel();
        board.getBox(4,0).increaseLevel();
        t1.selectWorker(board, coord(2,3));
        t1.move(board, coord(2,4));
        assertEquals(GodPart.ONE, t1.getGodPart());
        t1.build(board, coord(2,3));
        //it moved to state 2 and selected the unmoved worker as a reference
        assertEquals(GodPart.TWO, t1.getGodPart());
        assertEquals(4, t1.getCurrentRow());
        assertEquals(1, t1.getCurrentColumn());
        //now i send the confirmation
        t1.build(board, confirmation(ConfirmationEnum.Yes));
        assertEquals(GodPart.THREE, t1.getGodPart());
        //now i select the tower and it will remove one block from it
        assertEquals(3, board.getBox(4,0).getTower().size());
        t1.build(board, coord(4,0));
        assertEquals(2, board.getBox(4,0).getTower().size());
        assertEquals(GodPart.ONE, t1.getGodPart());
        board.drawBoard();

    }
    @Test
    void aresEffectRefuseUsageTest() throws DataFormatException {
            //case where we refuse to use the effect

            Player p1 = new Player("Peppino", 1, 12, 2000);
            Player p2 = new Player("Giovanni", 12, 3, 1999);
            Turn t1 = new Turn(p1, Color.GREEN, "ares");
            Turn t2 = new Turn(p2, Color.RED, "pan");
            Board board = new Board();
            t1.placeWorker(board, coord(2, 4), "A");
            t1.placeWorker(board, coord(4, 1), "B");
            t2.placeWorker(board, coord(0, 1), "A");
            t2.placeWorker(board, coord(3, 4), "B");
            board.getBox(3,2).placeDome();
            board.getBox(4,0).increaseLevel();
            board.getBox(4,0).increaseLevel();
            board.getBox(4,1).increaseLevel();
            board.getBox(4,2).increaseLevel();
            board.getBox(2,3).increaseLevel();
            board.drawBoard();

            //first i do my turn
            t1.selectWorker(board, coord(2,4));
            t1.move(board, coord(2,3));
            t1.build(board,coord(2,4));
            assertEquals(GodPart.TWO, t1.getGodPart());
            //now i will say no, the method will return true and go back to state one
            assertTrue(t1.build(board, confirmation(ConfirmationEnum.No)));
            assertEquals(GodPart.ONE, t1.getGodPart());
            board.drawBoard();



        }
    @Test
    void aresEffectDeniedTest() throws DataFormatException {
        //other two cases where the effect cannot be used

        Player p1 = new Player("Peppino", 1, 12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn(p1, Color.GREEN, "ares");
        Turn t2 = new Turn(p2, Color.RED, "pan");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 3), "A");
        t1.placeWorker(board, coord(4, 1), "B");
        t2.placeWorker(board, coord(0, 1), "A");
        t2.placeWorker(board, coord(3, 4), "B");
        board.drawBoard();
        board.getBox(3,2).placeDome();
        board.getBox(4,0).increaseLevel();
        board.getBox(4,0).increaseLevel();
        board.getBox(4,1).increaseLevel();
        board.getBox(4,2).increaseLevel();
        board.getBox(2,3).increaseLevel();
        board.getBox(2,4).increaseLevel();
        board.drawBoard();



            //now i remove the other worker and it won't work
            board.getBox(2,3).setOccupier(null);
            board.drawBoard();
            t1.selectWorker(board, coord(4,1));
            t1.move(board, coord(4,2));
            //there being no other worker, nothing will happen
            assertTrue(t1.build(board, coord(4,1)));
            assertEquals(GodPart.ONE, t1.getGodPart());

            //now i put the other worker back and surround him with domes (and one worker), and again the effect won't work
            t1.placeWorker(board, coord(2,3), "B");
            board.getBox(1,3).placeDome();
            board.getBox(1,4).placeDome();
            board.getBox(1,2).placeDome();
            board.getBox(2,2).placeDome();
            board.getBox(3,4).placeDome();
            board.getBox(2,4).placeDome();
            t1.selectWorker(board, coord(4,2));
            t1.move(board, coord(4,1));
            board.drawBoard();
            //it returns true and doesn't change state
            assertTrue(t1.build(board, coord(3,0)));
            assertEquals(GodPart.ONE, t1.getGodPart());

    }

    @Test
    void zeusEffectStandardTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Zeus");
        Board board = new Board();
        t1.placeWorker(board, coord(2, 2), "A");

        t1.selectWorker(board, coord(2, 2));

        assertEquals(0, board.getBox(2, 2).getTower().size());
        //now (2,2) has level 0
        t1.build(board, coord(2, 2));
        assertEquals(1, board.getBox(2, 2).getTower().size());
        assertEquals(2, t1.getCurrentRow());
        assertEquals(2, t1.getCurrentColumn());
        //now (2,2) has level 1 and the worker still is on (2,2)

        t1.build(board, coord(2, 2));
        assertEquals(2, board.getBox(2, 2).getTower().size());
        assertEquals(2, t1.getCurrentRow());
        assertEquals(2, t1.getCurrentColumn());
        //now (2,2) has level 2 and the worker still is on (2,2)

        clearBoardForFutureTests(board);
    }
    @Test
    void zeusEffectWinningAttemptTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Zeus");
        Board board = new Board();

        board.getBox(2,2).increaseLevel();
        board.getBox(2,2).increaseLevel();
        //now (2,2) has level 2
        t1.placeWorker(board, coord(2, 2), "A");
        //the worker is on (2,2)
        t1.selectWorker(board, coord(2, 2));

        t1.build(board, coord(2, 2));
        assertEquals(3, board.getBox(2, 2).getTower().size());
        assertEquals(2, t1.getCurrentRow());
        assertEquals(2, t1.getCurrentColumn());
        //now (2,2) has level 3 and the worker still is on (2,2)

        //the worker goes on level 3 from level 2 but he doesn't win: he has to move there, not to build
        assertFalse(t1.isWinner());

        clearBoardForFutureTests(board);
    }
    @Test
    void zeusEffectBuildingDomeAttemptTest() throws DataFormatException{
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Zeus");
        Board board = new Board();

        board.getBox(2,2).increaseLevel();
        board.getBox(2,2).increaseLevel();
        board.getBox(2,2).increaseLevel();
        //now (2,2) has level 3
        t1.placeWorker(board, coord(2, 2), "A");
        //the worker is on (2,2)
        t1.selectWorker(board, coord(2, 2));

        //trying to build again on (2,2)
        t1.build(board, coord(2, 2));
        assertEquals(3, board.getBox(2, 2).getTower().size());
        assertFalse(board.getBox(2, 2).isDomed());
        assertEquals(2, t1.getCurrentRow());
        assertEquals(2, t1.getCurrentColumn());
        // (2,2) still has level 3: the worker can't build under itself, he would be placed on a dome!

        clearBoardForFutureTests(board);
    }


    /*@Test
    public void chronusEffectPlayerBuildsAndWinTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Chronus");

        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t2 = new Turn(p2, Color.RED, "Pan");

        ArrayList<Player> listPlayerLobby = new ArrayList<>();
        listPlayerLobby.add(p1);
        listPlayerLobby.add(p2);

        Model match1 = new Model(listPlayerLobby);
        Board board1 = match1.getGameboard();

        t1.placeWorker(board1, coord(1, 1), "A");
        t2.placeWorker(board1, coord(3, 3), "B");

        //player1 has Chronus
        assertEquals(t1.getDivinityCard().getSpecificGodName(), Global.chronus);

        //case where the player with Chronus as his god builds the fifth tower
        assertFalse(t1.isWinner());

        //completing tower in (0,0)
        t1.selectWorker(board1, coord(1, 1));
        t1.build(board1, coord(0, 0));
        t1.build(board1, coord(0, 0));
        t1.build(board1, coord(0, 0));
        t1.build(board1, coord(0, 0));
        assertEquals(4, board1.getBox(0, 0).getTower().size());
        assertFalse(t1.isWinner());

        //completing tower in (0,1)
        t1.selectWorker(board1, coord(1, 1));
        t1.build(board1, coord(0, 1));
        t1.build(board1, coord(0, 1));
        t1.build(board1, coord(0, 1));
        t1.build(board1, coord(0, 1));
        assertEquals(4, board1.getBox(0, 1).getTower().size());
        assertFalse(t1.isWinner());

        //completing tower in (0,2)
        t1.selectWorker(board1, coord(1, 1));
        t1.build(board1, coord(0, 2));
        t1.build(board1, coord(0, 2));
        t1.build(board1, coord(0, 2));
        t1.build(board1, coord(0, 2));
        assertEquals(4, board1.getBox(0, 2).getTower().size());
        assertFalse(t1.isWinner());

        //completing tower in (1,0)
        t1.selectWorker(board1, coord(1, 1));
        t1.build(board1, coord(1, 0));
        t1.build(board1, coord(1, 0));
        t1.build(board1, coord(1, 0));
        t1.build(board1, coord(1, 0));
        assertEquals(4, board1.getBox(1, 0).getTower().size());
        assertFalse(t1.isWinner());

        //completing tower in (1,2): there are 5 complete towers!
        t1.selectWorker(board1, coord(1, 1));
        t1.build(board1, coord(1, 2));
        t1.build(board1, coord(1, 2));
        t1.build(board1, coord(1, 2));
        t1.build(board1, coord(1, 2));
        assertEquals(4, board1.getBox(0, 0).getTower().size());
        assertTrue(t1.isWinner());
    }
    @Test
    public void chronusEffectOtherPlayerBuildsTheLastBlockTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Chronus");

        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t2 = new Turn(p2, Color.RED, "Pan");

        ArrayList<Player> listPlayerLobby = new ArrayList<>();
        listPlayerLobby.add(p1);
        listPlayerLobby.add(p2);

        Model match1 = new Model(listPlayerLobby);
        Board board1 = match1.getGameboard();

        t1.placeWorker(board1, coord(1, 1), "A");
        t2.placeWorker(board1, coord(3, 3), "B");

        //player1 has Chronus
        assertEquals(t1.getDivinityCard().getSpecificGodName(), Global.chronus);

        //case where the player without Chronus as his god builds the fifth tower
        assertFalse(t1.isWinner());

        //p2 completes tower in (4,4)
        t1.selectWorker(board1, coord(3, 3));
        t1.build(board1, coord(4, 4));
        t1.build(board1, coord(4, 4));
        t1.build(board1, coord(4, 4));
        t1.build(board1, coord(4, 4));
        assertEquals(4, board1.getBox(4, 4).getTower().size());
        assertFalse(t1.isWinner());

        //p1 completes tower in (0,0)
        t1.selectWorker(board1, coord(1, 1));
        t1.build(board1, coord(0, 0));
        t1.build(board1, coord(0, 0));
        t1.build(board1, coord(0, 0));
        t1.build(board1, coord(0, 0));
        assertEquals(4, board1.getBox(0, 0).getTower().size());
        assertFalse(t1.isWinner());

        //p2 completes tower in (4,3)
        t1.selectWorker(board1, coord(3, 3));
        t1.build(board1, coord(4, 3));
        t1.build(board1, coord(4, 3));
        t1.build(board1, coord(4, 3));
        t1.build(board1, coord(4, 3));
        assertEquals(4, board1.getBox(4, 3).getTower().size());
        assertFalse(t1.isWinner());

        //p1 completes tower in (0,2)
        t1.selectWorker(board1, coord(1, 1));
        t1.build(board1, coord(0, 2));
        t1.build(board1, coord(0, 2));
        t1.build(board1, coord(0, 2));
        t1.build(board1, coord(0, 2));
        assertEquals(4, board1.getBox(0, 2).getTower().size());
        assertFalse(t1.isWinner());

        //p2 completes tower in (2,2): there are 5 complete towers!
        t1.selectWorker(board1, coord(3, 3));
        t1.build(board1, coord(2, 2));
        t1.build(board1, coord(2, 2));
        t1.build(board1, coord(2, 2));
        t1.build(board1, coord(2, 2));
        assertEquals(4, board1.getBox(2, 2).getTower().size());
        assertTrue(t1.isWinner());
    }*/
}