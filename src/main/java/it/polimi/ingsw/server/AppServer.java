package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Match;
import it.polimi.ingsw.server.model.Player;

import java.util.ArrayList;
import java.util.zip.DataFormatException;



public class AppServer
{
    public static void main( String[] args ) throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        //Player p3 = new Player("Eduardo", 12, 12, 1998);
        ArrayList <Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        //lobbyList.add(p3);
        Match game = new Match(lobbyList);
        Board gameboard = game.getGameboard();
        game.getGameboard().drawBoard();
        game.NOMVCrotate();


        /*
        Board board= Board.instance();
        //board.getBox(0,0).;
        Turn turn = new Turn(p1, "R", "atena");
        while (true) {
            turn.callTurn(board);
        }*/

    }

}
