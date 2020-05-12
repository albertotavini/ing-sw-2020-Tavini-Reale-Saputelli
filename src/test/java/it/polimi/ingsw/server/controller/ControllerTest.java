package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.View;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    //support methods to build playermoves, they're built the same way in the view
    public  static PlayerMove coord(int row, int column, Player p) throws DataFormatException {
        PlayerMove playermove = new PlayerMove(row, column, p);
        playermove.setGenericMessage("nothing interesting here");
        return playermove;
    }
    public static PlayerMove mess(String s, Player p) throws DataFormatException{
        PlayerMove playermove =new PlayerMove(s, p);
        return playermove;

    }
    //support method to clear the board
    public void clearBoardForFutureTests (Board board){
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

    @Test
    void checkGodExistenceTest() throws DataFormatException{
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        //Player p3 = new Player("Eduardo", 12, 12, 1998);
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        //lobbyList.add(p3);
        Model model = new Model(lobbyList);
        View view = new View(lobbyList);
        Controller controller = new Controller(model, view);

        //messages can be sent by either player 1 or 2, this method doesn't check currentPlayer
        //cases where various strings are called
        assertFalse(controller.checkGodExistence(mess("9999999", p1)));
        assertFalse(controller.checkGodExistence(mess("f", p2)));
        assertFalse(controller.checkGodExistence(mess("$/&(&%%$(&$&", p1)));
        assertFalse(controller.checkGodExistence(mess("ilcybernetico",p2)));
        assertFalse(controller.checkGodExistence(mess("pomodoripelati", p1)));
        assertFalse(controller.checkGodExistence(mess("serbuffa", p1)));
        assertFalse(controller.checkGodExistence(mess("cannolosiciliano", p2)));
        assertFalse(controller.checkGodExistence(mess("scaletta", p1)));
        assertFalse(controller.checkGodExistence(mess("bastaconitiktoksonia", p1)));
        assertFalse(controller.checkGodExistence(mess("andonio", p2)));
        assertFalse(controller.checkGodExistence(mess("3mend3ada", p2)));
        assertFalse(controller.checkGodExistence(mess("\0\0\0\0\0\0\0", p1)));
        assertFalse(controller.checkGodExistence(mess("\n\n\n\n\n\n\n\n", p2)));

        //cases where an actual name of a divinity is given
        assertTrue(controller.checkGodExistence(mess("athena", p2)));
        assertTrue(controller.checkGodExistence(mess("pan", p1)));
        assertTrue(controller.checkGodExistence(mess("minotaur", p1)));
        assertTrue(controller.checkGodExistence(mess("apollo", p2)));
        assertTrue(controller.checkGodExistence(mess("hephaestus", p2)));
        assertTrue(controller.checkGodExistence(mess("prometheus", p2)));
        assertTrue(controller.checkGodExistence(mess("demeter", p2)));
        assertTrue(controller.checkGodExistence(mess("artemis", p1)));
        assertTrue(controller.checkGodExistence(mess("atlas", p2)));

    }

    @Test
    void performPlaceTest() throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        Model model = new Model(lobbyList);
        View view = new View(lobbyList);
        Controller controller = new Controller(model, view);
        //before it initializes the gods, because without this part turns are not instantiated
        if (true) {
            controller.chooseGods(mess("athena", p2));
            controller.chooseGods(mess("minotaur", p2));
            controller.chooseGods(mess("minotaur", p1));
            assertTrue(controller.chooseGods(mess("athena", p2)));
        }
        controller.performPlace(coord(2,2, p1));
        assertNull(controller.getModel().getGameboard().getBox(2, 2).getOccupier());
        assertEquals(controller.getPlacePart(), PlacePart.FirstPlacing);
        controller.performPlace(coord(2,2, p2));
        assertNotNull(controller.getModel().getGameboard().getBox(2, 2).getOccupier());
        assertEquals(controller.getModel().getGameboard().getBox(2, 2).getOccupier().getPlayer(), p2);
        assertEquals(controller.getPlacePart(), PlacePart.SecondPlacing);
        controller.getModel().getGameboard().drawBoard();
        //if i try to place on the same spot or outside the board the state doesn't change and nothing happens
        controller.performPlace(coord(2,2, p2));
        controller.performPlace(coord(8,4, p2));
        assertEquals(controller.getPlacePart(), PlacePart.SecondPlacing);
        assertTrue(controller.performPlace(coord(3,1, p2)));
        assertEquals(controller.getModel().getGameboard().getBox(3, 1).getOccupier().getPlayer(), p2);
        //now the youngest player concluded the place, it switches currentplayer and go back to state one
        assertEquals(controller.getPlacePart(), PlacePart.FirstPlacing);
        assertNotEquals(controller.getModel().getCurrentPlayer(), p2);
        assertEquals(controller.getModel().getCurrentPlayer(), p1);
        //now the youngest player will not be able to place
        controller.performPlace(coord(0,1, p2));
        assertNull(controller.getModel().getGameboard().getBox(0, 1).getOccupier());
        //now the other player can do his first place
        controller.performPlace(coord(0,1, p1));
        assertNotNull(controller.getModel().getGameboard().getBox(0, 1).getOccupier());
        assertEquals(controller.getModel().getGameboard().getBox(0, 1).getOccupier().getPlayer(), p1);
        assertEquals(controller.getPlacePart(), PlacePart.SecondPlacing);
        controller.getModel().getGameboard().drawBoard();
        //it's not allowed to place on an opponent's worker
        controller.performPlace(coord(2,2, p1));
        assertNotEquals(controller.getModel().getGameboard().getBox(2, 2).getOccupier().getPlayer(), p1);
        assertEquals(controller.getPlacePart(), PlacePart.SecondPlacing);
        controller.getModel().getGameboard().drawBoard();
        //now the place is concluded, the state goes back to one and performPlace itself will return true again
        assertTrue(controller.performPlace(coord(4,3, p1)));
        assertEquals(controller.getModel().getGameboard().getBox(4, 3).getOccupier().getPlayer(), p1);
        assertEquals(controller.getPlacePart(), PlacePart.FirstPlacing);

        //the model doesn't differ if the players are two or three, so there is no need to test the other case
        clearBoardForFutureTests(controller.getModel().getGameboard());
    }

    @Test
    void performTurnTest()throws DataFormatException{
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        Model model = new Model(lobbyList);
        View view = new View(lobbyList);
        Controller controller = new Controller(model, view);
        //before it initializes the gods, because without this part turns are not instantiated
        if (true) {
            controller.chooseGods(mess("athena", p2));
            controller.chooseGods(mess("demeter", p2));
            controller.chooseGods(mess("demeter", p1));
            assertTrue(controller.chooseGods(mess("athena", p2)));
        }
        //then it does the place because without it turn would be useless
        if(true) {
            controller.performPlace(coord(1,3,p2));
            assertTrue(controller.performPlace(coord(3,0,p2)));
            controller.performPlace(coord(2,2, p1));
            assertTrue(controller.performPlace(coord(4,1,p1)));
        }
        controller.getModel().getGameboard().drawBoard();

        //now if i try to act on turn with the oldest player, which is not first, the method returns without changing anything
        controller.performTurn(coord(2,2, p1));
        assertEquals(controller.getTurnPart(), TurnPart.Select);
        //while if i select with the youngest, which is the currentplayer
        //first i try to insert invalid input, so nothing changes
        controller.performTurn(coord(1,0, p2));
        assertEquals(controller.getTurnPart(), TurnPart.Select);
        //then i give him right coordinates so the state changes
        controller.performTurn(coord(1,3, p2));
        assertEquals(controller.getTurnPart(), TurnPart.Move);
        //i try some invalid inputs and nothing changes
        controller.performTurn(coord(2,2, p1));
        controller.performTurn(coord(3,3, p2));
        controller.performTurn(coord(2,7, p2));
        //controller.performTurn(mess("peppino da capri", p2));
        assertEquals(controller.getTurnPart(), TurnPart.Move);
        //the i give proper coordinates with the right player
        controller.performTurn(coord(1,2, p2));
        assertNotNull(controller.getModel().getGameboard().getBox(1,2).getOccupier());
        assertNull(controller.getModel().getGameboard().getBox(1,3).getOccupier());
        assertEquals(controller.getTurnPart(), TurnPart.Build);
        //a few incorrect inputs again for the build
        controller.performTurn(coord(1,3, p1));
        controller.performTurn(coord(1,2, p2));
        controller.performTurn(coord(88,7, p2));
        controller.performTurn(new PlayerMove(ConfirmationEnum.No, p2));
        assertEquals(controller.getTurnPart(), TurnPart.Build);
        controller.performTurn(coord(1,1, p2));
        assertEquals(controller.getModel().getGameboard().getBox(1,1).getTower().size(),1);
        assertEquals(controller.getTurnPart(), TurnPart.Select);
        //also the current player has changed
        assertEquals(controller.getModel().getCurrentPlayer(), p1);
        controller.getModel().getGameboard().drawBoard();
        //now it must make his turn, we'll show one where he uses demeter's effect
        //first again a few invalid inputs
        controller.performTurn(coord(2,2, p2));
        controller.performTurn(coord(3,4, p1));
        controller.performTurn(coord(2,43, p1));
        //controller.performTurn(mess("yes", p1));
        controller.performTurn(new PlayerMove(ConfirmationEnum.Yes, p1));
        //controller.performTurn(mess("no", p1));
        controller.performTurn(new PlayerMove(ConfirmationEnum.No, p1));
        assertEquals(controller.getTurnPart(), TurnPart.Select);
        //then a correct one
        controller.performTurn(coord(4,1, p1));
        assertEquals(controller.getTurnPart(), TurnPart.Move);
        //incorrect inputs
        controller.performTurn(coord(4,1, p1));
        controller.performTurn(coord(5,1, p1));
        controller.performTurn(coord(2,1, p1));
        controller.performTurn(coord(4,2, p2));
        assertEquals(controller.getTurnPart(), TurnPart.Move);
        //then the right one
        controller.performTurn(coord(4,2, p1));
        assertEquals(controller.getTurnPart(), TurnPart.Build);
        assertNotNull(controller.getModel().getGameboard().getBox(4,2).getOccupier());
        assertNull(controller.getModel().getGameboard().getBox(4,1).getOccupier());

        //incorrect inputs, among which a legit place where to build, but demeter is waiting for its effect confirmation
        controller.performTurn(coord(4,1, p1));
        controller.performTurn(coord(5,1, p1));
        controller.performTurn(coord(2,1, p1));
        controller.performTurn(coord(4,2, p2));
        assertEquals(controller.getTurnPart(), TurnPart.Build);
        //controller.performTurn(mess("yes", p1));
        controller.performTurn(new PlayerMove(ConfirmationEnum.Yes, p1));
        controller.getModel().getGameboard().drawBoard();
        //the it won't let me build twice on the same level
        controller.performTurn(coord(4,3, p1));
        controller.performTurn(coord(4,3, p1));
        assertEquals(controller.getTurnPart(), TurnPart.Build);
        assertEquals(controller.getModel().getGameboard().getBox(4,3).getTower().size(),1);
        //a few invalid inputs as always
        controller.performTurn(coord(4,7, p1));
        controller.performTurn(coord(5,1, p1));
        controller.performTurn(coord(2,1, p1));
        controller.performTurn(coord(4,2, p2));
        assertEquals(controller.getTurnPart(), TurnPart.Build);
        //the i give a legit second build coordinate and the turn will end
        controller.performTurn(coord(3,3, p1));
        assertEquals(controller.getModel().getGameboard().getBox(3,3).getTower().size(),1);
        assertEquals(controller.getTurnPart(), TurnPart.Select);

        //and the current player will become p2 again
        assertEquals(controller.getModel().getCurrentPlayer(), p2);

        //this function doesn't pass through different branches if the players are two or three, so testing the two player version should do the job

        clearBoardForFutureTests(controller.getModel().getGameboard());









    }

    @Test
    void chooseGodsTestThreePlayers() throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        Player p3 = new Player("Eduardo", 12, 12, 1998);
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        lobbyList.add(p3);
        Model model = new Model(lobbyList);
        View view = new View(lobbyList);
        Controller controller = new Controller(model, view);


        if (true) {
            assertEquals(controller.getGamePart(), GamePart.God);
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            //if the youngest player sends a string which is not equal to a god name nothing changes
            controller.chooseGods(mess("peppino", p3));
            assertEquals(3, controller.getGodChoiceTimes());
            assertEquals(0, controller.getListOfGods().size());
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            //if a player which is not the youngest proposes a god, nothing changes
            controller.chooseGods(mess("athena", p1));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            assertEquals(3, controller.getGodChoiceTimes());
            assertEquals(0, controller.getListOfGods().size());
            //now the youngest proposes gods, so the GodchoiceTimes counter gets down
            controller.chooseGods(mess("athena", p3));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            assertEquals(2, controller.getGodChoiceTimes());
            assertEquals(1, controller.getListOfGods().size());
            //the same god cannot be chosen more than once
            controller.chooseGods(mess("athena", p3));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            assertEquals(2, controller.getGodChoiceTimes());
            assertEquals(1, controller.getListOfGods().size());
            controller.chooseGods(mess("pan", p3));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            assertEquals(1, controller.getGodChoiceTimes());
            assertEquals(2, controller.getListOfGods().size());
            controller.chooseGods(mess("prometheus", p3));
            //now that the 3 gods have been chosen it passes to another state
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OlderChooses);
            assertEquals(0, controller.getGodChoiceTimes());
            assertEquals(3, controller.getListOfGods().size());
            //now the oldest player must choose, so if the other two demand something nothing changes
            controller.chooseGods(mess("athena", p3));
            controller.chooseGods(mess("athena", p2));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OlderChooses);
            assertEquals(3, controller.getListOfGods().size());
            //a god not in the list given by the youngest cannot be chosen
            controller.chooseGods(mess("hephaestus", p1));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OlderChooses);
            assertEquals(3, controller.getListOfGods().size());
            controller.chooseGods(mess("prometheus", p1));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OtherChooses);
            assertEquals(2, controller.getListOfGods().size());
            //the other player will not be able to choose prometheus again, and neither the oldest player will not be able to choose again
            //or the youngest will be able to choose
            controller.chooseGods(mess("athena", p1));
            controller.chooseGods(mess("prometheus", p2));
            controller.chooseGods(mess("pan", p3));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OtherChooses);
            assertEquals(2, controller.getListOfGods().size());

            //now the chooseGods will give back true, because this part of the setup is now concluded
            assertTrue(controller.chooseGods(mess("athena", p2)));





        }
    }
    @Test
    void chooseGodsTestTwoPlayers( ) throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        Model model = new Model(lobbyList);
        View view = new View(lobbyList);
        Controller controller = new Controller(model, view);

        if (true) {
            assertEquals(controller.getGamePart(), GamePart.God);
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            //if the youngest player sends a string which is not equal to a god name nothing changes
            controller.chooseGods(mess("Nicola", p2));
            assertEquals(2, controller.getGodChoiceTimes());
            assertEquals(0, controller.getListOfGods().size());
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            //if a player which is not the youngest proposes a god, nothing changes
            controller.chooseGods(mess("athena", p1));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            assertEquals(2, controller.getGodChoiceTimes());
            assertEquals(0, controller.getListOfGods().size());
            //now the youngest proposes gods, so the GodchoiceTimes counter gets down
            controller.chooseGods(mess("minotaur", p2));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            assertEquals(1, controller.getGodChoiceTimes());
            assertEquals(1, controller.getListOfGods().size());
            //the same god cannot be chosen more than once
            controller.chooseGods(mess("minotaur", p2));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.InitialChoice);
            assertEquals(1, controller.getGodChoiceTimes());
            assertEquals(1, controller.getListOfGods().size());
            controller.chooseGods(mess("demeter", p2));
            //now that the 2 gods have been chosen it passes to another state
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OlderChooses);
            assertEquals(0, controller.getGodChoiceTimes());
            assertEquals(2, controller.getListOfGods().size());
            //now the oldest player must choose, so if the other demands something nothing changes
            controller.chooseGods(mess("demeter", p2));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OlderChooses);
            assertEquals(2, controller.getListOfGods().size());
            //a god not in the list given by the youngest cannot be chosen
            controller.chooseGods(mess("hephaestus", p1));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OlderChooses);
            assertEquals(2, controller.getListOfGods().size());
            controller.chooseGods(mess("minotaur", p1));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OtherChooses);
            assertEquals(1, controller.getListOfGods().size());
            //the other player will not be able to choose prometheus again, and neither the oldest player will not be able to choose again
            controller.chooseGods(mess("minotaur", p2));
            controller.chooseGods(mess("demeter", p1));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OtherChooses);
            assertEquals(1, controller.getListOfGods().size());

            //now that the other player chooses correctly it returns true and the list will be empty
            assertTrue(controller.chooseGods(mess("demeter", p2)));
            assertEquals(controller.getGodSetupPart(), GodSetupPart.OtherChooses);
            assertEquals(0, controller.getListOfGods().size());






        }

    }

    @Test
    void updateTestTwoPlayers() throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        Model model = new Model(lobbyList);
        View view = new View(lobbyList);
        Controller controller = new Controller(model, view);
        //having already tested separately the methods the controller calls in this function i will just put cases where their inputs are all correct

        assertEquals(controller.getGamePart(), GamePart.God);
        //now the initialization of gods
        if (true) {
            controller.update(mess("pan", p2),null);
            controller.update(mess("apollo", p2), null);
            controller.update(mess("apollo", p1), null);
            controller.update(mess("pan", p2), null);
        }
        assertEquals(controller.getGamePart(), GamePart.Place1);
        //now the place part
        if (true) {
            controller.update(coord(2,3, p2), null);
            controller.update(coord(1,2, p2), null);
            assertEquals(controller.getGamePart(), GamePart.Place2);
            controller.update(coord(0,3, p1), null);
            controller.update(coord(1,4, p1), null);
        }
        controller.getModel().getGameboard().drawBoard();
        assertEquals(controller.getGamePart(), GamePart.Turn);
        //now the execution of a turn to show it remains in the TurnPart while rotating player
        assertEquals(controller.getModel().getCurrentPlayer(), p2);
        if (true) {
            controller.update(coord(2,3 ,p2),null);
            controller.update(coord(2,4 ,p2),null);
            controller.update(coord(2,3 ,p2),null);
        }
        assertEquals(controller.getModel().getCurrentPlayer(), p1);
        assertEquals(controller.getGamePart(), GamePart.Turn);

        //now i remove a player just to show that by calling update with a single player it goes to WinnerPart
        controller.getModel().getPlayerList().remove(1);
        controller.update(mess("AOOOOOOOOO", p2),null);
        assertEquals(controller.getGamePart(), GamePart.Conclusion);

        clearBoardForFutureTests(controller.getModel().getGameboard());
    }

    @Test
    void updateTestThreePlayers() throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        Player p3 = new Player("Eduardo", 12, 12, 1998);
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        lobbyList.add(p3);
        Model model = new Model(lobbyList);
        View view = new View(lobbyList);
        Controller controller = new Controller(model, view);
        //having already tested separately the methods the controller calls in this function i will just put cases where their inputs are all correct

        assertEquals(controller.getGamePart(), GamePart.God);
        //now the initialization of gods
        if (true) {
            controller.update(mess("pan", p3), null);
            controller.update(mess("apollo", p3), null);
            controller.update(mess("minotaur", p3), null);
            controller.update(mess("apollo", p1), null);
            controller.update(mess("pan", p2), null);
            controller.update(mess("minotaur", p3), null);
        }
        assertEquals(controller.getGamePart(), GamePart.Place1);
        //now the place part
        if (true) {
            controller.update(coord(2,3, p3), null);
            controller.update(coord(1,2, p3), null);
            assertTrue(controller.getGamePart().equals(GamePart.Place2));
            controller.update(coord(0,3, p2), null);
            controller.update(coord(1,4, p2), null);
            assertTrue(controller.getGamePart().equals(GamePart.Place3));
            controller.update(coord(0,0, p1), null);
            controller.update(coord(4,4, p1), null);
        }
        assertEquals(controller.getGamePart(), GamePart.Turn);
        //now the execution of a turn to show it remains in the TurnPart while rotating player
        assertEquals(controller.getModel().getCurrentPlayer(), p3);
        if (true) {
            controller.update(coord(2,3 ,p3), null);
            controller.update(coord(2,4 ,p3), null);
            controller.update(coord(2,3 ,p3), null);
        }
        assertEquals(controller.getModel().getCurrentPlayer(), p2);
        assertEquals(controller.getGamePart(), GamePart.Turn);

        //now i remove two players just to show that by calling update with a single player it goes to WinnerPart
        controller.getModel().getPlayerList().remove(2);
        controller.getModel().getPlayerList().remove(1);
        controller.update(mess("AOOOOOOOOO", p3), null);
        assertEquals(controller.getGamePart(), GamePart.Conclusion);


        clearBoardForFutureTests(controller.getModel().getGameboard());
    }
}