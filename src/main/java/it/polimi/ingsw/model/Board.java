package it.polimi.ingsw.model;

public class Board {

    //il setup della gameBoard lo fa direttamente il costruttore
    //l'ho costruito singleton come su l'uml, valutate voi se ne vale la pena, un po' un esaurimento inutile

    private static Board instanceBoard;
    private Box[][] gameBoard = new Box[5][5];


    //overriding del costruttore di def. (per adesso.....)
    private Board(){
        //matrici viste come array di array, così dovrebbe andare bene, o meglio un for normale?
        for(Box[] aggregato : gameBoard){
            for(Box unit : aggregato) unit = new Box();
        }
    }

    public static Board instance() {
        if(instanceBoard == null) instanceBoard = new Board();
        return instanceBoard;
    }

    //va finito di definire
    /*
    public void drawBoard () {
        for (Box[] aggregato : gameBoard) {
            System.out.println(aggregato[0].drawBox());
        }

    }*/


}

class Box {

    //per adesso basta il costruttore di default
    //da vedere ancora il concetto di torre come aggregato di pezzi

    private Worker occupier;
    private Piece upperPiece;
    private Piece lowerPiece;

    public Worker getOccupier() {
        return occupier;
    }

    public void setOccupier(Worker occupier) {
        this.occupier = occupier;
    }


    public void drawBox() {
        if ((getOccupier() == null) && (upperPiece == null)) {
            System.out.println("-- -");
        }
        else if ((getOccupier() != null) && (upperPiece == null)) {
            System.out.println(getOccupier().getColour()+getOccupier().getWorkerTag()+" -");
        }
        else if ((getOccupier() != null) && (upperPiece == null)) {
            System.out.println("-- "+upperPiece.getLevel());
        }
        else if (getOccupier() != null && upperPiece != null) {
            System.out.println(getOccupier().getColour()+getOccupier().getWorkerTag()+upperPiece.getLevel());
        }
    }
}
