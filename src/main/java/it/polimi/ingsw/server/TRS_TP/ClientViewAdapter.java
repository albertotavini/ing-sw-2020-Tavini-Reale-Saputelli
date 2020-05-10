package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.model.BoardPhotography;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

public class ClientViewAdapter {

    //può essere una classe cli o gui
    private static MenuUserInterface menuUserInterface;
    private static InGameUserInterface inGameUserInterface;






    public static void setTypeInterface(MenuUserInterface menuUi, InGameUserInterface inGameUi){

        menuUserInterface = menuUi;
        inGameUserInterface = inGameUi;
    }




    public static MenuMessage askForInfoToCreateLobby(String creator){
        return menuUserInterface.askForInfoToCreateLobby(creator);
    }

    public static MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {
        return menuUserInterface.askForInfoToParticipateLobby(isPublic, namePlayer);
    }

    public static String askForName() { return menuUserInterface.askForName(); }

    public static Date askForDate() { return menuUserInterface.askForDate(); }

    public static void printMessage(String message) { menuUserInterface.printMessage(message); }

    public static boolean askBooleanQuestion(String message) { return menuUserInterface.askBooleanQuestion(message); }






    //IN GAME METHODS

    public static PlayerMove askForCoordinates(String message){return inGameUserInterface.askForCoordinates(message);}

    public static PlayerMove askForInGameConfirmation(String message){return inGameUserInterface.askForInGameConfirmation(message);}

    public static PlayerMove askForGodName(String message){return inGameUserInterface.askForGodName(message);}

    public static boolean updateBoard(BoardPhotography boardPhotography){return InGameUserInterface.clientBoardPhotography.updateClientBoardPhotography(boardPhotography);}

    public static void showBoard(BoardPhotography boardPhotography){inGameUserInterface.showBoard(boardPhotography);}










}
