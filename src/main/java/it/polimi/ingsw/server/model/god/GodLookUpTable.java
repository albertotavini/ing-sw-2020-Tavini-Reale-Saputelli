package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.model.Worker;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.ConfirmationEnum;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMoveType;

import java.util.HashMap;




//per adesso le divinità vengono istanziate qui, in seguito verranno lette da file


public class GodLookUpTable {

    private static HashMap<String, God> movelist = new HashMap<>();
    private static HashMap<String, God> buildlist = new HashMap<>();
    private static HashMap<String, God> opponentlist = new HashMap<>();
    private static HashMap<String, God> setuplist = new HashMap<>();
    private static HashMap<String, God> needsConfirmationlist = new HashMap<>();
    private static boolean alreadyInitialized = false;

    static class GodsImplementation {

        private static final SpecificEffect athenaEffect = new SpecificEffect() {

            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you have Athena, so remember the opponents won't be able to go up this turn if you did"));
                if (p.getType() != PlayerMoveType.COORD) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                board.setAllowedToScale(true);
                //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
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
            }
        };
        private static final SpecificEffect minotaurEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you have Minotaur, so remember you can also move by sending and opponent's worker to a free space right behind him"));
                if (p.getType() != PlayerMoveType.COORD) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                //asks for coordinate while box is not adiacent, or occupied by a dome, or too high to reach
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                        (board.isOccupied(row, column) && board.getBox(row, column).getOccupier().getColour().equals(turn.getColor())) ||
                        board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                    return false;
                }
                //if the place is occupied and it's possible sends the worker back
                if ( board.isOccupied(row, column) &&
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
        private static final SpecificEffect panEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you have Pan, so remember you can also win by going down two levels"));
                if (p.getType() != PlayerMoveType.COORD) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                //REIMPLEMENTS THE BASIC MOVE
                //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
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
            }
        };
        private static final SpecificEffect apolloEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you have Apollo, so remember you can also move by switching places with an opponent's worker in a reachable box"));
                if (p.getType() != PlayerMoveType.COORD) {return false;}
                int row = p.getRow();
                int column = p.getColumn();
                //asks for coordinate while box is not adiacent, or occupied by a dome or worker of the same color, or too high to reach
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                        (board.isOccupied(row, column) && board.getBox(row, column).getOccupier().getColour().equals(turn.getColor())) ||
                        board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                    return false;
                }
                //if the place is occupied switches the workers, if not just moves
                if ( board.isOccupied(row, column) ) {
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
        private static final SpecificEffect prometheusEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                //asks if the player wants to use the effect
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, "do you want to use prometheus' power (yes/no)? \n you'll be able to build also before moving, but you won't be able to move up"));
                    if (p.getType() != PlayerMoveType.CONFIRM) {return false;}
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "ok, now you can build before moving"));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.FOUR);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ("ok then, you can move regularly")));
                    }
                    return false;
                }

                //if the power is used first it calls a basicBuild
                if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    if (turn.basicBuild(board, p)) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "ok, now you can move, but remember, no going up!"));
                    }
                    return false;
                }

                //then it calls a move where you can't go up
                if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();

                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
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
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    if( turn.basicMove(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }
                    else {return false;}
                }



                return false;
            }
        };
        private static final SpecificEffect artemisEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if(! artemisCanBeUsed(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                    turn.setGodPart(GodPart.FOUR);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you cannot use artemis' effect, you'll move just once"));
                }
                //needs to know if the player wants to activate the effect
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, "do you want to use artemis' power (yes/no)? you'll be able to move twice, but not back to the place you were initially"));
                    if (p.getType() != PlayerMoveType.CONFIRM) {return false;}
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        turn.setPrevCoord(new PlayerMove( turn.getCurrentRow(), turn.getCurrentColumn(),turn.getPlayer()));
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "ok then, where do you want to move first?"));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.FOUR);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "ok then, you'll move just once"));
                    }
                    return false;
                }
                //moves the first time in a basic way
                if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    if (turn.basicMove(board, p)) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "now the second move!"));
                    }
                    return false;
                }
                //moves the second time preventing to go back where the worker was
                if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();
                    //REIMPLEMENTS THE BASIC MOVE with the control on the coordinates where the worker initially was
                    //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ){
                        return false;
                    }
                    if (board.getBox(row, column).equals(board.getBox(turn.getPrevCoord().getRow(), turn.getPrevCoord().getColumn()))) {
                        board.setModelMessage(board.getModelMessage().copyAndAddError(ModelError.SAMEBOX));
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
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you completed the move, now time to build"));
                    turn.setPrevCoord( new PlayerMove(Global.INVALID ,Global.INVALID , turn.getPlayer()));
                    return true;

                }
                //if the effect is not used just uses basic move
                if (turn.getGodPart() == GodPart.FOUR) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    if (turn.basicMove(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you completed the move, now time to build"));
                        turn.setPrevCoord( new PlayerMove(Global.INVALID,Global.INVALID , turn.getPlayer()));
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
        private static final SpecificEffect atlasEffect  = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, "Do you want to use Atlas' power?"));
                    if (p.getType() != PlayerMoveType.CONFIRM) {return false;}
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "Ok, now you can build a dome wherever you want."));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "Ok then, you can build regularly"));
                    }
                    return false;
                }

                if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();

                    //asks coordinates while box is not adjacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.getBox(row,column).getTower().size() == 4 || board.isDomed(row, column)) {
                        return false;
                    }

                    board.getBox(row, column).placeDome();
                    turn.setGodPart(GodPart.ONE);
                    return true;
                }

                if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    if( turn.basicBuild(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }
                    else { return false; }
                }

                return false;
            }
        };
        private static final SpecificEffect demeterEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if(!demeterCanBeUSed(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                    turn.setGodPart(GodPart.FOUR);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you cannot us demeter's effect, you'll just build once"));
                }
                //checks confirmation
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, "do you want to use demeter's power (yes/no)? you'll be able to build twice, but not in the same box"));
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "ok then, where do you want to build first?"));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.FOUR);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES,"ok then, you'll build just once"));
                    }
                    return false;
                }
                //builds for the first time
                else if (turn.getGodPart() == GodPart.TWO) {
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks coordinates while box is not adiacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column)) {
                        return false;
                    }
                    board.getBox(row, column).increaseLevel();
                    turn.setPrevCoord( new PlayerMove(row, column, turn.getPlayer()));
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "ok now you can build another time, but remember, not on the same spot you built before"));
                    turn.setGodPart(GodPart.THREE);
                }
                //builds the second time but not on the same spot
                else if (turn.getGodPart() == GodPart.THREE) {
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks coordinates while box is not adiacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row,column) ) {
                        return false;
                    }
                    if ( board.getBox(row, column).equals(board.getBox(turn.getPrevCoord().getRow(), turn.getPrevCoord().getColumn()))) {
                        board.setModelMessage(board.getModelMessage().copyAndAddError(ModelError.SAMEBOX));
                        return false;
                    }
                    board.getBox(row, column).increaseLevel();
                    turn.setGodPart(GodPart.ONE);
                    turn.setPrevCoord( new PlayerMove(Global.INVALID, Global.INVALID, turn.getPlayer()));
                    return true;
                }
                //just builds once if player refused
                else if (turn.getGodPart() == GodPart.FOUR) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    if(turn.basicBuild(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        turn.setPrevCoord( new PlayerMove(Global.INVALID, Global.INVALID, turn.getPlayer()));
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
        private static final SpecificEffect hephaestusEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, "Do you want to use Hephaestus' power? If yes, you will build twice on the box you selected (but not a dome)"));
                    if (p.getType() != PlayerMoveType.CONFIRM) {return false;}
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "Ok then, where do you want to build two blocks?"));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "Ok then, you'll build just once"));
                    }
                    return false;
                }

                if(turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();

                    //asks coordinates while box is not adiacent, occupied by worker or dome, or has an height equals to 3 or more: he can't build domes!
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
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
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    if(turn.basicBuild(board, p)){
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }
                }

                return false;
            }
        };
        private static final SpecificEffect hestiaEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {

                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks coordinates while box is not adiacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column)) {
                        return false;
                    }
                    board.getBox(row, column).increaseLevel();
                    turn.setGodPart(GodPart.TWO);
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, "do you want to build a second time? "));
                }
                else if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.CONFIRM) {return false;}
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES,"you can build again, but not on the perimeter!" ));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.NO){
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }

                }
                else if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks coordinates while box is not adiacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
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
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {

                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
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
                        board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, "you moved on the perimeter, want to move again?"));
                    }
                    else {
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }

                }
                else if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.CONFIRM){return false;}
                    if(p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you can move again!"));
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES,""));
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
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks coordinates while box is not adiacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column)) {
                        return false;
                    }
                    board.getBox(row, column).increaseLevel();
                    if (findUnmovedWorker(board, turn) &&
                            thereAreBlocksBearby(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, "do you want to remove a block near your unmoved worker?"));
                        return false;
                    }

                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ""));
                    return true;
                }
                else if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.CONFIRM) {return false;}
                    if (p.getConfirmation() == ConfirmationEnum.YES){
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, " you can remove a block neighboring the worker in "));
                    }
                    else if (p.getConfirmation() == ConfirmationEnum.NO){
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ""));
                        return true;
                    }
                }
                else if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {return false;}
                    int row = p.getRow();
                    int column = p.getColumn();
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column)) {
                        return false;
                    }
                    if (board.getBox( row, column).getTower().isEmpty()) {return false;}
                    board.getBox(row, column).decreaseLevel();

                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ""));
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
                        if(board.boxIsNear(r,c, row, column) && !board.getBox(r,c).getTower().isEmpty() && !board.isDomed(r,c)
                                && board.getBox(r,c).getOccupier() == null){
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        private static final SpecificEffect zeusEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, "you have Zeus, so remember you can build a block under yourself too, but you can't win building a block under yourself."));

                if (p.getType() != PlayerMoveType.COORD) {
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
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                        board.isDomed(row, column)) {
                    return false;
                }

                board.getBox(row, column).increaseLevel();
                return true;
            }
        };
        private static final SpecificEffect chronusEffect = new SpecificEffect() {
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
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
            }
        };

        static final God athena = new God(Global.ATHENA, Global.ATHENA_DESCRIPTION, athenaEffect);
        static final God minotaur = new God(Global.MINOTAUR, Global.MINOTAUR_DESCRIPTION, minotaurEffect);
        static final God pan = new God (Global.PAN, Global.PAN_DESCRIPTION, panEffect);
        static final God apollo = new God(Global.APOLLO, Global.APOLLO_DESCRIPTION, apolloEffect);
        static final God prometheus = new God (Global.PROMETHEUS, Global.PROMETHEUS_DESCRIPTION, prometheusEffect);
        static final God artemis = new God (Global.ARTEMIS, Global.ARTEMIS_DESCRIPTION, artemisEffect);
        static final God atlas = new God (Global.ATLAS, Global.ATLAS_DESCRIPTION, atlasEffect);
        static final God demeter = new God (Global.DEMETER, Global.DEMETER_DESCRIPTION, demeterEffect);
        static final God hephaestus = new God (Global.HEPHAESTUS, Global.HEPHAESTUS_DESCRIPTION, hephaestusEffect);
        static final God chronus = new God (Global.CHRONUS, Global.CHRONUS_DESCRIPTION, chronusEffect);
        static final God hestia = new God (Global.HESTIA, Global.HESTIA_DESCRIPTION, hestiaEffect);
        static final God triton = new God(Global.TRITON, Global.TRITON_DESCRIPTION, tritonEffect);
        static final God ares = new God(Global.ARES, Global.ARES_DESCRIPTION, aresEffect);
        static final God zeus = new God(Global.ZEUS, Global.ZEUS_DESCRIPTION, zeusEffect);


        static void initializeGodsFromSourceCode() {

            movelist.put(Global.ATHENA, athena);
            athena.addEffectTypes(GodTypeEffect.ON_MOVE);

            movelist.put(Global.MINOTAUR, minotaur);
            minotaur.addEffectTypes(GodTypeEffect.ON_MOVE);

            movelist.put(Global.PAN, pan);
            pan.addEffectTypes(GodTypeEffect.ON_MOVE);

            movelist.put(Global.APOLLO, apollo);
            apollo.addEffectTypes(GodTypeEffect.ON_MOVE);

            movelist.put(Global.PROMETHEUS, prometheus);
            needsConfirmationlist.put(Global.PROMETHEUS, prometheus);
            prometheus.addEffectTypes(GodTypeEffect.ON_MOVE);
            prometheus.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);

            movelist.put(Global.ARTEMIS, artemis);
            needsConfirmationlist.put(Global.ARTEMIS, artemis);
            artemis.addEffectTypes(GodTypeEffect.ON_MOVE);
            artemis.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);

            buildlist.put(Global.ATLAS, atlas);
            needsConfirmationlist.put(Global.ATLAS, atlas);
            atlas.addEffectTypes(GodTypeEffect.ON_BUILD);
            atlas.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);

            buildlist.put(Global.DEMETER, demeter);
            needsConfirmationlist.put(Global.DEMETER, demeter);
            demeter.addEffectTypes(GodTypeEffect.ON_BUILD);
            demeter.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);

            buildlist.put(Global.HEPHAESTUS, hephaestus);
            needsConfirmationlist.put(Global.HEPHAESTUS, hephaestus);
            hephaestus.addEffectTypes(GodTypeEffect.ON_BUILD);
            hephaestus.addEffectTypes(GodTypeEffect.ON_NEEDCONFIRMATION);

            buildlist.put(Global.HESTIA, hestia);
            hestia.addEffectTypes(GodTypeEffect.ON_BUILD);

            movelist.put(Global.TRITON, triton);
            triton.addEffectTypes(GodTypeEffect.ON_MOVE);

            buildlist.put(Global.ARES, ares);
            ares.addEffectTypes(GodTypeEffect.ON_BUILD);

            buildlist.put(Global.ZEUS, zeus);
            zeus.addEffectTypes(GodTypeEffect.ON_BUILD);

            opponentlist.put(Global.CHRONUS, chronus);
            chronus.addEffectTypes(GodTypeEffect.ON_OPPONENT);


        }

        static void initializeGodsFromFile(){




        }

    }


    private GodLookUpTable() {

    //hiding default constructor

    }

    public static God lookUp(String godname) {

        godname = godname.toUpperCase();

        //inizializzazione delle carte e della lista o liste a cui appartengono
        if( !alreadyInitialized ) {

            GodsImplementation.initializeGodsFromSourceCode();

            alreadyInitialized = true;

        }



        if(movelist.containsKey(godname)) return movelist.get(godname);
        if(buildlist.containsKey(godname)) return buildlist.get(godname);
        if(opponentlist.containsKey(godname)) return opponentlist.get(godname);
        if(setuplist.containsKey(godname)) return setuplist.get(godname);
        if(needsConfirmationlist.containsKey(godname)) return needsConfirmationlist.get(godname);

        else return null;


    }


    public static boolean isEffectMove(String godname) {
        godname = godname.toUpperCase();
        return movelist.containsKey(godname);
    }

    public static boolean isEffectBuild(String godname) {
        godname = godname.toUpperCase();
        return buildlist.containsKey(godname);
    }

    public static boolean isEffectOnOpponent(String godname) {
        godname = godname.toUpperCase();
        return opponentlist.containsKey(godname);
    }

    public static boolean isEffectSetup(String godname) {
        godname = godname.toUpperCase();
        return setuplist.containsKey(godname);
    }

    public static boolean isEffectNeedConfirmation(String godname) {
        godname = godname.toUpperCase();
        return needsConfirmationlist.containsKey(godname);
    }

}

