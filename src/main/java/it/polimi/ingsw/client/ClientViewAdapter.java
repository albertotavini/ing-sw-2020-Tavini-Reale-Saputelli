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

    /**
     * this method is used to switch from the first type of gui to the second
     */
    static void fromMenuToInGameGui() {

        if(menuUserInterface instanceof MenuGui){

            ((MenuGui) menuUserInterface).setMenuGuiVisible(false);

        }


        if(inGameUserInterface instanceof InGameGui){

            ((InGameGui) inGameUserInterface).setInGameGuiVisible(true);

        }

    }

    /**
     * this method is used to switch from the first type of gui to the second
     */
    static void fromInGameGuiToMenu() {

        if(menuUserInterface instanceof MenuGui){

            ((MenuGui) menuUserInterface).setMenuGuiVisible(true);

        }


        if(inGameUserInterface instanceof InGameGui){

            ((InGameGui) inGameUserInterface).setInGameGuiVisible(false);

        }

    }

    /**
     * this method is meant to be called to clear some parts of the interfaces in order to start a new match with no info on
     * the previous one in the way
     */
    static void resetInterfaceForNewMatch() {

        if(menuUserInterface instanceof MenuGui) ((MenuGui) menuUserInterface).resetMenuGui();

        if(inGameUserInterface instanceof InGameGui) ((InGameGui) inGameUserInterface).resetInGameGui();

        if (inGameUserInterface instanceof InGameCli) ((InGameCli) inGameUserInterface).resetGodInfo();
    }


    /**
     * calls the gui or cli version of the method
     *
     * @param creator name of the creator of the lobby
     * @return message to be sent to server
     */
    static MenuMessage askForInfoToCreateLobby(String creator){
        return menuUserInterface.askForInfoToCreateLobby(creator);
    }

    /**
     * calls the gui or cli version of the method
     *
     * @param isPublic true if public false if private
     * @param namePlayer that wants to join
     * @return message to be sent to server
     */
    static MenuMessage askForInfoToParticipateLobby(boolean isPublic, String namePlayer) {
        return menuUserInterface.askForInfoToParticipateLobby(isPublic, namePlayer);
    }

    /**
     * calls the gui or cli version of the method
     *
     * @return string with name
     */
    static String askForName() { return menuUserInterface.askForName(); }

    /**
     * calls the gui or cli version of the method
     *
     * @return string with the date
     */
    static Date askForDate() { return menuUserInterface.askForDate(); }

    /**
     * calls the gui or cli version of the method
     *
     * @param message to be printed
     */
    static void printMenuMessage(String message) { menuUserInterface.printMenuMessage(message); }

    /**
     *  calls the gui or cli version of the method
     *
     * @param message to be displayed with question to be answered
     * @return true if the client says yes, false if he says no
     */
    static boolean askBooleanQuestion(String message) { return menuUserInterface.askBooleanQuestion(message); }






    //IN GAME METHODS

    /**
     * calls the CLI or GUI version of the method
     *
     * @param message to be displayed to user
     * @return playermove to be sent
     */
    static PlayerMove askForCoordinates(String message){return inGameUserInterface.askForCoordinates(message);}
    /**
     * calls the CLI or GUI version of the method
     *
     * @param message to be displayed to user
     * @return playermove to be sent
     */
    static PlayerMove askForInGameConfirmation(String message){return inGameUserInterface.askForInGameConfirmation(message);}
    /**
     * calls the CLI or GUI version of the method
     *
     * @param message to be displayed to user
     * @return playermove to be sent
     */
    static PlayerMove askForGodName(String message){return inGameUserInterface.askForGodName(message);}

    /**
     * calls the CLI or GUI version of the method
     *
     * @param boardPhotography to be shown
     * @return true if has changed
     */
    static boolean updateBoard(BoardPhotography boardPhotography){

        if(inGameUserInterface instanceof InGameCli) {
            return ((InGameCli) inGameUserInterface).clientBoardPhotography.updateClientBoardPhotography(boardPhotography);}

        else {

            return ((InGameGui) inGameUserInterface).clientBoardPhotography.updateClientBoardPhotography(boardPhotography);
        }

    }

    /**
     * calls the CLI or GUI version of the method
     *
     * @param boardPhotography to be shown
     */
    static void showBoard(BoardPhotography boardPhotography){inGameUserInterface.showBoard(boardPhotography);}

    /**
     * calls the CLI or GUI version of the method
     *
     * @param message to be printed
     */
    static void printInGameMessage(String message){ inGameUserInterface.printInGameMessage(message); }

    /**
     * calls the CLI or GUI version of the method
     *
     * @param message to be printed on secondary path
     */
    static void printSecondaryInGameMessage(String message) {inGameUserInterface.printSecondaryInGameMessage(message);}

    /**
     * calls the CLI or GUI version of the method
     *
     * @param message with info on the god chosen
     * @param yours true if the god has been chosen by the user of this interface, false if not
     */
    static void showChosenGods(ModelMessage message, boolean yours) {inGameUserInterface.showChosenGods(message, yours); }

    /**
     * this method is called when a message has to be send on the chat of the GUI
     *
     * @param message to be sent on chat
     */
    static void sendChatMessage(String message){
        clientFsm.sendChatMessage(PlayerMove.buildChatPlayerMove(message));
    }

    /**
     * when a message is sent on the chat all the GUI's need to refresh it and show the new message, that is the role of this
     *method
     *
     * @param message to be added to the chat
     */
    static void refreshChat(String message) {

        if(inGameUserInterface instanceof InGameGui){

            ((InGameGui) inGameUserInterface).updateChat(message);

        }

    }











}
