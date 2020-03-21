package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Ciao Progettino!" );
        Board gameBoard = Board.instance();
        gameBoard.drawBoard();
    }

}
