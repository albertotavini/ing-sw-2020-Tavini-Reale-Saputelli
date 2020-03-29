package it.polimi.ingsw.model;

import org.junit.Test;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

public class MatchTest {

    @Test
    public void findYoungestTestThreePeople() throws DataFormatException {
        ArrayList<Player> playerList = new ArrayList<Player>();

        Player playerGiulio = new Player("Giulio", 22, 12, 1990);
        playerList.add(playerGiulio);
        Player playerMarco = new Player("Marco", 22, 12, 1985);
        playerList.add(playerMarco);
        Player playerFranco = new Player("Franco", 22, 12, 1980);
        playerList.add(playerFranco);

        Match match = new Match(playerList);

        assertEquals(match.findYoungest().get(), playerGiulio);
        assertNotEquals(match.findYoungest().get(), playerMarco);
        assertNotEquals(match.findYoungest().get(), playerFranco);
    }

    @Test
    public void findYoungestTestTwoPeople() throws DataFormatException{
        ArrayList<Player> playerList = new ArrayList<Player>();

        Player playerGianni = new Player("Gianni", 22, 12, 1990);
        playerList.add(playerGianni);
        Player playerLoris = new Player("Loris", 22, 12, 1985);
        playerList.add(playerLoris);

        Match match = new Match(playerList);

        assertEquals(match.findYoungest().get(), playerGianni);
        assertNotEquals(match.findYoungest().get(), playerLoris);
    }

    @Test
    public void arrangeByAgeTest() throws DataFormatException {
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
        game.arrangeByAge();
        game.getPlayerList().forEach(p -> System.out.println(p));

    }
    /*
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

    }*/
}