package it.polimi.ingsw;

import it.polimi.ingsw.model.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.zip.DataFormatException;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws DataFormatException
    {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        Player p3 = new Player("Eduardo", 12, 12, 1998);
        ArrayList <Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        lobbyList.add(p3);
        Match game = new Match (lobbyList);
        game.getGameboard().drawBoard();
        game.rotate();


    }

}
