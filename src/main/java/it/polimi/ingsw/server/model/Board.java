package it.polimi.ingsw.server.model;



import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.ModelMessage.ModelMessageType;
import it.polimi.ingsw.server.utils.Global;


public class Board {

    private Box[][] matrixBoard = new Box[Global.dim][Global.dim];
    //parameter needed for athena's effect 
    private boolean allowedToScale;
    private ModelMessage modelMessage;


    //overriding del costruttore di def. (per adesso.....)
    public Board(){
        modelMessage = new ModelMessage(ModelMessageType.NeedsGodName, "We'd like to know the divinity names");
        for(int i = 0 ; i < Global.dim; i++){
            for(int j = 0; j < Global.dim; j++) {
                matrixBoard[i][j] = new Box(i, j);
            }
        }
        allowedToScale = true;
    }

    public ModelMessage getModelMessage() {
        return modelMessage;
    }

    public void setModelMessage(ModelMessage modelMessage) {
        this.modelMessage = modelMessage;
    }

    public Box[][] getMatrixBoard() {
        return matrixBoard;
    }

    public boolean isDomed(int row, int column) {
        return getBox(row, column).isDomed();
    }

    public Box getBox (int row, int column ){
        return matrixBoard[row][column];
    }

    private void setBox(Box box, int row, int column) {
        matrixBoard[row][column] = box;
    }

    private boolean isAllowedToScale() {
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
            return c1 - c2 == 1 || c2 - c1 == 1 || c2 - c1 == 0;
        }
        else {return false;}
    }

    //checks if there is a place where it is possible to move near box r, c
    public boolean isNearbySpaceFree (int r, int c) {
        for (int i=0; i<Global.dim; i++) {
            for (int j=0; j<Global.dim; j++) {
                //checks every near box
                if (boxIsNear(r, c, i, j)) {
                    // if the box is not occupied by a worker or dome
                    if (getBox(i, j).getOccupier() == null && !isDomed(i,j)) {
                        //if not too high
                        if (isAllowedToScale() && getBox(i,j).getTower().size() - getBox(r,c).getTower().size() <= 1) {
                            //then there's a place where it is possible to move
                            return true;
                        }
                        else if (!isAllowedToScale() && getBox(i,j).getTower().size() - getBox(r,c).getTower().size() <= 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public boolean isScalable(int r1, int c1, int r2, int c2) {
        if (!boxIsNear(r1, c1, r2, c2)) {
            return false;
        }
        if (isAllowedToScale()) {
            return (getBox(r2, c2).getTower().size() - getBox(r1, c1).getTower().size()) < 2;
        }
        else {//considers the case athena's effect has been activated and opponents cannot scale
            return (getBox(r2, c2).getTower().size() - getBox(r1, c1).getTower().size()) < 1;
        }
    }

    public boolean inBoundaries (int row, int column){
        if (row <0 || row >= Global.dim ) return false;
        else return column >= 0 && column < Global.dim;
    }

    public void placeWorker(Worker w, int row, int column) {
        if (inBoundaries(row, column)) {
            matrixBoard[row][column].setOccupier(w);
        }
    }

    /*
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

    public void placeDome (int row, int column) {
        if (inBoundaries(row, column)) {
            matrixBoard[row][column].placeDome();
        } else {
            System.out.println("Coordinates outside of the board.");
            //throw new IllegalArgumentException("Coordinates outside of the board.");
        }
    }*/

    /*
    public static Board instance() {
        if(instanceBoard == null) {instanceBoard = new Board();}
        return instanceBoard;
    }


     */

    public void drawBoard () {
        System.out.println("       0       1       2       3       4 ");
        int rowIndex = 0;
        for (Box[] line : matrixBoard) {
            System.out.println(" "+rowIndex+ "   "+line[0]+"   "+line[1]+ "   "+line[2]+"   "+line[3]+"   "+line[4]);
            rowIndex++;
        }

    }

    BoardPhotography takePhotograph() {
        BoardPhotography photography = new BoardPhotography();
        for (int r = 0; r < Global.dim; r++) {
            for (int c = 0; c < Global.dim ; c++) {
                photography.setBoxPhoto(r, c, getBox(r, c).photographBox());
            }
        }
        return photography;

    }

    //method to simplify the move operation in Turn, it's tested in TurnTest, basic move
    public void moveWorker (int r1, int c1, int r2, int c2) {
        Worker w = getBox(r1, c1).getOccupier();
        getBox(r1, c1).setOccupier(null);
        getBox(r2, c2).setOccupier(w);
    }

    Board cloneBoard() {
        Board clonedBoard = new Board();
        Box[][] matrixClone = new Box [Global.dim][Global.dim];
        Box provBox;
        for (int r = 0; r<Global.dim ; r++){
            for (int c = 0; c<Global.dim; c++ ) {
                provBox = getBox(r, c).cloneBox();
                clonedBoard.setBox( provBox, r ,c );
            }
        }
        return clonedBoard;
    }

}


