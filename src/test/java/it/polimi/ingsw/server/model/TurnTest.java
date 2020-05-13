package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.god.GodLookUpTable;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;
import org.junit.Test;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

//all methods have been tested, except for NOMVC methods, because they were used with stdin

public class TurnTest {

    private Board board = new Board();

    //I have to clear the board for future tests
    private void clearBoardForFutureTests(Board board){
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

    //support method to build playermoves
    private static PlayerMove coord(int row, int column) throws DataFormatException {
        Player p1 = new Player("Peppino", 1,12, 2000);
        return new PlayerMove(row, column, p1);
    }


    @Test
    public void selectWorkerTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, Color.YELLOW, "A" );
        //Worker workerB1 = new Worker( player1, Color.YELLOW, "B" );
        Turn turn = new Turn (player1,Color.YELLOW, "pippo");

        board.placeWorker(workerA1, 2, 2);

        //box not in boundaries
        assertFalse( turn.selectWorker(board, coord(7, 7)) );
        //away box
        assertFalse( turn.selectWorker(board, coord(4, 4)) );
        //not occupied box
        assertFalse( turn.selectWorker(board, coord(1, 1)));

        Player player2 = new Player("Franco", 10, 10, 2000);
        Worker workerA2 = new Worker( player2, Color.RED, "A" );
        //Worker workerB2 = new Worker( player2, Color.RED, "B" );

        board.placeWorker(workerA2, 3,3);

        //box occupied by a different color worker
        assertFalse( turn.selectWorker(board, coord(3, 3)));

        //real selecting
        assertTrue( turn.selectWorker(board, coord(2, 2)));
        assertEquals( turn.getCurrentRow(), 2);
        assertEquals( turn.getCurrentColumn(), 2);

        clearBoardForFutureTests(board);
    }

    @Test
    public void placeWorkerTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, Color.YELLOW, "A" );
        Worker workerB1 = new Worker( player1, Color.YELLOW, "B" );
        Turn turn = new Turn (player1,Color.YELLOW, "pippo");

        //trying to place outside the board
        assertFalse( turn.placeWorker(board, coord(7,7 ), "A"));

        board.placeWorker(workerB1, 3, 3);
        //trying to place in a box already occupied
        assertFalse( turn.placeWorker(board, coord(3,3 ), "A"));

        //real placing
        assertTrue( turn.placeWorker(board, coord(2, 2), "A") );
        assertEquals( board.getBox(2,2).getOccupier(), workerA1);

        clearBoardForFutureTests(board);
    }


    //testing checkIfCanMove method, blocking the worker with buildings
    @Test
    public void checkIfCanMoveBuildingTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, Color.YELLOW, "A" );
        Worker workerB1 = new Worker( player1, Color.YELLOW, "B" );
        Turn turn = new Turn (player1,Color.YELLOW, "pippo");

        board.placeWorker(workerA1, 1, 1);
        board.placeWorker(workerB1, 4, 4);

        assertTrue(turn.checkIfCanMove(board));

        //increasing to level 1 every box adjacent to workerA
        board.getBox(0,0).increaseLevel();
        board.getBox(0,1).increaseLevel();
        board.getBox(0,2).increaseLevel();

        board.getBox(1,0).increaseLevel();
        board.getBox(1,2).increaseLevel();

        board.getBox(2,0).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(2,2).increaseLevel();

        //increasing to level 1 every box adjacent to workerB
        board.getBox(3,3).increaseLevel();
        board.getBox(3,4).increaseLevel();
        board.getBox(4,3).increaseLevel();

        //now both the workers are not blocked!
        assertTrue(turn.checkIfCanMove(board));


        //increasing to level 2 every box adjacent to workerA
        board.getBox(0,0).increaseLevel();
        board.getBox(0,1).increaseLevel();
        board.getBox(0,2).increaseLevel();

        board.getBox(1,0).increaseLevel();
        board.getBox(1,2).increaseLevel();

        board.getBox(2,0).increaseLevel();
        board.getBox(2,1).increaseLevel();
        board.getBox(2,2).increaseLevel();

        //just workerA is blocked: player can still move
        assertTrue(turn.checkIfCanMove(board));

        //increasing to level 2 every box adjacent to workerB
        board.getBox(3,3).increaseLevel();
        board.getBox(3,4).increaseLevel();
        board.getBox(4,3).increaseLevel();

        //now both the workers are blocked by constructions!
        assertFalse(turn.checkIfCanMove(board));

        clearBoardForFutureTests(board);

    }

    //testing checkIfCanMove method, blocking the worker with other workers too
    @Test
    public void checkIfCanMoveOtherWorkersTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, Color.YELLOW, "A" );
        Worker workerB1 = new Worker( player1, Color.YELLOW, "B" );
        Turn turn = new Turn (player1,Color.YELLOW, "pippo");

        board.placeWorker(workerA1, 0, 0);
        board.placeWorker(workerB1, 4, 4);

        assertTrue(turn.checkIfCanMove(board));

        //now let's try with blocking moves with workers
        Player player2 = new Player("Franco", 10, 10, 2000);
        Worker workerA2 = new Worker( player2, Color.RED, "A" );
        Worker workerB2 = new Worker( player2, Color.RED, "B" );

        //blocking workerA1

        //placing a worker on (1,1)
        board.placeWorker(workerA2, 1,1);
        assertTrue(turn.checkIfCanMove(board));

        board.getBox(0,1).increaseLevel();
        board.getBox(0,1).increaseLevel();
        //now (0,1) has level 2
        board.getBox(1,0).increaseLevel();
        board.getBox(1,0).increaseLevel();
        //now (1,0) has level 2: workerA1 is blocked!
        //but not workerA2: player1 still can move
        assertTrue(turn.checkIfCanMove(board));

        //blocking workerA2

        //placing a worker on (3,3)
        board.placeWorker(workerB2, 3,3 );
        assertTrue(turn.checkIfCanMove(board));

        board.getBox(4,3).increaseLevel();
        board.getBox(4,3).increaseLevel();
        //now (3,4) has level 2
        board.getBox(3,4).increaseLevel();
        board.getBox(3,4).increaseLevel();
        //now (3,4) has level 2: workerA2 is blocked too!
        //now player1 can't move anymore!
        assertFalse(turn.checkIfCanMove(board));

        clearBoardForFutureTests(board);
    }


    @Test
    public void checkIfCanBuildTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA = new Worker( player1, Color.YELLOW, "A" );
        Turn turn = new Turn (player1, Color.YELLOW, "pippo");

        board.placeWorker(workerA, 0, 2);

        turn.selectWorker(board, new PlayerMove(0, 2, player1));
        turn.basicMove(board, new PlayerMove(1, 2, player1));
        //now workerA is in (1,2)

        //every box has level 0
        //workerA can build
        assertTrue(turn.checkIfCanBuild(board));

        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++)  {
                board.getBox(i, j).increaseLevel();
                board.getBox(i, j).increaseLevel();
                board.getBox(i, j).increaseLevel();
                board.getBox(i, j).increaseLevel();
            }
        }
        assertFalse(turn.checkIfCanBuild(board));

        board.getBox(1, 2).decreaseLevel();
        //now the only box which has not level 4 is the one with workerA
        //he still can't build
        assertFalse(turn.checkIfCanBuild(board));

        board.getBox(1,4).decreaseLevel();
        //now there is a box with level < 4 but is away from workerA
        //he still can't build
        assertFalse(turn.checkIfCanBuild(board));

        board.getBox(2,2 ).decreaseLevel();
        //now there is a box with level < 4 adjacent to workerA
        //he can build!
        assertTrue(turn.checkIfCanBuild(board));
        board.drawBoard();

        clearBoardForFutureTests(board);
    }

    //testing basicMove method for standard cases
    @Test
    public void basicMoveStandardTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA = new Worker( player1, Color.YELLOW, "A" );
        Worker workerB = new Worker( player1, Color.YELLOW, "B" );
        Turn turn = new Turn (player1, Color.YELLOW, "pippo");

        board.placeWorker(workerA, 0, 2);
        board.placeWorker(workerB, 0, 1);

        turn.selectWorker(board, coord(0,2 ));
        //workerA is in (0,2)

        //testing an away box [(4,4)]
        assertFalse(turn.basicMove(board, coord(4,4)));
        //testing an occupied box (in (0,1) there is workerB) [(0,1)]
        assertFalse(turn.basicMove(board, coord(0,1)));
        //testing a full box (tower level 4) [(0,3)]
        board.getBox(0,3).increaseLevel();
        board.getBox(0,3).increaseLevel();
        board.getBox(0,3).increaseLevel();
        board.getBox(0,3).increaseLevel();
        assertFalse(turn.basicMove(board, coord(0,3)));
        //testing a box with level < 4 but too high for our worker at level 0 [(1,1)]
        board.getBox(1,1).increaseLevel();
        board.getBox(1,1).increaseLevel();
        assertFalse(turn.basicMove(board, coord(1,1)));

        //the only boxes where workerA can be moved are (1,2) and (1,3)
        assertTrue(turn.basicMove(board, coord(1,2)));
        turn.basicMove(board,coord(0,2));
        turn.basicMove(board, coord(1,3));
        turn.basicMove(board,coord(0,2));

        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if((i==1 && j==2) || (i==1 && j==3)) {
                    assertTrue(turn.basicMove(board,coord(i,j)));
                    turn.basicMove(board, coord(0,2));
                }
                else
                    assertFalse(turn.basicMove(board, coord(i,j)));
            }
        }
        //I decide to move him to (1,2)
        turn.basicMove(board, coord(1,2));
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

        clearBoardForFutureTests(board);

    }

    //testing basicMove method for winning cases
    @Test
    public void basicMoveWinningCaseTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA = new Worker( player1, Color.YELLOW, "A" );
        Turn turn = new Turn (player1, Color.YELLOW, "pippo");

        board.placeWorker(workerA, 1,2);

        board.getBox(1,2).increaseLevel();
        board.getBox(1,2).increaseLevel();
        board.getBox(1,2).increaseLevel();
        //(1,2) has level 3
        board.getBox(1,3).increaseLevel();
        board.getBox(1,3).increaseLevel();
        board.getBox(1,3).increaseLevel();
        //(1,3) has level 3

        board.setAllowedToScale(true);

        //firstly, testing moving to level 3 from an other level 3

        //before moving, winner is false
        assertFalse(turn.isWinner());
        turn.selectWorker(board, coord(1,2));
        turn.basicMove(board, coord(1,3));
        //after moving, winner is still false
        assertFalse(turn.isWinner());

        //then, testing moving to level 3 from a level 2

        board.getBox(2,2).increaseLevel();
        board.getBox(2,2).increaseLevel();
        //(2,2) has level 2

        board.drawBoard();
        //before moving, winner is false
        assertFalse(turn.isWinner());
        turn.selectWorker(board, coord(1,3));
        turn.basicMove(board, coord(2,2));
        turn.basicMove(board, coord(1,2));
        //after moving, winner is true
        board.drawBoard();
        assertTrue(turn.isWinner());
        board.drawBoard();

        clearBoardForFutureTests(board);
    }

    @Test
    public void moveTest() throws DataFormatException {

        //player1 has "minotaur", from move_list, as his God
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, Color.YELLOW, "A" );
        Turn turnPlayer1 = new Turn (player1, Color.YELLOW, "minotaur");

        board.placeWorker(workerA1, 2, 2);
        turnPlayer1.selectWorker(board, coord(2,2));
        //Minotaur is in "move_list": player1 will activate his effect!
        assertTrue( GodLookUpTable.isEffectMove(turnPlayer1.getDivinityCard().getSpecificGodName()) );
        assertTrue( turnPlayer1.move(board, coord(2,3)) );

        //player2 has "atlas", not from move_list but from build_list, as his God
        Player player2 = new Player("Franco", 10, 10, 2000);
        Worker workerA2 = new Worker( player2, Color.RED, "A" );
        Turn turnPlayer2 = new Turn (player2, Color.RED, "atlas");

        board.placeWorker(workerA2, 4, 4);
        turnPlayer2.selectWorker(board, coord(4,4));
        //Atlas isn't in "move_list": player2 will just activate the basicMove!
        assertFalse( GodLookUpTable.isEffectMove(turnPlayer2.getDivinityCard().getSpecificGodName()) );
        assertTrue( turnPlayer2.move(board, coord(3,4)) );

        clearBoardForFutureTests(board);
    }

    @Test
    public void basicBuildTest() throws DataFormatException {

        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA = new Worker( player1, Color.YELLOW, "A" );
        Worker workerB = new Worker( player1, Color.YELLOW, "B" );
        Turn turn = new Turn (player1, Color.YELLOW, "pippo");

        board.placeWorker(workerA, 0, 2);
        board.placeWorker(workerB, 0, 1);

        turn.selectWorker(board, coord(0,2 ));
        //workerA is in (0,2)

        //testing an away box [(4,4)]
        turn.basicBuild(board, coord(4,4));
        //testing an occupied box (in (0,1) there is workerB) [(0,1)]
        turn.basicBuild(board, coord(0,1));
        //testing a full box (tower level 4) [(0,3)]
        board.getBox(0,3).increaseLevel();
        board.getBox(0,3).increaseLevel();
        board.getBox(0,3).increaseLevel();
        board.getBox(0,3).increaseLevel();
        assertFalse(turn.basicBuild(board, coord(0,3)));

        //workerA could just build in (1,1), (1,2), (1,3)
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if((i==1 && j==1) || (i==1 && j==2) || (i==1 && j==3)) {
                    assertTrue( turn.basicBuild(board,coord(i,j)) );
                    //the build increments the tower level
                    assertEquals( board.getBox(i,j).getTower().size(), 1);
                }
                else{
                    assertFalse( turn.basicBuild(board, coord(i,j)) );
                }
            }
        }

        clearBoardForFutureTests(board);
    }

    @Test
    public void buildTest() throws DataFormatException {

        //player1 has "atlas", from build_list, as his God
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, Color.YELLOW, "A" );
        Turn turnPlayer1 = new Turn (player1, Color.YELLOW, "atlas");

        board.placeWorker(workerA1, 2, 2);
        turnPlayer1.selectWorker(board, coord(2,2));
        //Atlas is in "build_list": player1 will activate his effect!
        assertTrue( GodLookUpTable.isEffectBuild(turnPlayer1.getDivinityCard().getSpecificGodName()) );

        //player2 has "minotaur", not from build_list but from move_list, as his God
        Player player2 = new Player("Franco", 10, 10, 2000);
        Worker workerA2 = new Worker( player2, Color.RED, "A" );
        Turn turnPlayer2 = new Turn (player2, Color.RED, "minotaur");

        board.placeWorker(workerA2, 4, 4);
        turnPlayer2.selectWorker(board, coord(4,4));
        //Atlas isn't in "move_list": player2 will just activate the basicMove!
        assertFalse( GodLookUpTable.isEffectBuild(turnPlayer2.getDivinityCard().getSpecificGodName()) );

        clearBoardForFutureTests(board);
    }

    @Test
    public void clearBoardTest() throws DataFormatException {
        Player player1 = new Player("Marco", 2, 2, 2000);
        Worker workerA1 = new Worker( player1, Color.YELLOW, "A" );
        Worker workerB1 = new Worker( player1, Color.YELLOW, "B" );
        Turn turnPlayer1 = new Turn (player1, Color.YELLOW, "pippo");
        board.placeWorker(workerA1, 1,1);
        board.placeWorker(workerB1, 3,1);

        Player player2 = new Player("Franco", 10, 10, 2000);
        Worker workerA2 = new Worker( player2, Color.RED, "A" );
        Worker workerB2 = new Worker( player2, Color.RED, "B" );
        board.placeWorker(workerA2, 2,2);
        board.placeWorker(workerB2, 4,1);


        //clearing board from player1 workers
        turnPlayer1.clearBoard(board);

        //testing if there still are player1's workers
        for (int i=0; i<5; i++) {
            for (int j = 0; j < 5; j++) {
                if( board.getBox(i, j).getOccupier() != null)
                    assertNotEquals(board.getBox(i, j).getOccupier().getColour(), turnPlayer1.getColor());
            }
        }

        clearBoardForFutureTests(board);

    }


}