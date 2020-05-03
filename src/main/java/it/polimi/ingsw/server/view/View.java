package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.BoardPhotography;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.observers.ModelMessage.*;
import it.polimi.ingsw.server.observers.ObservableVC;
import it.polimi.ingsw.server.observers.ObserverMV;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

import java.util.List;
import java.util.Scanner;

public class View extends ObservableVC <PlayerMove> implements ObserverMV<BoardPhotography> {

    private Player player;
    private Scanner scanner;
    private List<Player> viewList;
    private int Index;

    private boolean done = false;
    private ConfirmationEnum confirmation;

    private ModelMessage currentModelMessage;

    public ModelMessage getCurrentModelMessage() { return currentModelMessage; }

    public void setCurrentModelMessage(ModelMessage currentModelMessage) { this.currentModelMessage = currentModelMessage; }

    //solo per testare, se togli rimuovi controllo con +++ nella playerMove
    public View (List<Player> viewList) {
        this.viewList = viewList;
        Index=0;
        player = viewList.get(Index);
        scanner = new Scanner(System.in);
        currentModelMessage = new ModelMessage(ModelMessageType.NeedsGodName, " Welcome to the game");

    }

    public View (Player player) {
        this.player = player;
        scanner = new Scanner(System.in);


    }


    public void playerMove (){
        while (true) {
            System.out.println(player.getName()+", you're currently handling the View, insert a casual string if you need to know what to do");
            String s;
            do {
                s = scanner.next();
            } while (s.length()<2);
            if (s.equals("+++")){
                changeViewHandler();
            }
            try {
                if (currentModelMessage.getModelMessageType() == ModelMessageType.NeedsCoordinates) {
                    if(s.length() == 3 && s.charAt(1) == ',') {
                        String[] inputs = s.split(",");

                        int row, column;
                        row = Integer.parseInt(inputs[0]);
                        column = Integer.parseInt(inputs[1]);
                        PlayerMove message = new PlayerMove(row, column, this.player);

                        notify(message);
                    }
                }

                else if (currentModelMessage.getModelMessageType() == ModelMessageType.NeedsGodName){//if it is needed to send a confirmation to the will of activating god's powers, or select god's
                    PlayerMove message = new PlayerMove(s, this.player);
                    notify(message);
                }

                else if(currentModelMessage.getModelMessageType() == ModelMessageType.NeedsConfirmation){
                    if(s.toUpperCase().equals("YES")){
                        confirmation = ConfirmationEnum.Yes;
                        PlayerMove message = new PlayerMove(confirmation, this.player);
                        notify(message);
                    }
                    else if(s.toUpperCase().equals("NO")){
                        confirmation = ConfirmationEnum.No;
                        PlayerMove message = new PlayerMove(confirmation, this.player);
                        notify(message);
                    }
                }

                else if(currentModelMessage.getModelMessageType() == ModelMessageType.GameOver){
                    done = true;
                }


            } catch (NumberFormatException e) {
                System.out.println(" incorrect input, try again ");
            }
        }


    }

    public void changeViewHandler () {

        if(player == viewList.get(0)) { player = viewList.get(1); }

        else {
            if (viewList.size() == 2) {
                if (player == viewList.get(1)) {
                    player = viewList.get(0);
                }
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

    public void run (){
        while(!done) {
            playerMove();
        }
    }

    /*@Override
    public void update(Board board) {
        if (board.getBoardMessage().equals("Game over.")) {
            done = true;
        }
        board.drawBoard();
    }*/

    @Override
    public void update(BoardPhotography photography, Object obj) {

        photography.show();
        if ( obj instanceof ModelMessage) {
            currentModelMessage = (ModelMessage) obj;
            System.out.println(currentModelMessage.getMessage());
        }




        /*if (modelMessage instanceof ModelGameOver) {
            //currentModelMessage = modelMessage;
        }

        else if (modelMessage instanceof ModelNeedsConfirmation){
            //setto currentModelMessage a ModelNeedsConfirmation

        }

        else if (modelMessage instanceof ModelNeedsCoordinates){
            //setto currentModelMessage a ModelNeedsCoordinates
        }

        else if(modelMessage instanceof ModelNeedsGodName){
            //setto currentModelMessage a ModelNeedsGodName
        }*/

    }


}
