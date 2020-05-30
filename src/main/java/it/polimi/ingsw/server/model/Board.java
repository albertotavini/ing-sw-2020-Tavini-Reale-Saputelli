package it.polimi.ingsw.server.model;



import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.bothsides.utils.Global;


/**
 * This class represents the game board, where will be placed workers and pieces.
 * During a match, the Board conveys all the informations needed to understand the game status.
 */
public class Board {

    private Box[][] matrixBoard = new Box[Global.DIM][Global.DIM];
    //parameter needed for athena's effect
    private boolean allowedToScale;
    private ModelMessage modelMessage;


    /**
     * The constructor initializes the Board with all its Box objects and sets the first ModelMessage, asking for divinity names.
     * Moreover, the Board is set scalable by every player due to the current inactivation of Athena's effect.
     */
    public Board(){
        modelMessage = new ModelMessage(ModelMessageType.GODNAME, "We'd like to know the divinity names");
        for(int i = 0; i < Global.DIM; i++){
            for(int j = 0; j < Global.DIM; j++) {
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

    /**
     * This method is used to know if two given boxes (of the current Board) are near between each other or not.
     * It is useful then to decide about a worker's move or build.
     *
     * @param r1 first box's row
     * @param c1 first box's column
     * @param r2 second box's row
     * @param c2 second box's column
     * @return true if the two boxes are near, false if they are not.
     */
    public boolean boxIsNear (int r1, int c1, int r2, int c2) {
        if (!inBoundaries(r1, c1) || !inBoundaries(c2,r2)) {
            modelMessage = modelMessage.copyAndAddError(ModelError.OUTOFBOUND);
            return false;
        }

        //same box
        if (r1 == r2 && c1==c2) {
            modelMessage = modelMessage.copyAndAddError(ModelError.CURRENTBOX);
            return false;
        }

        //different boxes
        if (r1-r2 == 1 || r2-r1 == 1 || r1-r2 == 0) {
            if (c1 - c2 == 1 || c2 - c1 == 1 || c2 - c1 == 0){
                return true;
            }
        }
        modelMessage = modelMessage.copyAndAddError(ModelError.TOOFAR);
        return false;
    }

    /**
     * This method checks if on the boxes, which are adiacent to the given one, there is at least one free space.
     *
     * @param r the given box's row
     * @param c the given box's column
     * @return true if there is at least one space free, false otherwise.
     */
    //checks if there is a place where it is possible to move or build near box r,c
    public boolean isNearbySpaceFree (int r, int c) {
        for (int i = 0; i<Global.DIM; i++) {
            for (int j = 0; j<Global.DIM; j++) {
                //checks every near box
                // if the box is not occupied by a worker or dome
                if (boxIsNear(r, c, i, j) && getBox(i, j).getOccupier() == null && !isDomed(i,j)  &&
                        (isAllowedToScale() && getBox(i,j).getTower().size() - getBox(r,c).getTower().size() <= 1 ) ||
                        (!isAllowedToScale() && getBox(i,j).getTower().size() - getBox(r,c).getTower().size() <= 0))
                {
                            //then there's a place where it is possible to move
                            return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is used to check if a worker can move from a box to another one.
     * It also checks the Athena's effect condition.
     *
     * @param r1 current box's row
     * @param c1 current box's column
     * @param r2 new box's row
     * @param c2 new box's column
     * @return true if the new box is not too high to move on than the current one, false otherwise.
     */
    public boolean isScalable(int r1, int c1, int r2, int c2) {
        if (!boxIsNear(r1, c1, r2, c2)) {
            return false;
        }
        if (isAllowedToScale()) {
            if ( (getBox(r2, c2).getTower().size() - getBox(r1, c1).getTower().size()) < 2) {
                return true;
            }else {
                modelMessage = modelMessage.copyAndAddError(ModelError.TOOHIGH);
            }
        }
        else {//considers the case athena's effect has been activated and opponents cannot scale
            if ((getBox(r2, c2).getTower().size() - getBox(r1, c1).getTower().size()) < 1){
                return true;
            } else {
                modelMessage = modelMessage.copyAndAddError(ModelError.TOOHIGHATHENA);
            }
        }
        return false;
    }

    /**
     * This method checks if a given box is in the board's boundaries.
     *
     * @param row given box's row
     * @param column given box's column
     * @return true if the given box is in the board's boundaries, false otherwise.
     */
    public boolean inBoundaries (int row, int column){
        if (row >= 0 && row < Global.DIM) {
            if (column >= 0 && column < Global.DIM) {
                return true;
            }
        }
        modelMessage = modelMessage.copyAndAddError(ModelError.OUTOFBOUND);
        return false;
    }

    /**
     * This method sets the given Worker as the occupier of the given box.
     *
     * @param w the given worker
     * @param row the given box's row
     * @param column the given box's column
     */
    public void placeWorker(Worker w, int row, int column) {
        if (inBoundaries(row, column)) {
            matrixBoard[row][column].setOccupier(w);
        }
    }

    /**
     * This method is used to print the board on the CLI, with its buildings and workers.
     */
    public void drawBoard () {
        System.out.println("       0       1       2       3       4 ");
        int rowIndex = 0;
        for (Box[] line : matrixBoard) {
            System.out.println(" "+rowIndex+ "   "+line[0]+"   "+line[1]+ "   "+line[2]+"   "+line[3]+"   "+line[4]);
            rowIndex++;
        }

    }

    /**
     * This method checks if a given box is domed.
     * In this case an error is sent to inform the player about the presence of a dome on the given box.
     *
     * @param row the given's box row
     * @param column the given's box column
     * @return true if the given box is domed, false otherwise.
     */
    public boolean isDomed(int row, int column) {
        boolean domed = getBox(row, column).isDomed();
        if (domed) {
            modelMessage = modelMessage.copyAndAddError(ModelError.DOMETHERE);
        }
        return domed;
    }

    /**
     * This method checks if a given box is occupied by a worker.
     * In this case an error is sent to inform the player about the presence of a worker on the given box.
     *
     * @param row the given's box row
     * @param column the given's box column
     * @return true if the given box is occupied by a worker, false otherwise.
     */
    public boolean isOccupied(int row, int column){
        boolean occupied = (getBox(row, column).getOccupier() != null);
        if (occupied) {
            modelMessage = modelMessage.copyAndAddError(ModelError.WORKERTHERE);
        }
        return occupied;
    }

    /**
     * This method takes a photography of the board, composed of photos of the boxes.
     *
     * @return the photography of the board.
     */
    BoardPhotography takePhotograph() {
        BoardPhotography photography = new BoardPhotography();
        for (int r = 0; r < Global.DIM; r++) {
            for (int c = 0; c < Global.DIM; c++) {
                photography.setBoxPhoto(r, c, getBox(r, c).photographBox());
            }
        }
        return photography;

    }

    /**
     * This method is used to move a worker from a box to another one.
     *
     * @param r1 current box's row
     * @param c1 current box's column
     * @param r2 new box's row
     * @param c2 new box's column
     */
    //method to simplify the move operation in Turn, it's tested in TurnTest, basic move
    public void moveWorker (int r1, int c1, int r2, int c2) {
        Worker w = getBox(r1, c1).getOccupier();
        getBox(r1, c1).setOccupier(null);
        getBox(r2, c2).setOccupier(w);
    }

    Board cloneBoard() {
        Board clonedBoard = new Board();
        Box provBox;
        for (int r = 0; r<Global.DIM; r++){
            for (int c = 0; c<Global.DIM; c++ ) {
                provBox = getBox(r, c).cloneBox();
                clonedBoard.setBox( provBox, r ,c );
            }
        }
        return clonedBoard;
    }

}


