package it.polimi.ingsw.model;

import it.polimi.ingsw.model.god.GenericGod;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DataTruncation;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class TurnTest {

    @Test
    void checkIfCanMove() {
    }

    @Test  //DA FIXARE HO CAMBIATO LA SEGNATURA DEL METODO
    void checkIfCanBuild() throws DataFormatException {
        Board board = Board.instance();
        Player p1 = new Player("Marco", 2, 2, 2000);
        Worker w1 = new Worker( p1, "Y", "A" );
        Worker w2 = new Worker( p1, "Y", "B" );
        Turn turn = new Turn (p1, "Y", "pippo");
        board.placeWorker(w1, 1, 2);
        for (int i =0; i<5; i++) {
            for (int j=0; j<5; j++)  {
                board.increaseLevel(i, j);
                board.increaseLevel(i, j);
                board.increaseLevel(i, j);
                board.increaseLevel(i, j);
            }
        }
        board.drawBoard();
        assertFalse(turn.checkIfCanBuild(board));
        board.decreaseLevel(1, 2);
        board.drawBoard();
        //si libera la casella del mio player, ancora non mi posso costruire
        assertFalse(turn.checkIfCanBuild(board));
        board.decreaseLevel(1,4);
        board.drawBoard();
        //si libera una casella lontana dal mio player, ancora non posso costruire
        assertFalse(turn.checkIfCanBuild(board));
        board.decreaseLevel(2,2 );
        board.drawBoard();
        //si libera una casella di fianco al mio worker, posso costruire
        //assertTrue(turn.checkIfCanBuild(board));



    }

    @Test
    void moveTest() throws DataFormatException {


    }
}