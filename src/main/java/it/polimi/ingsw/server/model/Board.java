package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.model.piece.Block;
import it.polimi.ingsw.server.model.piece.Dome;
import it.polimi.ingsw.server.model.piece.Piece;

import java.util.Stack;

public class Board {

    private static Board instanceBoard;
    private Box[][] matrixBoard = new Box[5][5];
    //parameter needed for athena's effect 
    private boolean allowedToScale;
    private String boardMessage;


    //overriding del costruttore di def. (per adesso.....)
    private Board(){
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j < 5; j++) {
                matrixBoard[i][j] = new Box(i, j);
            }
        }
        allowedToScale = true;
    }

    public String getBoardMessage() {
        return boardMessage;
    }

    public void setBoardMessage(String boardMessage) {
        this.boardMessage = boardMessage;
    }

    public boolean isDomed(int row, int column) {
        return getBox(row, column).isDomed();
    }

    public Box getBox (int row, int column ){
        return matrixBoard[row][column];
    }

    public boolean isAllowedToScale() {
        return allowedToScale;
    }

    public void setAllowedToScale(boolean allowedToScale) {
        this.allowedToScale = allowedToScale;
    }

    public boolean boxIsNear (int r1, int c1, int r2, int c2) /*throws IllegalArgumentException*/ {
        if (!inBoundaries(r1, c1) || !inBoundaries(c2,r2)) {
            System.out.println("Insert valid coordinates.");
            return false;
            //throw new IllegalArgumentException("Insert valid coordinates.");
        }

        //same box
        if (r1 == r2 && c1==c2) { return false; }

        //different boxes
        if (r1-r2 == 1 || r2-r1 == 1 || r1-r2 == 0) {
            if (c1-c2 == 1 || c2-c1 == 1 || c2-c1 == 0) {
                return true;
            }
            else {return false;}
        }
        else {return false;}
    }

    //checks if there is a place where it is possible to move near box r, c
    public boolean isNearbySpaceFree (int r, int c) {
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                //checks every near box
                if (boxIsNear(r, c, i, j)) {
                    // if the box is not occupied by a worker or dome
                    if (getBox(i, j).getOccupier() == null && !isDomed(r,c)) {
                        //if not too high
                        if (getBox(i,j).getTowerSize() - getBox(r,c).getTowerSize() <= 1 ) {
                            //then there's a place where it is possible to move
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*
    public boolean boxIsNear(Box box1, Box box2){
        if (box1.equals(box2)) { return false; }

        //different boxes
        if (box1.getRow()-box2.getRow() == 1 || box2.getRow()-box1.getRow() == 1 || box1.getRow()-box2.getRow()== 0) {
            if (box1.getColumn()-box2.getColumn() == 1 || box2.getColumn()-box1.getColumn() == 1 || box1.getColumn()-box2.getColumn()== 0) {
                return true;
            }
            else { return false; }
        }
        else { return false; }
    }
    */

    public boolean isScalable(int r1, int c1, int r2, int c2) {
        if (!boxIsNear(r1, c1, r2, c2)) {
            return false;
        }
        if (isAllowedToScale()) {
            if ((getBox(r2, c2).getTowerSize() - getBox(r1, c1).getTowerSize()) < 2) {
                return true;
            } else {
                return false;
            }
        }
        else {//considers the case athena's effect has been activated and opponents cannot scale
            if ((getBox(r2, c2).getTowerSize() - getBox(r1, c1).getTowerSize()) < 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean inBoundaries (int row, int column){
        if (row <0 || row>4 ) return false;
        else if (column<0 || column>4) return false;
        else return true;
    }

    public void placeWorker(Worker w, int row, int column) {
        if (inBoundaries(row, column)) {
            matrixBoard[row][column].setOccupier(w);
        }
    }

    public void increaseLevel (int row, int column) {
        if (inBoundaries(row, column)) {
            matrixBoard[row][column].increaseLevel();
        }
        else {
            System.out.println("Coordinates outside of the board.");
            //throw new IllegalArgumentException("Coordinates outside of the board.");
            }
    }

    public void decreaseLevel (int row, int column) {
        if (inBoundaries(row, column)) {
            matrixBoard[row][column].decreaseLevel();
        } else {
            System.out.println("Coordinates outside of the board.");
            //throw new IllegalArgumentException("Coordinates outside of the board.");
        }
    }

    public static Board instance() {
        if(instanceBoard == null) {instanceBoard = new Board();}
        return instanceBoard;
    }

    public void drawBoard () {
        System.out.println("       0       1       2       3       4 ");
        int rowIndex = 0;
        for (Box[] line : matrixBoard) {
            System.out.println(" "+rowIndex+ "   "+line[0]+"   "+line[1]+ "   "+line[2]+"   "+line[3]+"   "+line[4]);
            rowIndex++;
        }
        System.out.println(boardMessage);

    }

    //method to simplify the move operation in Turn, it's tested in TurnTest, basic move
    public void moveWorker (int r1, int c1, int r2, int c2) {
        Worker w = getBox(r1, c1).getOccupier();
        getBox(r1, c1).setOccupier(null);
        getBox(r2, c2).setOccupier(w);
    }

    //method to simplify minotaur's effect ( a TUMOR of a function ), it's test is in activateMinotaurEffectTest in GenericGodTest
    public boolean sendsOpponentBack(int r1, int c1, int r2, int c2) {
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

            } else if (r2 > r1) {
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
        if (inBoundaries(r3, c3) && getBox(r3, c3).getOccupier() == null && !isDomed(r3,c3)) {
            yours = getBox(r1, c1).getOccupier();
            getBox(r1, c1).setOccupier(null);
            other = getBox(r2, c2).getOccupier();
            getBox(r2, c2).setOccupier(yours);
            getBox(r3, c3).setOccupier(other);
            return true;
        }else {
            return false;
        }
    }

    //method to simplify apollo's effect, it's test is in activateApolloEffectTest in GenericGodTest
    //parse is done in the god's effect
    public void switchWorkers(int r1, int c1, int r2, int c2) {
        Worker yours = getBox(r1, c1).getOccupier();
        Worker other = getBox(r2, c2).getOccupier();
        getBox(r2, c2).setOccupier(yours);
        getBox(r1, c1).setOccupier(other);

    }
}


