package it.polimi.ingsw.model;

public class Board {

    //il setup della gameBoard lo fa direttamente il costruttore
    //l'ho costruito singleton come su l'uml, valutate voi se ne vale la pena, un po' un esaurimento inutile

    private static Board instanceBoard;
    private Box[][] gameBoard = new Box[5][5];


    //overriding del costruttore di def. (per adesso.....)
    private Board(){

        for(int i=0 ; i<5; i++){
            for(int j=0; j<5; j++) {
                gameBoard[i][j] = new Box( i, j);
            }
        }
    }

    //added 23/03
    public Box getBox ( int row, int column ){
        return gameBoard[row][column];
    }

    public boolean boxIsNear (int r1, int c1, int r2, int c2){
        //mettere il parse sulle row e column
        //same box
        if (r1 == r2 && c1==c2) {return false;}

        //different boxes
        if (r1-r2 == 1 || r2-r1 == 1 || r1-r2== 0) {
            if (c1-c2 == 1 || c2-c1== 1 || c2-c1==0) {
                return true;
            }
            else {return false;}
        }
        else {return false;}
    }

    //added 23/03
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


    public boolean inBoundaries (int row, int column){
        if (row <0 || row>4 ) return false;
        else if (column<0 || column>4) return false;
        else return true;
    }

    public void placeWorker(Worker w, int row, int column) {
        if (inBoundaries(row, column)) {
            gameBoard[row][column].setOccupier(w);
        }
    }

    public void increaseLevel (int row, int column) {
        if (inBoundaries(row, column)) {
            gameBoard[row][column].increaseLevel();
        }
    }

    public static Board instance() {
        if(instanceBoard == null) instanceBoard = new Board();
        return instanceBoard;
    }

    //va finito di definire

    public void drawBoard () {
        System.out.println("la situazione sulla board è la seguente ");
        for (Box[] line : gameBoard) {
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
    private Piece upperPiece;
    private Piece lowerPiece;

    public Box (int row, int column){
        this.occupier = null;
        this.upperPiece = new Piece(0);
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }

    public int getColumn() {
        return column;
    }

    public Piece getLowerPiece() {
        return lowerPiece;
    }

    public Piece getUpperPiece(){
        return upperPiece;
    }

    public void setLowerPiece(Piece lowerPiece) {
        this.lowerPiece = lowerPiece;
    }

    public void setUpperPiece(Piece upperPiece) {
        this.upperPiece = upperPiece;
    }

    public void increaseLevel() {
        //il caso in cui chiedo di costrire oltre la dome va considerato qui o fuori?
        if (getUpperPiece() instanceof  Dome) {return;}
        //se minore di 3 livelli metto un altro blocco, sennò una dome
        if (getUpperPiece().getLevel()!= 3) {
            int i = getUpperPiece().getLevel() + 1;
            this.setLowerPiece(this.getUpperPiece());
            this.setUpperPiece(new Block(i));
        }
        else {
            this.setLowerPiece(this.getUpperPiece());
            this.setUpperPiece(new Dome(4));
        }
    }

    public Worker getOccupier() {
        return occupier;
    }

    public void setOccupier(Worker occupier) {
        this.occupier = occupier;
    }



    public String toString() {
        if ((getOccupier() == null) && (upperPiece == null)) {
            return "-- -";
        }
        else if ((getOccupier() != null) && (upperPiece == null)) {
            return getOccupier().getColour()+getOccupier().getWorkerTag()+" -";
        }
        else if ((getOccupier() == null) && (upperPiece != null)) {
            return "-- "+upperPiece.getLevel();
        }
        else if (getOccupier() != null && upperPiece != null) {
            return getOccupier().getColour()+getOccupier().getWorkerTag()+" "+upperPiece.getLevel();
        }
        else return " ";
    }
}
