package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.enums.GamePart;
import it.polimi.ingsw.server.controller.enums.GodSetupPart;
import it.polimi.ingsw.server.controller.enums.PlacePart;
import it.polimi.ingsw.server.controller.enums.TurnPart;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.ConfirmationEnum;
import it.polimi.ingsw.server.view.ViewOffline;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    //support methods to build playermoves, they're built the same way in the view
    static PlayerMove coord(int row, int column, Player p) {
        PlayerMove playermove = new PlayerMove(row, column, p);
        playermove.setGenericMessage("nothing interesting here");
        return playermove;
    }
    static PlayerMove mess(String s, Player p){
        return new PlayerMove(s, p);


    }
    //support method to clear the board
    private void clearBoardForFutureTests (Board board){
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
    void checkGodExistenceSuccessTest() throws DataFormatException{

        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        //Player p3 = new Player("Eduardo", 12, 12, 1998);
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        //lobbyList.add(p3);
        Model model = new Model(lobbyList);
        ViewOffline viewOffline = new ViewOffline(lobbyList);
        Controller controller = new Controller(model, viewOffline);

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
        assertTrue(controller.checkGodExistence(mess("triton", p2)));
        assertTrue(controller.checkGodExistence(mess("hestia", p2)));
        assertTrue(controller.checkGodExistence(mess("ares", p2)));
        assertTrue(controller.checkGodExistence(mess("zeus", p2)));

    }

    @Test
    void checkGodExistenceFailTest() throws DataFormatException{
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        //Player p3 = new Player("Eduardo", 12, 12, 1998);
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        //lobbyList.add(p3);
        Model model = new Model(lobbyList);
        ViewOffline viewOffline = new ViewOffline(lobbyList);
        Controller controller = new Controller(model, viewOffline);

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

    }


    @Test
    void performPlaceTest() throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        Model model = new Model(lobbyList);
        ViewOffline viewOffline = new ViewOffline(lobbyList);
        Controller controller = new Controller(model, viewOffline);
        //before it initializes the gods, because without this part turns are not instantiated
            controller.chooseGods(mess("athena", p2));
            controller.chooseGods(mess("minotaur", p2));
            controller.chooseGods(mess("minotaur", p1));
            assertTrue(controller.chooseGods(mess("athena", p2)));

        controller.performPlace(coord(2,2, p1));
        assertNull(controller.getModel().getGameboard().getBox(2, 2).getOccupier());
        assertEquals(PlacePart.FIRST, controller.getPlacePart());
        controller.performPlace(coord(2,2, p2));
        assertNotNull(controller.getModel().getGameboard().getBox(2, 2).getOccupier());
        assertEquals(controller.getModel().getGameboard().getBox(2, 2).getOccupier().getPlayer(), p2);
        assertEquals(PlacePart.SECOND, controller.getPlacePart());
        controller.getModel().getGameboard().drawBoard();
        //if i try to place on the same spot or outside the board the state doesn't change and nothing happens
        controller.performPlace(coord(2,2, p2));
        controller.performPlace(coord(8,4, p2));
        assertEquals(PlacePart.SECOND, controller.getPlacePart());
        assertTrue(controller.performPlace(coord(3,1, p2)));
        assertEquals(controller.getModel().getGameboard().getBox(3, 1).getOccupier().getPlayer(), p2);
        //now the youngest player concluded the place, it switches currentplayer and go back to state one
        assertEquals(PlacePart.FIRST, controller.getPlacePart());
        assertNotEquals(controller.getModel().getCurrentPlayer(), p2);
        assertEquals(controller.getModel().getCurrentPlayer(), p1);
        //now the youngest player will not be able to place
        controller.performPlace(coord(0,1, p2));
        assertNull(controller.getModel().getGameboard().getBox(0, 1).getOccupier());
        //now the other player can do his first place
        controller.performPlace(coord(0,1, p1));
        assertNotNull(controller.getModel().getGameboard().getBox(0, 1).getOccupier());
        assertEquals(controller.getModel().getGameboard().getBox(0, 1).getOccupier().getPlayer(), p1);
        assertEquals(PlacePart.SECOND, controller.getPlacePart());
        controller.getModel().getGameboard().drawBoard();
        //it's not allowed to place on an opponent's worker
        controller.performPlace(coord(2,2, p1));
        assertNotEquals(controller.getModel().getGameboard().getBox(2, 2).getOccupier().getPlayer(), p1);
        assertEquals(PlacePart.SECOND, controller.getPlacePart());
        controller.getModel().getGameboard().drawBoard();
        //now the place is concluded, the state goes back to one and performPlace itself will return true again
        assertTrue(controller.performPlace(coord(4,3, p1)));
        assertEquals(controller.getModel().getGameboard().getBox(4, 3).getOccupier().getPlayer(), p1);
        assertEquals(PlacePart.FIRST, controller.getPlacePart());

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
        ViewOffline viewOffline = new ViewOffline(lobbyList);
        Controller controller = new Controller(model, viewOffline);
        //before it initializes the gods, because without this part turns are not instantiated
        controller.chooseGods(mess("athena", p2));
        controller.chooseGods(mess("demeter", p2));
        controller.chooseGods(mess("demeter", p1));
        assertTrue(controller.chooseGods(mess("athena", p2)));
        //then it does the place because without it turn would be useless
        controller.performPlace(coord(1,3, p2));
        assertTrue(controller.performPlace(coord(3,0, p2)));
        controller.performPlace(coord(2,2, p1));
        assertTrue(controller.performPlace(coord(4,1, p1)));
        controller.getModel().getGameboard().drawBoard();


        //now if i try to act on turn with the oldest player, which is not first, the method returns without changing anything
        controller.performTurn(coord(2,2, p1));
        assertEquals(TurnPart.SELECT, controller.getTurnPart());
        //while if i select with the youngest, which is the currentplayer
        //first i try to insert invalid input, so nothing changes
        controller.performTurn(coord(1,0, p2));
        assertEquals(TurnPart.SELECT, controller.getTurnPart());
        //then i give him right coordinates so the state changes
        controller.performTurn(coord(1,3, p2));
        assertEquals(TurnPart.MOVE, controller.getTurnPart());
        //i try some invalid inputs and nothing changes
        controller.performTurn(coord(2,2, p1));
        controller.performTurn(coord(3,3, p2));
        controller.performTurn(coord(2,7, p2));
        //controller.performTurn(mess("peppino da capri", p2));
        assertEquals(TurnPart.MOVE, controller.getTurnPart());
        //the i give proper coordinates with the right player
        controller.performTurn(coord(1,2, p2));
        assertNotNull(controller.getModel().getGameboard().getBox(1,2).getOccupier());
        assertNull(controller.getModel().getGameboard().getBox(1,3).getOccupier());
        assertEquals(TurnPart.BUILD, controller.getTurnPart());
        //a few incorrect inputs again for the build
        controller.performTurn(coord(1,3, p1));
        controller.performTurn(coord(1,2, p2));
        controller.performTurn(coord(88,7, p2));
        controller.performTurn(new PlayerMove(ConfirmationEnum.No, p2));
        assertEquals(TurnPart.BUILD, controller.getTurnPart());
        controller.performTurn(coord(1,1, p2));
        assertEquals(1, controller.getModel().getGameboard().getBox(1,1).getTower().size());
        assertEquals(TurnPart.SELECT, controller.getTurnPart());
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
        assertEquals(TurnPart.SELECT, controller.getTurnPart());
        //then a correct one
        controller.performTurn(coord(4,1, p1));
        assertEquals(TurnPart.MOVE, controller.getTurnPart());
        //incorrect inputs
        controller.performTurn(coord(4,1, p1));
        controller.performTurn(coord(5,1, p1));
        controller.performTurn(coord(2,1, p1));
        controller.performTurn(coord(4,2, p2));
        assertEquals(TurnPart.MOVE, controller.getTurnPart());
        //then the right one
        controller.performTurn(coord(4,2, p1));
        assertEquals(TurnPart.BUILD, controller.getTurnPart());
        assertNotNull(controller.getModel().getGameboard().getBox(4,2).getOccupier());
        assertNull(controller.getModel().getGameboard().getBox(4,1).getOccupier());

        //incorrect inputs, among which a legit place where to build, but demeter is waiting for its effect confirmation
        controller.performTurn(coord(4,1, p1));
        controller.performTurn(coord(5,1, p1));
        controller.performTurn(coord(2,1, p1));
        controller.performTurn(coord(4,2, p2));
        assertEquals(TurnPart.BUILD, controller.getTurnPart());
        //controller.performTurn(mess("yes", p1));
        controller.performTurn(new PlayerMove(ConfirmationEnum.Yes, p1));
        controller.getModel().getGameboard().drawBoard();
        //the it won't let me build twice on the same level
        controller.performTurn(coord(4,3, p1));
        controller.performTurn(coord(4,3, p1));
        assertEquals(TurnPart.BUILD, controller.getTurnPart());
        assertEquals(1, controller.getModel().getGameboard().getBox(4,3).getTower().size());
        //a few invalid inputs as always
        controller.performTurn(coord(4,7, p1));
        controller.performTurn(coord(5,1, p1));
        controller.performTurn(coord(2,1, p1));
        controller.performTurn(coord(4,2, p2));
        assertEquals(TurnPart.BUILD, controller.getTurnPart());
        //the i give a legit second build coordinate and the turn will end
        controller.performTurn(coord(3,3, p1));
        assertEquals(1, controller.getModel().getGameboard().getBox(3,3).getTower().size());
        assertEquals(TurnPart.SELECT, controller.getTurnPart());

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
        ViewOffline viewOffline = new ViewOffline(lobbyList);
        Controller controller = new Controller(model, viewOffline);


        assertEquals(GamePart.GOD, controller.getGamePart());
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        //if the youngest player sends a string which is not equal to a god name nothing changes
        controller.chooseGods(mess("peppino", p3));
        assertEquals(3, controller.getGodChoiceTimes());
        assertEquals(0, controller.getListOfGods().size());
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        //if a player which is not the youngest proposes a god, nothing changes
        controller.chooseGods(mess("athena", p1));
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        assertEquals(3, controller.getGodChoiceTimes());
        assertEquals(0, controller.getListOfGods().size());
        //now the youngest proposes gods, so the GodchoiceTimes counter gets down
        controller.chooseGods(mess("athena", p3));
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        assertEquals(2, controller.getGodChoiceTimes());
        assertEquals(1, controller.getListOfGods().size());
        //the same god cannot be chosen more than once
        controller.chooseGods(mess("athena", p3));
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        assertEquals(2, controller.getGodChoiceTimes());
        assertEquals(1, controller.getListOfGods().size());
        controller.chooseGods(mess("pan", p3));
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        assertEquals(1, controller.getGodChoiceTimes());
        assertEquals(2, controller.getListOfGods().size());
        controller.chooseGods(mess("prometheus", p3));
        //now that the 3 gods have been chosen it passes to another state
        assertEquals(GodSetupPart.OLDERCHOOSES, controller.getGodSetupPart());
        assertEquals(0, controller.getGodChoiceTimes());
        assertEquals(3, controller.getListOfGods().size());
        //now the oldest player must choose, so if the other two demand something nothing changes
        controller.chooseGods(mess("athena", p3));
        controller.chooseGods(mess("athena", p2));
        assertEquals(GodSetupPart.OLDERCHOOSES, controller.getGodSetupPart());
        assertEquals(3, controller.getListOfGods().size());
        //a god not in the list given by the youngest cannot be chosen
        controller.chooseGods(mess("hephaestus", p1));
        assertEquals(GodSetupPart.OLDERCHOOSES, controller.getGodSetupPart());
        assertEquals(3, controller.getListOfGods().size());
        controller.chooseGods(mess("prometheus", p1));
        assertEquals(GodSetupPart.OTHERCHOOSES, controller.getGodSetupPart());
        assertEquals(2, controller.getListOfGods().size());
        //the other player will not be able to choose prometheus again, and neither the oldest player will not be able to choose again
        //or the youngest will be able to choose
        controller.chooseGods(mess("athena", p1));
        controller.chooseGods(mess("prometheus", p2));
        controller.chooseGods(mess("pan", p3));
        assertEquals(GodSetupPart.OTHERCHOOSES, controller.getGodSetupPart());
        assertEquals(2, controller.getListOfGods().size());

        //now the chooseGods will give back true, because this part of the setup is now concluded
        assertTrue(controller.chooseGods(mess("athena", p2)));


    }
    @Test
    void chooseGodsTestTwoPlayers( ) throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        Model model = new Model(lobbyList);
        ViewOffline viewOffline = new ViewOffline(lobbyList);
        Controller controller = new Controller(model, viewOffline);

        assertEquals(GamePart.GOD, controller.getGamePart());
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        //if the youngest player sends a string which is not equal to a god name nothing changes
        controller.chooseGods(mess("Nicola", p2));
        assertEquals(2, controller.getGodChoiceTimes());
        assertEquals(0, controller.getListOfGods().size());
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        //if a player which is not the youngest proposes a god, nothing changes
        controller.chooseGods(mess("athena", p1));
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        assertEquals(2, controller.getGodChoiceTimes());
        assertEquals(0, controller.getListOfGods().size());
        //now the youngest proposes gods, so the GodchoiceTimes counter gets down
        controller.chooseGods(mess("minotaur", p2));
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        assertEquals(1, controller.getGodChoiceTimes());
        assertEquals(1, controller.getListOfGods().size());
        //the same god cannot be chosen more than once
        controller.chooseGods(mess("minotaur", p2));
        assertEquals(GodSetupPart.INITIALCHOICE, controller.getGodSetupPart());
        assertEquals(1, controller.getGodChoiceTimes());
        assertEquals(1, controller.getListOfGods().size());
        controller.chooseGods(mess("demeter", p2));
        //now that the 2 gods have been chosen it passes to another state
        assertEquals(GodSetupPart.OLDERCHOOSES, controller.getGodSetupPart());
        assertEquals(0, controller.getGodChoiceTimes());
        assertEquals(2, controller.getListOfGods().size());
        //now the oldest player must choose, so if the other demands something nothing changes
        controller.chooseGods(mess("demeter", p2));
        assertEquals(GodSetupPart.OLDERCHOOSES, controller.getGodSetupPart());
        assertEquals(2, controller.getListOfGods().size());
        //a god not in the list given by the youngest cannot be chosen
        controller.chooseGods(mess("hephaestus", p1));
        assertEquals(GodSetupPart.OLDERCHOOSES, controller.getGodSetupPart());
        assertEquals(2, controller.getListOfGods().size());
        controller.chooseGods(mess("minotaur", p1));
        assertEquals(GodSetupPart.OTHERCHOOSES, controller.getGodSetupPart());
        assertEquals(1, controller.getListOfGods().size());
        //the other player will not be able to choose prometheus again, and neither the oldest player will not be able to choose again
        controller.chooseGods(mess("minotaur", p2));
        controller.chooseGods(mess("demeter", p1));
        assertEquals(GodSetupPart.OTHERCHOOSES, controller.getGodSetupPart());
        assertEquals(1, controller.getListOfGods().size());

        //now that the other player chooses correctly it returns true and the list will be empty
        assertTrue(controller.chooseGods(mess("demeter", p2)));
        assertEquals(GodSetupPart.OTHERCHOOSES, controller.getGodSetupPart());
        assertEquals(0, controller.getListOfGods().size());


    }

    @Test
    void updateTestTwoPlayers() throws DataFormatException {
        Player p1 = new Player("Alberto", 27, 4, 1998);
        Player p2 = new Player("Simone", 2, 5, 1998 );
        ArrayList<Player> lobbyList = new ArrayList<>();
        lobbyList.add(p1);
        lobbyList.add(p2);
        Model model = new Model(lobbyList);
        ViewOffline viewOffline = new ViewOffline(lobbyList);
        Controller controller = new Controller(model, viewOffline);
        //having already tested separately the methods the controller calls in this function i will just put cases where their inputs are all correct

        assertEquals(GamePart.GOD, controller.getGamePart());
        //now the initialization of gods
        controller.update(mess("pan", p2),null);
        controller.update(mess("apollo", p2), null);
        controller.update(mess("apollo", p1), null);
        controller.update(mess("pan", p2), null);
        assertEquals(GamePart.PLACE1, controller.getGamePart());
        //now the place part
        controller.update(coord(2,3, p2), null);
        controller.update(coord(1,2, p2), null);
        assertEquals(GamePart.PLACE2, controller.getGamePart());
        controller.update(coord(0,3, p1), null);
        controller.update(coord(1,4, p1), null);
        controller.getModel().getGameboard().drawBoard();
        assertEquals(GamePart.TURN, controller.getGamePart());
        //now the execution of a turn to show it remains in the TurnPart while rotating player
        assertEquals(controller.getModel().getCurrentPlayer(), p2);
        controller.update(coord(2,3 ,p2),null);
        controller.update(coord(2,4 ,p2),null);
        controller.update(coord(2,3 ,p2),null);
        assertEquals(controller.getModel().getCurrentPlayer(), p1);
        assertEquals(GamePart.TURN, controller.getGamePart());
        //now i remove a player just to show that by calling update with a single player it goes to WinnerPart
        controller.getModel().getPlayerList().remove(1);
        controller.update(mess("AOOOOOOOOO", p2),null);
        assertEquals(GamePart.CONCLUSION, controller.getGamePart());

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
        ViewOffline viewOffline = new ViewOffline(lobbyList);
        Controller controller = new Controller(model, viewOffline);
        //having already tested separately the methods the controller calls in this function i will just put cases where their inputs are all correct

        assertEquals(GamePart.GOD, controller.getGamePart());
        //now the initialization of gods
        controller.update(mess("pan", p3), null);
        controller.update(mess("apollo", p3), null);
        controller.update(mess("minotaur", p3), null);
        controller.update(mess("apollo", p1), null);
        controller.update(mess("pan", p2), null);
        controller.update(mess("minotaur", p3), null);
        assertEquals(GamePart.PLACE1, controller.getGamePart());
        //now the place part
        controller.update(coord(2,3, p3), null);
        controller.update(coord(1,2, p3), null);
        assertEquals(GamePart.PLACE2, controller.getGamePart());
        controller.update(coord(0,3, p2), null);
        controller.update(coord(1,4, p2), null);
        assertEquals(GamePart.PLACE3, controller.getGamePart());
        controller.update(coord(0,0, p1), null);
        controller.update(coord(4,4, p1), null);
        assertEquals(GamePart.TURN, controller.getGamePart());
        //now the execution of a turn to show it remains in the TurnPart while rotating player
        assertEquals(controller.getModel().getCurrentPlayer(), p3);
        controller.update(coord(2,3 ,p3), null);
        controller.update(coord(2,4 ,p3), null);
        controller.update(coord(2,3 ,p3), null);
        assertEquals(controller.getModel().getCurrentPlayer(), p2);
        assertEquals(GamePart.TURN, controller.getGamePart());

        //now i remove two players just to show that by calling update with a single player it goes to WinnerPart
        controller.getModel().getPlayerList().remove(2);
        controller.getModel().getPlayerList().remove(1);
        controller.update(mess("AOOOOOOOOO", p3), null);
        assertEquals(GamePart.CONCLUSION, controller.getGamePart());

        clearBoardForFutureTests(controller.getModel().getGameboard());
    }
}