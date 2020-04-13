package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.server.observers.Observer;

import java.util.List;
import java.util.Scanner;

public class View extends Observable <playerMove> implements Observer<Board> {

    private Player player;
    private Scanner scanner;
    private boolean done = false;
    private List<Player> viewList;
    private int Index;

    //solo per testare, se togli rimuovi controllo con +++ nella playerMove
    public View (List<Player> viewList) {
        this.viewList = viewList;
        Index=0;
        player = viewList.get(Index);
        scanner = new Scanner(System.in);

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
            try {
                if (s.charAt(1) == ',') {
                    String[] inputs = s.split(",");
                    handleMove(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]));
                    break;
                }
                else if (s.equals("+++")){
                    changeViewHandler();
                }
                else {//if it is needed to send a confirmation to the will of activating god's powers, or select god's
                    handleMessage(s);
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


    public void handleMessage(String string){
        //with the messages it inserts generic coordinates that would result in a false in whatever parse
        playerMove message = new playerMove(7,7 , this.player);
        message.setGenericMessage(string);
        notify(message);

    }

    public void handleMove(int row, int column) {
        playerMove message = new playerMove(row, column, this.player);
        message.setGenericMessage("nothing interesting here");
        notify(message);
    }

    @Override
    public void update(Board board) {
        if (board.getBoardMessage().equals("Game over.")) {
            done = true;
        }
        board.drawBoard();
    }





}
