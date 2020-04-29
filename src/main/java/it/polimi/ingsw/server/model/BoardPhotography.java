package it.polimi.ingsw.server.model;

import java.io.Serializable;

public class BoardPhotography implements Serializable {

    BoxPhotography[][] matrixPhotograph;

    public BoardPhotography () {
        matrixPhotograph = new BoxPhotography[5][5];
    }

    public void setBoxPhoto(int row, int column, BoxPhotography boxPhotography) {
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
}



