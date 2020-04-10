package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.model.Worker;
import it.polimi.ingsw.server.utils.Global;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.server.view.playerMove;

import java.util.HashMap;




//per adesso le divinità vengono istanziate qui, in seguito verranno lette da file


public class GodLookUpTable {

    private static HashMap<String, God> move_list = new HashMap<>();
    private static HashMap<String, God> build_list = new HashMap<>();
    private static HashMap<String, God> opponent_list = new HashMap<>();
    private static HashMap<String, God> setup_list = new HashMap<>();

    private static boolean alreadyInitialized = false;


    private static SpecificEffect athenaEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board,Turn turn, playerMove p) {
            int row = p.getRow();
            int column = p.getColumn();
            board.setAllowedToScale(true);
            //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                    board.getBox(row, column).getTowerSize() == 4 || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                return false;
            }
            //moves the worker
            Worker w = board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getOccupier();
            board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).setOccupier(null);
            board.getBox(row, column).setOccupier(w);
            //check if the player scaled one level so to deny the possibility to opponets next turn
            if ((board.getBox(row, column).getTowerSize()- board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() ) == 1){
                board.setAllowedToScale(false);
            }
            //checks if the player won
            if (board.getBox(row, column).getTowerSize()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() ==2) {
                turn.setWinner(true);
            }
            //changes the current coordinates for a correct build;
            turn.setCurrentRow(row);
            turn.setCurrentColumn(column);
            return true;
        }
    };
    private static SpecificEffect minotaurEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, playerMove p) {
            int row = p.getRow();
            int column = p.getColumn();
            //asks for coordinate while box is not adiacent, or occupied by a dome, or too high to reach
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                    (board.getBox(row, column).getOccupier() != null && board.getBox(row, column).getOccupier().getColour().equals(turn.getColor())) ||
                    board.getBox(row, column).getTowerSize() == 4 || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                return false;
            }
            //if the place is occupied and it's possible sends the worker back
            if ( board.getBox(row, column).getOccupier() != null ) {
                if (!board.sendsOpponentBack(turn.getCurrentRow(), turn.getCurrentColumn(), row,column)) {
                    return false;
                }
            }
            //if not occupied by opponent's worker, moves normally
            if (board.getBox(row, column).getOccupier()==null) {
                Worker w = board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getOccupier();
                board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).setOccupier(null);
                board.getBox(row, column).setOccupier(w);

            }
            //checks if the player won
            if (board.getBox(row, column).getTowerSize()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() ==2) {
                turn.setWinner(true);
            }
            //changes the current coordinates for a correct build;
            turn.setCurrentRow(row);
            turn.setCurrentColumn(column);
            return true;
        }
    };
    private static SpecificEffect panEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, playerMove p) {
            int row = p.getRow();
            int column = p.getColumn();
            //REIMPLEMENTS THE BASIC MOVE
            //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                    board.getBox(row, column).getTowerSize() == 4 || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                return false;
            }
            //moves the worker
            Worker w = board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getOccupier();
            board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).setOccupier(null);
            board.getBox(row, column).setOccupier(w);
            //checks if the player won
            if (board.getBox(row, column).getTowerSize()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() ==2) {
                turn.setWinner(true);
            }
            //ADDING THE NEW WINNER CONDITION if going down of 2 or more levels
            if((board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() - board.getBox(row, column).getTowerSize()) >= 2) {
                turn.setWinner(true);
            }
            //changes the current coordinates for a correct build;
            turn.setCurrentRow(row);
            turn.setCurrentColumn(column);
            return true;

        }
    };
    private static SpecificEffect apolloEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, playerMove p) {
            int row = p.getRow();
            int column = p.getColumn();
            //asks for coordinate while box is not adiacent, or occupied by a dome, or too high to reach
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                    (board.getBox(row, column).getOccupier() != null && board.getBox(row, column).getOccupier().getColour().equals(turn.getColor())) ||
                 board.getBox(row, column).getTowerSize() == 4 || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                return false;
            }
            //if the place is occupied switches the workers, if not just moves
            if ( board.getBox(row, column).getOccupier() != null ) {
                Worker yours = board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getOccupier();
                Worker other = board.getBox(row, column).getOccupier();
                board.getBox(row, column).setOccupier(yours);
                board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).setOccupier(other);

            } else {
                Worker w = board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getOccupier();
                board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).setOccupier(null);
                board.getBox(row, column).setOccupier(w);
            }

            //checks if the player won
            if (board.getBox(row, column).getTowerSize()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() ==2) {
               turn.setWinner(true);
            }
            //changes the current coordinates for a correct build;
            turn.setCurrentRow(row);
            turn.setCurrentColumn(column);
            return true;
        }
    };

    private static final God atena = new God(Global.athena, Global.athenaDescription, athenaEffect);
    private static final God minotaur = new God(Global.minotaur, Global.minotaurDescription, minotaurEffect);
    private static final God pan = new God (Global.pan, Global.panDescription, panEffect);
    private static final God apollo = new God(Global.apollo, Global.apolloDescription, apolloEffect);

    public static God lookUp(String godname) {

        godname = godname.toUpperCase();

        //inizializzazione delle carte e della lista o liste a cui appartengono
        if( !alreadyInitialized ) {

            move_list.put(Global.athena, atena);
            atena.addEffectTypes(Global.on_move);

            move_list.put(Global.minotaur, minotaur);
            minotaur.addEffectTypes(Global.on_move);

            move_list.put(Global.pan, pan);
            pan.addEffectTypes(Global.on_move);

            move_list.put(Global.apollo, apollo);
            apollo.addEffectTypes(Global.on_move);


            alreadyInitialized = true;

        }



        if(move_list.containsKey(godname)) return move_list.get(godname);
        if(build_list.containsKey(godname)) return build_list.get(godname);
        if(opponent_list.containsKey(godname)) return opponent_list.get(godname);

        else return null;


    }


    public static boolean isEffectMove(String godname) {
        godname = godname.toUpperCase();
        return move_list.containsKey(godname);
    }

    public static boolean isEffectBuild(String godname) {
        godname = godname.toUpperCase();
        return build_list.containsKey(godname);
    }

    public static boolean isEffectOnOpponent(String godname) {
        godname = godname.toUpperCase();
        return opponent_list.containsKey(godname);
    }

    public static boolean isEffectSetup(String godname) {
        godname = godname.toUpperCase();
        return setup_list.containsKey(godname);
    }

}