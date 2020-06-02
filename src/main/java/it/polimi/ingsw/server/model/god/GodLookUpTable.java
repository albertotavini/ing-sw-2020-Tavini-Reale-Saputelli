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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;




//per adesso le divinità vengono istanziate qui, in seguito verranno lette da file


public class GodLookUpTable {

    private static HashMap<String, God> movelist = new HashMap<>();
    private static HashMap<String, God> buildlist = new HashMap<>();
    private static HashMap<String, God> opponentlist = new HashMap<>();
    private static HashMap<String, God> setuplist = new HashMap<>();
    private static HashMap<String, God> needsConfirmationlist = new HashMap<>();
    private static boolean alreadyInitialized = false;

    /**
     * implementation of the god's effects
     * the majority of them is meant to be called INSTEAD of turn.basicMove or turn.BasicBuild
     * when turn calls Move or Build
     * effects made of more than one part consider the GodPart enum in the player's TURN
     */
    static class GodsImplementation {

        private static final SpecificEffect athenaEffect = new SpecificEffect() {

            /**
             *athena's power works with the allowedToScale flag in the board
             *(choice made for extendibility, cause also other advanced gods influence the possibility to go up)
             *when the effect is called during the player move phase it sets the parameter to true, so that if
             *the turn before it was activated, now it needs to be activated again
             *code is equal to the turn.basimove one except for the fact that when the character goes up, it sets
             *allowedToScale to false
             *
             * @param board of the game
             * @param turn of the player with the god
             *
             * @param p playermove received from controller
             * @return true when the worker has been moved correctly, false when input doesn't allow any action
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.YOUHAVEATHENA));
                if (p.getType() != PlayerMoveType.COORD) {
                    return false;
                }
                int row = p.getRow();
                int column = p.getColumn();
                board.setAllowedToScale(true);
                //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                        board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                    return false;
                }
                //moves the worker
                board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                //check if the player scaled one level so to deny the possibility to opponets next turn
                if ((board.getBox(row, column).getTower().size() - board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size()) == 1) {
                    board.setAllowedToScale(false);
                }
                //checks if the player won
                if ((board.getBox(row, column).getTower().size() == Global.WINNINGLEVEL) && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() == (Global.WINNINGLEVEL -1)) {
                    turn.setWinner(true);
                }

                turn.setCurrentRow(row);
                turn.setCurrentColumn(column);
                return true;
            }
        };
        private static final SpecificEffect minotaurEffect = new SpecificEffect() {
            /**
             * the code is similiar to the turn.basicbuild one, except for the fact that
             * when the box chose is occupied by an opponent's worker it applies the effect
             * by calling sendsOpponentBack
             *
             * @param board of the game
             * @param turn of the player
             * @param p received from the controller
             * @return when the worker has been moved correctly, false when input doesn't allow any action
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOMOVE));
                if (p.getType() != PlayerMoveType.COORD) {
                    return false;
                }
                int row = p.getRow();
                int column = p.getColumn();
                //asks for coordinate while box is not adiacent, or occupied by a dome, or too high to reach
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                        (board.isOccupied(row, column) && board.getBox(row, column).getOccupier().getColour().equals(turn.getColor())) ||
                        board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                    return false;
                }
                //if the place is occupied and it's possible sends the worker back
                if (board.isOccupied(row, column) &&
                        (!sendsOpponentBack(board, turn.getCurrentRow(), turn.getCurrentColumn(), row, column))) {
                    return false;
                }
                //if not occupied by opponent's worker, moves normally
                if (board.getBox(row, column).getOccupier() == null) {
                    board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);

                }
                //checks if the player won
                if ((board.getBox(row, column).getTower().size() == Global.WINNINGLEVEL) && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() == (Global.WINNINGLEVEL -1)) {
                    turn.setWinner(true);
                }
                //changes the current coordinates for a correct build
                turn.setCurrentRow(row);
                turn.setCurrentColumn(column);
                return true;
            }

            /**
             * this method calculates the box where the opponent's worker will be sent
             * if it is possible to send the opponent's there, it will do it and return true
             * else it will return false and do nothing
             *
             * @param board of the game
             * @param r1 row where the player is
             * @param c1 column where the player is
             * @param r2 row of the opponent's worker
             * @param c2 column of the opponent's worker
             * @return true if the operation has been correctly completed, false when input doesn't allow any action
             */
            private boolean sendsOpponentBack(Board board, int r1, int c1, int r2, int c2) {
                Worker yours;
                Worker other;
                //the coordinates where the opponent's worker would be sent, initialized to an out of the board value
                int r3 = 7;
                int c3 = 7;
                if (r1 == r2) {
                    r3 = r1;
                    if (c1 > c2) {
                        c3 = c2 - 1;
                    } else if (c2 > c1) {
                        c3 = c2 + 1;
                    }
                } else if (c1 == c2) {
                    c3 = c1;
                    if (r1 > r2) {
                        r3 = r2 - 1;

                    } else {
                        r3 = r2 + 1;
                    }
                }

                //this is the part for the oblique application of the effect
                else {
                    if (r1 > r2 && c1 > c2) {
                        r3 = r2 - 1;
                        c3 = c2 - 1;
                    }
                    if (r2 > r1 && c2 > c1) {
                        r3 = r2 + 1;
                        c3 = c2 + 1;
                    }
                    if (r2 > r1 && c2 < c1) {
                        r3 = r2 + 1;
                        c3 = c2 - 1;
                    }
                    if (r2 < r1 && c2 > c1) {
                        r3 = r2 - 1;
                        c3 = c2 + 1;
                    }
                }
                if (board.inBoundaries(r3, c3) && board.getBox(r3, c3).getOccupier() == null && !board.isDomed(r3, c3)) {
                    yours = board.getBox(r1, c1).getOccupier();
                    board.getBox(r1, c1).setOccupier(null);
                    other = board.getBox(r2, c2).getOccupier();
                    board.getBox(r2, c2).setOccupier(yours);
                    board.getBox(r3, c3).setOccupier(other);
                    return true;
                } else {
                    return false;
                }
            }
        };
        private static final SpecificEffect panEffect = new SpecificEffect() {
            /**
             * the effect is similar to a basicmove, except for another control on the level of the box where the worker is moved
             * if it goes down 2 or more levels, it sets turn.winner to true
             *
             *
             * @param board of the game
             * @param turn of the player
             * @param p receveived from controller
             * @return true if the move operation has been correctly completed, false when input doesn't allow any action
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOMOVE));
                if (p.getType() != PlayerMoveType.COORD) {
                    return false;
                }
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
                if ((board.getBox(row, column).getTower().size() == Global.WINNINGLEVEL) && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() == (Global.WINNINGLEVEL -1)) {
                    turn.setWinner(true);
                }
                //ADDING THE NEW WINNER CONDITION if going down of 2 or more levels
                if ((board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() - board.getBox(row, column).getTower().size()) >= 2) {
                    turn.setWinner(true);
                }
                //changes the current coordinates for a correct build
                turn.setCurrentRow(row);
                turn.setCurrentColumn(column);
                return true;
            }
        };
        private static final SpecificEffect apolloEffect = new SpecificEffect() {
            /**
             * this effect is similar to turn.basicMove
             * with the exception that when a near box with an opponent's worker is chosen, it calls switchWorkers
             *
             * @param board of the game
             * @param turn of the player
             * @param p received from controller
             * @return true if the move operation has been correctly completed, false when input doesn't allow any action
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOMOVE));
                if (p.getType() != PlayerMoveType.COORD) {
                    return false;
                }
                int row = p.getRow();
                int column = p.getColumn();
                //asks for coordinate while box is not adiacent, or occupied by a dome or worker of the same color, or too high to reach
                if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) ||
                        (board.isOccupied(row, column) && board.getBox(row, column).getOccupier().getColour().equals(turn.getColor())) ||
                        board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                    return false;
                }
                //if the place is occupied switches the workers, if not just moves
                if (board.isOccupied(row, column)) {
                    switchWorkers(board, turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                } else {
                    board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                }

                //checks if the player won
                if ((board.getBox(row, column).getTower().size() == Global.WINNINGLEVEL) && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() == (Global.WINNINGLEVEL -1)) {
                    turn.setWinner(true);
                }
                //changes the current coordinates for a correct build
                turn.setCurrentRow(row);
                turn.setCurrentColumn(column);
                return true;
            }

            /**
             * this method simply switches the position of the two workers
             *
             * @param board of the game
             * @param r1 of the player
             * @param c1 of the player
             * @param r2 of the opponent's worker
             * @param c2 of the opponent's worker
             */
            private void switchWorkers(Board board, int r1, int c1, int r2, int c2) {
                Worker yours = board.getBox(r1, c1).getOccupier();
                Worker other = board.getBox(r2, c2).getOccupier();
                board.getBox(r2, c2).setOccupier(yours);
                board.getBox(r1, c1).setOccupier(other);

            }
        };
        private static final SpecificEffect prometheusEffect = new SpecificEffect() {
            /**
             * this effect is divided in parts and is maent to be executed with more than one input
             * it will operate different code depending in which part we're in
             * every time it ends and returns true GodPart is set to One
             * it will operate different code depending in which part we're in
             * GodPart.ONE waits for a CONFIRMATION PlayerMove that tells if the player wants or doesn't want to activate the effect
             * GodPart.TWO is where we land if the player says yes, it will execute a basic build and when done moves to THREE
             * GodPart.THREE is similar to a basicBuild, but it won't allow to go up
             * when receives correct input move to part four
             * GodPart.FOUR is where we land if the player says no, and it simply calls a basicMove
             *
             * @param board of the game
             * @param turn of the player
             * @param p received from controller
             * @return true when input has been correctly processed in Godpart 3 or 4, false elsewhere
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                //asks if the player wants to use the effect
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, Global.DOYOUWANTTOUSEGODEFFECT));
                    if (p.getType() != PlayerMoveType.CONFIRM) {
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.BUILDBEFOREMOVING));
                    } else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.FOUR);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, (Global.SELECTWHERETOMOVE)));
                    }
                    return false;
                }

                //if the power is used first it calls a basicBuild
                if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    if (turn.basicBuild(board, p)) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.NOGOINGUP));
                    }
                    return false;
                }

                //then it calls a move where you can't go up
                if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    int row = p.getRow();
                    int column = p.getColumn();

                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column) ||
                            (board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() - board.getBox(row, column).getTower().size()) < 0) {
                        return false;
                    }
                    //moves the worker
                    board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                    //checks if the player won
                    if ((board.getBox(row, column).getTower().size() == Global.WINNINGLEVEL) && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() == (Global.WINNINGLEVEL -1)) {
                        turn.setWinner(true);
                    }

                    turn.setCurrentRow(row);
                    turn.setCurrentColumn(column);
                    turn.setGodPart(GodPart.ONE);
                    return true;
                }

                //if the power is not used calls a basic move and when executed correctly it resets godState and returns true
                if (turn.getGodPart() == GodPart.FOUR) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    if (turn.basicMove(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    } else {
                        return false;
                    }
                }


                return false;
            }
        };
        private static final SpecificEffect artemisEffect = new SpecificEffect() {
            /**
             *
             *this effect is divided in parts and is meant to be executed with more than one input
             *it will operate different code depending in which part we're in
             * every time it ends and returns true GodPart is set to ONE
             * GodPart.ONE is where we wait for the CONFIRMATION playermove to activate or not the effect
             * GodPart.TWO is where we land if the player says yes, it calls a basicMove, when receives correct input moves to THREE
             * GodPart.THREE is similar to a basicMove, but prevents the player from going back to the place he was at the beginning
             * of the turn. After receiving correct input concludes the effect
             *GodPart.TWO is where we land if the player says no, it simply executes a basicMove
             *
             *
             *
             *
             * @param board of the game
             * @param turn of the player
             * @param p receveived from the controller
             * @return true when part THREE or FOUR concludes correctly, false elsewhere
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (!artemisCanBeUsed(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                    turn.setGodPart(GodPart.FOUR);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.GODPOWERNOTALLOWED));
                }
                //needs to know if the player wants to activate the effect
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, Global.DOYOUWANTTOUSEGODEFFECT));
                    if (p.getType() != PlayerMoveType.CONFIRM) {
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        turn.setPrevCoord(PlayerMove.buildCoordPlayerMove(turn.getCurrentRow(), turn.getCurrentColumn(), turn.getPlayer()));
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.MOVEFIRSTTIME));
                    } else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.FOUR);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOMOVE));
                    }
                    return false;
                }
                //moves the first time in a basic way
                if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    if (turn.basicMove(board, p)) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SECONDMOVE));
                    }
                    return false;
                }
                //moves the second time preventing to go back where the worker was
                if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    int row = p.getRow();
                    int column = p.getColumn();
                    //REIMPLEMENTS THE BASIC MOVE with the control on the coordinates where the worker initially was
                    //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column) || !board.isScalable(turn.getCurrentRow(), turn.getCurrentColumn(), row, column)) {
                        return false;
                    }
                    if (board.getBox(row, column).equals(board.getBox(turn.getPrevCoord().getRow(), turn.getPrevCoord().getColumn()))) {
                        board.setModelMessage(board.getModelMessage().copyAndAddError(ModelError.SAMEBOX));
                        return false;
                    }
                    //moves the worker
                    board.moveWorker(turn.getCurrentRow(), turn.getCurrentColumn(), row, column);
                    //checks if the player won
                    if ((board.getBox(row, column).getTower().size() == Global.WINNINGLEVEL) && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() == (Global.WINNINGLEVEL -1)) {
                        turn.setWinner(true);
                    }
                    //changes the current coordinates for a correct build
                    turn.setCurrentRow(row);
                    turn.setCurrentColumn(column);
                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOBUILD));
                    turn.setPrevCoord(PlayerMove.buildCoordPlayerMove(Global.INVALID_BOX, Global.INVALID_BOX, turn.getPlayer()));
                    return true;

                }
                //if the effect is not used just uses basic move
                if (turn.getGodPart() == GodPart.FOUR) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    if (turn.basicMove(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOBUILD));
                        turn.setPrevCoord(PlayerMove.buildCoordPlayerMove(Global.INVALID_BOX, Global.INVALID_BOX, turn.getPlayer()));
                        return true;
                    }
                }

                return false;
            }

            /**
             * simple parse method that is called at the beginning of the effect
             * it returns false if the effect can't be used, in that case GodPart is set automatically to FOUR
             *
             * @param board of the game
             * @param row of the player's worker
             * @param column of the player's worker
             * @return true if the effect can be used, false if not
             */
            private boolean artemisCanBeUsed(Board board, int row, int column) {
                for (int r = 0; r < Global.BOARD_DIM; r++) {
                    for (int c = 0; c < Global.BOARD_DIM; c++) {
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
        private static final SpecificEffect atlasEffect = new SpecificEffect() {
            /**
             * This effect is divided in parts and is meant to be executed with more than one input
             * it will operate different code depending in which part we're in
             * every time it ends and returns true GodPart is set to ONE.
             * GodPart.ONE waits for a CONFIRMATION PlayerMove that tells if the player wants or doesn't want to activate the effect
             * GodPart.TWO makes the player placing a dome on the chosen box, if he decided to activate Atlas' effect
             * GodPart.THREE makes the player placing a standard block on the chosen box, if he decided not to activate Atlas' effect
             *
             * @param board the game board
             * @param turn turn of the player
             * @param p move received by controller
             * @return true when input has been correctly processed in Godpart 2 or 3, false elsewhere
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, Global.DOYOUWANTTOUSEGODEFFECT));
                    if (p.getType() != PlayerMoveType.CONFIRM) {
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.YOUCANBUILDADOME));
                    } else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOBUILD));
                    }
                    return false;
                }

                if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    int row = p.getRow();
                    int column = p.getColumn();

                    //asks coordinates while box is not adjacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.getBox(row, column).getTower().size() == 4 || board.isDomed(row, column)) {
                        return false;
                    }

                    board.getBox(row, column).placeDome();
                    turn.setGodPart(GodPart.ONE);
                    return true;
                }

                if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    if (turn.basicBuild(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    } else {
                        return false;
                    }
                }

                return false;
            }
        };
        private static final SpecificEffect demeterEffect = new SpecificEffect() {
            /**
             *this effect is divided in parts and is meant to be executed with more than one input
             *it will operate different code depending in which part we're in
             *every time it ends and returns true GodPart is set to ONE
             *
             *GodPart.ONE is where we wait for the CONFIRMATION playermove to activate or not the effect
             *GodPart.TWO is where we land if the player says yes, it calls a basicBuild, when receives correct input moves to THREE
             *GodPart.THREE is similar to a basicBuild, but prevents the player from build on the same spot he built before
             *of the turn. After receiving correct input concludes the effect
             *GodPart.TWO is where we land if the player says no, it simply executes a basicBuild
             *
             * @param board of the game
             * @param turn of the player
             * @param p received from controller
             * @return true when part THREE or FOUR concludes correctly, false elsewhere
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (!demeterCanBeUSed(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                    turn.setGodPart(GodPart.FOUR);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.GODPOWERNOTALLOWED));
                }
                //checks confirmation
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, Global.DOYOUWANTTOUSEGODEFFECT));
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.BUILDFIRSTTIME));
                    } else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.FOUR);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOBUILD));
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
                    turn.setPrevCoord(PlayerMove.buildCoordPlayerMove(row, column, turn.getPlayer()));
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.BUILDSECONDTIME ));
                    turn.setGodPart(GodPart.THREE);
                }
                //builds the second time but not on the same spot
                else if (turn.getGodPart() == GodPart.THREE) {
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks coordinates while box is not adiacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column)) {
                        return false;
                    }
                    if (board.getBox(row, column).equals(board.getBox(turn.getPrevCoord().getRow(), turn.getPrevCoord().getColumn()))) {
                        board.setModelMessage(board.getModelMessage().copyAndAddError(ModelError.SAMEBOX));
                        return false;
                    }
                    board.getBox(row, column).increaseLevel();
                    turn.setGodPart(GodPart.ONE);
                    turn.setPrevCoord(PlayerMove.buildCoordPlayerMove(Global.INVALID_BOX, Global.INVALID_BOX, turn.getPlayer()));
                    return true;
                }
                //just builds once if player refused
                else if (turn.getGodPart() == GodPart.FOUR) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    if (turn.basicBuild(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        turn.setPrevCoord(PlayerMove.buildCoordPlayerMove(Global.INVALID_BOX, Global.INVALID_BOX, turn.getPlayer()));
                        return true;
                    }
                }
                return false;
            }

            /**
             * simple parse method that tells if there is enough space to activate the effect
             * when not possible, the specificEffect will go automatically to part FOUR
             *
             * @param board of the game
             * @param row of the player's worker
             * @param column of the player's worker
             * @return if the effect can be activated, false if not
             */
            private boolean demeterCanBeUSed(Board board, int row, int column) {
                int freeSpaces = 0;
                for (int r = 0; r < Global.BOARD_DIM; r++) {
                    for (int c = 0; c < Global.BOARD_DIM; c++) {
                        //for all the boxes near the one i'm asking about, i up the counter, if there's at least 2, i can activate the effect and build
                        if (board.boxIsNear(row, column, r, c) &&
                                board.getBox(r, c).getOccupier() == null && !board.isDomed(r, c)) {
                            freeSpaces++;
                        }
                    }
                }
                return freeSpaces > 1;
            }
        };
        private static final SpecificEffect hephaestusEffect = new SpecificEffect() {
             /**
             * This effect is divided in parts and is meant to be executed with more than one input
             * it will operate different code depending in which part we're in
             * every time it ends and returns true GodPart is set to ONE.
             * GodPart.ONE waits for a CONFIRMATION PlayerMove that tells if the player wants or doesn't want to activate the effect
             * GodPart.TWO makes the player placing again on the chosen box, if he decided to activate Hephaestus' effect (and if he can still build a standard block, but not a dome)
             * GodPart.THREE makes the player placing a standard block on the chosen box, if he decided not to activate Hephaestus' effect
             *
             * @param board the game board
             * @param turn turn of the player
             * @param p move received by controller
             * @return true when input has been correctly processed in Godpart 2 or 3, false elsewhere
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {
                    board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, Global.DOYOUWANTTOUSEGODEFFECT));
                    if (p.getType() != PlayerMoveType.CONFIRM) {
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.BUILDTWOBLOCKS));
                    } else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOBUILD));
                    }
                    return false;
                }

                if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    int row = p.getRow();
                    int column = p.getColumn();

                    //asks coordinates while box is not adiacent, occupied by worker or dome, or has an height equals to 3 or more: he can't build domes!
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column) || board.getBox(row, column).getTower().size() > 2) {
                        return false;
                    }

                    //building twice
                    if (board.getBox(row, column).getTower().size() < 2) {
                        board.getBox(row, column).increaseLevel();
                        board.getBox(row, column).increaseLevel();
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }

                    //building once
                    else if (board.getBox(row, column).getTower().size() == 2) {
                        board.getBox(row, column).increaseLevel();
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }
                }

                if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    if (turn.basicBuild(board, p)) {
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }
                }

                return false;
            }
        };
        private static final SpecificEffect hestiaEffect = new SpecificEffect() {
            /**
             *this effect is divided in parts and is meant to be executed with more than one input
             *it will operate different code depending in which part we're in
             *every time it ends and returns true GodPart is set to ONE
             *
             * ONE: it builds the first time, the controls if it is possibile to activate the effect, if yes moves to part TWO
             * else it concludes and returns true
             * TWO: here it waits for confirmation of the will to use or not use the effect
             * if yes it moves to three, else it returns true and concludes
             * THREE: code similar to a basicBuild but with the difference that it won't allow it on perimeter
             *
             * @param board of the game
             * @param turn of the player
             * @param p received from controller
             * @return true when either there is a refusal in TWO or when THREE receives correct input and is concluded
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {

                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks coordinates while box is not adiacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column)) {
                        return false;
                    }
                    board.getBox(row, column).increaseLevel();
                    //i control if it is possible to use the effect, if yes i go on asking confirmation
                    if (checkIfPossible(board, turn.getCurrentRow(), turn.getCurrentColumn())){
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, Global.BUILDAGAIN));
                        return false;
                    }
                    //if not i conclude the turn and set back to part one
                    turn.setGodPart(GodPart.ONE);
                    return true;
                } else if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.CONFIRM) {
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.AGAINBUTNOTONPERIMETER));
                    } else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }

                } else if (turn.getGodPart() == GodPart.THREE) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
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

            /**
             * parse method to control if the effect can be activated,
             * if not the Effect automatically goes to part FOUR
             *
             * @param board of the game
             * @param row of the player's worker
             * @param column of the player's worker
             * @return true if there is a box near the worker where he can build that is not on perimeter
             */
            private boolean checkIfPossible(Board board, int row, int column) {
                for (int r = 0; r < Global.BOARD_DIM; r++) {
                    for (int c = 0; c < Global.BOARD_DIM; c++) {
                        if(board.boxIsNear(row, column, r,c) && !board.getBox(r,c ).isDomed() &&
                        board.getBox(r,c ).getOccupier() == null && !onPerimeter(r,c )) {
                            return true;
                        }

                    }
                }
                        return false;
            }

            /**
             * @param row of the box demanded
             * @param column of the box demanded
             * @return true if on the perimeter
             */
            private boolean onPerimeter(int row, int column) {
                if (row == 0 || row == Global.BOARD_DIM - 1) {
                    return true;
                }
                return column == 0 || column == Global.BOARD_DIM - 1;
            }
        };
        private static final SpecificEffect tritonEffect = new SpecificEffect() {
            /**
             *this effect is divided in parts and is meant to be executed with more than one input
             *it will operate different code depending in which part we're in
             *every time it ends and returns true GodPart is set to ONE
             *
             * ONE: similar to a basicMove, it controls where the worker moved, and if on perimeter sets to TWO
             * else it returns true and ends
             * TWO: waits for CONFIRMATION of the player's will to use the effect
             * if YES it sets back to ONE but returns false, so that the entire effect is executed again from beginning
             * if NO it returns true and concludes
             *
             * @param board of the game
             * @param turn of the player
             * @param p received from controller
             * @return true when the player refuses in TWO or during ONE the move happened out of the perimeter
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {

                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
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
                    if ((board.getBox(row, column).getTower().size() == Global.WINNINGLEVEL) && board.getBox(turn.getCurrentRow(), turn.getCurrentColumn()).getTower().size() == (Global.WINNINGLEVEL -1)) {
                        turn.setWinner(true);
                    }
                    //changes the current coordinates for a correct build
                    turn.setCurrentRow(row);
                    turn.setCurrentColumn(column);
                    if (onPerimeter(row, column)) {
                        turn.setGodPart(GodPart.TWO);
                        board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, Global.MOVEAGAIN));
                    } else {
                        turn.setGodPart(GodPart.ONE);
                        return true;
                    }

                } else if (turn.getGodPart() == GodPart.TWO) {
                    if (p.getType() != PlayerMoveType.CONFIRM) {
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOMOVE));
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOBUILD));
                        return true;
                    }
                }
                return false;
            }

            /**
             * @param row of the box demanded
             * @param column of the box demanded
             * @return true if on the perimeter
             */
            private boolean onPerimeter(int row, int column) {
                if (row == 0 || row == Global.BOARD_DIM - 1) {
                    return true;
                }
                return column == 0 || column == Global.BOARD_DIM - 1;
            }
        };
        private static final SpecificEffect aresEffect = new SpecificEffect() {
            /**
             *this effect is divided in parts and is meant to be executed with more than one input
             *it will operate different code depending in which part we're in
             *every time it ends and returns true GodPart is set to ONE
             *
             * ONE: uses a basicBuild and then controls if the effect can be activated, if yes it goes to TWO
             * else it concludes and returns true
             * TWO: it waits for CONFIRMATION to the will to use the effect, if yes sets THREE, if no it returns true and
             * concludes the effect
             * THREE: the support method already set the position of the umovedworker, so here it awaits only the input for the box
             * where the block will be removed
             *
             *
             * @param board of the game
             * @param turn of the player
             * @param p received from controller
             * @return true when either the effect concludes in THREE or it's application is not possible in ONE, or there is a refusal in TWO
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                if (turn.getGodPart() == GodPart.ONE) {
                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    int row = p.getRow();
                    int column = p.getColumn();
                    //asks coordinates while box is not adiacent, occupied by worker or dome
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column)) {
                        return false;
                    }
                    board.getBox(row, column).increaseLevel();
                    //i control if there is a worker that can apply the effect
                    if (findUnmovedWorker(board, turn)) {
                        if (thereAreBlocksBearby(board, turn.getCurrentRow(), turn.getCurrentColumn())) {
                            //if yes i go to the confirmation part
                            turn.setGodPart(GodPart.TWO);
                            board.setModelMessage(new ModelMessage(ModelMessageType.CONFIRMATION, Global.DOYOUWANTTOUSEGODEFFECT));
                            return false;
                        }
                    }

                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ""));
                    return true;
                } else if (turn.getGodPart() == GodPart.TWO) {
                    //part two is where i ask confirmation
                    if (p.getType() != PlayerMoveType.CONFIRM) {
                        return false;
                    }
                    if (p.getConfirmation() == ConfirmationEnum.YES) {
                        turn.setGodPart(GodPart.THREE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.YOUCANREMOVEABLOCKNEAR + turn.getCurrentRow() + Global.COMMA + turn.getCurrentColumn()));
                    } else if (p.getConfirmation() == ConfirmationEnum.NO) {
                        turn.setGodPart(GodPart.ONE);
                        board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ""));
                        return true;
                    }
                } else if (turn.getGodPart() == GodPart.THREE) {
                    //if the player accepts, this part uses the effect

                    if (p.getType() != PlayerMoveType.COORD) {
                        return false;
                    }
                    int row = p.getRow();
                    int column = p.getColumn();
                    if (!board.boxIsNear(turn.getCurrentRow(), turn.getCurrentColumn(), row, column) || board.isOccupied(row, column) ||
                            board.isDomed(row, column)) {
                        board.setModelMessage(board.getModelMessage().copyAndAddError(ModelError.THEREISNOBOXTOREMOVE));
                        return false;
                    }
                    if (board.getBox(row, column).getTower().isEmpty()) {
                        board.setModelMessage(board.getModelMessage().copyAndAddError(ModelError.THEREISNOBOXTOREMOVE));
                        return false;
                    }
                    board.getBox(row, column).decreaseLevel();

                    turn.setGodPart(GodPart.ONE);
                    board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ""));
                    return true;
                }
                return false;
            }

            /**
             * searches for the unmoved worker and sets currentRow and currentColumn in the turn correspondingly
             *
             * @param board of the game
             * @param turn of the player
             * @return true if the worker has been found
             */
            private boolean findUnmovedWorker(Board board, Turn turn) {
                for (int r = 0; r < Global.BOARD_DIM; r++) {
                    for (int c = 0; c < Global.BOARD_DIM; c++) {
                        if (board.getBox(r, c).getOccupier() != null &&
                                (board.getBox(r, c).getOccupier().getColour().equals(turn.getColor()) &&
                                        !(r == turn.getCurrentRow() && c == turn.getCurrentColumn()))) {
                            turn.setCurrentRow(r);
                            turn.setCurrentColumn(c);
                            return true;
                        }
                    }
                }
                return false;
            }

            /**
             * @param board of the game
             * @param row where the unmoved worker is
             * @param column where the unmoved worker is
             * @return true if there are removable blocks near him, false elsewhere
             */
            private boolean thereAreBlocksBearby(Board board, int row, int column) {
                //this method controls if the unmoved worker has blocks that can be removed near himself
                for (int r = 0; r < Global.BOARD_DIM; r++) {
                    for (int c = 0; c < Global.BOARD_DIM; c++) {
                        if (board.boxIsNear(r, c, row, column) && !board.getBox(r, c).getTower().isEmpty() && !board.isDomed(r, c)
                                && board.getBox(r, c).getOccupier() == null) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        private static final SpecificEffect zeusEffect = new SpecificEffect() {
            /**
             * This effect is similar to basicbuild, but allows the player to build on his standing box too (just if he won't stand on a dome in case of effect's activation).
             * On the other hand, he can't win by placing a block under himself.
             *
             * @param board the game board
             * @param turn of the player
             * @param p move received by controller
             * @return true if the placing has been executed successfully, false otherwise
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                board.setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, Global.SELECTWHERETOMOVE));

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
            /**
             * This effect makes the player win if there are 5 complete towers on the board.
             * In this case, it will set turn.winner to true.
             *
             * @param board the game board
             * @param turn of the player
             * @param p move received from the controller
             * @return true if the player won, false otherwise
             */
            @Override
            public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p) {
                int completeTowers = 0;

                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (board.getBox(i, j).isComplete()) {
                            completeTowers++;
                        }
                    }
                }

                if (completeTowers >= 5) {
                    turn.setWinner(true);
                    return true;
                } else return false;
            }
        };

        static final God athena = new God(Global.ATHENA, Global.ATHENA_DESCRIPTION, athenaEffect);
        static final God minotaur = new God(Global.MINOTAUR, Global.MINOTAUR_DESCRIPTION, minotaurEffect);
        static final God pan = new God(Global.PAN, Global.PAN_DESCRIPTION, panEffect);
        static final God apollo = new God(Global.APOLLO, Global.APOLLO_DESCRIPTION, apolloEffect);
        static final God prometheus = new God(Global.PROMETHEUS, Global.PROMETHEUS_DESCRIPTION, prometheusEffect);
        static final God artemis = new God(Global.ARTEMIS, Global.ARTEMIS_DESCRIPTION, artemisEffect);
        static final God atlas = new God(Global.ATLAS, Global.ATLAS_DESCRIPTION, atlasEffect);
        static final God demeter = new God(Global.DEMETER, Global.DEMETER_DESCRIPTION, demeterEffect);
        static final God hephaestus = new God(Global.HEPHAESTUS, Global.HEPHAESTUS_DESCRIPTION, hephaestusEffect);
        static final God chronus = new God(Global.CHRONUS, Global.CHRONUS_DESCRIPTION, chronusEffect);
        static final God hestia = new God(Global.HESTIA, Global.HESTIA_DESCRIPTION, hestiaEffect);
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

        static void initializeGodsFromFile() throws IOException, ClassNotFoundException {


            ListOfGodContainer listOfGodContainer = readListContainerFromFile();

            ArrayList<God> arrayGod = listOfGodContainer.getGodArrayList();


            for(God n : arrayGod){

                if(n.hasEffectType(GodTypeEffect.ON_MOVE)){ movelist.put(n.godName, n); }

                if(n.hasEffectType(GodTypeEffect.ON_BUILD)){ buildlist.put(n.godName, n); }

                if(n.hasEffectType(GodTypeEffect.ON_OPPONENT)){ opponentlist.put(n.godName, n); }

                if(n.hasEffectType(GodTypeEffect.ON_SETUP)){ setuplist.put(n.godName, n); }

                if(n.hasEffectType(GodTypeEffect.ON_NEEDCONFIRMATION)){ needsConfirmationlist.put(n.godName, n); }

            }


        }


        private static ListOfGodContainer readListContainerFromFile() throws IOException, ClassNotFoundException {


            ClassLoader classLoader = GodLookUpTable.class.getClassLoader();

            InputStream inputStream = classLoader.getResourceAsStream(Global.GODSJSONFILE);

            ObjectInputStream objectinputstream = new ObjectInputStream(inputStream);
            ListOfGodContainer read = (ListOfGodContainer) objectinputstream.readObject();

            objectinputstream.close();


            return read;


        }

    }


    private GodLookUpTable() {

    //hiding default constructor

    }

    public static God lookUp(String godname) {

        godname = godname.toUpperCase();

        //inizializzazione delle carte e della lista o liste a cui appartengono
        if( !alreadyInitialized ) {


            try {

                GodsImplementation.initializeGodsFromFile();

            } catch (Exception e) {

                GodsImplementation.initializeGodsFromSourceCode();
            }


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

