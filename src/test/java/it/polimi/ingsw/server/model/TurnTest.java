package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.model.Worker;
import it.polimi.ingsw.server.view.playerMove;
import org.junit.Test;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

public class TurnTest {

    Board board = Board.instance();

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
    public void checkIfCanMoveTest() {
    }

    @Test
    public void checkIfCanBuildTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA = new Worker( player1, "Y", "A" );
        //Worker workerB = new Worker( player1, "Y", "B" );
        Turn turn = new Turn (player1, "Y", "pippo");
        player1.setPersonalTurn(turn);

        board.placeWorker(workerA, 0, 2);

        player1.getPersonalTurn().selectWorker(board, new playerMove(0, 2, player1));
        player1.getPersonalTurn().basicMove(board, new playerMove(1, 2, player1));
        //now workerA is in (1,2)

        //every box has level 0
        //workerA can build
        assertTrue(turn.checkIfCanMove(board));

        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++)  {
                board.increaseLevel(i, j);
                board.increaseLevel(i, j);
                board.increaseLevel(i, j);
                board.increaseLevel(i, j);
            }
        }
        assertFalse(turn.checkIfCanBuild(board));

        board.decreaseLevel(1, 2);
        //now the only box which has not level 4 is the one with workerA
        //he still can't build
        assertFalse(turn.checkIfCanBuild(board));

        board.decreaseLevel(1,4);
        //now there is a box with level < 4 but is away from workerA
        //he still can't build
        assertFalse(turn.checkIfCanBuild(board));

        board.decreaseLevel(2,2 );
        //now there is a box with level < 4 adjacent to workerA
        //he can build!
        assertTrue(turn.checkIfCanBuild(board));

        clearBoardForFutureTests(board);
    }

    @Test
    public void basicMoveTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA = new Worker( player1, "Y", "A" );
        Worker workerB = new Worker( player1, "Y", "B" );
        Turn turn = new Turn (player1, "Y", "pippo");
        player1.setPersonalTurn(turn);

        board.placeWorker(workerA, 0, 2);
        board.placeWorker(workerB, 0, 1);

        player1.getPersonalTurn().selectWorker(board, new playerMove(0, 2, player1));
        //now workerA is in (0,2)

        //testing an away box [(4,4)]
        assertFalse(player1.getPersonalTurn().basicMove(board, new playerMove(4, 4, player1)));
        //testing an occupied box (in (0,1) there is workerB) [(0,1)]
        assertFalse(player1.getPersonalTurn().basicMove(board, new playerMove(0, 1, player1)));
        //testing a full box (tower level 4) [(0,3)]
        board.increaseLevel(0,3);
        board.increaseLevel(0,3);
        board.increaseLevel(0,3);
        board.increaseLevel(0,3);
        assertFalse(player1.getPersonalTurn().basicMove(board, new playerMove(0, 3, player1)));
        //testing a box with level < 4 but too high for our worker at level 0 [(1,1)]
        board.increaseLevel(1,1);
        board.increaseLevel(1,1);
        assertFalse(player1.getPersonalTurn().basicMove(board, new playerMove(1, 1, player1)));

        //the only boxes where workerA can be moved are (1,2) and (1,3)
        assertTrue(player1.getPersonalTurn().basicMove(board, new playerMove(1, 2, player1)));
        player1.getPersonalTurn().basicMove(board,new playerMove(0,2, player1));
        assertTrue(player1.getPersonalTurn().basicMove(board, new playerMove(1, 3, player1)));
        player1.getPersonalTurn().basicMove(board,new playerMove(0,2, player1));

        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if((i==1 && j==2) || (i==1 && j==3)) {
                    assertTrue(player1.getPersonalTurn().basicMove(board, new playerMove( i, j, player1)));
                    player1.getPersonalTurn().basicMove(board, new playerMove(0, 2, player1));
                }
                else
                    assertFalse(player1.getPersonalTurn().basicMove(board, new playerMove(i, j, player1)));
            }
        }
        //I decide to move him to (1,2)
        player1.getPersonalTurn().basicMove(board, new playerMove(1, 2, player1));
        //(0,2) will have a null occupier
        assertNull(board.getBox(0,2).getOccupier());
        //(1,2) will have workerA as its occupier
        assertEquals(board.getBox(1,2).getOccupier(), workerA);
        //new coordinates of workerA will be (1,2)

        for(int i=0; i<5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == 1 && j == 2) {
                    assertTrue( turn.getCurrentRow() == board.getBox(i, j).getRow() && turn.getCurrentColumn() == board.getBox(i, j).getColumn() );
                } else {
                    assertFalse( turn.getCurrentRow() == board.getBox(i, j).getRow() && turn.getCurrentColumn() == board.getBox(i, j).getColumn() );
                }
            }
        }

        //testing win case

        board.increaseLevel(1,2);
        board.increaseLevel(1,2);
        board.increaseLevel(1,2);
        //(1,2) has level 3
        board.increaseLevel(1,3);
        board.increaseLevel(1,3);
        board.increaseLevel(1,3);
        //(1,3) has level 3

        board.setAllowedToScale(true);

        //firstly, testing moving to level 3 from an other level 3

        //before moving, winner is false
        assertFalse(turn.isWinner());
        player1.getPersonalTurn().selectWorker(board, new playerMove(1, 2, player1));
        player1.getPersonalTurn().basicMove(board, new playerMove(1, 3, player1));
        //after moving, winner is still false
        assertFalse(turn.isWinner());

        //then, testing moving to level 3 from a level 2

        board.increaseLevel(2,2);
        board.increaseLevel(2,2);
        //(2,2) has level 2

        //before moving, winner is false
        assertFalse(turn.isWinner());
        player1.getPersonalTurn().selectWorker(board, new playerMove(1, 3, player1));
        player1.getPersonalTurn().basicMove(board, new playerMove(2, 2, player1));
        player1.getPersonalTurn().basicMove(board, new playerMove(1, 2, player1));
        //after moving, winner is true
        assertTrue(turn.isWinner());

        clearBoardForFutureTests(board);
    }
}