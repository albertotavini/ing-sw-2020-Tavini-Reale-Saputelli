package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.model.Worker;
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


    private GodLookUpTable() throws Exception {

    throw new Exception("You can't create a GodLookUpTable class!");

    }



    private static final SpecificEffect athenaEffect = (board, turn, p) -> {
        board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you have Athena, so remember the opponents won't be able to go up this turn if you did"));
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
        if ((board.getBox(row, column).getTower().size()- board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() ) == 1){
            board.setAllowedToScale(false);
        }
        //checks if the player won
        if (board.getBox(row, column).getTower().size() == 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() == 2) {
            turn.setWinner(true);
        }

        turn.setCurrentRow(row);
        turn.setCurrentColumn(column);
        return true;
    };
    private static final SpecificEffect minotaurEffect = new SpecificEffect() {
        @Override
        public boolean ActivateSpecificEffect(Board board, Turn turn, PlayerMove p) {
            board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you have Minotaur, so remember you can also move by sending and opponent's worker to a free space right behind him"));
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
            if ( board.getBox(row, column).getOccupier() != null &&
                (!sendsOpponentBack(board, turn.getCurrentRow(), turn.getCurrentColumn(), row,column)) ) {
                    return false;
            }
            //if not occupied by opponent's worker, moves normally
            if (board.getBox(row, column).getOccupier()==null) {
                board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);

            }
            //checks if the player won
            if (board.getBox(row, column).getTower().size()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() ==2) {
                turn.setWinner(true);
            }
            //changes the current coordinates for a correct build
            turn.setCurrentRow(row);
            turn.setCurrentColumn(column);
            return true;
        }

        //method to simplify minotaur's effect, it's test is in activateMinotaurEffectTest
        private boolean sendsOpponentBack(Board board, int r1, int c1, int r2, int c2) {
            Worker yours;
            Worker other;
            //the coordinates where the opponent's worker would be sent, initialized to an out of the board value
            int r3 = 7 ;
            int c3 = 7 ;
            if (r1 == r2) {
                r3=r1;
                if (c1 > c2) {
                    c3=c2-1;
                } else if (c2 > c1) {
                    c3= c2+1;
                }
            }
            else if (c1 == c2) {
                c3 = c1;
                if (r1 > r2) {
                    r3 = r2 - 1;

                } else  {
                    r3 = r2 + 1;
                }
            }

            //this is the part for the oblique application of the effect
            else {
                if (r1 > r2 && c1 > c2) {
                    r3=r2-1;
                    c3=c2-1;
                }
                if (r2 > r1 && c2 > c1) {
                    r3=r2+1;
                    c3=c2+1;
                }
                if (r2 > r1 && c2 < c1) {
                    r3=r2+1;
                    c3=c2-1;
                }
                if (r2 < r1 && c2 > c1) {
                    r3=r2-1;
                    c3=c2+1;
                }
            }
            if (board.inBoundaries(r3, c3) && board.getBox(r3, c3).getOccupier() == null && !board.isDomed(r3,c3)) {
                yours = board.getBox(r1, c1).getOccupier();
                board.getBox(r1, c1).setOccupier(null);
                other = board.getBox(r2, c2).getOccupier();
                board.getBox(r2, c2).setOccupier(yours);
                board.getBox(r3, c3).setOccupier(other);
                return true;
            }else {
                return false;
            }
        }
    };
    private static final SpecificEffect panEffect = (board, turn, p) -> {
        board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you have Pan, so remember you can also win by going down two levels"));
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
        if (board.getBox(row, column).getTower().size()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() ==2) {
            turn.setWinner(true);
        }
        //ADDING THE NEW WINNER CONDITION if going down of 2 or more levels
        if((board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() - board.getBox(row, column).getTower().size()) >= 2) {
            turn.setWinner(true);
        }
        //changes the current coordinates for a correct build
        turn.setCurrentRow(row);
        turn.setCurrentColumn(column);
        return true;

    };
    private static final SpecificEffect apolloEffect = new SpecificEffect() {
        @Override
        public boolean ActivateSpecificEffect(Board board, Turn turn, PlayerMove p) {
            board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you have Apollo, so remember you can also move by switching places with an opponent's worker in a reachable box"));
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
                switchWorkers(board, turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
            } else {
                board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
            }

            //checks if the player won
            if (board.getBox(row, column).getTower().size()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() ==2) {
               turn.setWinner(true);
            }
            //changes the current coordinates for a correct build
            turn.setCurrentRow(row);
            turn.setCurrentColumn(column);
            return true;
        }

        //method to simplify apollo's effect, it's test is in activateApolloEffectTest in GenericGodTest
        private void switchWorkers(Board board, int r1, int c1, int r2, int c2) {
            Worker yours = board.getBox(r1, c1).getOccupier();
            Worker other = board.getBox(r2, c2).getOccupier();
            board.getBox(r2, c2).setOccupier(yours);
            board.getBox(r1, c1).setOccupier(other);

        }
    };
    private static final SpecificEffect prometheusEffect = (board, turn, p) -> {
        //asks if the player wants to use the effect
        if (turn.getGodPart() == GodPart.ONE) {
            board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCONFIRMATION, "do you want to use prometheus' power (yes/no)? \n you'll be able to build also before moving, but you won't be able to move up"));
            if (p.getType() != PlayerMoveType.Confirm) {return false;}
            if (p.getConfirmation() == ConfirmationEnum.Yes) {
                turn.setGodPart(GodPart.TWO);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "ok, now you can build before moving"));
            }
            else if (p.getConfirmation() == ConfirmationEnum.No) {
                turn.setGodPart(GodPart.FOUR);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, ("ok then, you can move regularly")));
            }
            return false;
        }

        //if the power is used first it calls a basicBuild
        if (turn.getGodPart() == GodPart.TWO) {
            if (p.getType() != PlayerMoveType.Coord) {return false;}
            if (turn.basicBuild(board, p)) {
                turn.setGodPart(GodPart.THREE);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "ok, now you can move, but remember, no going up!"));
            }
            return false;
        }

        //then it calls a move where you can't go up
        if (turn.getGodPart() == GodPart.THREE) {
            if (p.getType() != PlayerMoveType.Coord) {return false;}
            int row = p.getRow();
            int column = p.getColumn();

            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                    board.isDomed(row, column) ||
                    (board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() - board.getBox(row, column).getTower().size()) < 0  ){
                return false;
            }
            //moves the worker
            board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
            //checks if the player won
            if (board.getBox(row, column).getTower().size()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() ==2) {
                turn.setWinner(true);
            }

            turn.setCurrentRow(row);
            turn.setCurrentColumn(column);
            turn.setGodPart(GodPart.ONE);
            return true;
        }

        //if the power is not used calls a basic move and when executed correctly it resets godState and returns true
        if (turn.getGodPart() == GodPart.FOUR){
            if (p.getType() != PlayerMoveType.Coord) {return false;}
            if( turn.basicMove(board, p)) {
                turn.setGodPart(GodPart.ONE);
                return true;
            }
            else {return false;}
        }



        return false;
    };
    private static final SpecificEffect artemisEffect = new SpecificEffect() {
        @Override
        public boolean ActivateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if(! artemisCanBeUsed(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                    turn.setGodPart(GodPart.FOUR);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you cannot use artemis' effect, you'll move just once"));
                }
                //needs to know if the player wants to activate the effect
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCONFIRMATION, "do you want to use artemis' power (yes/no)? you'll be able to move twice, but not back to the place you were initially"));
                    if (p.getType() != PlayerMoveType.Confirm) {return false;}
                    if (p.getConfirmation() == ConfirmationEnum.Yes) {
                        turn.setGodPart(GodPart.TWO);
                        turn.setPrevCoord(new PlayerMove( turn.getCurrentRow(), turn.getCurrentColumn(),turn.getPlayer()));
                        board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "ok then, where do you want to move first?"));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.No) {
                        turn.setGodPart(GodPart.FOUR);
                        board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "ok then, you'll move just once"));
                    }
                    return false;
                }
                //moves the first time in a basic way
                if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.Coord) {return false;}
                    if (turn.basicMove(board, p)) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "now the second move!"));
                    }
                    return false;
                }
                //moves the second time preventing to go back where the worker was
                if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.Coord) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();
                    //REIMPLEMENTS THE BASIC MOVE with the control on the coordinates where the worker initially was
                    //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                            board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                            board.getBox(row, column).equals(board.getBox(turn.getPrevCoord().getRow(), turn.getPrevCoord().getColumn()))) {
                        return false;
                    }
                    //moves the worker
                    board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                    //checks if the player won
                    if (board.getBox(row, column).getTower().size()== 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() ==2) {
                        turn.setWinner(true);
                    }
                    //changes the current coordinates for a correct build
                    turn.setCurrentRow(row);
                    turn.setCurrentColumn(column);
                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you completed the move, now time to build"));
                    turn.setPrevCoord( new PlayerMove(7,7 , turn.getPlayer()));
                    return true;

                }
                //if the effect is not used just uses basic move
                if (turn.getGodPart() == GodPart.FOUR) {
                    if (p.getType() != PlayerMoveType.Coord) {return false;}
                    if (turn.basicMove(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you completed the move, now time to build"));
                        turn.setPrevCoord( new PlayerMove(7,7 , turn.getPlayer()));
                        return true;
                    }
                }

                return false;
        }

        //contrary to methods above, this will be tested in another test and not in activateArtemisEffectTest
        private boolean artemisCanBeUsed(Board board, int row, int column) {
            for (int r = 0; r<Global.DIM; r++) {
                for (int c = 0; c<Global.DIM; c++) {
                    //for all the boxes near the one i'm asking about, if it is free, i return true if there's another free box near it
                    if (board.boxIsNear(row, column, r, c) &&
                        board.getBox(r, c).getOccupier() == null && !board.isDomed(r, c) && board.isNearbySpaceFree(r, c)) {
                            return true;
                    }
                }
            }
            return false;
        }
    };
    private static final SpecificEffect atlasEffect  = (board, turn, p) -> {

        if (turn.getGodPart() == GodPart.ONE) {
            board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCONFIRMATION, "Do you want to use Atlas' power?"));
            if (p.getType() != PlayerMoveType.Confirm) {return false;}
            if (p.getConfirmation() == ConfirmationEnum.Yes) {
                turn.setGodPart(GodPart.TWO);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "Ok, now you can build a dome wherever you want."));
            }
            else if (p.getConfirmation() == ConfirmationEnum.No) {
                turn.setGodPart(GodPart.THREE);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "Ok then, you can build regularly"));
            }
            return false;
        }

        if (turn.getGodPart() == GodPart.TWO) {
            if (p.getType() != PlayerMoveType.Coord) {return false;}
            int row = p.getRow();
            int column = p.getColumn();

            //asks coordinates while box is not adjacent, occupied by worker or dome
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                    board.getBox(row,column).getTower().size() == 4 || board.getBox(row, column).isDomed()) {
                return false;
            }

            board.getBox(row, column).placeDome();
            turn.setGodPart(GodPart.ONE);
            return true;
        }

        if (turn.getGodPart() == GodPart.THREE) {
            if (p.getType() != PlayerMoveType.Coord) {return false;}
            if( turn.basicBuild(board, p)) {
                turn.setGodPart(GodPart.ONE);
                return true;
            }
            else { return false; }
        }

        return false;
    };
    private static final SpecificEffect demeterEffect = new SpecificEffect() {
        @Override
        public boolean ActivateSpecificEffect(Board board, Turn turn, PlayerMove p) {
            if(!demeterCanBeUSed(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                turn.setGodPart(GodPart.FOUR);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you cannot us demeter's effect, you'll just build once"));
            }
            if (turn.getGodPart() == GodPart.ONE) {
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCONFIRMATION, "do you want to use demeter's power (yes/no)? you'll be able to build twice, but not in the same box"));
                if (p.getConfirmation() == ConfirmationEnum.Yes) {
                    turn.setGodPart(GodPart.TWO);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "ok then, where do you want to build first?"));
                }
                else if (p.getConfirmation() == ConfirmationEnum.No) {
                    turn.setGodPart(GodPart.FOUR);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES,"ok then, you'll build just once"));
                }
                return false;
            }
            else if (turn.getGodPart() == GodPart.TWO) {
                int row = p.getRow();
                int column = p.getColumn();
                //asks coordinates while box is not adiacent, occupied by worker or dome
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column)) {
                    return false;
                }
                board.getBox(row, column).increaseLevel();
                turn.setPrevCoord( new PlayerMove(row, column, turn.getPlayer()));
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "ok now you can build another time, but remember, not on the same spot you built before"));
                turn.setGodPart(GodPart.THREE);
            }

            else if (turn.getGodPart() == GodPart.THREE) {
                int row = p.getRow();
                int column = p.getColumn();
                //asks coordinates while box is not adiacent, occupied by worker or dome
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row,column) || board.getBox(row, column).equals(board.getBox(turn.getPrevCoord().getRow(), turn.getPrevCoord().getColumn()))) {
                    return false;
                }
                board.getBox(row, column).increaseLevel();
                turn.setGodPart(GodPart.ONE);
                turn.setPrevCoord( new PlayerMove(7, 7, turn.getPlayer()));
                return true;
            }
            else if (turn.getGodPart() == GodPart.FOUR) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                if(turn.basicBuild(board, p)) {
                    turn.setGodPart(GodPart.ONE);
                    turn.setPrevCoord( new PlayerMove(-1, -1, turn.getPlayer()));
                    return true;
                }
            }
            return false;
        }
        //contrary to methods above, this will be tested in board test and not in activateDemeterEffectTest
        private boolean demeterCanBeUSed (Board board, int row, int column) {
            int freeSpaces = 0;
            for (int r = 0; r<Global.DIM; r++) {
                for (int c = 0; c<Global.DIM; c++) {
                    //for all the boxes near the one i'm asking about, i up the counter, if there's at least 2, i can activate the effect and build
                    if (board.boxIsNear(row, column, r, c) &&
                        board.getBox(r,c).getOccupier()==null && !board.isDomed(r,c)) {
                            freeSpaces++;
                    }
                }
            }
            return freeSpaces > 1;
        }
    };
    private static final SpecificEffect hephaestusEffect = (board, turn, p) -> {

        if (turn.getGodPart() == GodPart.ONE) {
            board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCONFIRMATION, "Do you want to use Hephaestus' power? If yes, you will build twice on the box you selected (but not a dome)"));
            if (p.getType() != PlayerMoveType.Confirm) {return false;}
            if (p.getConfirmation() == ConfirmationEnum.Yes) {
                turn.setGodPart(GodPart.TWO);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "Ok then, where do you want to build two blocks?"));
            }
            else if (p.getConfirmation() == ConfirmationEnum.No) {
                turn.setGodPart(GodPart.THREE);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "Ok then, you'll build just once"));
            }
            return false;
        }

        if(turn.getGodPart() == GodPart.TWO) {
            if (p.getType() != PlayerMoveType.Coord) {return false;}
            int row = p.getRow();
            int column = p.getColumn();

            //asks coordinates while box is not adiacent, occupied by worker or dome, or has an height equals to 3 or more: he can't build domes!
            if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                    board.isDomed(row, column) || board.getBox(row, column).getTower().size() > 2) {
                return false;
            }

            //building twice
            if(board.getBox(row,column).getTower().size() < 2) {
                board.getBox(row, column).increaseLevel();
                board.getBox(row, column).increaseLevel();
                turn.setGodPart(GodPart.ONE);
                return true;
            }

            //building once
            else if(board.getBox(row,column).getTower().size() == 2) {
                board.getBox(row, column).increaseLevel();
                turn.setGodPart(GodPart.ONE);
                return true;
            }
        }

        if(turn.getGodPart() == GodPart.THREE){
            if (p.getType() != PlayerMoveType.Coord) {return false;}
            if(turn.basicBuild(board, p)){
                turn.setGodPart(GodPart.ONE);
                return true;
            }
        }

        return false;
    };
    private static final SpecificEffect hestiaEffect = new SpecificEffect() {
        @Override
        public boolean ActivateSpecificEffect(Board board, Turn turn, PlayerMove p) {
            if (turn.getGodPart() == GodPart.ONE) {

                if (p.getType() != PlayerMoveType.Coord) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                //asks coordinates while box is not adiacent, occupied by worker or dome
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column)) {
                    return false;
                }
                board.getBox(row, column).increaseLevel();
                turn.setGodPart(GodPart.TWO);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCONFIRMATION, "do you want to build a second time? "));
            }
            else if (turn.getGodPart() == GodPart.TWO) {
                if (p.getType() != PlayerMoveType.Confirm) {return false;}
                if (p.getConfirmation() == ConfirmationEnum.Yes) {
                    turn.setGodPart(GodPart.THREE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES,"you can build again, but not on the perimeter!" ));
                }
                else if (p.getConfirmation() == ConfirmationEnum.No){
                    turn.setGodPart(GodPart.ONE);
                    return true;
                }

            }
            else if (turn.getGodPart() == GodPart.THREE) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                //asks coordinates while box is not adiacent, occupied by worker or dome
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column) || onPerimeter(row, column)) {
                    return false;
                }
                board.getBox(row, column).increaseLevel();
                turn.setGodPart(GodPart.ONE);
                return true;
            }
            return false;
        }

        private boolean onPerimeter(int row, int column) {
            if (row == 0 || row == Global.DIM - 1 ){return true;}
            return column == 0 || column == Global.DIM - 1;
        }
    };
    private static final SpecificEffect tritonEffect = new SpecificEffect() {
        @Override
        public boolean ActivateSpecificEffect(Board board, Turn turn, PlayerMove p) {
            if (turn.getGodPart() == GodPart.ONE) {

                if (p.getType() != PlayerMoveType.Coord) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                    return false;
                }
                //moves the worker
                board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                //checks if the player won
                if (board.getBox(row, column).getTower().size() == 3 && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() ==2) {
                    turn.setWinner(true);
                }
                //changes the current coordinates for a correct build
                turn.setCurrentRow(row) ;
                turn.setCurrentColumn(column);
                if (onPerimeter(row, column)) {
                    turn.setGodPart(GodPart.TWO);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCONFIRMATION, "you moved on the perimeter, want to move again?"));
                }
                else {
                    turn.setGodPart(GodPart.ONE);
                    return true;
                }

            }
            else if (turn.getGodPart() == GodPart.TWO) {
                if (p.getType() != PlayerMoveType.Confirm){return false;}
                if(p.getConfirmation() == ConfirmationEnum.Yes) {
                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you can move again!"));
                    return false;
                }
                if (p.getConfirmation() == ConfirmationEnum.No) {
                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES,""));
                    return true;
                }
            }
            return false;
        }

        private boolean onPerimeter(int row, int column) {
            if (row == 0 || row == Global.DIM - 1){return true;}
            return column == 0 || column == Global.DIM - 1;
        }
    };
    private static final SpecificEffect aresEffect = new SpecificEffect() {
        @Override
        public boolean ActivateSpecificEffect(Board board, Turn turn, PlayerMove p) {
            if (turn.getGodPart() == GodPart.ONE) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                //asks coordinates while box is not adiacent, occupied by worker or dome
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column)) {
                    return false;
                }
                board.getBox(row, column).increaseLevel();
                if (findUnmovedWorker(board, turn) &&
                    thereAreBlocksBearby(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCONFIRMATION, "do you want to remove a block near your unmoved worker?"));
                        return false;
                }

                turn.setGodPart(GodPart.ONE);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, ""));
                return true;
            }
            else if (turn.getGodPart() == GodPart.TWO) {
                if (p.getType() != PlayerMoveType.Confirm) {return false;}
                if (p.getConfirmation() == ConfirmationEnum.Yes){
                    turn.setGodPart(GodPart.THREE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, " you can remove a block neighboring the worker in "));
                }
                else if (p.getConfirmation() == ConfirmationEnum.No){
                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, ""));
                    return true;
                }
            }
            else if (turn.getGodPart() == GodPart.THREE) {
                if (p.getType() != PlayerMoveType.Coord) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                        board.isDomed(row, column)) {
                    return false;
                }
                if (board.getBox( row, column).getTower().isEmpty()) {return false;}
                board.getBox(row, column).decreaseLevel();

                turn.setGodPart(GodPart.ONE);
                board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, ""));
                return true;
            }
            return false;
        }

        private boolean findUnmovedWorker (Board board, Turn turn) {
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c<5; c++) {
                    if (board.getBox(r,c).getOccupier()!= null &&
                       (board.getBox(r,c).getOccupier().getColour().equals(turn.getColor()) &&
                        r != turn.getCurrentRow() && c!=turn.getCurrentColumn())){
                                turn.setCurrentRow(r);
                                turn.setCurrentColumn(c);
                                return true;
                    }
                }
            }
            return false;
        }
        private boolean thereAreBlocksBearby (Board board, int row, int column){
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c<5; c++) {
                    if(board.boxIsNear(r,c, row, column) && !board.getBox(r,c).getTower().isEmpty() && !board.getBox(r,c).isDomed()
                       && board.getBox(r,c).getOccupier() == null){
                        return true;
                    }
                }
            }
            return false;
        }
    };
    private static final SpecificEffect zeusEffect = (board, turn, p) -> {
        board.setModelMessage(new ModelMessage(ModelMessageType.NEEDSCOORDINATES, "you have Zeus, so remember you can build a block under yourself too, but you can't win building a block under yourself."));

        if (p.getType() != PlayerMoveType.Coord) {
            return false;
        }

        int row = p.getRow();
        int column = p.getColumn();

        //the player wants to build under his player
        if (row == turn.getCurrentRow() && column == turn.getCurrentColumn()) {
            //tower's level has to be < 3: if it's not, player will be placed on a dome, and it's illegal!
            if (board.getBox(row, column).getTower().size() < 3) {
                board.getBox(row, column).increaseLevel();
                return true;
            } else return false;
        }

        //asks coordinates while box is not adiacent, occupied by worker or dome
        if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.getBox(row, column).getOccupier() != null ||
                board.isDomed(row, column)) {
            return false;
        }

        board.getBox(row, column).increaseLevel();
        return true;
    };
    private static final SpecificEffect chronusEffect = (board, turn, p) -> {

        int completeTowers = 0;

        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                if (board.getBox(i,j).isComplete()) { completeTowers++; }
            }
        }

        //System.out.println("CompleteTowers vale " + completeTowers);

        if(completeTowers >= 5) {
            turn.setWinner(true);
            return true;
        }
        else return false;
    };

    private static final God atena = new God(Global.ATHENA, Global.ATHENA_DESCRIPTION, athenaEffect);
    private static final God minotaur = new God(Global.MINOTAUR, Global.MINOTAUR_DESCRIPTION, minotaurEffect);
    private static final God pan = new God (Global.PAN, Global.PAN_DESCRIPTION, panEffect);
    private static final God apollo = new God(Global.APOLLO, Global.APOLLO_DESCRIPTION, apolloEffect);
    private static final God prometheus = new God (Global.PROMETHEUS, Global.PROMETHEUS_DESCRIPTION, prometheusEffect);
    private static final God artemis = new God (Global.ARTEMIS, Global.ARTEMIS_DESCRIPTION, artemisEffect);
    private static final God atlas = new God (Global.ATLAS, Global.ATLAS_DESCRIPTION, atlasEffect);
    private static final God demeter = new God (Global.DEMETER, Global.DEMETER_DESCRIPTION, demeterEffect);
    private static final God hephaestus = new God (Global.HEPHAESTUS, Global.HEPHAESTUS_DESCRIPTION, hephaestusEffect);
    private static final God chronus = new God (Global.CHRONUS, Global.CHRONUS_DESCRIPTION, chronusEffect);
    private static final God hestia = new God (Global.HESTIA, Global.HESTIA_DESCRIPTION, hestiaEffect);
    private static final God triton = new God(Global.TRITON, Global.TRITON_DESCRIPTION, tritonEffect);
    private static final God ares = new God(Global.ARES, Global.ARES_DESCRIPTION, aresEffect);
    private static final God zeus = new God(Global.ZEUS, Global.ZEUS_DESCRIPTION, zeusEffect);



    public static God lookUp(String godname) {

        godname = godname.toUpperCase();

        //inizializzazione delle carte e della lista o liste a cui appartengono
        if( !alreadyInitialized ) {

            move_list.put(Global.ATHENA, atena);
            atena.addEffectTypes(GodTypeEffect.on_move);

            move_list.put(Global.MINOTAUR, minotaur);
            minotaur.addEffectTypes(GodTypeEffect.on_move);

            move_list.put(Global.PAN, pan);
            pan.addEffectTypes(GodTypeEffect.on_move);

            move_list.put(Global.APOLLO, apollo);
            apollo.addEffectTypes(GodTypeEffect.on_move);

            move_list.put(Global.PROMETHEUS, prometheus);
            needsConfirmation_list.put(Global.PROMETHEUS, prometheus);
            prometheus.addEffectTypes(GodTypeEffect.on_move);
            prometheus.addEffectTypes(GodTypeEffect.on_needconfirmation);

            move_list.put(Global.ARTEMIS, artemis);
            needsConfirmation_list.put(Global.ARTEMIS, artemis);
            artemis.addEffectTypes(GodTypeEffect.on_move);
            artemis.addEffectTypes(GodTypeEffect.on_needconfirmation);

            build_list.put(Global.ATLAS, atlas);
            needsConfirmation_list.put(Global.ATLAS, atlas);
            atlas.addEffectTypes(GodTypeEffect.on_build);
            atlas.addEffectTypes(GodTypeEffect.on_needconfirmation);

            build_list.put(Global.DEMETER, demeter);
            needsConfirmation_list.put(Global.DEMETER, demeter);
            demeter.addEffectTypes(GodTypeEffect.on_build);
            demeter.addEffectTypes(GodTypeEffect.on_needconfirmation);

            build_list.put(Global.HEPHAESTUS, hephaestus);
            needsConfirmation_list.put(Global.HEPHAESTUS, hephaestus);
            hephaestus.addEffectTypes(GodTypeEffect.on_build);
            hephaestus.addEffectTypes(GodTypeEffect.on_needconfirmation);

            build_list.put(Global.HESTIA, hestia);
            hestia.addEffectTypes(GodTypeEffect.on_build);

            move_list.put(Global.TRITON, triton);
            triton.addEffectTypes(GodTypeEffect.on_move);

            build_list.put(Global.ARES, ares);
            ares.addEffectTypes(GodTypeEffect.on_build);

            build_list.put(Global.ZEUS, zeus);
            zeus.addEffectTypes(GodTypeEffect.on_build);

            opponent_list.put(Global.CHRONUS, chronus);
            chronus.addEffectTypes(GodTypeEffect.on_opponent);

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