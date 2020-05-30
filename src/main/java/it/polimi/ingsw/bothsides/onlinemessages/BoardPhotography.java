package it.polimi.ingsw.bothsides.onlinemessages;

import java.io.Serializable;

/**
 * class that represents the board but using only primitive types, so that it doesn't create problems when sent via TCP
 * it is included in the notify message that model sends to view/remoteview together with modelMessage
 */
public class BoardPhotography implements Serializable {

    private BoxPhotography[][] matrixPhotograph;

    public BoardPhotography () {
        matrixPhotograph = new BoxPhotography[5][5];
        for (int r = 0; r < 5 ; r++ ){
            for (int c = 0; c < 5; c++){
                matrixPhotograph[r][c] = new BoxPhotography(r,c);
            }
        }
    }

    public void setBoxPhoto(int row, int column, BoxPhotography boxPhotography) {
        matrixPhotograph[row][column] = boxPhotography;

    }
    public BoxPhotography getBoxPhoto(int row, int column){
        return matrixPhotograph[row][column];
    }

    /**
     * method that is used to show the photography on CLI
     */
    public void show () {
        System.out.println("       0       1       2       3       4 ");
        int rowIndex = 0;
        for (BoxPhotography[] line : matrixPhotograph) {
            System.out.println(" "+rowIndex+ "   "+line[0]+"   "+line[1]+ "   "+line[2]+"   "+line[3]+"   "+line[4]);
            rowIndex++;
        }

    }

    public BoxPhotography[][] getMatrixPhotograph() {
        return matrixPhotograph;
    }

    /**
     * the equals is used to check if the version that the client has is updated
     * if not the client knows it needs to be  printed on CLI/changed on GUI
     * @param object photography on client
     * @return boolean answer
     */
    @Override
    public boolean equals (Object object) {
        if (! (object instanceof BoardPhotography) ) {return false;}
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (! this.getBoxPhoto(r, c).equals(((BoardPhotography) object).getBoxPhoto(r,c))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * sonarLint suggested to override also this method after doing so with equals
     * not used
     * @return int value
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}



