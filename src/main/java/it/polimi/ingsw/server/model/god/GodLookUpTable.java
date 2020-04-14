package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.model.Worker;
import it.polimi.ingsw.server.utils.Global;
import it.polimi.ingsw.server.*;
import it.polimi.ingsw.server.view.playerMove;

import java.util.ArrayList;
import java.util.HashMap;




//per adesso le divinità vengono istanziate qui, in seguito verranno lette da file


public class GodLookUpTable {

    private static HashMap<String, God> move_list = new HashMap<>();
    private static HashMap<String, God> build_list = new HashMap<>();
    private static HashMap<String, God> opponent_list = new HashMap<>();
    private static HashMap<String, God> setup_list = new HashMap<>();

    private static boolean alreadyInitialized = false;

    //a few things needed for some effects, among which a state pattern
    private static GodState godState = GodStateOne.getInstance();
    public static playerMove prevCoord;
    public static GodState getGodState() {
        return godState;
    }



    private static SpecificEffect athenaEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board,Turn turn, playerMove p) {
            board.setBoardMessage("you have Athena, so remember the opponents won't be able to go up this turn if you did");
            int row = p.getRow();
            int column = p.getColumn();
            board.setAllowedToScale(true);
            //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                    board.isDomed(row,column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                return false;
            }
            //moves the worker
            board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
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
            board.setBoardMessage("you have Minotaur, so remember you can also move by sending and opponent's worker to a free space right behind him");
            int row = p.getRow();
            int column = p.getColumn();
            //asks for coordinate while box is not adiacent, or occupied by a dome, or too high to reach
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                    (board.getBox(row, column).getOccupier() != null && board.getBox(row, column).getOccupier().getColour().equals(turn.getColor())) ||
                    board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
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
                board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);

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
            board.setBoardMessage("you have Pan, so remember you can also win by going down two levels");
            int row = p.getRow();
            int column = p.getColumn();
            //REIMPLEMENTS THE BASIC MOVE
            //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                    board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                return false;
            }
            //moves the worker
            board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
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
            board.setBoardMessage("you have Apollo, so remember you can also move by switching places with an opponent's worker in a reachable box");
            int row = p.getRow();
            int column = p.getColumn();
            //asks for coordinate while box is not adiacent, or occupied by a dome or worker of the same color, or too high to reach
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                    (board.getBox(row, column).getOccupier() != null && board.getBox(row, column).getOccupier().getColour().equals(turn.getColor())) ||
                 board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                return false;
            }
            //if the place is occupied switches the workers, if not just moves
            if ( board.getBox(row, column).getOccupier() != null ) {
                board.switchWorkers(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
            } else {
                board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
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
    private static SpecificEffect prometheusEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, playerMove p) {
            //asks if the player wants to use the effect
            if (godState instanceof GodStateOne) {
                board.setBoardMessage("do you want to use prometheus' power (yes/no)? \n you'll be able to build also before moving, but you won't be able to move up");
                if (p.getGenericMessage().equals("yes")) {
                    godState = GodStateTwo.getInstance();
                }
                else if (p.getGenericMessage().equals("no")) {
                    godState = GodStateFour.getInstance();
                }
                return false;
            }

            //if the power is used first it calls a basicBuild
            if (godState instanceof GodStateTwo) {
                board.setBoardMessage("ok, now you can build before moving");
                if (turn.basicBuild(board, p)) {
                    godState = GodStateThree.getInstance();
                }
                return false;
            }

            //then it calls a move where you can't go up
            if (godState instanceof GodStateThree) {
                board.setBoardMessage("ok, now you can move, but remember, no going up!");
                int row = p.getRow();
                int column = p.getColumn();

                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column) ||
                        (board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() - board.getBox(row, column).getTowerSize()) < 0  ){
                    return false;
                }
                //moves the worker
                board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                //checks if the player won
                if (board.getBox(row, column).getTowerSize()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() ==2) {
                    turn.setWinner(true);
                }
                //changes the current coordinates for a correct build;
                turn.setCurrentRow(row);
                turn.setCurrentColumn(column);
                godState = GodStateOne.getInstance();
                return true;
            }

            //if the power is not used calls a basic move and when executed correctly it resets godState and returns true
            if (godState instanceof GodStateFour){
                board.setBoardMessage("ok then, you can move regularly");
                if( turn.basicMove(board, p)) {
                    godState = GodStateOne.getInstance();
                    return true;
                }
                else {return false;}
            }



            return false;
        }
    };
    private static SpecificEffect artemisEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, playerMove p) {
                //needs to know if the player wants to activate the effect
                if (godState instanceof GodStateOne) {
                    board.setBoardMessage("do you want to use artemis' power (yes/no)? you'll be able to move twice, but not back to the place you were initially");
                    if (p.getGenericMessage().equals("yes")) {
                        godState = GodStateTwo.getInstance();
                        prevCoord = new playerMove( turn.getCurrentRow(), turn.getCurrentColumn(),turn.getPlayer());
                        board.setBoardMessage("ok then, where do you want to move first?");
                    }
                    else if (p.getGenericMessage().equals("no")) {
                        godState = GodStateFour.getInstance();
                        board.setBoardMessage("ok then, you'll move just once");
                    }
                    return false;
                }
                //moves the first time in a basic way
                if (godState instanceof GodStateTwo) {
                    if (turn.basicMove(board, p)) {
                        godState = GodStateThree.getInstance();
                        board.setBoardMessage("now the second move!");
                    }
                    return false;
                }
                //moves the second time preventing to go back where the worker was
                if (godState instanceof  GodStateThree) {
                    int row = p.getRow();
                    int column = p.getColumn();
                    //REIMPLEMENTS THE BASIC MOVE with the control on the coordinates where the worker initially was
                    //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                            board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                            board.getBox(row, column).equals(board.getBox(prevCoord.getRow(), prevCoord.getColumn()))) {
                        return false;
                    }
                    //moves the worker
                    board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                    //checks if the player won
                    if (board.getBox(row, column).getTowerSize()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTowerSize() ==2) {
                        turn.setWinner(true);
                    }
                    //changes the current coordinates for a correct build;
                    turn.setCurrentRow(row);
                    turn.setCurrentColumn(column);
                    godState = GodStateOne.getInstance();
                    board.setBoardMessage("you completed the move, now time to build");
                    prevCoord = new playerMove(7,7 , turn.getPlayer());
                    return true;

                }
                //if the effect is not used just uses basic move;
                if (godState instanceof GodStateFour) {
                    if (turn.basicMove(board, p)) {
                        godState = GodStateOne.getInstance();
                        board.setBoardMessage("you completed the move, now time to build");
                        prevCoord = new playerMove(7,7 , turn.getPlayer());
                        return true;
                    }
                }

                return false;
        }
    };
    private static SpecificEffect atlasEffect  = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, playerMove p) {
            //dai uno sguardo a quelli che ho fatto io per avere un'idea, il controllo yes/no sempre all'inizio
            //usa il metodo che ho creato per Box placeDome() se il giocatore ti dice di si, ovviamente all'interno di una versione modificata di basicbuild
            return false;
        }
    };
    private static SpecificEffect demeterEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, playerMove p) {
            if (godState instanceof GodStateOne) {
                board.setBoardMessage("do you want to use demeter's power (yes/no)? you'll be able to build twice, but not in the same box");
                if (p.getGenericMessage().equals("yes")) {
                    godState = GodStateTwo.getInstance();
                    board.setBoardMessage("ok then, where do you want to build first?");
                }
                else if (p.getGenericMessage().equals("no")) {
                    godState = GodStateFour.getInstance();
                    board.setBoardMessage("ok then, you'll build just once");
                }
                return false;
            }
            else if (godState instanceof GodStateTwo) {
                int row = p.getRow();
                int column = p.getColumn();
                //asks coordinates while box is not adiacent, occupied by worker or dome
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column)) {
                    return false;
                }
                board.increaseLevel(row, column);
                prevCoord = new playerMove(row, column, turn.getPlayer());
                board.setBoardMessage("ok now you can build another time, but remember, not on the same spot you built before");
                godState = GodStateThree.getInstance();
            }

            else if (godState instanceof GodStateThree) {
                int row = p.getRow();
                int column = p.getColumn();
                //asks coordinates while box is not adiacent, occupied by worker or dome
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row,column) || board.getBox(row, column).equals(board.getBox(prevCoord.getRow(), prevCoord.getColumn()))) {
                    return false;
                }
                board.increaseLevel(row, column);
                godState = GodStateOne.getInstance();
                prevCoord = new playerMove(7, 7, turn.getPlayer());
                return true;
            }
            else if (godState instanceof GodStateFour) {
                if(turn.basicBuild(board, p)) {
                    godState = GodStateOne.getInstance();
                    prevCoord = new playerMove(7, 7, turn.getPlayer());
                    return true;
                }
            }
            return false;
        }
    };
    private static SpecificEffect hephaestusEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, playerMove p) {
            //dai uno sguardo a quelli che ho fatto io per avere un'idea, il controllo yes/no sempre all'inizio
            //ti suggerirei di vederlo come un "ho ricevuto si?" se mi chiede di costruire su una casella di livello 0 o 1, costruisco due volte, sennò una
            return false;
        }
    };

    private static final God atena = new God(Global.athena, Global.athenaDescription, athenaEffect);
    private static final God minotaur = new God(Global.minotaur, Global.minotaurDescription, minotaurEffect);
    private static final God pan = new God (Global.pan, Global.panDescription, panEffect);
    private static final God apollo = new God(Global.apollo, Global.apolloDescription, apolloEffect);
    private static final God prometheus = new God (Global.prometheus, Global.prometheusDescription, prometheusEffect);
    private static final God artemis = new God (Global.artemis, Global.artemisDescription, artemisEffect);
    private static final God atlas = new God (Global.atlas, Global.atlasDescription, atlasEffect);
    private static final God demeter = new God (Global.demeter, Global.demeterDescription, demeterEffect);
    private static final God hephaestus = new God (Global.hephaestus, Global.hephaestusDescription, hephaestusEffect);



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

            move_list.put(Global.prometheus, prometheus);
            prometheus.addEffectTypes(Global.on_move);

            move_list.put(Global.artemis, artemis);
            artemis.addEffectTypes(Global.on_move);

            build_list.put(Global.atlas, atlas);
            atlas.addEffectTypes(Global.on_build);

            build_list.put(Global.demeter, demeter);
            demeter.addEffectTypes(Global.on_build);

            build_list.put(Global.hephaestus, hephaestus);
            hephaestus.addEffectTypes(Global.on_build);


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