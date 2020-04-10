package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.NewController;
import it.polimi.ingsw.server.model.Match;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.utils.Global;
import it.polimi.ingsw.server.view.View;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

public class AppMVC {
    public static void main( String[] args ) throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        Player p3 = new Player("Eduardo", 12, 12, 1998);
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        lobbyList.add(p3);
        System.out.println(Global.santorini);
        Match game = new Match(lobbyList);
        View view = new View(lobbyList);
        NewController controller = new NewController(game, view);
        view.addObserver(controller);
        game.addObserver(view);
        view.run();
    }
}
