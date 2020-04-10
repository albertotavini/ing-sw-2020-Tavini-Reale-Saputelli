package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.view.playerMove;
import org.junit.jupiter.api.Test;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class GenericGodTest {
    //metodo di supporto che ti crea la playermove
    public  static playerMove gimme(int row, int column) throws DataFormatException {
        Player p1 = new Player("Peppino", 01,12, 2000);
        return new playerMove(row, column, p1);
    }
    //I have to clear the board for future tests, because board is an instance
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
        Turn t1 = new Turn (p1, "G", "minotaur");
        Turn t2 = new Turn (p2, "R", "pan");
        Board board = Board.instance();

        //cases of horizontal successful usage
        if (true) {
            t1.placeWorker(board, gimme(2, 2), "B");
            t2.placeWorker(board, gimme(2, 3), "A");
            t1.selectWorker(board, gimme(2, 2));
            t1.move(board, gimme(2, 3));
            assertTrue(board.getBox(2, 3).getOccupier().getColour() == "G");
            assertTrue(board.getBox(2, 4).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, gimme(2, 3), "B");
            t2.placeWorker(board, gimme(2, 2), "A");
            t1.selectWorker(board, gimme(2, 3));
            t1.move(board, gimme(2, 2));
            assertTrue(board.getBox(2, 2).getOccupier().getColour() == "G");
            assertTrue(board.getBox(2, 1).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

        }

        //cases of vertical successful usage
        if (true) {
            t1.placeWorker(board, gimme(1, 3), "B");
            t2.placeWorker(board, gimme(2, 3), "A");
            t1.selectWorker(board, gimme(1, 3));
            t1.move(board, gimme(2, 3));
            assertTrue(board.getBox(2, 3).getOccupier().getColour() == "G");
            assertTrue(board.getBox(3, 3).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, gimme(2, 3), "B");
            t2.placeWorker(board, gimme(1, 3), "A");
            t1.selectWorker(board, gimme(2, 3));
            t1.move(board, gimme(1, 3));
            assertTrue(board.getBox(1, 3).getOccupier().getColour() == "G");
            assertTrue(board.getBox(0, 3).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);
        }

        //cases of diagonal successful usage
        if (true) {
            t1.placeWorker(board, gimme(3, 3), "B");
            t2.placeWorker(board, gimme(2, 2), "A");
            t1.selectWorker(board, gimme(3, 3));
            t1.move(board, gimme(2, 2));
            assertTrue(board.getBox(2, 2).getOccupier().getColour() == "G");
            assertTrue(board.getBox(1, 1).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, gimme(2, 2), "B");
            t2.placeWorker(board, gimme(3, 3), "A");
            t1.selectWorker(board, gimme(2, 2));
            t1.move(board, gimme(3, 3));
            assertTrue(board.getBox(3, 3).getOccupier().getColour() == "G");
            assertTrue(board.getBox(4, 4).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, gimme(2, 3), "B");
            t2.placeWorker(board, gimme(3, 2), "A");
            t1.selectWorker(board, gimme(2, 3));
            t1.move(board, gimme(3, 2));
            assertTrue(board.getBox(3, 2).getOccupier().getColour() == "G");
            assertTrue(board.getBox(4, 1).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, gimme(4, 0), "B");
            t2.placeWorker(board, gimme(3, 1), "A");
            t1.selectWorker(board, gimme(4, 0));
            t1.move(board, gimme(3, 1));
            assertTrue(board.getBox(3, 1).getOccupier().getColour() == "G");
            assertTrue(board.getBox(2, 2).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);
        }

        //cases with other workers or domes preventing the effect
        if (true){
            t1.placeWorker(board, gimme(2, 3), "B");
            t2.placeWorker(board, gimme(2, 2), "A");
            t1.selectWorker(board, gimme(2, 3));
            board.getBox(2,1).increaseLevel();
            board.getBox(2,1).increaseLevel();
            board.getBox(2,1).increaseLevel();
            board.getBox(2,1).increaseLevel();
            t1.move(board, gimme(2, 2));
            assertTrue(board.getBox(2, 3).getOccupier().getColour() == "G");
            assertTrue(board.getBox(2, 2).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, gimme(2, 3), "B");
            t2.placeWorker(board, gimme(2, 2), "A");
            t2.placeWorker(board, gimme(2, 1), "B");
            t1.selectWorker(board, gimme(2, 3));
            t1.move(board, gimme(2, 2));
            assertTrue(board.getBox(2, 3).getOccupier().getColour() == "G");
            assertTrue(board.getBox(2, 2).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);
        }

        //cases where the opponent worker would be sent out of the board
        if (true) {
            t1.placeWorker(board, gimme(1, 1), "B");
            t2.placeWorker(board, gimme(0, 1), "A");
            t1.selectWorker(board, gimme(1, 1));
            t1.move(board, gimme(0, 1));
            assertTrue(board.getBox(1, 1).getOccupier().getColour() == "G");
            assertTrue(board.getBox(0, 1).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

            t1.placeWorker(board, gimme(1, 1), "B");
            t2.placeWorker(board, gimme(0, 0), "A");
            board.drawBoard();
            t1.selectWorker(board, gimme(1, 1));
            t1.move(board, gimme(0, 0));
            board.drawBoard();
            assertTrue(board.getBox(1, 1).getOccupier().getColour() == "G");
            assertTrue(board.getBox(0, 0).getOccupier().getColour() == "R");
            clearBoardForFutureTests(board);

        }



    }
}