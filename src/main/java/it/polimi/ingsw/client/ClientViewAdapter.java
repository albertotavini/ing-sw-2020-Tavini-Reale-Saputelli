package it.polimi.ingsw.client;

import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.server.model.Date;
import it.polimi.ingsw.bothsides.onlinemessages.setupmessages.MenuMessage;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

/**
 * this class has the role to filter the methods from the interfaces and call the ones for the CLI or the ones for the GUI depending on
 * which interface is being used
 */
public class ClientViewAdapter {

    private ClientViewAdapter(){
        //hiding the default constructor
    }


    //può essere una classe cli o gui
    private static MenuUserInterface menuUserInterface;
    private static InGameUserInterface inGameUserInterface;

    private static ClientFsm clientFsm;

    static boolean isMenuInterfaceAGui() {

        return menuUserInterface instanceof MenuGui;
    }

    public static boolean isInGameInterfaceAGui(){

        return inGameUserInterface instanceof InGameGui;
    }


    static void setTypeInterface(MenuUserInterface menuUi, InGameUserInterface inGameUi){

        menuUserInterface = menuUi;
        inGameUserInterface = inGameUi;
    }

    static void setClientFsm(ClientFsm fsm){

        clientFsm = fsm;
    }

    static ClientFsm getClientFsm() {

        return clientFsm;
    }

    static void fromMenuToInGameGui() {

        if(menuUserInterface instanceof MenuGui){

            ((MenuGui) menuUserInterface).setMenuGuiVisible(false);

        }


        if(inGameUserInterface instanceof InGameGui){

            ((InGameGui) inGameUserInterface).setInGameGuiVisible(true);

        }

    }

    static void fromInGameGuiToMenu() {

        if(menuUserInterface instanceof MenuGui){

            ((MenuGui) menuUserInterface).setMenuGuiVisible(true);

        }


        if(inGameUserInterface instanceof InGameGui){

            ((InGameGui) inGameUserInterface).setInGameGuiVisible(false);

        }

    }

    static void resetGuiForNewMatch() {

        if(menuUserInterface instanceof MenuGui) ((MenuGui) menuUserInterface).resetMenuGui();

        if(inGameUserInterface instanceof InGameGui) ((InGameGui) inGameUserInterface).resetInGameGui();

    }




    static MenuMessage askForInfoToCreateLobby(String creator){
        return menuUserInterface.askForInfoToCreateLobby(creator);
    }

    static MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {
        return menuUserInterface.askForInfoToParticipateLobby(isPublic, namePlayer);
    }

    static String askForName() { return menuUserInterface.askForName(); }

    static Date askForDate() { return menuUserInterface.askForDate(); }

    static void printMenuMessage(String message) { menuUserInterface.printMenuMessage(message); }

    static boolean askBooleanQuestion(String message) { return menuUserInterface.askBooleanQuestion(message); }






    //IN GAME METHODS

    static PlayerMove askForCoordinates(String message){return inGameUserInterface.askForCoordinates(message);}

    static PlayerMove askForInGameConfirmation(String message){return inGameUserInterface.askForInGameConfirmation(message);}

    static PlayerMove askForGodName(String message){return inGameUserInterface.askForGodName(message);}

    static boolean updateBoard(BoardPhotography boardPhotography){

        if(inGameUserInterface instanceof InGameCli) {
            return ((InGameCli) inGameUserInterface).clientBoardPhotography.updateClientBoardPhotography(boardPhotography);}

        else {

            return ((InGameGui) inGameUserInterface).clientBoardPhotography.updateClientBoardPhotography(boardPhotography);
        }

    }

    static void showBoard(BoardPhotography boardPhotography){inGameUserInterface.showBoard(boardPhotography);}

    static void printInGameMessage(String message){ inGameUserInterface.printInGameMessage(message); }

    static void printSecondaryInGameMessage(String message) {inGameUserInterface.printSecondaryInGameMessage(message);}

    static void showChosenGods(ModelMessage message, boolean yours) {inGameUserInterface.showChosenGods(message, yours); }

    static void sendChatMessage(String message){
        clientFsm.sendChatMessage(PlayerMove.buildChatPlayerMove(message));
    }

    static void refreshChat(String message) {

        if(inGameUserInterface instanceof InGameGui){

            ((InGameGui) inGameUserInterface).updateChat(message);

        }

    }











}
