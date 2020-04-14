package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.View;
import it.polimi.ingsw.server.view.playerMove;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    //support methods to build playermoves, they're built the same way in the view
    public  static playerMove coord(int row, int column, Player p) throws DataFormatException {
        playerMove playermove = new playerMove(row, column, p);
        playermove.setGenericMessage("nothing interesting here");
        return playermove;
    }
    public static playerMove mess(String s, Player p) throws DataFormatException{
        playerMove playermove =new playerMove(7,7, p);
        playermove.setGenericMessage(s);
        return playermove;

    }
    //support method to clear the board
    public void clearBoardForFutureTests (Board board){
        for (int i=0; i<5; i++) {
            for (int j= 0; j < 5; j++) {

                //setting every box's level at 0
                while (board.getBox(i, j).getTowerSize() != 0)
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
        assertTrue(controller.getModel().getGameboard().getBox(2,2).getOccupier() == null );
        assertTrue(controller.getCurrentPlaceState().equals(FirstPlacingState.getInstance()));
        controller.performPlace(coord(2,2, p2));
        assertFalse(controller.getModel().getGameboard().getBox(2,2).getOccupier() == null );
        assertTrue(controller.getModel().getGameboard().getBox(2,2).getOccupier().getPlayer().equals(p2) );
        assertTrue(controller.getCurrentPlaceState().equals(SecondPlacingState.getInstance()));
        controller.getModel().getGameboard().drawBoard();
        //if i try to place on the same spot or outside the board the state doesn't change and nothing happens
        controller.performPlace(coord(2,2, p2));
        controller.performPlace(coord(8,4, p2));
        assertTrue(controller.getCurrentPlaceState().equals(SecondPlacingState.getInstance()));
        assertTrue(controller.performPlace(coord(3,1, p2)));
        assertTrue(controller.getModel().getGameboard().getBox(3,1).getOccupier().getPlayer().equals(p2) );
        //now the youngest player concluded the place, it switches currentplayer and go back to state one
        assertTrue(controller.getCurrentPlaceState().equals(FirstPlacingState.getInstance()));
        assertFalse(controller.getModel().getCurrentPlayer().equals(p2));
        assertTrue(controller.getModel().getCurrentPlayer().equals(p1));
        //now the youngest player will not be able to place
        controller.performPlace(coord(0,1, p2));
        assertTrue(controller.getModel().getGameboard().getBox(0,1).getOccupier() == null );
        //now the other player can do his first place
        controller.performPlace(coord(0,1, p1));
        assertFalse(controller.getModel().getGameboard().getBox(0,1).getOccupier() == null );
        assertTrue(controller.getModel().getGameboard().getBox(0,1).getOccupier().getPlayer().equals(p1) );
        assertTrue(controller.getCurrentPlaceState().equals(SecondPlacingState.getInstance()));
        controller.getModel().getGameboard().drawBoard();
        //it's not allowed to place on an opponent's worker
        controller.performPlace(coord(2,2, p1));
        assertFalse(controller.getModel().getGameboard().getBox(2,2).getOccupier().getPlayer().equals(p1) );
        assertTrue(controller.getCurrentPlaceState().equals(SecondPlacingState.getInstance()));
        controller.getModel().getGameboard().drawBoard();
        //now the place is concluded, the state goes back to one and performPlace itself will return true again
        assertTrue(controller.performPlace(coord(4,3, p1)));
        assertTrue(controller.getModel().getGameboard().getBox(4,3).getOccupier().getPlayer().equals(p1) );
        assertTrue(controller.getCurrentPlaceState().equals(FirstPlacingState.getInstance()));

        //the model doesn't differ if the players are two or three, so there is no need to test the other case
        clearBoardForFutureTests(controller.getModel().getGameboard());
    }

    @Test
    void performTurnTest(){


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
            assertTrue(controller.getCurrentGameState().equals(GodPart.getInstance()));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            //if the youngest player sends a string which is not equal to a god name nothing changes
            controller.chooseGods(mess("peppino", p3));
            assertTrue(controller.getGodChoiceTimes() == 3);
            assertTrue(controller.getListOfGods().size() == 0);
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            //if a player which is not the youngest proposes a god, nothing changes
            controller.chooseGods(mess("athena", p1));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 3);
            assertTrue(controller.getListOfGods().size() == 0);
            //now the youngest proposes gods, so the GodchoiceTimes counter gets down
            controller.chooseGods(mess("athena", p3));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 2);
            assertTrue(controller.getListOfGods().size() == 1);
            //the same god cannot be chosen more than once
            controller.chooseGods(mess("athena", p3));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 2);
            assertTrue(controller.getListOfGods().size() == 1);
            controller.chooseGods(mess("pan", p3));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 1);
            assertTrue(controller.getListOfGods().size() == 2);
            controller.chooseGods(mess("prometheus", p3));
            //now that the 3 gods have been chosen it passes to another state
            assertTrue(controller.getCurrentGodSetupState().equals(OlderChooses.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 0);
            assertTrue(controller.getListOfGods().size() == 3);
            //now the oldest player must choose, so if the other two demand something nothing changes
            controller.chooseGods(mess("athena", p3));
            controller.chooseGods(mess("athena", p2));
            assertTrue(controller.getCurrentGodSetupState().equals(OlderChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 3);
            //a god not in the list given by the youngest cannot be chosen
            controller.chooseGods(mess("hephaestus", p1));
            assertTrue(controller.getCurrentGodSetupState().equals(OlderChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 3);
            controller.chooseGods(mess("prometheus", p1));
            assertTrue(controller.getCurrentGodSetupState().equals(OtherChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 2);
            //the other player will not be able to choose prometheus again, and neither the oldest player will not be able to choose again
            //or the youngest will be able to choose
            controller.chooseGods(mess("athena", p1));
            controller.chooseGods(mess("prometheus", p2));
            controller.chooseGods(mess("pan", p3));
            assertTrue(controller.getCurrentGodSetupState().equals(OtherChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 2);

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
            assertTrue(controller.getCurrentGameState().equals(GodPart.getInstance()));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            //if the youngest player sends a string which is not equal to a god name nothing changes
            controller.chooseGods(mess("Nicola", p2));
            assertTrue(controller.getGodChoiceTimes() == 2);
            assertTrue(controller.getListOfGods().size() == 0);
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            //if a player which is not the youngest proposes a god, nothing changes
            controller.chooseGods(mess("athena", p1));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 2);
            assertTrue(controller.getListOfGods().size() == 0);
            //now the youngest proposes gods, so the GodchoiceTimes counter gets down
            controller.chooseGods(mess("minotaur", p2));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 1);
            assertTrue(controller.getListOfGods().size() == 1);
            //the same god cannot be chosen more than once
            controller.chooseGods(mess("minotaur", p2));
            assertTrue(controller.getCurrentGodSetupState().equals(InitialChoice.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 1);
            assertTrue(controller.getListOfGods().size() == 1);
            controller.chooseGods(mess("demeter", p2));
            //now that the 2 gods have been chosen it passes to another state
            assertTrue(controller.getCurrentGodSetupState().equals(OlderChooses.getInstance()));
            assertTrue(controller.getGodChoiceTimes() == 0);
            assertTrue(controller.getListOfGods().size() == 2);
            //now the oldest player must choose, so if the other demands something nothing changes
            controller.chooseGods(mess("demeter", p2));
            assertTrue(controller.getCurrentGodSetupState().equals(OlderChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 2);
            //a god not in the list given by the youngest cannot be chosen
            controller.chooseGods(mess("hephaestus", p1));
            assertTrue(controller.getCurrentGodSetupState().equals(OlderChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 2);
            controller.chooseGods(mess("minotaur", p1));
            assertTrue(controller.getCurrentGodSetupState().equals(OtherChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 1);
            //the other player will not be able to choose prometheus again, and neither the oldest player will not be able to choose again
            controller.chooseGods(mess("minotaur", p2));
            controller.chooseGods(mess("demeter", p1));
            assertTrue(controller.getCurrentGodSetupState().equals(OtherChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 1);

            //now that the other player chooses correctly it returns true and the list will be empty
            assertTrue(controller.chooseGods(mess("demeter", p2)));
            assertTrue(controller.getCurrentGodSetupState().equals(OtherChooses.getInstance()));
            assertTrue(controller.getListOfGods().size() == 0);






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

        assertTrue(controller.getCurrentGameState().equals(GodPart.getInstance()));
        //now the initialization of gods
        if (true) {
            controller.update(mess("pan", p2));
            controller.update(mess("apollo", p2));
            controller.update(mess("apollo", p1));
            controller.update(mess("pan", p2));
        }
        assertTrue(controller.getCurrentGameState().equals(PlacePart1.getInstance()));
        //now the place part
        if (true) {
            controller.update(coord(2,3, p2));
            controller.update(coord(1,2, p2));
            assertTrue(controller.getCurrentGameState().equals(PlacePart2.getInstance()));
            controller.update(coord(0,3, p1));
            controller.update(coord(1,4, p1));
        }
        assertTrue(controller.getCurrentGameState().equals(TurnPart.getInstance()));
        //now the execution of a turn to show it remains in the TurnPart while rotating player
        assertTrue(controller.getModel().getCurrentPlayer().equals(p2));
        if (true) {
            controller.update(coord(2,3 ,p2));
            controller.update(coord(2,4 ,p2));
            controller.update(coord(2,3 ,p2));
        }
        assertTrue(controller.getModel().getCurrentPlayer().equals(p1));
        assertTrue(controller.getCurrentGameState().equals(TurnPart.getInstance()));

        //now i remove a player just to show that by calling update with a single player it goes to WinnerPart
        controller.getModel().getPlayerList().remove(1);
        controller.update(mess("AOOOOOOOOO", p2));
        assertTrue(controller.getCurrentGameState().equals(WinnerPart.getInstance()));

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

        assertTrue(controller.getCurrentGameState().equals(GodPart.getInstance()));
        //now the initialization of gods
        if (true) {
            controller.update(mess("pan", p3));
            controller.update(mess("apollo", p3));
            controller.update(mess("minotaur", p3));
            controller.update(mess("apollo", p1));
            controller.update(mess("pan", p2));
            controller.update(mess("minotaur", p3));
        }
        assertTrue(controller.getCurrentGameState().equals(PlacePart1.getInstance()));
        //now the place part
        if (true) {
            controller.update(coord(2,3, p3));
            controller.update(coord(1,2, p3));
            assertTrue(controller.getCurrentGameState().equals(PlacePart2.getInstance()));
            controller.update(coord(0,3, p2));
            controller.update(coord(1,4, p2));
            assertTrue(controller.getCurrentGameState().equals(PlacePart3.getInstance()));
            controller.update(coord(0,0, p1));
            controller.update(coord(4,4, p1));
        }
        assertTrue(controller.getCurrentGameState().equals(TurnPart.getInstance()));
        //now the execution of a turn to show it remains in the TurnPart while rotating player
        assertTrue(controller.getModel().getCurrentPlayer().equals(p3));
        if (true) {
            controller.update(coord(2,3 ,p3));
            controller.update(coord(2,4 ,p3));
            controller.update(coord(2,3 ,p3));
        }
        assertTrue(controller.getModel().getCurrentPlayer().equals(p2));
        assertTrue(controller.getCurrentGameState().equals(TurnPart.getInstance()));

        //now i remove two players just to show that by calling update with a single player it goes to WinnerPart
        controller.getModel().getPlayerList().remove(2);
        controller.getModel().getPlayerList().remove(1);
        controller.update(mess("AOOOOOOOOO", p3));
        assertTrue(controller.getCurrentGameState().equals(WinnerPart.getInstance()));


        clearBoardForFutureTests(controller.getModel().getGameboard());
    }
}