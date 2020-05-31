package it.polimi.ingsw.server.model;


import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.server.model.god.*;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMoveType;


/**
 * This class represents the player's turn.
 * Indeed it contains informations about the player related to the Turn, his color, his assigned divinity card
 * and his potential win.
 */
public class Turn {

    private final Player relatedPlayer;
    private final Color color;
    private final GenericGod divinityCard;
    //memorise where the worker is for the move, then where it's been moved for the build
    private int currentRow = 0;
    private int currentColumn = 0;
    private boolean winner = false;

    //these attributes are needed in some god's effects
    private PlayerMove prevCoord;
    private GodPart godPart;

    /**
     * The constructor initializes the Turn object with its attributes.
     * It also by sets the Turn in the first GodPart.
     *
     * @param p the given related Player
     * @param color the given related Player's color
     * @param godName the given related Player's divinity card
     */
    public Turn(Player p, Color color, String godName){
        this.relatedPlayer = p;
        this.color = color;
        this.divinityCard = new GenericGod(godName);
        prevCoord = new PlayerMove(7, 7, relatedPlayer);
        godPart = GodPart.ONE;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public void setCurrentColumn(int currentColumn) {
        this.currentColumn = currentColumn;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public boolean isWinner() {
        return winner;
    }

    public Player getPlayer() { return this.relatedPlayer; }

    public Color getColor() { return color; }

    public GenericGod getDivinityCard() { return divinityCard; }

    public PlayerMove getPrevCoord() {
        return prevCoord;
    }

    public void setPrevCoord(PlayerMove prevCoord) {
        this.prevCoord = prevCoord;
    }

    public GodPart getGodPart() {
        return godPart;
    }

    public void setGodPart(GodPart godPart) {
        this.godPart = godPart;
    }


    /**
     * This method checks if both the related player's workers can move during the turn.
     * If none of them can, the player loses.
     *
     * @param board the game board
     * @return true if at least one of the related player's worker can move, false otherwise.
     */
    //Check on both of the workers which belong to the player who can move during the current turn
    public boolean checkIfCanMove(Board board) {
        int blockedWorkers = 0;
        for (int r = 0; r < Global.BOARD_DIM; r++) {
            for (int c = 0; c <Global.BOARD_DIM; c++)  {
                //if it is occupied by a worker of the correct colour
                if (board.getBox(r, c).getOccupier()!= null && board.getBox(r, c).getOccupier().getColour().equals(this.getColor())
                    && !board.isNearbySpaceFree(r,c)) {
                     blockedWorkers++;
                }
            }
        }
        //if both the workers have no free space around the player cannot move
        return blockedWorkers != Global.NUMOFWORKERS;
    }

    /**
     * This method checks if the worker moved in a certain box can build after his move.
     * If it can't, the player loses.
     *
     * @param board the game board
     * @return true if the chosen worker can build, false otherwise
     */
    //checks if the worker moved in (currentRow, currentColumn) can build
    public boolean checkIfCanBuild(Board board) {
        for (int r = 0; r<Global.BOARD_DIM; r++) {
            for (int c = 0; c<Global.BOARD_DIM; c++)  {
                if (board.boxIsNear(currentRow, currentColumn, r, c ) && board.getBox(r, c).getOccupier() == null && !board.isDomed(r,c) ) {
                    //if the box is near and not occupied by workers or domes, it is possible for the player to build
                        return true;

                }
            }
        }
        return false;
    }

    /**
     * This method is used to make the player choose one of his workers on the game board.
     * The worker will be asked to the player until he doesn't make a valid choice.
     *
     * @param board the game board
     * @param p the player's move
     * @return true if the player did a valid choice, false otherwise
     */
    public boolean selectWorker (Board board, PlayerMove p) {
        if (p.getType() != PlayerMoveType.COORD) {return false;}
        int row = p.getRow();
        int column = p.getColumn();
        //asks the player the worker while out of board, box not occupied or occupied by other worker, worker who can't move
        if (!board.inBoundaries(row, column) || !board.isOccupied(row, column) ||
               !board.getBox(row, column).getOccupier().getColour().equals(this.getColor()) ||
               !board.isNearbySpaceFree(row, column)) {
           board.setModelMessage(board.getModelMessage().copyAndAddError(ModelError.NOTYOURWORKERTHERE));
           return false;
       }
        this.currentRow = row;
        this.currentColumn = column;
        return true;
    }

    /**
     * This method is used during the player's move.
     * </p>
     * If the player's divinity card's effect can be activated during the move, it will be activated or it will be
     * asked to the player if he wants to activate it, depending on the specific effect.
     * If the player's divinity card's effect can't be activated during the move, the method will just
     * make the player move one of his workers as usual.
     *
     * @param board the given game board
     * @param p the player's move
     * @return true if the move has been successfully effectuated, false otherwise
     */
    public boolean move (Board board, PlayerMove p) {
        //this method decides whether we need to apply the god's effect or not
        if (GodLookUpTable.isEffectMove(getDivinityCard().getSpecificGodName())) {
            return getDivinityCard().activateEffect(board, this, p);
        }
        else {
            return basicMove(board, p);
        }
    }

    /**
     * This method is used to effectuate the standard move of a chosen worker.
     * </p>
     * The move will be asked to the player until he doesn't make a valid choice.
     * If the move has been effectuated successfully, the method will firstly check if this move made the current player won
     * and finally it will update the Turn's attribute currentRow and currentColumn.
     *
     * @param board the given game board
     * @param p the player's move
     * @return true if the move has been successfully effectuated, false otherwise
     */
    //the move algorithm without god powers
    public boolean basicMove (Board board, PlayerMove p) {
        if (p.getType() != PlayerMoveType.COORD) {return false;}
        int row = p.getRow();
        int column = p.getColumn();
        //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
        if (!board.boxIsNear(currentRow, currentColumn, row, column) || board.isOccupied(row, column) ||
                board.isDomed(row, column) || !board.isScalable(currentRow, currentColumn, row, column)) {
            return false;
        }
        //moves the worker
        board.moveWorker(currentRow, currentColumn, row, column);
        //checks if the player won
        if ((board.getBox(row, column).getTower().size() == Global.WINNINGLEVEL) && board.getBox(currentRow, currentColumn).getTower().size() == (Global.WINNINGLEVEL -1)) {
            winner = true;
        }
        //changes the current coordinates for a correct build
        this.currentRow = row;
        this.currentColumn = column;
        return true;

    }

    /**
     * This method is used during the player's build.
     * If the player's divinity card's effect can be activated during the build, it will be activated or it will be
     * asked to the player if he wants to activate it, depending on the specific effect.
     * If the player's divinity card's effect can't be activated during the build, the method will just
     * make the player build with his worker as usual.
     *
     * @param board the given game board
     * @param p the player's move
     * @return true if the build has been successfully effectuated, false otherwise
     */
    public boolean build (Board board, PlayerMove p) {
        if (GodLookUpTable.isEffectBuild(getDivinityCard().getSpecificGodName())) {
            return getDivinityCard().activateEffect(board, this, p);
        }
        else {
            return basicBuild(board, p);
        }
    }

    /**
     * This method is used to effectuate the standard build with a chosen worker.
     * </p>
     * The build will be asked to the player until he doesn't make a valid choice.
     * If the build has been effectuated successfully, the level of the chosen Box will be increased.
     *
     * @param board the given game board
     * @param p the player's move
     * @return true if the build has been successfully effectuated, false otherwise
     */
    //the build algorithm without god powers
    public boolean basicBuild (Board board, PlayerMove p) {
        if (p.getType() != PlayerMoveType.COORD) {return false;}
        int row = p.getRow();
        int column = p.getColumn();
        //asks coordinates while box is not adiacent, occupied by worker or dome
        if (!board.boxIsNear(currentRow, currentColumn, row, column) || board.isOccupied(row, column) ||
                board.isDomed(row, column)) {
            return false;
        }
        board.getBox(row, column).increaseLevel();
        return true;

    }

    /**
     * This method is used to place a worker on a given Box.
     * The Box will be asked to the player until he doesn't make a valid choice.
     * After the placing, the method will update the chosen Box current occupier.
     *
     * @param board the given game board
     * @param p the player's move
     * @param workerTag the chosen worker's tag
     * @return true if the placing has been successfully effectuated, false otherwise
     */
    public boolean placeWorker (Board board, PlayerMove p, String workerTag) {
        if (p.getType() != PlayerMoveType.COORD) {return false;}
        int row = p.getRow();
        int column = p.getColumn();
        if (!board.inBoundaries(row, column) || board.isOccupied(row, column) ) {
            return false;
        }
        //found an unoccupied box, creates and then places the first worker
        board.getBox(row, column).setOccupier(new Worker(relatedPlayer,  getColor(), workerTag));
        return true;
    }

    /**
     * This method is used when a player loses to remove all of his workers from the board
     *
     * @param board the given board
     */
    //called when a player loses, removes his workers from the board
    public void clearBoard(Board board) {
        for (int r = 0; r<Global.BOARD_DIM; r++) {
            for (int c = 0; c<Global.BOARD_DIM; c++)  {
                //if it is occupied by a worker of the correct colour
                if (board.getBox(r, c).getOccupier() != null && board.getBox(r, c).getOccupier().getColour().equals(this.getColor())) {
                    board.getBox(r, c).setOccupier(null);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Turn)) return false;
        return ((Turn) obj).relatedPlayer.equals(this.relatedPlayer);
    }

    /**
     * This method is present in this class because it was asked by Sonar.
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

}


