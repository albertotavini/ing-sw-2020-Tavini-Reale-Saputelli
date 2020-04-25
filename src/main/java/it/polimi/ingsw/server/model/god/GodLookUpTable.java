package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.utils.Global;
import it.polimi.ingsw.server.view.PlayerMove.ConfirmationEnum;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMoveType;

import java.util.HashMap;




//per adesso le divinità vengono istanziate qui, in seguito verranno lette da file


public class GodLookUpTable {

    private static HashMap<String, God> move_list = new HashMap<>();
    private static HashMap<String, God> build_list = new HashMap<>();
    private static HashMap<String, God> opponent_list = new HashMap<>();
    private static HashMap<String, God> setup_list = new HashMap<>();
    private static HashMap<String, God> needsConfirmation_list = new HashMap<>();

    private static boolean alreadyInitialized = false;

    //a few things needed for some effects, among which a state pattern
    private static GodState godState = GodStateOne.getInstance();
    public static PlayerMove prevCoord;
    public static GodState getGodState() {
        return godState;
    }

    private static SpecificEffect athenaEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board,Turn turn, PlayerMove p) {
            //board.setBoardMessage("you have Athena, so remember the opponents won't be able to go up this turn if you did");
            board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "you have Athena, so remember the opponents won't be able to go up this turn if you did"));
            if (p.getType() != PlayerMoveType.Coord) {return false;}
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
        public boolean SpecificEffect(Board board, Turn turn, PlayerMove p) {
            //board.setBoardMessage("you have Minotaur, so remember you can also move by sending and opponent's worker to a free space right behind him");
            board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "you have Minotaur, so remember you can also move by sending and opponent's worker to a free space right behind him"));
            if (p.getType() != PlayerMoveType.Coord) {return false;}
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
        public boolean SpecificEffect(Board board, Turn turn, PlayerMove p) {
            //board.setBoardMessage("you have Pan, so remember you can also win by going down two levels");
            board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "you have Pan, so remember you can also win by going down two levels"));
            if (p.getType() != PlayerMoveType.Coord) {return false;}
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
        public boolean SpecificEffect(Board board, Turn turn, PlayerMove p) {
            //board.setBoardMessage("you have Apollo, so remember you can also move by switching places with an opponent's worker in a reachable box");
            board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "you have Apollo, so remember you can also move by switching places with an opponent's worker in a reachable box"));
            if (p.getType() != PlayerMoveType.Coord) {return false;}
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
        public boolean SpecificEffect(Board board, Turn turn, PlayerMove p) {
            //asks if the player wants to use the effect
            if (godState instanceof GodStateOne) {
                //board.setBoardMessage("do you want to use prometheus' power (yes/no)? \n you'll be able to build also before moving, but you won't be able to move up");
                board.setModelMessage(new ModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use prometheus' power (yes/no)? \n you'll be able to build also before moving, but you won't be able to move up"));
                if (p.getType() != PlayerMoveType.Confirm) {return false;}
                if (p.getConfirmation() == ConfirmationEnum.Yes) {
                    godState = GodStateTwo.getInstance();
                    //board.setBoardMessage("ok, now you can build before moving");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "ok, now you can build before moving"));
                }
                else if (p.getConfirmation() == ConfirmationEnum.No) {
                    godState = GodStateFour.getInstance();
                    //board.setBoardMessage("ok then, you can move regularly");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, ("ok then, you can move regularly")));
                }
                return false;
            }

            //if the power is used first it calls a basicBuild
            if (godState instanceof GodStateTwo) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                if (turn.basicBuild(board, p)) {
                    godState = GodStateThree.getInstance();
                    //board.setBoardMessage("ok, now you can move, but remember, no going up!");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "ok, now you can move, but remember, no going up!"));
                }
                return false;
            }

            //then it calls a move where you can't go up
            if (godState instanceof GodStateThree) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
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
                if (p.getType() != PlayerMoveType.Coord) {return false;}
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
        public boolean SpecificEffect(Board board, Turn turn, PlayerMove p) {
                if(! board.artemisCanBeUsed(turn.getCurrentRow(), turn.getCurrentColumn())) {
                    godState = GodStateFour.getInstance();
                    //board.setBoardMessage("you cannot use artemis' effect, you'll move just once");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "you cannot use artemis' effect, you'll move just once"));
                }
                //needs to know if the player wants to activate the effect
                if (godState instanceof GodStateOne) {
                    //board.setBoardMessage("do you want to use artemis' power (yes/no)? you'll be able to move twice, but not back to the place you were initially");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use artemis' power (yes/no)? you'll be able to move twice, but not back to the place you were initially"));
                    if (p.getType() != PlayerMoveType.Confirm) {return false;}
                    if (p.getConfirmation() == ConfirmationEnum.Yes) {
                        godState = GodStateTwo.getInstance();
                        prevCoord = new PlayerMove( turn.getCurrentRow(), turn.getCurrentColumn(),turn.getPlayer());
                        //board.setBoardMessage("ok then, where do you want to move first?");
                        board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "ok then, where do you want to move first?"));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.No) {
                        godState = GodStateFour.getInstance();
                        //board.setBoardMessage("ok then, you'll move just once");
                        board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "ok then, you'll move just once"));
                    }
                    return false;
                }
                //moves the first time in a basic way
                if (godState instanceof GodStateTwo) {
                    if (p.getType() != PlayerMoveType.Coord) {return false;}
                    if (turn.basicMove(board, p)) {
                        godState = GodStateThree.getInstance();
                        //board.setBoardMessage("now the second move!");
                        board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "now the second move!"));
                    }
                    return false;
                }
                //moves the second time preventing to go back where the worker was
                if (godState instanceof  GodStateThree) {
                    if (p.getType() != PlayerMoveType.Coord) {return false;}
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
                    //board.setBoardMessage("you completed the move, now time to build");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "you completed the move, now time to build"));
                    prevCoord = new PlayerMove(7,7 , turn.getPlayer());
                    return true;

                }
                //if the effect is not used just uses basic move;
                if (godState instanceof GodStateFour) {
                    if (p.getType() != PlayerMoveType.Coord) {return false;}
                    if (turn.basicMove(board, p)) {
                        godState = GodStateOne.getInstance();
                        //board.setBoardMessage("you completed the move, now time to build");
                        board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "you completed the move, now time to build"));
                        prevCoord = new PlayerMove(7,7 , turn.getPlayer());
                        return true;
                    }
                }

                return false;
        }
    };
    private static SpecificEffect atlasEffect  = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, PlayerMove p) {

            if (godState instanceof GodStateOne) {
                //board.setBoardMessage("Do you want to use Atlas' power?");
                board.setModelMessage(new ModelMessage(ModelMessageType.NeedsConfirmation, "Do you want to use Atlas' power?"));
                if (p.getType() != PlayerMoveType.Confirm) {return false;}
                if (p.getConfirmation() == ConfirmationEnum.Yes) {
                    godState = GodStateTwo.getInstance();
                    //board.setBoardMessage("Ok, now you can build a dome wherever you want.");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "Ok, now you can build a dome wherever you want."));
                }
                else if (p.getConfirmation() == ConfirmationEnum.No) {
                    godState = GodStateThree.getInstance();
                    //board.setBoardMessage("Ok then, you can build regularly");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "Ok then, you can build regularly"));
                }
                return false;
            }

            if (godState instanceof GodStateTwo) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                int row = p.getRow();
                int column = p.getColumn();

                //asks coordinates while box is not adjacent, occupied by worker or dome
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.getBox(row,column).getTowerSize() == 4 || board.getBox(row, column).isDomed()) {
                    return false;
                }

                board.placeDome(row, column);
                godState = GodStateOne.getInstance();
                return true;
            }

            if (godState instanceof GodStateThree) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                if( turn.basicBuild(board, p)) {
                    godState = GodStateOne.getInstance();
                    return true;
                }
                else { return false; }
            }

            return false;
        }
    };
    private static SpecificEffect demeterEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, PlayerMove p) {
            if(!board.demeterCanBeUSed(turn.getCurrentRow(), turn.getCurrentColumn())) {
                godState = GodStateFour.getInstance();
                //board.setBoardMessage("you cannot us demeter's effect, you'll just build once");
                board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "you cannot us demeter's effect, you'll just build once"));
            }
            if (godState instanceof GodStateOne) {
                //board.setBoardMessage("do you want to use demeter's power (yes/no)? you'll be able to build twice, but not in the same box");
                board.setModelMessage(new ModelMessage(ModelMessageType.NeedsConfirmation, "do you want to use demeter's power (yes/no)? you'll be able to build twice, but not in the same box"));
                if (p.getConfirmation() == ConfirmationEnum.Yes) {
                    godState = GodStateTwo.getInstance();
                    //board.setBoardMessage("ok then, where do you want to build first?");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "ok then, where do you want to build first?"));
                }
                else if (p.getConfirmation() == ConfirmationEnum.No) {
                    godState = GodStateFour.getInstance();
                    //board.setBoardMessage("ok then, you'll build just once");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates,"ok then, you'll build just once"));
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
                prevCoord = new PlayerMove(row, column, turn.getPlayer());
                //board.setBoardMessage("ok now you can build another time, but remember, not on the same spot you built before");
                board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "ok now you can build another time, but remember, not on the same spot you built before"));
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
                prevCoord = new PlayerMove(7, 7, turn.getPlayer());
                return true;
            }
            else if (godState instanceof GodStateFour) {
                if(turn.basicBuild(board, p)) {
                    godState = GodStateOne.getInstance();
                    prevCoord = new PlayerMove(7, 7, turn.getPlayer());
                    return true;
                }
            }
            return false;
        }
    };
    private static SpecificEffect hephaestusEffect = new SpecificEffect() {
        @Override
        public boolean SpecificEffect(Board board, Turn turn, PlayerMove p) {

            if (godState instanceof GodStateOne) {
                //board.setBoardMessage("Do you want to use Hephaestus' power (yes/no)? If yes, you will build twice on the box you selected (but not a dome)");
                board.setModelMessage(new ModelMessage(ModelMessageType.NeedsConfirmation, "Do you want to use Hephaestus' power (yes/no)? If yes, you will build twice on the box you selected (but not a dome)"));
                if (p.getType() != PlayerMoveType.Confirm) {return false;}
                if (p.getConfirmation() == ConfirmationEnum.Yes) {
                    godState = GodStateTwo.getInstance();
                    //board.setBoardMessage("Ok then, where do you want to build two blocks?");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "Ok then, where do you want to build two blocks?"));
                }
                else if (p.getConfirmation() == ConfirmationEnum.No) {
                    godState = GodStateThree.getInstance();
                    //board.setBoardMessage("Ok then, you'll build just once");
                    board.setModelMessage(new ModelMessage(ModelMessageType.NeedsCoordinates, "Ok then, you'll build just once"));
                }
                return false;
            }

            if(godState instanceof GodStateTwo) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                int row = p.getRow();
                int column = p.getColumn();

                //asks coordinates while box is not adiacent, occupied by worker or dome, or has an height equals to 3 or more: he can't build domes!
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column) || board.getBox(row, column).getTowerSize() > 2) {
                    return false;
                }

                //building twice
                if(board.getBox(row,column).getTowerSize() < 2) {
                    board.increaseLevel(row, column);
                    board.increaseLevel(row, column);
                    godState = GodStateOne.getInstance();
                    return true;
                }

                //building once
                else if(board.getBox(row,column).getTowerSize() == 2) {
                    board.increaseLevel(row, column);
                    godState = GodStateOne.getInstance();
                    return true;
                }
            }

            if(godState instanceof GodStateThree){
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                if(turn.basicBuild(board, p)){
                    godState = GodStateOne.getInstance();
                    return true;
                }
            }

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
            needsConfirmation_list.put(Global.prometheus, prometheus);
            prometheus.addEffectTypes(Global.on_move);
            prometheus.addEffectTypes(Global.on_needconfirmation);

            move_list.put(Global.artemis, artemis);
            needsConfirmation_list.put(Global.artemis, artemis);
            artemis.addEffectTypes(Global.on_move);
            artemis.addEffectTypes(Global.on_needconfirmation);

            build_list.put(Global.atlas, atlas);
            needsConfirmation_list.put(Global.atlas, atlas);
            atlas.addEffectTypes(Global.on_build);
            atlas.addEffectTypes(Global.on_needconfirmation);

            build_list.put(Global.demeter, demeter);
            needsConfirmation_list.put(Global.demeter, demeter);
            demeter.addEffectTypes(Global.on_build);
            demeter.addEffectTypes(Global.on_needconfirmation);

            build_list.put(Global.hephaestus, hephaestus);
            needsConfirmation_list.put(Global.hephaestus, hephaestus);
            hephaestus.addEffectTypes(Global.on_build);
            hephaestus.addEffectTypes(Global.on_needconfirmation);


            alreadyInitialized = true;

        }



        if(move_list.containsKey(godname)) return move_list.get(godname);
        if(build_list.containsKey(godname)) return build_list.get(godname);
        if(opponent_list.containsKey(godname)) return opponent_list.get(godname);
        if(setup_list.containsKey(godname)) return setup_list.get(godname);
        if(needsConfirmation_list.containsKey(godname)) return needsConfirmation_list.get(godname);

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

    public static boolean isEffectNeedConfirmation(String godname) {
        godname = godname.toUpperCase();
        return needsConfirmation_list.containsKey(godname);
    }

}