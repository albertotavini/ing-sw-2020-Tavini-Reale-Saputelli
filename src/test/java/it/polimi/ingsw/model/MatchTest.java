package it.polimi.ingsw.model;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    @Test
    void setgame() throws DataFormatException {
        Player p1 = new Player ("Marco", 1 ,12, 1998);
        Player p2 = new Player ("Luca ", 2, 4, 1995);
        Player p3 = new Player ("Arturo", 4,12, 2000);
        ArrayList <Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        lobbyList.add(p3);
        Match game = new Match(lobbyList);
        System.out.println("la lista dei giocatori inizialmente è");
        game.getPlayerList().forEach(p -> System.out.println(p));
        System.out.println("\nla lista dei giocatori dal più giovane al più vecchio è");
        game.setgame();
        game.getPlayerList().forEach(p -> System.out.println(p));

    }

    @Test
    void findYoungest() throws DataFormatException {
        Player p1 = new Player ("Marco", 1 ,12, 2004);
        Player p2 = new Player ("Luca ", 2, 4, 2000);
        Player p3 = new Player ("Arturo", 13,7, 1999);
        ArrayList <Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        lobbyList.add(p3);
        Match game = new Match(lobbyList);
        System.out.println("il giocatore più giovane è " +game.findYoungest().get());

    }
}