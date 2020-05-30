package it.polimi.ingsw.server.view;

import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.*;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.ConfirmationEnum;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

import java.util.List;
import java.util.Scanner;

/**
 * a version of the MVC component ment to test offline usage of controller and model
 * it features an array of players through which one can iterate to simulate a real game
 * inserti "+++" in cli will allow that
 */
public class ViewOffline extends Observable <PlayerMove> implements Observer<BoardPhotography> {

    private Player player;
    private Scanner scanner;
    private List<Player> viewList;

    private boolean done = false;

    private ModelMessage currentModelMessage;

    public ModelMessage getCurrentModelMessage() { return currentModelMessage; }

    public void setCurrentModelMessage(ModelMessage currentModelMessage) { this.currentModelMessage = currentModelMessage; }

    /**
     * constructor that initializes the scanner of system.in
     * the first currentModelMessage
     * and the default starting player
     * @param viewList list of players that will be playing this offline match
     */
    public ViewOffline(List<Player> viewList) {
        this.viewList = viewList;
        int index = 0;
        player = viewList.get(index);
        scanner = new Scanner(System.in);
        currentModelMessage = new ModelMessage(ModelMessageType.GODNAME, " Welcome to the game");

    }

    public ViewOffline(Player player) {
        this.player = player;
        scanner = new Scanner(System.in);
    }


    /**
     * method that asks input from command interface and processes it
     * if "+++" needs to change the player which is current
     * the basing on the type of currentModelMessage
     * if COORDINATES will wait for a "x,y" format and when received will send a coordinate playermove
     * if CONFIRMATION will wait for "yes" or "no" and when received will send a confirmation playermove
     * if GODNAME will send directly send the string as a message playermove
     */
    private void playerMove(){
        while (!done) {
            System.out.println(player.getName()+", you're currently handling the View, insert a casual string if you need to know what to do");
            String s;
            do {
                s = scanner.next();
            } while (s.length()<2);
            if (s.equals("+++")){
                changeViewHandler();
            }
                if (currentModelMessage.getModelMessageType() == ModelMessageType.COORDINATES) {
                    if(s.length() == 3 && s.charAt(1) == ',') {
                        handleCoordinates(s);
                    }
                }

                else if (currentModelMessage.getModelMessageType() == ModelMessageType.GODNAME){//if it is needed to send a confirmation to the will of activating god's powers, or select god's
                    PlayerMove message = new PlayerMove(s, this.player);
                    notify(message, null);
                }

                else if(currentModelMessage.getModelMessageType() == ModelMessageType.CONFIRMATION){
                    handleConfirmation(s);

                }

                else if(currentModelMessage.getModelMessageType() == ModelMessageType.GAMEOVER){
                    done = true;
                }



        }
    }

    /**
     * processes an input like "x,y" and sends it to the controller as a coordinate playermove
     * @param s string taken from playermove method (CLI Input)
     */
    private void handleCoordinates (String s) {
    try {

        String[] inputs = s.split(",");

        int row;
        int column;
        row = Integer.parseInt(inputs[0]);
        column = Integer.parseInt(inputs[1]);
        PlayerMove message = new PlayerMove(row, column, this.player);

        notify(message, null);
    } catch (NumberFormatException e) {
        System.out.println(" incorrect input, try again ");
    }
    }

    /**
     * metodo that iterates on the different players to set the one that sends playermoves
     */
    private void changeViewHandler () {

        if(player == viewList.get(0)) { player = viewList.get(1); }

        else {
            if (viewList.size() == 2 && (player == viewList.get(1)) ) {
                    player = viewList.get(0);
            }

            if (viewList.size() == 3) {
                if (player == viewList.get(1)) {
                    player = viewList.get(2);
                }
                else if (player == viewList.get(2)) {
                    player = viewList.get(0);
                }
            }
        }
    }

    /**
     * processes input to send a confirmation playermove
     * @param s string from CLI passed from playermove method
     */
    private void handleConfirmation (String s) {

        ConfirmationEnum confirmation;

        if (s.equalsIgnoreCase("YES")) {
            confirmation = ConfirmationEnum.YES;
            PlayerMove message = new PlayerMove(confirmation, this.player);
            notify(message, null);
        } else if (s.equalsIgnoreCase("NO")) {
            confirmation = ConfirmationEnum.NO;
            PlayerMove message = new PlayerMove(confirmation, this.player);
            notify(message, null);
        }
    }


    /**
     * method that continues to ask playermoves until dome is brought to false
     *
     */
    public void run (){
        while(!done) {
            playerMove();
        }
    }


    /**
     * called when model does notify, it shows the photography received
     * memorizes the new modelmessage
     * and eventually prints the ModelError
     *
     * @param photography the update of the board
     * @param obj the modelMessage
     */
    @Override
    public void update(BoardPhotography photography, Object obj) {

        photography.show();
        if ( obj instanceof ModelMessage) {
            currentModelMessage = (ModelMessage) obj;
            System.out.println(currentModelMessage.getMessage());
            if (currentModelMessage.getModelError() != ModelError.NONE) {
                System.out.println("ERROR: "+currentModelMessage.getModelError().toString());
            }
        }


    }


}
