package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.BoardPhotography;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.*;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.server.observers.Observer;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.ConfirmationEnum;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

import java.util.List;
import java.util.Scanner;

public class ViewOffline extends Observable <PlayerMove> implements Observer<BoardPhotography> {

    private Player player;
    private Scanner scanner;
    private List<Player> viewList;

    private boolean done = false;

    private ModelMessage currentModelMessage;

    public ModelMessage getCurrentModelMessage() { return currentModelMessage; }

    public void setCurrentModelMessage(ModelMessage currentModelMessage) { this.currentModelMessage = currentModelMessage; }

    //solo per testare, se togli rimuovi controllo con +++ nella playerMove
    public ViewOffline(List<Player> viewList) {
        this.viewList = viewList;
        int index = 0;
        player = viewList.get(index);
        scanner = new Scanner(System.in);
        currentModelMessage = new ModelMessage(ModelMessageType.NEEDSGODNAME, " Welcome to the game");

    }

    public ViewOffline(Player player) {
        this.player = player;
        scanner = new Scanner(System.in);
    }


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
                if (currentModelMessage.getModelMessageType() == ModelMessageType.NEEDSCOORDINATES) {
                    if(s.length() == 3 && s.charAt(1) == ',') {
                        handleCoordinates(s);
                    }
                }

                else if (currentModelMessage.getModelMessageType() == ModelMessageType.NEEDSGODNAME){//if it is needed to send a confirmation to the will of activating god's powers, or select god's
                    PlayerMove message = new PlayerMove(s, this.player);
                    notify(message, null);
                }

                else if(currentModelMessage.getModelMessageType() == ModelMessageType.NEEDSCONFIRMATION){
                    handleConfirmation(s);

                }

                else if(currentModelMessage.getModelMessageType() == ModelMessageType.GAMEOVER){
                    done = true;
                }



        }
    }

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

    public void changeViewHandler () {

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

    public void run (){
        while(!done) {
            playerMove();
        }
    }


    @Override
    public void update(BoardPhotography photography, Object obj) {

        photography.show();
        if ( obj instanceof ModelMessage) {
            currentModelMessage = (ModelMessage) obj;
            System.out.println(currentModelMessage.getMessage());
        }


    }


}
