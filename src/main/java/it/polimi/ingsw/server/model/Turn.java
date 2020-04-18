package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.model.god.GenericGod;
import it.polimi.ingsw.server.model.god.GodLookUpTable;
import it.polimi.ingsw.server.view.playerMove;

import java.util.Scanner;

//NOMVC methods were used with stdin to try methods

public class Turn {

    private final Player relatedPlayer;
    private final Color color;
    Scanner sc = new Scanner(System.in);
    //memorise where the worker is for the move, then where it's been moved for the build
    private int currentRow = 0;
    private int currentColumn = 0;
    private boolean winner = false;
    private GenericGod divinityCard;

    public Turn(Player p, Color color, String godName){
        this.relatedPlayer = p;
        this.color = color;
        this.divinityCard = new GenericGod(godName);
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


    //Check on both of the workers which belong to the player who can move during the current turn
    public boolean checkIfCanMove(Board board) {
        int blockedWorkers = 0;
        for (int r=0; r<5; r++) {
            for (int c=0; c<5; c++)  {
                //if it is occupied by a worker of the correct colour
                if (board.getBox(r, c).getOccupier()!= null && board.getBox(r, c).getOccupier().getColour().equals(this.getColor())) {
                    if (!board.isNearbySpaceFree(r,c)) {blockedWorkers++;}
                }
            }
        }
        //if both the workers have no free space around the player cannot move
        if (blockedWorkers == 2) { return false; }
        else { return true; }
    }

    //checks if the worker moved (in currentRow, currentColumn) can build
    public boolean checkIfCanBuild(Board board) {
        for (int r=0; r<5; r++) {
            for (int c=0; c<5; c++)  {
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

    public boolean selectWorker (Board board, playerMove p) {
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

    //finds a worker to move
    public void NOMVCselectWorker (Board board) {
        int row = 5;
        int column = 5;
        System.out.println("Insert coordinates of the worker you want to move");
        //asks the player the worker while
        do {
            do {
                do {
                    System.out.println("Insert row");
                    row = sc.nextInt();
                    System.out.println("Insert column");
                    column = sc.nextInt();
                } while (!board.inBoundaries(row, column));
            } while (board.getBox(row,column).getOccupier() == null);
        } while (!board.getBox(row, column).getOccupier().getColour().equals(this.getColor()) ||
                !board.isNearbySpaceFree(row, column)) ;
        this.currentRow = row;
        this.currentColumn = column;
    }

    public boolean move (Board board, playerMove p) {
        int row = p.getRow();
        int column = p.getColumn();
        //this method decides whether we need to apply the god's effect or not
        if (GodLookUpTable.isEffectMove(getDivinityCard().getSpecificGodName())) {
            return getDivinityCard().activateEffect(board, this, p);
        }
        else {
            return basicMove(board, p);
        }
    }

    //the move algorithm without god powers
    public boolean basicMove (Board board, playerMove p) {
        int row = p.getRow();
        int column = p.getColumn();
        //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
        if (!board.boxIsNear(currentRow, currentColumn, row, column) || board.getBox(row, column).getOccupier() != null ||
                board.isDomed(row, column) || !board.isScalable(currentRow, currentColumn, row, column)) {
            return false;
        }
        //moves the worker
        Worker w = board.getBox(currentRow, currentColumn).getOccupier();
        board.getBox(currentRow, currentColumn).setOccupier(null);
        board.getBox(row, column).setOccupier(w);
        //checks if the player won
        if (board.getBox(row, column).getTowerSize()== 3 && board.getBox(currentRow, currentColumn).getTowerSize() ==2) {
            winner = true;
        }
        //changes the current coordinates for a correct build;
        this.currentRow = row;
        this.currentColumn = column;
        return true;

    }

    public void NOMVCmove (Board board) {
        System.out.println("Your worker is on (" +currentRow+" , "+currentColumn+"). Where do you want to move him?");
        int row;
        int column;
        boolean done = false;
        //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
        do {
            System.out.println("Insert row");
            row = sc.nextInt();
            System.out.println("Insert column");
            column = sc.nextInt();
            //this method decides whether we need to apply the god's effect or not
            if (GodLookUpTable.isEffectMove(getDivinityCard().getSpecificGodName())) {
                done = getDivinityCard().activateEffect(board, this, new playerMove(row, column, getPlayer()));
            }
            else {
                done = basicMove(board, new playerMove(row, column, getPlayer()));
            }
        } while (!done);

        }

    public boolean build (Board board, playerMove p) {
        int row = p.getRow();
        int column = p.getColumn();
        if (GodLookUpTable.isEffectBuild(getDivinityCard().getSpecificGodName())) {
            return getDivinityCard().activateEffect(board, this, p);
        }
        else {
            return basicBuild(board, p);
        }
    }

    //the build algorithm without god powers
    public boolean basicBuild (Board board, playerMove p) {
        int row = p.getRow();
        int column = p.getColumn();
        //asks coordinates while box is not adiacent, occupied by worker or dome
        if (!board.boxIsNear(currentRow, currentColumn, row, column) || board.getBox(row, column).getOccupier() != null ||
                board.isDomed(row, column)) {
            return false;
        }
        board.increaseLevel(row, column);
        return true;

    }

    public void NOMVCbuild (Board board) {
            System.out.println("Your worker moved to (" +currentRow+" , "+currentColumn+"). Where do you want to build?");
            int row;
            int column;
            boolean done = false;
            //asks coordinates while box is not adiacent, occupied by worker or dome
            do {
                System.out.println("Insert row");
                row = sc.nextInt();
                System.out.println("Insert column");
                column = sc.nextInt();

                if (GodLookUpTable.isEffectBuild(getDivinityCard().getSpecificGodName())) {
                    done = getDivinityCard().activateEffect(board, this, new playerMove(row, column, getPlayer()));
                }
                else {
                    done = basicBuild(board, new playerMove(row, column, getPlayer()));
                }
            }while (!done);

            return;
        }


    public boolean placeWorker (Board board, playerMove p, String workerTag) {
        int row = p.getRow();
        int column = p.getColumn();
        if (!board.inBoundaries(row, column) || board.getBox(row, column).getOccupier() != null ) {
            return false;
        }
        //found an unoccupied box, creates and then places the first worker
        board.getBox(row, column).setOccupier(new Worker(relatedPlayer,  getColor(), workerTag));
        return true;
    }

    //method to place workers when game begins
    public void NOMVCplaceWorkers (Board board) {
        int row;
        int column;
        System.out.println("Where do you want to place worker 1?");
        do {
            do {
                System.out.println("Insert row");
                row = sc.nextInt();
                System.out.println("Insert column");
                column = sc.nextInt();
            }while(!board.inBoundaries(row, column));
        } while (board.getBox(row, column).getOccupier() != null );

        //found an unoccupied box, creates and then places the first worker
        board.getBox(row, column).setOccupier(new Worker(relatedPlayer,  getColor(), "A"));


        System.out.println("Where do you want to place worker 2?");
        do {
            do {
                System.out.println("Insert row");
                row = sc.nextInt();
                System.out.println("Insert column");
                column = sc.nextInt();
            } while(!board.inBoundaries(row, column));
        } while (board.getBox(row, column).getOccupier() != null );
        //does the same for the second
        board.getBox(row, column).setOccupier(new Worker(relatedPlayer, getColor(), "B"));
    }

    //calls all the methods for taking one's turn
    public boolean NOMVCcallTurn(Board board) {
        if (!checkIfCanMove(board)) {
            return false;
        }

        NOMVCselectWorker(board);
        NOMVCmove(board);

        if (winner) {return true;}

        if (!checkIfCanBuild(board)) {
            return false;
        }
        NOMVCbuild(board);
        return true;
    }

    //called when a player loses, removes his workers from the board
    public void clearBoard(Board board) {
        for (int r=0; r<5; r++) {
            for (int c=0; c<5; c++)  {
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
        if( ((Turn) obj).relatedPlayer.equals(this.relatedPlayer)) return true;
        else return false;
    }

}


