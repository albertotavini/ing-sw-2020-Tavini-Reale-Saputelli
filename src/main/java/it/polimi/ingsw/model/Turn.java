package it.polimi.ingsw.model;


public class Turn {

    private Player relatedPlayer;
    private String color;

    public Turn(Player p, String color){
        this.relatedPlayer = p;
        this.color = color;
    }

    public Player getPlayer() { return this.relatedPlayer; }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }



    //Check on both of the workers which belong to the player who has to move during the current turn
    public boolean checkIfCanMove(Board board){
        int cont = 0;
        for (int i =0; i<5; i++) {
            for (int j=0; j<5; j++)  {
                //if it is occupied by a worker of the correct colour
                if (board.getBox(i, j).getOccupier().getColour().equals(this.getColor())) {
                    if (!board.isNearbySpaceFree(i, j)) {cont++;}
                }
            }
        }
        //if both the workers have no free space around the player cannot move
        if (cont == 2) {return false;}
        else {return true;}
    }

    //checks if the worker in the box (r, c) can build ( it is KNOWN THAT THE WORKER IS THERE, no other verification)
    public boolean checkIfCanBuild(Board board, int r, int c){
        for (int i =0; i<5; i++) {
            for (int j=0; j<5; j++)  {
                if (board.boxIsNear(r, c, i, j )) {
                    //if the box is near and not occupied by workers or domes, it is possible for the player to build
                    if (board.getBox(i, j).getOccupier() == null && board.getBox(i, j).getTowerSize() < 4 ) {
                        return true;
                    }

                }
            }
        }
        return false;
    }



    /*
    public Box move(Worker currentWorker){
        chosenWorker = currentWorker;

        //System.out.println("You can move your chosen worker in the next boxes:\n");
        //for(int i=-1; i<2; i++){
        //    for(int j=-1; j<2; j++){
        //        if( //board[i][j] contains a piece with piece.height <= chosenWorker.box.height
        //        )
        //            System.out.println( "(" + i + ";" + j + ")\n" );
        //    }
        //}

        do{
            System.out.println("Where do you want to move your worker?\n");
            if (!(validMove(/*player's input )))
                System.out.println("The box you chose is not valid. Choose a valid box.\n");
            else
                System.out.println("You chose" + player's input + "\n");
                //invio della mossa al controller per aggiornare il model
        } while ( !(validMove(player's input) );

    }
    */
    /*
    public Box build (){

        do{
            System.out.println("Where do you want to build your new piece?\n");
            if (!(validBuild(player's input )))
                System.out.println("The box you chose is not valid. Choose a valid box.\n");
            else
                System.out.println("You chose" + player's input + "\n");
                //invio della mossa al controller per aggiornare il model
        } while ( !(validBuild(player's input )) );
    }

    public boolean validMove(Box movingBox){
        //sending the input to the controller to check on the model database
    }

    public boolean validBuild(Box buildingBox){
        //sending the input to the controller to check on the model database
    }

    public boolean turnCompleted(){
        //ends the turn
    }

    public void wins(){
        relatedPlayer.winner = true;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Turn)) return false;
        if( ((Turn) obj).relatedPlayer.equals(this.relatedPlayer)) return true;
        else return false;
    }

    */



}


