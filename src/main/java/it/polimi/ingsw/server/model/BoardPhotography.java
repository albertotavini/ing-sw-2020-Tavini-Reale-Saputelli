package it.polimi.ingsw.server.model;

import java.io.Serializable;

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

    void setBoxPhoto(int row, int column, BoxPhotography boxPhotography) {
        matrixPhotograph[row][column] = boxPhotography;

    }
    public BoxPhotography getBoxPhoto(int row, int column){
        return matrixPhotograph[row][column];
    }

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
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}



