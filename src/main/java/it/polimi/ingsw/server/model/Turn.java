package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.model.god.*;
import it.polimi.ingsw.server.utils.Global;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMoveType;


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

    public Turn(Player p, Color color, String godName){
        this.relatedPlayer = p;
        this.color = color;
        this.divinityCard = new GenericGod(godName);
        prevCoord = new PlayerMove(7, 7, relatedPlayer);
        //godState = GodStateOne.getInstance();
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


    //Check on both of the workers which belong to the player who can move during the current turn
    public boolean checkIfCanMove(Board board) {
        int blockedWorkers = 0;
        for (int r = 0; r< Global.dim; r++) {
            for (int c=0; c<Global.dim; c++)  {
                //if it is occupied by a worker of the correct colour
                if (board.getBox(r, c).getOccupier()!= null && board.getBox(r, c).getOccupier().getColour().equals(this.getColor())) {
                    if (!board.isNearbySpaceFree(r,c)) {blockedWorkers++;}
                }
            }
        }
        //if both the workers have no free space around the player cannot move
        return blockedWorkers != 2;
    }

    //checks if the worker moved (in currentRow, currentColumn) can build
    public boolean checkIfCanBuild(Board board) {
        for (int r=0; r<Global.dim; r++) {
            for (int c=0; c<Global.dim; c++)  {
                if (board.boxIsNear(currentRow, currentColumn, r, c )) {
                    //if the box is near and not occupied by workers or domes, it is possible for the player to build
                    if (board.getBox(r, c).getOccupier() == null && !board.isDomed(r,c) ) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    public boolean selectWorker (Board board, PlayerMove p) {
        if (p.getType() != PlayerMoveType.Coord) {return false;}
        int row = p.getRow();
        int column = p.getColumn();
        //asks the player the worker while out of board, box not occupied or occupied by other worker, worker who can't move
       if (!board.inBoundaries(row, column) || board.getBox(row,column).getOccupier() == null ||
               !board.getBox(row, column).getOccupier().getColour().equals(this.getColor()) ||
               !board.isNearbySpaceFree(row, column)) {
           return false;
       }
       this.currentRow = row;
       this.currentColumn = column;
       return true;
    }

    public boolean move (Board board, PlayerMove p) {
        //this method decides whether we need to apply the god's effect or not
        if (GodLookUpTable.isEffectMove(getDivinityCard().getSpecificGodName())) {
            return getDivinityCard().activateEffect(board, this, p);
        }
        else {
            return basicMove(board, p);
        }
    }

    //the move algorithm without god powers
    public boolean basicMove (Board board, PlayerMove p) {
        if (p.getType() != PlayerMoveType.Coord) {return false;}
        int row = p.getRow();
        int column = p.getColumn();
        //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
        if (!board.boxIsNear(currentRow, currentColumn, row, column) || board.getBox(row, column).getOccupier() != null ||
                board.isDomed(row, column) || !board.isScalable(currentRow, currentColumn, row, column)) {
            return false;
        }
        //moves the worker
        board.moveWorker(currentRow, currentColumn, row, column);
        //checks if the player won
        if (board.getBox(row, column).getTower().size() == 3 && board.getBox(currentRow, currentColumn).getTower().size() ==2) {
            winner = true;
        }
        //changes the current coordinates for a correct build;
        this.currentRow = row;
        this.currentColumn = column;
        return true;

    }

    public boolean build (Board board, PlayerMove p) {
        if (GodLookUpTable.isEffectBuild(getDivinityCard().getSpecificGodName())) {
            return getDivinityCard().activateEffect(board, this, p);
        }
        else {
            return basicBuild(board, p);
        }
    }

    //the build algorithm without god powers
    public boolean basicBuild (Board board, PlayerMove p) {
        if (p.getType() != PlayerMoveType.Coord) {return false;}
        int row = p.getRow();
        int column = p.getColumn();
        //asks coordinates while box is not adiacent, occupied by worker or dome
        if (!board.boxIsNear(currentRow, currentColumn, row, column) || board.getBox(row, column).getOccupier() != null ||
                board.isDomed(row, column)) {
            return false;
        }
        board.getBox(row, column).increaseLevel();
        return true;

    }

    public boolean placeWorker (Board board, PlayerMove p, String workerTag) {
        if (p.getType() != PlayerMoveType.Coord) {return false;}
        int row = p.getRow();
        int column = p.getColumn();
        if (!board.inBoundaries(row, column) || board.getBox(row, column).getOccupier() != null ) {
            return false;
        }
        //found an unoccupied box, creates and then places the first worker
        board.getBox(row, column).setOccupier(new Worker(relatedPlayer,  getColor(), workerTag));
        return true;
    }


    //called when a player loses, removes his workers from the board
    public void clearBoard(Board board) {
        for (int r=0; r<Global.dim; r++) {
            for (int c=0; c<Global.dim; c++)  {
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

}


