package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.playerMove;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class GenericGodTest {
    //support methods to build playermoves, they're built the same way in the view
    public  static playerMove coord(int row, int column) throws DataFormatException {
        Player p1 = new Player("Peppino", 01,12, 2000);
        playerMove playermove = new playerMove(row, column, p1);
        playermove.setGenericMessage("nothing interesting here");
        return playermove;
    }
    public static playerMove mess(String s) throws DataFormatException{
        Player p1 = new Player("Peppino", 01,12, 2000);
        playerMove playermove =new playerMove(7,7, p1);
        playermove.setGenericMessage(s);
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
        Player p1 = new Player("Peppino", 01,12, 2000);
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
            assertTrue(board.getBox(2, 3).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(2, 4).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(2, 2), "A");
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(2, 2));
            assertTrue(board.getBox(2, 2).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(2, 1).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);

        }

        //cases of vertical successful usage
        if (true) {
            t1.placeWorker(board, coord(1, 3), "B");
            t2.placeWorker(board, coord(2, 3), "A");
            t1.selectWorker(board, coord(1, 3));
            t1.move(board, coord(2, 3));
            assertTrue(board.getBox(2, 3).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(3, 3).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(1, 3), "A");
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(1, 3));
            assertTrue(board.getBox(1, 3).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(0, 3).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);
        }

        //cases of diagonal successful usage
        if (true) {
            t1.placeWorker(board, coord(3, 3), "B");
            t2.placeWorker(board, coord(2, 2), "A");
            t1.selectWorker(board, coord(3, 3));
            t1.move(board, coord(2, 2));
            assertTrue(board.getBox(2, 2).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(1, 1).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 2), "B");
            t2.placeWorker(board, coord(3, 3), "A");
            t1.selectWorker(board, coord(2, 2));
            t1.move(board, coord(3, 3));
            assertTrue(board.getBox(3, 3).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(4, 4).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(3, 2), "A");
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(3, 2));
            assertTrue(board.getBox(3, 2).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(4, 1).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(4, 0), "B");
            t2.placeWorker(board, coord(3, 1), "A");
            t1.selectWorker(board, coord(4, 0));
            t1.move(board, coord(3, 1));
            assertTrue(board.getBox(3, 1).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(2, 2).getOccupier().getColour().abbrev() == "R");
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
            assertTrue(board.getBox(2, 3).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(2, 2).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(2, 3), "B");
            t2.placeWorker(board, coord(2, 2), "A");
            t2.placeWorker(board, coord(2, 1), "B");
            t1.selectWorker(board, coord(2, 3));
            t1.move(board, coord(2, 2));
            assertTrue(board.getBox(2, 3).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(2, 2).getOccupier().getColour().abbrev() == "R");
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
            assertTrue(board.getBox(1, 1).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(0, 1).getOccupier().getColour().abbrev() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, coord(1, 1), "B");
            t2.placeWorker(board, coord(0, 0), "A");
            board.drawBoard();
            t1.selectWorker(board, coord(1, 1));
            t1.move(board, coord(0, 0));
            board.drawBoard();
            assertTrue(board.getBox(1, 1).getOccupier().getColour().abbrev() == "G");
            assertTrue(board.getBox(0, 0).getOccupier().getColour().abbrev() == "R");
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
            assertTrue(board.getBox(2,3).getOccupier().getColour() == Color.GREEN);
            assertTrue(board.getBox(1,2).getOccupier().getColour() == Color.RED);
            t1.selectWorker(board, coord(2,3));
            t1.move(board, coord(1,2));
            t1.build(board, coord(0,2));
            assertTrue(board.getBox(2,3).getOccupier().getColour() == Color.RED);
            assertTrue(board.getBox(1,2).getOccupier().getColour() == Color.GREEN);
            //as expected, the workers switched positions
            board.drawBoard();
            t2.selectWorker(board, coord(2,3));
            t2.move(board, coord(2,4));
            t2.build(board, coord(1,4));
            board.drawBoard();
            t1.selectWorker(board, coord(1,2));
            t1.move(board, coord(2,3));
            //now the player with apollo simply moved in an empty space and left an empty space behind
            assertTrue(board.getBox(2,3).getOccupier().getColour() == Color.GREEN);
            assertTrue(board.getBox(1,2).getOccupier() == null);
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
            assertTrue(board.getBox(2,3).getOccupier().getColour() == Color.RED);
            assertTrue(board.getBox(2,4).getOccupier().getColour() == Color.GREEN);
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
            assertTrue(board.getBox(1,1).getOccupier().getColour() == Color.GREEN);
            assertTrue(board.getBox(1,2).getOccupier().getColour() == Color.RED);
            t1.move(board, coord(1,2));
            assertTrue(board.getBox(1,1).getOccupier().getColour() == Color.RED);
            assertTrue(board.getBox(1,2).getOccupier().getColour() == Color.GREEN);
            board.drawBoard();
        }

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
            assertFalse(t1.move(board, mess(" zoo marine")));
            assertTrue(GodLookUpTable.getGodState().equals(GodStateOne.getInstance()));
            //sends some coordinates
            assertFalse(t1.move(board, coord(2,2)));
            assertTrue(GodLookUpTable.getGodState().equals(GodStateOne.getInstance()));
        }


        //cases where the effect is activated or not
        if (true) {
            t1.selectWorker(board, coord(2, 3));
            assertTrue(GodLookUpTable.getGodState().equals(GodStateOne.getInstance()));
            assertFalse(t1.move(board, mess("yes")));
            assertTrue(GodLookUpTable.getGodState().equals(GodStateTwo.getInstance()));
            assertFalse(t1.move(board, coord(1, 3)));
            assertTrue(GodLookUpTable.getGodState().equals(GodStateThree.getInstance()));
            //when accepted to use the power, the first coordinates given to the move tell where to build prior moving
            assertTrue(board.getBox(1, 3).getTowerSize() == 1);
            assertTrue(board.getBox(1, 3).getOccupier() == null);
            board.drawBoard();
            //then if the player asks to move to where he built, he's denied to, because now he can't move up
            assertFalse(t1.move(board, coord(1, 3)));
            assertTrue(board.getBox(1, 3).getOccupier() == null);
            //now the move will return true after completing successfully the move part of the effect
            assertTrue(t1.move(board, coord(2, 2)));
            assertTrue(GodLookUpTable.getGodState().equals(GodStateOne.getInstance()));
            t1.build(board, coord(2, 1));
            board.drawBoard();
            //now a turn for the second player
            t2.selectWorker(board, coord(3, 4));
            t2.move(board, coord(3, 3));
            t2.build(board, coord(3, 2));
            board.drawBoard();
            t1.selectWorker(board, coord(4,1));
            //when saying no, the player will be able to conclude the move in one step, even going up
            assertFalse(t1.move(board, mess("no")));
            assertTrue(GodLookUpTable.getGodState().equals(GodStateFour.getInstance()));
            assertTrue(t1.move(board, coord(3,2)));
            board.drawBoard();
        }
        clearBoardForFutureTests(board);



    }
}