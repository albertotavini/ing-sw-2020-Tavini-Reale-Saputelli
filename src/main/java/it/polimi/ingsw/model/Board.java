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
                gameBoard[i][j] = new Box();
            }
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

    private Worker occupier;
    private Piece upperPiece;
    private Piece lowerPiece;

    public Box (){
        this.occupier = null;
        this.upperPiece = new Piece(0);
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
            return getOccupier().getColour()+getOccupier().getWorkerTag()+upperPiece.getLevel();
        }
        else return " ";
    }
}
