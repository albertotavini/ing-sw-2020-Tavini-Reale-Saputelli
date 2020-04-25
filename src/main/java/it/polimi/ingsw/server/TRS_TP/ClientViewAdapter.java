package it.polimi.ingsw.server.TRS_TP;

import it.polimi.ingsw.server.model.Date;

public class ClientViewAdapter {

    //può essere una classe cli o gui
    private static UserInterface userInterface;

    public static void setTypeInterface(UserInterface ui){

        userInterface = ui;
    }




    public static boolean askIfPlayerWantsToCreate() { return userInterface.askIfPlayerWantsToCreate(); }

    public static MenuMessages askForInfoToCreateLobby(){
        return userInterface.askForInfoToCreateLobby();
    }

    public static boolean askIfWantsToParticipateLobbyPublic() {return userInterface.askIfWantsToParticipateLobbyPublic();}

    public static MenuMessages askForInfoToParticipateLobby(boolean isPublic) {
        return userInterface.askForInfoToParticipateLobby(isPublic);
    }

    public static String askForName() { return userInterface.askForName(); }

    public static Date askForDate() { return userInterface.askForDate(); }

    public static void printMessage(String message) { userInterface.printMessage(message); }

    public static boolean askForConfirm(String message) { return userInterface.askForConfirm(message); }



}
