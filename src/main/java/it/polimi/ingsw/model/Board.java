package it.polimi.ingsw.model;


import java.util.Stack;

public class Board {

    //il setup della gameBoard lo fa direttamente il costruttore
    //l'ho costruito singleton come su l'uml, valutate voi se ne vale la pena, un po' un esaurimento inutile

    private static Board instanceBoard;
    private Box[][] matrixBoard = new Box[5][5];


    //overriding del costruttore di def. (per adesso.....)
    private Board(){
        for(int i = 0 ; i < 5; i++){
            for(int j = 0; j < 5; j++) {
                matrixBoard[i][j] = new Box(i, j);
            }
        }
    }

    public Box getBox ( int row, int column ){
        return matrixBoard[row][column];
    }

    public boolean boxIsNear (int r1, int c1, int r2, int c2){
        if (!inBoundaries(r1, c1) || !inBoundaries(c2,r2)) {
            System.out.println("Insert valid coordinates.");
            return false;
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
                    if (getBox(i, j).getOccupier() == null && getBox(i, j).getTowerSize() < 4) {
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
        if ((getBox(r2,c2).getTowerSize() - getBox(r1, c1).getTowerSize() ) < 2) {
            return true;
        } else {
            return false;
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
        else {System.out.println("Coordinates outside of the board.");}
    }

    public void decreaseLevel (int row, int column) {
        if (inBoundaries(row, column)) {
            matrixBoard[row][column].decreaseLevel();
        } else {
            System.out.println("Coordinates outside of the board.");
        }
    }


    public static Board instance() {
        if(instanceBoard == null) {instanceBoard = new Board();}
        return instanceBoard;
    }

    //va finito di definire
    public void drawBoard () {
        System.out.println("This is the situation on the board:");
        for (Box[] line : matrixBoard) {
            System.out.println(line[0]+"   "+line[1]+ "   "+line[2]+"   "+line[3]+"   "+line[4]);
        }

    }

}

class Box {

    //per adesso basta il costruttore di default
    //da vedere ancora il concetto di torre come aggregato di pezzi
    final private int row;
    final private int column;
    private Worker occupier;
    //private Piece upperPiece;
    //private Piece lowerPiece;
    private Stack <Piece> tower;
    private int towerSize;

    public Box (int row, int column){
        this.occupier = null;
        //this.upperPiece = new Piece(0);
        this.tower = new Stack<Piece>();
        tower.add(new Piece (0));
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }

    public Stack<Piece> getTower() {
        return tower;
    }

    public int getColumn() {
        return column;
    }

    public int getTowerSize() {
        return towerSize;
    }

    public void setTowerSize(int towerSize){
        this.towerSize = towerSize;
    }

    public void increaseLevel () {
        int height = tower.get(tower.size() - 1).getLevel() + 1;
        if (height < 4) {
            tower.add(new Block (height));
            setTowerSize(height);
        }
        else if (height == 4) {
            tower.add(new Dome (height));
            setTowerSize(height);
        }
        else { System.out.println("This tower is complete."); }
    }

    public void decreaseLevel() {
        int height = tower.get(tower.size() - 1).getLevel();
        if (height > 0) {
            tower.remove(tower.size() - 1);
            setTowerSize(height - 1);
        }
        else {System.out.println("This tower doesn't have floors.");}
    }


    public Worker getOccupier() {
        return occupier;
    }

    public void setOccupier(Worker occupier) {
        this.occupier = occupier;
    }



    public String toString() {
        if ((getOccupier() == null) && (tower.size()==1)) {
            return "-- -";
        }
        else if ((getOccupier() != null) && (tower.size()==1)) {
            return getOccupier().getColour()+getOccupier().getWorkerTag()+" -";
        }
        else if ((getOccupier() == null) && (tower.size()>1)) {
            return "-- "+tower.get(tower.size()-1).getLevel();
        }
        else if (getOccupier() != null && tower.size()>1) {
            return getOccupier().getColour()+getOccupier().getWorkerTag()+" "+tower.get(tower.size()-1).getLevel();
        }
        else return " ";
    }

}
