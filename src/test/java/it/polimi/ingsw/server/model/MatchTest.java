package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.Match;
import it.polimi.ingsw.server.model.Player;
import org.junit.Test;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

public class MatchTest {

    //added 1/04, it still has to be tested
    @Test
    public void deletePlayerTest() throws DataFormatException {
        ArrayList<Player> playerList = new ArrayList<Player>();

        Player playerA = new Player("Giulio", 22, 12, 1990);
        playerList.add(playerA);
        Player playerB = new Player("Marco", 22, 12, 1985);
        playerList.add(playerB);
        Player playerC = new Player("Franco", 22, 12, 1980);
        playerList.add(playerC);

        Match match = new Match(playerList);

        //testing player list's size before the remove
        assertTrue( match.getPlayerList().size() == 3);

        //testing the situation where Loser loses
        match.deletePlayer(playerA);

        //the player list will contain both the players not removed
        assertFalse(match.getPlayerList().contains(playerA));
        assertTrue(match.getPlayerList().contains(playerB));
        assertTrue(match.getPlayerList().contains(playerC));

        //testing player list's size after the remove
        assertTrue( match.getPlayerList().size() == 2);
    }

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

    @Test
    public void updatePlayersAfterLosingTestTwoPeople() throws DataFormatException{
        ArrayList<Player> playerList = new ArrayList<Player>();

        Player playerGianni = new Player("Gianni", 22, 12, 1990);
        playerList.add(playerGianni);
        Player playerLoris = new Player("Loris", 22, 12, 1985);
        playerList.add(playerLoris);

        Match match = new Match(playerList);

        //testing playerlist's size before updating the list
        assertTrue( playerList.size() == 2 );

        match.setCurrentPlayer(playerGianni); //now playerGianni is element 0

        //testing the method with current player as element 0 of the list
        match.updatePlayersAfterLosing();
        //size has to be decremented
        assertTrue(match.getPlayerList().size() == 1 );

        assertTrue( match.getPlayerList().contains(playerLoris) );
        assertFalse( match.getPlayerList().contains(playerGianni) );

        match.getPlayerList().add(playerGianni);
        assertTrue( playerList.size() == 2 );

        match.setCurrentPlayer(playerGianni); //now playerGianni is element 1

        //testing the method with current player as element 1 of the list
        match.updatePlayersAfterLosing();

        //size has to be decremented
        assertTrue(match.getPlayerList().size() == 1 );

        assertTrue( match.getPlayerList().contains(playerLoris) );
        assertFalse( match.getPlayerList().contains(playerGianni) );

    }

    @Test
    public void updatePlayersAfterLosingTestThreePeople() throws DataFormatException{
        ArrayList<Player> playerList = new ArrayList<Player>();

        Player playerGianni = new Player("Gianni", 22, 12, 1990);
        playerList.add(playerGianni);
        Player playerLoris = new Player("Loris", 22, 12, 1985);
        playerList.add(playerLoris);
        Player playerFranco = new Player("Franco", 22, 12, 1980);
        playerList.add(playerFranco);

        Match match = new Match(playerList);

        //testing playerlist's size before updating the list
        assertTrue( playerList.size() == 3 );

        match.setCurrentPlayer(playerGianni); //now playerGianni is element 0

        //testing the method with current player as element 0 of the list
        match.updatePlayersAfterLosing();
        //size has to be decremented
        assertTrue(match.getPlayerList().size() == 2 );

        //Gianni is removed
        assertTrue( match.getPlayerList().contains(playerLoris) );
        assertTrue( match.getPlayerList().contains(playerFranco) );
        assertFalse( match.getPlayerList().contains(playerGianni) );

        //adding again Gianni
        match.getPlayerList().add(playerGianni);
        assertTrue( playerList.size() == 3 );

        match.setCurrentPlayer(playerFranco); //now playerFranco is element 1

        //testing the method with current player as element 1 of the list
        match.updatePlayersAfterLosing();

        //size has to be decremented
        assertTrue(match.getPlayerList().size() == 2 );

        assertTrue( match.getPlayerList().contains(playerLoris) );
        assertTrue( match.getPlayerList().contains(playerGianni) );
        assertFalse( match.getPlayerList().contains(playerFranco) );

        //adding again Franco
        match.getPlayerList().add(playerFranco); //now playerFranco is element 2

        match.setCurrentPlayer(playerFranco); //now playerFranco is element 2

        //testing the method with current player as element 2 of the list
        match.updatePlayersAfterLosing();

        //size has to be decremented
        assertTrue(match.getPlayerList().size() == 2 );

        assertTrue( match.getPlayerList().contains(playerLoris) );
        assertTrue( match.getPlayerList().contains(playerGianni) );
        assertFalse( match.getPlayerList().contains(playerFranco) );

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