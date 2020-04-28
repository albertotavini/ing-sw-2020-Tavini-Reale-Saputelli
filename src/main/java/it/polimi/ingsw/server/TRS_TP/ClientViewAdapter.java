package it.polimi.ingsw.server.TRS_TP;

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




    public static boolean askIfPlayerWantsToCreate() { return menuUserInterface.askIfPlayerWantsToCreate(); }

    public static MenuMessages askForInfoToCreateLobby(){
        return menuUserInterface.askForInfoToCreateLobby();
    }

    public static boolean askIfWantsToParticipateLobbyPublic() {return menuUserInterface.askIfWantsToParticipateLobbyPublic();}

    public static MenuMessages askForInfoToParticipateLobby(boolean isPublic) {
        return menuUserInterface.askForInfoToParticipateLobby(isPublic);
    }

    public static String askForName() { return menuUserInterface.askForName(); }

    public static Date askForDate() { return menuUserInterface.askForDate(); }

    public static void printMessage(String message) { menuUserInterface.printMessage(message); }

    public static boolean askForConfirm(String message) { return menuUserInterface.askForConfirm(message); }






    //IN GAME METHODS

    public static PlayerMove askForCoordinates(String message){return inGameUserInterface.askForCoordinates(message);}

    public static PlayerMove askForInGameConfirmation(String message){return inGameUserInterface.askForInGameConfirmation(message);}

    public static PlayerMove askForGodName(String message){return inGameUserInterface.askForGodName(message);}










}
