package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;
import org.junit.jupiter.api.Test;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class GenericGodTest {
    //support methods to build playermoves, they're built the same way in the view
    public static PlayerMove coord(int row, int column) throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        PlayerMove playermove = new PlayerMove(row, column, p1);
        playermove.setGenericMessage("nothing interesting here");
        return playermove;
    }
    public static PlayerMove mess(String s) throws DataFormatException{
        //the playermoves are done like this becayse it's always player 1 to test godpowers
        Player p1 = new Player("Peppino", 1,12, 2000);
        PlayerMove playermove = new PlayerMove(s, p1);
        return playermove;
    }
    public static PlayerMove confirmation(ConfirmationEnum confirmation) throws DataFormatException {
        //the playermoves are done like this becayse it's always player 1 to test godpowers
        Player p1 = new Player("Peppino", 1,12, 2000);
        PlayerMove playermove = new PlayerMove(confirmation, p1);
        return playermove;
    }


    //support method to clear the board
    public void clearBoardForFutureTests (Board board){
        for (int i=0; i<5; i++) {
            for (int j= 0; j < 5; j++) {

                //setting every box's level at 0
                while (board.getBox(i, j).getTowerSize() != 0)
                    board.getBox(i, j).decreaseLevel();

                //removing every player
                if (board.getBox(i, j).getOccupier() != null)
                    board.getBox(i, j).setOccupier(null);

            }
        }
    }

    @Test
    void activateEffectMinotaurTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = Board.instance();

        //cases of horizontal successful usage
        if (true) {
            t1.placeWorker(board, coord(2, 2), "B");
            t2.placeWorker(board, coord(2, 3), "A");
            t1.selectWorker(board, coord(2, 2));
            t1.move(board, coord(2, 3));
            assertEquals(board.getBox(2, 3).getOccupier().getColour().abbrev(),"G");
            assertEquals(board.getBox(2, 4).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(2, 2), "A");
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(2, 2));
            assertEquals(board.getBox(2, 2).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(2, 1).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

        }

        //cases of vertical successful usage
        if (true) {
            t1.placeWorker(board, coord(1, 3), "B");
            t2.placeWorker(board, coord(2, 3), "A");
            t1.selectWorker(board, coord(1, 3));
            t1.move(board, coord(2, 3));
            assertEquals(board.getBox(2, 3).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(3, 3).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(1, 3), "A");
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(1, 3));
            assertEquals(board.getBox(1, 3).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(0, 3).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);
        }

        //cases of diagonal successful usage
        if (true) {
            t1.placeWorker(board, coord(3, 3), "B");
            t2.placeWorker(board, coord(2, 2), "A");
            t1.selectWorker(board, coord(3, 3));
            t1.move(board, coord(2, 2));
            assertEquals(board.getBox(2, 2).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(1, 1).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 2), "B");
            t2.placeWorker(board, coord(3, 3), "A");
            t1.selectWorker(board, coord(2, 2));
            t1.move(board, coord(3, 3));
            assertEquals(board.getBox(3, 3).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(4, 4).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(3, 2), "A");
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(3, 2));
            assertEquals(board.getBox(3, 2).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(4, 1).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(4, 0), "B");
            t2.placeWorker(board, coord(3, 1), "A");
            t1.selectWorker(board, coord(4, 0));
            t1.move(board, coord(3, 1));
            assertEquals(board.getBox(3, 1).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(2, 2).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);
        }

        //cases with other workers or domes preventing the effect
        if (true){
            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(2, 2), "A");
            t1.selectWorker(board, coord(2, 3));
            board.getBox(2,1).increaseLevel();
            board.getBox(2,1).increaseLevel();
            board.getBox(2,1).increaseLevel();
            board.getBox(2,1).increaseLevel();
            t1.move(board, coord(2, 2));
            assertEquals(board.getBox(2, 3).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(2, 2).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(2, 2), "A");
            t2.placeWorker(board, coord(2, 1), "B");
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(2, 2));
            assertEquals(board.getBox(2, 3).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(2, 2).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);
        }

        //cases where the opponent worker would be sent out of the board
        //there is no need to test the whole border, because if the calculation of r3, c3 is always correct (as the SUCCESSFUL part of the test shows)
        //there's no reason the InBoundaries method will fail to test if other variations of r3,c3 will cause problems
        if (true) {
            t1.placeWorker(board, coord(1, 1), "B");
            t2.placeWorker(board, coord(0, 1), "A");
            t1.selectWorker(board, coord(1, 1));
            t1.move(board, coord(0, 1));
            assertEquals(board.getBox(1, 1).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(0, 1).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(1, 1), "B");
            t2.placeWorker(board, coord(0, 0), "A");
            board.drawBoard();
            t1.selectWorker(board, coord(1, 1));
            t1.move(board, coord(0, 0));
            board.drawBoard();
            assertEquals(board.getBox(1, 1).getOccupier().getColour().abbrev(), "G");
            assertEquals(board.getBox(0, 0).getOccupier().getColour().abbrev(), "R");
            clearBoardForFutureTests(board);

        }


        //case where it is denied to send back one of your workers
        if (true) {
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

    }

    @Test
    void activatePanEffectTest() throws  DataFormatException {
        Player p1 = new Player("Peppino", 01,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "minotaur");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = Board.instance();

        //considered that the effect of pan is a basicMove with another winning condition, i just verify that this part works

        //case from 2 to 0
        if (true) {
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

        //case from 3 to 0
        if (true) {
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
            t2.move(board , coord(2,1));
            assertTrue(t2.isWinner());
            clearBoardForFutureTests(board);
            t2.setWinner(false);

        }

        //case from 3 to 1
        if (true) {
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
        }

        clearBoardForFutureTests(board);
    }

    @Test
    void activateAthenaEffectTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 01,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "athena");
        Turn t2 = new Turn (p2, Color.RED, "minotaur");
        Board board = Board.instance();

        //case from 0 to 0, from 0 to 1, from 1 to 1, from 1 to 2
        if (true) {
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
            t1.build(board, coord(2,0));
            t2.selectWorker(board, coord(1,2));
            //the player with athena moved from level 1 to level 2, the other player cannot move from 2 to 3
            assertFalse(t2.move(board, coord(2,2)));
            assertTrue(t2.move(board, coord(0,2)));
            t2.build(board, coord(1,2));
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


    }

    @Test
    void activateApolloEffectTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 01,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "apollo");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = Board.instance();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(3,2),  "B");
        t2.placeWorker(board, coord(1,2), "A");
        t2.placeWorker(board, coord(3,4), "B");

        if (false) {
            board.drawBoard();
            assertEquals(board.getBox(2,3).getOccupier().getColour(), Color.GREEN);
            assertEquals(board.getBox(1,2).getOccupier().getColour(), Color.RED);
            t1.selectWorker(board, coord(2,3));
            t1.move(board, coord(1,2));
            t1.build(board, coord(0,2));
            assertEquals(board.getBox(2,3).getOccupier().getColour(), Color.RED);
            assertEquals(board.getBox(1,2).getOccupier().getColour(), Color.GREEN);
            //as expected, the workers switched positions
            board.drawBoard();
            t2.selectWorker(board, coord(2,3));
            t2.move(board, coord(2,4));
            t2.build(board, coord(1,4));
            board.drawBoard();
            t1.selectWorker(board, coord(1,2));
            t1.move(board, coord(2,3));
            //now the player with apollo simply moved in an empty space and left an empty space behind
            assertEquals(board.getBox(2,3).getOccupier().getColour(), Color.GREEN);
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
            assertEquals(board.getBox(2,3).getOccupier().getColour(), Color.RED);
            assertEquals(board.getBox(2,4).getOccupier().getColour(), Color.GREEN);
            t1.build(board, coord(1,4));
            board.drawBoard();
            clearBoardForFutureTests(board);

        }

        //cases from level which the opponent's worker would not normally be able to reach
        if (true) {
            board.getBox(1,1).increaseLevel();
            board.getBox(1,1).increaseLevel();
            board.getBox(1,1).increaseLevel();
            board.moveWorker(2,3,1,1);
            board.drawBoard();
            t1.selectWorker(board, coord(1,1));
            assertEquals(board.getBox(1,1).getOccupier().getColour(), Color.GREEN);
            assertEquals(board.getBox(1,2).getOccupier().getColour(), Color.RED);
            t1.move(board, coord(1,2));
            assertEquals(board.getBox(1,1).getOccupier().getColour(), Color.RED);
            assertEquals(board.getBox(1,2).getOccupier().getColour(), Color.GREEN);
            board.drawBoard();
        }
        clearBoardForFutureTests(board);

    }

    @Test
    void activatePrometheusEffectTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 01,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "prometheus");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = Board.instance();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        //t2.placeWorker(board, coord(1,2), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();

        //case where the input confirmation message is wrong or the player sends coordinates
        if (true) {
            t1.selectWorker(board, coord (2,3));
            //sends something different from yes/no
            assertFalse(t1.move(board, confirmation(ConfirmationEnum.NotDef)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            //sends some coordinates
            assertFalse(t1.move(board, coord(2,2)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
        }


        //cases where the effect is activated or not
        if (true) {
            t1.selectWorker(board, coord(2, 3));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            assertFalse(t1.move(board, confirmation(ConfirmationEnum.Yes)));
            assertEquals(GodLookUpTable.getGodState(), GodStateTwo.getInstance());
            assertFalse(t1.move(board, coord(1, 3)));
            assertEquals(GodLookUpTable.getGodState(), GodStateThree.getInstance());
            //when accepted to use the power, the first coordinates given to the move tell where to build prior moving
            assertEquals(board.getBox(1, 3).getTowerSize(), 1);
            assertNull(board.getBox(1, 3).getOccupier());
            board.drawBoard();
            //then if the player asks to move to where he built, he's denied to, because now he can't move up
            assertFalse(t1.move(board, coord(1, 3)));
            assertNull(board.getBox(1, 3).getOccupier());
            //now the move will return true after completing successfully the move part of the effect
            assertTrue(t1.move(board, coord(2, 2)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
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
            assertEquals(GodLookUpTable.getGodState(), GodStateFour.getInstance());
            assertTrue(t1.move(board, coord(3,2)));
            board.drawBoard();
        }
        clearBoardForFutureTests(board);



    }

    @Test
    void activateArtemisEffectTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 01,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "artemis");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = Board.instance();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        //t2.placeWorker(board, coord(1,2), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();

        //case where the input confirmation message is wrong or the player sends coordinates
        if (true) {
            t1.selectWorker(board, coord (2,3));
            //sends something different from yes/no
            assertFalse(t1.move(board, confirmation(ConfirmationEnum.NotDef)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            //sends some coordinates
            assertFalse(t1.move(board, coord(2,2)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
        }

        //cases where the player chooses to use or not use the effect
        if (true) {
            t1.selectWorker(board, coord(2,3));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.move(board, confirmation(ConfirmationEnum.Yes));
            assertEquals(GodLookUpTable.getGodState(), GodStateTwo.getInstance());
            t1.move(board, coord(2,2));
            assertEquals(GodLookUpTable.getGodState(), GodStateThree.getInstance());
            board.drawBoard();
            //if i try to go back to the old position the move does not conclude and i'm still in the second
            assertFalse(t1.move(board, coord(2,3)));
            assertEquals(GodLookUpTable.getGodState(), GodStateThree.getInstance());
            board.drawBoard();
            assertTrue(t1.move(board, coord(2,1)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, coord(2,0));
            board.drawBoard();
            //a turn for the second player
            t2.selectWorker(board, coord(3,4));
            t2.move(board, coord(4,4));
            t2.build(board, coord(3,4));
            //then a turn where the player with artemis chooses not tu use its effect
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            assertFalse(t1.move(board, confirmation(ConfirmationEnum.No)));
            assertEquals(GodLookUpTable.getGodState(), GodStateFour.getInstance());
            //now the move will be completed with just on act
            assertTrue(t1.move(board, coord(2,0)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, coord(2,1));
            board.drawBoard();

        }
        clearBoardForFutureTests(board);
    }

    @Test
    public void activateAtlasEffectTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        Turn t1 = new Turn (p1, Color.GREEN, "atlas");
        Board board = Board.instance();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");

        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t2 = new Turn (p2, Color.RED, "pan");
        t2.placeWorker(board, coord(0,0), "B");

        //case where the input confirmation message is wrong or the player sends coordinates
        if (true) {
            t1.selectWorker(board, coord (2,3));
            //sends something different from yes/no
            assertFalse(t1.build(board, confirmation(ConfirmationEnum.NotDef)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            //sends some coordinates
            assertFalse(t1.build(board, coord(2,2)));
            //if the message is not yes/no, nothing happens and the state will remain GodStateOne
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
        }

        //cases where the player chooses to use or not use the effect
        if (true) {
            //firstly, a turn where I activate Atlas'effect
            t1.selectWorker(board, coord(2,3));
            t1.move(board,coord(2,2));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, confirmation(ConfirmationEnum.Yes));
            assertEquals(GodLookUpTable.getGodState(), GodStateTwo.getInstance());
            t1.build(board, coord(2,3));
            //there will be a dome where the player built
            assertEquals( board.getBox(2,3).getTowerSize(), 1 );
            assertTrue( board.getBox(2,3).isDomed() );
            //returning to GodStateOne
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());

            //then, a turn for the second player
            t2.selectWorker(board, coord(0,0));
            t2.move(board, coord(0,1));
            t2.build(board, coord(0,0));

            //finally, a turn where I don't activate Atlas'effect
            t1.selectWorker(board, coord(2,2));
            t1.move(board,coord(3,3));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, confirmation(ConfirmationEnum.No));
            assertEquals(GodLookUpTable.getGodState(), GodStateThree.getInstance());
            t1.build(board, coord(4,4));
            assertFalse( board.getBox(4,4).isDomed() );
            assertEquals( board.getBox(4,4).getTowerSize(), 1);
            //returning to GodStateOne
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());

            clearBoardForFutureTests(board);

        }
    }

    @Test
    public void activateHephaestusEffectTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 1, 12, 2000);
        Turn t1 = new Turn(p1, Color.GREEN, "Hephaestus");
        Board board = Board.instance();
        t1.placeWorker(board, coord(2, 3), "A");

        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t2 = new Turn(p2, Color.RED, "pan");
        t2.placeWorker(board, coord(0, 0), "B");

        //case where the input confirmation message is wrong or the player sends coordinates
        if (true) {
            t1.selectWorker(board, coord(2, 3));
            //sends something different from yes/no
            assertFalse(t1.build(board, confirmation(ConfirmationEnum.NotDef)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            //sends some coordinates
            assertFalse(t1.build(board, coord(2, 2)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
        }

        //cases where the player chooses to use or not use the effect
        if (true) {
            //firstly, a turn where I activate Hephaestus' effect
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(2, 2));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, confirmation(ConfirmationEnum.Yes));
            assertEquals(GodLookUpTable.getGodState(), GodStateTwo.getInstance());
            //before building, (1,1) has level 0
            assertEquals(board.getBox(1, 1).getTowerSize(), 0);
            t1.build(board, coord(1, 1));
            //after building, (1,1) has level 2, because of Hephaestus' effect
            assertEquals(board.getBox(1, 1).getTowerSize(), 2);
            //returning to GodStateOne
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());

            //then, a turn for the second player
            t2.selectWorker(board, coord(0, 0));
            t2.move(board, coord(0, 1));
            t2.build(board, coord(0, 0));

            //then, a turn where I don't activate Hephaestus'effect
            t1.selectWorker(board, coord(2, 2));
            t1.move(board, coord(3, 3));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, confirmation(ConfirmationEnum.No));
            assertEquals(GodLookUpTable.getGodState(), GodStateThree.getInstance());
            //before building, (4,4) has level 0
            assertEquals(board.getBox(4, 4).getTowerSize(), 0);
            t1.build(board, coord(4, 4));
            //after building, (4,4) has level 1
            assertEquals(board.getBox(4, 4).getTowerSize(), 1);
            //returning to GodStateOne
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());

            //then, an other turn for the second player
            t2.selectWorker(board, coord(0, 1));
            t2.move(board, coord(0, 0));
            t2.build(board, coord(0, 1));

        }

        //finally, testing Hephaestus'effect on limit cases
        if(true) {
            t1.selectWorker(board, coord(3, 3));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, confirmation(ConfirmationEnum.Yes));

            //case with a single construction
            assertEquals(board.getBox(2, 2).getTowerSize(), 0);
            board.getBox(2, 2).increaseLevel();
            board.getBox(2, 2).increaseLevel();
            //before building, (2,2) has level 2, so I just build once
            assertEquals(board.getBox(2, 2).getTowerSize(), 2);
            t1.build(board, coord(2, 2));
            //after building, (2,2) has level 3 instead of 4, even if I activated the effect, and it's not domed
            assertEquals(board.getBox(2, 2).getTowerSize(), 3);
            assertFalse(board.getBox(2, 2).isDomed());
            //returning to GodStateOne
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());

            //then, an other turn for the second player
            t2.selectWorker(board, coord(0, 0));
            t2.move(board, coord(0, 1));
            t2.build(board, coord(0, 0));

            //case where the box is already at level 3, I don't leave the player to build
            t1.selectWorker(board, coord(3, 3));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, confirmation(ConfirmationEnum.Yes));
            assertEquals(board.getBox(2, 3).getTowerSize(), 0);
            board.getBox(2, 3).increaseLevel();
            board.getBox(2, 3).increaseLevel();
            board.getBox(2, 3).increaseLevel();
            //before building, (2,3) has level 3
            assertEquals(board.getBox(2, 3).getTowerSize(), 3);
            t1.build(board, coord(2, 3));
            //after building, (2,3) has level 3, like before
            assertEquals(board.getBox(2, 3).getTowerSize(), 3);
            assertFalse(board.getBox(2, 3).isDomed());
            //he is still at GodStateTwo, he has still to build then
            assertEquals(GodLookUpTable.getGodState(), GodStateTwo.getInstance());

        }
            clearBoardForFutureTests(board);
    }

    @Test
    void activateDemeterEffectTest() throws DataFormatException {
        Player p1 = new Player("Peppino", 01,12, 2000);
        Player p2 = new Player("Giovanni", 12, 3, 1999);
        Turn t1 = new Turn (p1, Color.GREEN, "demeter");
        Turn t2 = new Turn (p2, Color.RED, "pan");
        Board board = Board.instance();
        t1.placeWorker(board, coord(2,3),  "A");
        t1.placeWorker(board, coord(4,1),  "B");
        t2.placeWorker(board, coord(0,1), "A");
        t2.placeWorker(board, coord(3,4), "B");
        board.drawBoard();

        //cases where the answer to the will to activate the effect isn't given in the right way
        //and after a case where the player chooses not to activate te effect
        if (true) {
            t1.selectWorker(board, coord (2,3));
            t1.move(board, coord(2,2));
            //sends something different from yes/no
            assertFalse(t1.build(board, confirmation(ConfirmationEnum.NotDef)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            //sends some coordinates
            assertFalse(t1.build(board, coord(2,1)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());

            //by saying no it sends to state Four where it simply make a basicbuild then resets godstate and returns true
            t1.build(board, confirmation(ConfirmationEnum.No));
            assertEquals(GodLookUpTable.getGodState(), GodStateFour.getInstance());
            assertTrue(t1.build(board, coord(2,1)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());

        }
        //part where the effect is actually tested
        if (true) {
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
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            t1.build(board, confirmation(ConfirmationEnum.Yes));
            assertEquals(GodLookUpTable.getGodState(), GodStateTwo.getInstance());
            t1.build(board, coord(2,1));
            assertEquals(GodLookUpTable.getGodState(), GodStateThree.getInstance());
            board.drawBoard();

            //now if asked to do the second build on the same sport the build stays in state 3 and returns false
            assertFalse(t1.build(board, coord(2,1)));
            assertEquals(GodLookUpTable.getGodState(), GodStateThree.getInstance());
            board.drawBoard();

            //while if asked to build on a different box, does the build and returns true after setting godState back to one
            assertTrue(t1.build(board, coord(2,2)));
            assertEquals(GodLookUpTable.getGodState(), GodStateOne.getInstance());
            board.drawBoard();

        }
        clearBoardForFutureTests(board);

    }
}