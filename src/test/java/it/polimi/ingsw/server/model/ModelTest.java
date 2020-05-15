package it.polimi.ingsw.server.model;

import org.junit.Test;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

//all methods have been tested, except for NOMVC methods, because they were used with stdin

public class ModelTest {

    @Test
    public void deletePlayerTest() throws DataFormatException {
        ArrayList<Player> playerList = new ArrayList<>();

        Player playerA = new Player("Giulio", 22, 12, 1990);
        playerList.add(playerA);
        Player playerB = new Player("Marco", 22, 12, 1985);
        playerList.add(playerB);
        Player playerC = new Player("Franco", 22, 12, 1980);
        playerList.add(playerC);

        Model model = new Model(playerList);

        //testing player list's size before the remove
        assertEquals( model.getPlayerList().size(), 3);

        //testing the situation where Loser loses
        model.deletePlayer(playerA);

        //the player list will contain both the players not removed
        assertFalse(model.getPlayerList().contains(playerA));
        assertTrue(model.getPlayerList().contains(playerB));
        assertTrue(model.getPlayerList().contains(playerC));

        //testing player list's size after the remove
        assertEquals( model.getPlayerList().size(), 2);
    }


    //testing findYoungest method on two players match
    @Test
    public void findYoungestTwoPlayersTest() throws DataFormatException{
        ArrayList<Player> playerList = new ArrayList<>();

        Player playerGianni = new Player("Gianni", 22, 12, 1990);
        playerList.add(playerGianni);
        Player playerLoris = new Player("Loris", 22, 12, 1985);
        playerList.add(playerLoris);

        Model model = new Model(playerList);

        if (model.findYoungest().isPresent()) {
            assertEquals(model.findYoungest().get(), playerGianni);
            assertNotEquals(model.findYoungest().get(), playerLoris);
        }
    }

    //testing findYoungest method on three players match
    @Test
    public void findYoungestThreePlayersTest() throws DataFormatException {
        ArrayList<Player> playerList = new ArrayList<>();

        Player playerGiulio = new Player("Giulio", 22, 12, 1990);
        playerList.add(playerGiulio);
        Player playerMarco = new Player("Marco", 22, 12, 1985);
        playerList.add(playerMarco);
        Player playerFranco = new Player("Franco", 22, 12, 1980);
        playerList.add(playerFranco);

        Model model = new Model(playerList);

        if (model.findYoungest().isPresent()) {
            assertEquals(model.findYoungest().get(), playerGiulio);
            assertNotEquals(model.findYoungest().get(), playerMarco);
            assertNotEquals(model.findYoungest().get(), playerFranco);
        }
    }


    @Test
    public void checkIfOnePlayerRemainsTest() throws DataFormatException {
        ArrayList<Player> playerList = new ArrayList<>();

        Player playerGiulio = new Player("Giulio", 22, 12, 1990);
        playerList.add(playerGiulio);
        Player playerMarco = new Player("Marco", 22, 12, 1985);
        playerList.add(playerMarco);
        Player playerFranco = new Player("Franco", 22, 12, 1980);
        playerList.add(playerFranco);

        Model model = new Model(playerList);

        //there are 3 players
        assertFalse( model.checkIfOnePlayerRemains() );
        assertEquals( model.getPlayerList().size(), 3);
        //removing one player
        model.setCurrentPlayer(playerFranco);
        model.updatePlayersAfterLosing();
        assertFalse( model.checkIfOnePlayerRemains() );
        //now there are 2 players
        assertFalse( model.checkIfOnePlayerRemains() );
        assertEquals( model.getPlayerList().size(), 2);
        //removing again one player
        model.setCurrentPlayer(playerMarco);
        model.updatePlayersAfterLosing();
        //now there is just one player
        assertTrue( model.checkIfOnePlayerRemains() );
        assertEquals( model.getPlayerList().size(), 1);

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
        Model game = new Model(lobbyList);
        System.out.println("Initially players' list is");
        game.getPlayerList().forEach(System.out::println);
        System.out.println("\nplayers' list in order by age (from the youngest to the oldest) is ");
        game.arrangeByAge();
        game.getPlayerList().forEach(System.out::println);
    }


    //testing updateTurn method on two players match
    @Test
    public void updateTurnTwoPlayersTest() throws DataFormatException {
        ArrayList<Player> playerList = new ArrayList<>();

        Player playerGianni = new Player("Gianni", 22, 12, 1990);
        playerList.add(playerGianni);
        Player playerLoris = new Player("Loris", 22, 12, 1985);
        playerList.add(playerLoris);

        Model model = new Model(playerList);

        assertEquals (model.getPlayerList().size(), 2);

        model.setCurrentPlayer(playerGianni);
        assertEquals( model.getCurrentPlayer(), playerGianni );
        model.updateTurn();
        assertEquals( model.getCurrentPlayer(), playerLoris );
        model.updateTurn();
        assertEquals( model.getCurrentPlayer(), playerGianni );

    }

    //testing updateTurn method on three players match
    @Test
    public void updateTurnThreePlayersTest() throws DataFormatException {
        ArrayList<Player> playerList = new ArrayList<>();

        Player playerGianni = new Player("Gianni", 22, 12, 1990);
        playerList.add(playerGianni);
        Player playerLoris = new Player("Loris", 22, 12, 1985);
        playerList.add(playerLoris);
        Player playerFranco = new Player("Franco", 22, 12, 1980);
        playerList.add(playerFranco);

        Model model = new Model(playerList);

        assertEquals (model.getPlayerList().size(), 3);

        model.setCurrentPlayer(playerGianni);
        assertEquals( model.getCurrentPlayer(), playerGianni );
        model.updateTurn();
        assertEquals( model.getCurrentPlayer(), playerLoris );
        model.updateTurn();
        assertEquals( model.getCurrentPlayer(), playerFranco );
        model.updateTurn();
        assertEquals( model.getCurrentPlayer(), playerGianni );
    }


    //testing updatePlayersAfterLosing method on two players match
    @Test
    public void updatePlayersAfterLosingTwoPlayersTest() throws DataFormatException{
        ArrayList<Player> playerList = new ArrayList<>();

        Player playerGianni = new Player("Gianni", 22, 12, 1990);
        playerList.add(playerGianni);
        Player playerLoris = new Player("Loris", 22, 12, 1985);
        playerList.add(playerLoris);

        Model model = new Model(playerList);

        //testing playerlist's size before updating the list
        assertEquals( playerList.size(), 2 );

        model.setCurrentPlayer(playerGianni); //now playerGianni is element 0

        //testing the method with current player as element 0 of the list
        model.updatePlayersAfterLosing();
        //size has to be decremented
        assertEquals(model.getPlayerList().size(), 1 );

        assertTrue( model.getPlayerList().contains(playerLoris) );
        assertFalse( model.getPlayerList().contains(playerGianni) );

        model.getPlayerList().add(playerGianni);
        assertEquals( playerList.size(), 2 );

        model.setCurrentPlayer(playerGianni); //now playerGianni is element 1

        //testing the method with current player as element 1 of the list
        model.updatePlayersAfterLosing();

        //size has to be decremented
        assertEquals(model.getPlayerList().size(), 1 );

        assertTrue( model.getPlayerList().contains(playerLoris) );
        assertFalse( model.getPlayerList().contains(playerGianni) );

    }

    //testing updatePlayersAfterLosing method on three players match
    @Test
    public void updatePlayersAfterLosingThreePlayersTest() throws DataFormatException{
        ArrayList<Player> playerList = new ArrayList<>();

        Player playerGianni = new Player("Gianni", 22, 12, 1990);
        playerList.add(playerGianni);
        Player playerLoris = new Player("Loris", 22, 12, 1985);
        playerList.add(playerLoris);
        Player playerFranco = new Player("Franco", 22, 12, 1980);
        playerList.add(playerFranco);

        Model model = new Model(playerList);

        //testing playerlist's size before updating the list
        assertEquals( playerList.size(), 3 );

        model.setCurrentPlayer(playerGianni); //now playerGianni is element 0

        //testing the method with current player as element 0 of the list
        model.updatePlayersAfterLosing();
        //size has to be decremented
        assertEquals(model.getPlayerList().size(), 2 );

        //Gianni is removed
        assertTrue( model.getPlayerList().contains(playerLoris) );
        assertTrue( model.getPlayerList().contains(playerFranco) );
        assertFalse( model.getPlayerList().contains(playerGianni) );

        //adding again Gianni
        model.getPlayerList().add(playerGianni);
        assertEquals( playerList.size(), 3 );

        model.setCurrentPlayer(playerFranco); //now playerFranco is element 1

        //testing the method with current player as element 1 of the list
        model.updatePlayersAfterLosing();

        //size has to be decremented
        assertEquals(model.getPlayerList().size(), 2 );

        assertTrue( model.getPlayerList().contains(playerLoris) );
        assertTrue( model.getPlayerList().contains(playerGianni) );
        assertFalse( model.getPlayerList().contains(playerFranco) );

        //adding again Franco
        model.getPlayerList().add(playerFranco); //now playerFranco is element 2

        model.setCurrentPlayer(playerFranco); //now playerFranco is element 2

        //testing the method with current player as element 2 of the list
        model.updatePlayersAfterLosing();

        //size has to be decremented
        assertEquals(model.getPlayerList().size(), 2 );

        assertTrue( model.getPlayerList().contains(playerLoris) );
        assertTrue( model.getPlayerList().contains(playerGianni) );
        assertFalse( model.getPlayerList().contains(playerFranco) );

    }

}