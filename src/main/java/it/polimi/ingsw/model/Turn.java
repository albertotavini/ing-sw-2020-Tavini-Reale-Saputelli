package it.polimi.ingsw.model;


public class Turn {

    private Player relatedPlayer;
    private Worker chosenWorker; //the chosen worker during the player's move

    public Turn(Player p){
        this.relatedPlayer = p;
    }

    public Player getPlayer() { return this.relatedPlayer; }

    public Worker getChosenWorker() { return this.chosenWorker; }

    //Check on both of the workers which belong to the player who has to move during the current turn
    public boolean checkIfCanMove(){

        for(/*entrambi i worker del player*/)

        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                if( //the worker cannot move to an adiacent box, due to the presence of an other worker
                    //or to the presence of pieces which can't be climbed from the worker
                )
                    return false;
            }
        }
    }


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
            if (!(validMove(/*player's input*/ )))
                System.out.println("The box you chose is not valid. Choose a valid box.\n");
            else
                System.out.println("You chose" + /*player's input*/ + "\n");
                //invio della mossa al controller per aggiornare il model
        } while ( !(validMove(/*player's input*/ )) );

    }

    //from here the designated worker is the chosen one during the move
    public boolean checkIfCanBuild(){
        Box currentBox = chosenWorker.occupiedBox;

        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                if(/*the chosen worker cannot build in an adiacent box, due to the presence of domes or other workers*/)
                    return false;
            }
        }

    }

    public Box build (){

        do{
            System.out.println("Where do you want to build your new piece?\n");
            if (!(validBuild(/*player's input*/ )))
                System.out.println("The box you chose is not valid. Choose a valid box.\n");
            else
                System.out.println("You chose" + /*player's input*/ + "\n");
                //invio della mossa al controller per aggiornare il model
        } while ( !(validBuild(/*player's input*/ )) );
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
}

@Override
public boolean equals(Object obj) {
    if(!(obj instanceof Turn)) return false;
    if( ((Turn) obj).relatedPlayer.equals(this.relatedPlayer)) return true;
    else return false;
}

