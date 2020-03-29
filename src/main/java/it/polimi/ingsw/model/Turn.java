package it.polimi.ingsw.model;


import java.util.Scanner;

public class Turn {

    private Player relatedPlayer;
    private String color;
    Scanner sc = new Scanner(System.in);
    //memorise where the worker is for the move, then where it's been moved for the build
    private int currentRow = 5;
    private int currentColumn = 5;
    private boolean  winner = false;

    public Turn(Player p, String color){
        this.relatedPlayer = p;
        this.color = color;
    }
    public boolean isWinner() {
        return winner;
    }


    public Player getPlayer() { return this.relatedPlayer; }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }



    //Check on both of the workers which belong to the player who can move during the current turn
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

    //checks if the worker moved (in currentRow, currentColumn) can build
    public boolean checkIfCanBuild(Board board){
        for (int i =0; i<5; i++) {
                for (int j=0; j<5; j++)  {
                if (board.boxIsNear(currentRow, currentColumn, i, j )) {
                    //if the box is near and not occupied by workers or domes, it is possible for the player to build
                    if (board.getBox(i, j).getOccupier() == null && board.getBox(i, j).getTowerSize() < 4 ) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    //finds a worker to move
    public void selectWorker (Board board) {
        int row = 5;
        int column = 5;
        System.out.println(" dammi le coordinate del worker che vuoi muovere");
        do {
            do {
                do {
                    System.out.println("dammi la riga");
                    row = sc.nextInt();
                    System.out.println("dammi la colonna ");
                    column = sc.nextInt();
                } while (!board.inBoundaries(row, column));
            } while (board.getBox(row,column).getOccupier() == null);
        } while (! board.getBox(row,column).getOccupier().getColour().equals(getColor())) ;
        this.currentRow = row;
        this.currentColumn = column;
    }


    public void move (Board board) {
        System.out.println("il tuo workeruccio si trova in (" +currentRow+" , "+currentColumn+") dove lo vuoi mettere?");
        int row;
        int column;
        //asks for coordinate while box is not adiacent, or occupied by a dome or worker, or too high to reach
        do {
            do {
                do {
                    do {
                        System.out.println("dammi la riga");
                        row = sc.nextInt();
                        System.out.println("dammi la colonna");
                        column = sc.nextInt();
                    } while (!board.boxIsNear(currentRow, currentColumn, row, column));
                }while ( board.getBox(row, column).getOccupier() != null );
            } while  ( board.getBox(row,column).getTowerSize() == 4 );
        } while (!board.isScalable(currentRow, currentColumn, row, column));

        //moves the worker
        Worker w = board.getBox(currentRow, currentColumn).getOccupier();
        board.getBox(currentRow, currentColumn).setOccupier(null);
        board.getBox(row, column).setOccupier(w);
        //checks if the player won
        if (board.getBox(row, column).getTowerSize()== 3 && board.getBox(currentRow, currentColumn).getTowerSize() ==2) {
            winner = true;
        }
        //changes the current coordinates for a correct build;
        this.currentRow = row;
        this.currentColumn = column;

        }

    public void build (Board board) {
            System.out.println("il tuo workeruccio si è mosso in (" +currentRow+" , "+currentColumn+") dove vuoi costruire?");
            int row;
            int column;
            //asks coordinates while box is not adiacent, occupied by worker or dome
            do {
                do {
                    do {
                        System.out.println("dammi la riga");
                        row = sc.nextInt();
                        System.out.println("dammi la colonna");
                        column = sc.nextInt();
                    }while (!board.boxIsNear(currentRow, currentColumn, row, column));
                } while ( board.getBox(row, column).getOccupier() != null );
            } while  ( board.getBox(row,column).getTowerSize() == 4);

            board.increaseLevel(row, column);


        }

    //method to place workers when game begins
    public void placeWorkers (Board board) {
        int row;
        int column;
        System.out.println("workeruccio 1 dove?");
        do {
            do {
                System.out.println("dammi la riga");
                row = sc.nextInt();
                System.out.println("dammi la colonna");
                column = sc.nextInt();
            }while(!board.inBoundaries(row, column));
        } while (board.getBox(row, column).getOccupier() != null );

        //found an unoccupied box, creates and then places the first worker
        board.getBox(row, column).setOccupier(new Worker(relatedPlayer,  getColor(), "A"));
        System.out.println("workeruccio 2 dove?");
        do {
            do {
                System.out.println("dammi la riga");
                row = sc.nextInt();
                System.out.println("dammi la colonna");
                column = sc.nextInt();
            }while(!board.inBoundaries(row, column));
        } while (board.getBox(row, column).getOccupier() != null );
        //does the same for the second
        board.getBox(row, column).setOccupier(new Worker (relatedPlayer, getColor(), "B"));
    }

    //calls al the methods for taking one's turn
    public boolean callTurn(Board board) {
        if (!checkIfCanMove(board)) {
            return false;
        }
        selectWorker(board);
        move(board);
        if (winner) {return true;}

        if (!checkIfCanBuild(board)) {
            return false;
        }
        build(board);
        return true;
    }

    //called when a player loses, removes his workers from the board
    public void clearBoard(Board board) {
        for (int i =0; i<5; i++) {
            for (int j=0; j<5; j++)  {
                //if it is occupied by a worker of the correct colour
                if (board.getBox(i, j).getOccupier().getColour().equals(this.getColor())) {
                    board.getBox(i, j).setOccupier(null);
                }
            }
        }
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
    */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Turn)) return false;
        if( ((Turn) obj).relatedPlayer.equals(this.relatedPlayer)) return true;
        else return false;
    }

}


