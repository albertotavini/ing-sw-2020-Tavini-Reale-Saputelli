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
            System.out.println("Select your coordinates (r,c) ");
            String s =scanner.next();
            try {
                String[] inputs = s.split(",");
                handleMove(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]));
                break;
            } catch (NumberFormatException e) {
                System.out.println(" incorrect input, try again ");
            }
        }


    }


    public void handleMove(int row, int column) {
        notify(new playerMove(row, column));
    }

    @Override
    public void update(Board board) {
        board.drawBoard();
    }





}
