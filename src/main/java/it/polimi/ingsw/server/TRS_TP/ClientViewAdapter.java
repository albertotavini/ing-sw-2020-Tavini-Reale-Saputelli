package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.model.Date;

public class ClientViewAdapter {

    //può essere una classe cli o gui
    private static MenuUserInterface menuUserInterface;

    public static void setTypeInterface(MenuUserInterface ui){

        menuUserInterface = ui;
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



}
