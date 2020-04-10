package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Match;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.observers.Observable;
import it.polimi.ingsw.server.observers.Observer;

import java.util.Scanner;

public class View extends Observable <playerMove> implements Observer<Board> {

    private Player player;
    private Scanner scanner;

    protected View (Player player) {
        this.player = player;
        scanner = new Scanner(System.in);


    }


    public void playerMove (){
        while (true) {
            System.out.println("You may insert the input that the board is demanding");
            String s =scanner.next();
            try {
                if (s.charAt(1) == ',') {
                    String[] inputs = s.split(",");
                    handleMove(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]));
                    break;
                }
                else {//if it is needed to send a confirmation to the will of activating god's powers
                    handleMessage(s);
                }

            } catch (NumberFormatException e) {
                System.out.println(" incorrect input, try again ");
            }
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
        board.drawBoard();
    }





}
