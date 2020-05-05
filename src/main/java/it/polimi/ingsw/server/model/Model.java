package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;
import it.polimi.ingsw.server.observers.Observable;

import java.util.*;
import java.util.stream.Collectors;

//NOMVC methods were used with stdin to try methods

public class Model extends Observable <BoardPhotography> {

    private ArrayList<Player> playerList;
    private Map <Player, Turn> turnMap;
    private Board gameboard;

    public Model(ArrayList<Player> listPlayerLobby){
        playerList = listPlayerLobby.stream().collect(Collectors.toCollection(ArrayList::new));
        turnMap = new HashMap<>();
        arrangeByAge();
        gameboard = new Board();
        System.out.println("Welcome to the Santorini! Insert any input to know what to do");
    }

    public Map<Player, Turn> getTurnMap() {
        return turnMap;
    }

    public Board getGameboard() {
        return gameboard;
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    public void deletePlayer(Player loser){
        playerList.remove(loser);
    }

    public Optional<Player> findYoungest () {
        return playerList.stream().reduce( (player1, player2) -> player1.getBirthDate().younger(player2.getBirthDate()) ? player1 : player2 );
    }

    //this method sets the order of turns based on birthdate
    public void arrangeByAge () {
        ArrayList <Player> prov = new ArrayList<>();

        //sets youngest as first turn taker
        prov.add(findYoungest().get());
        playerList.removeIf( p -> p.equals(prov.get(0)));

        //sets second youngest as second turn taker
        prov.add(findYoungest().get());
        playerList.removeIf( p -> p.equals(prov.get(1)));

        //if there's a third player adds it as last turn taker
        if (playerList.size() != 0) {
            prov.add(playerList.get(0));
        }
        playerList = prov;
    }

    public boolean checkIfOnePlayerRemains() {
        if (playerList.size() == 1) return true;
        else return false;
    }

    //iterates on the Turns and if necessary removes players who lost, should be part of Controller
    public void NOMVCrotate() {
        boolean turnCompleted;
        boolean gameCompleted = false;

        while (!checkIfOnePlayerRemains() && !gameCompleted) {
            for (Player p: playerList) {
                System.out.println(p.getName()+" it's your turn, remember " +turnMap.get(p).getColor()+ " workers");
                turnCompleted = turnMap.get(p).NOMVCcallTurn(gameboard);

                if (!turnCompleted) {
                    //if the player loses removes his workers from board and him from list of players
                    turnMap.get(p).clearBoard(gameboard);
                    playerList.removeIf(player -> player.equals(p));
                }

                if (turnMap.get(p).isWinner()) {
                    System.out.println( p.getName() + " wins the game.");
                    gameCompleted = true;
                    break;
                }
                gameboard.drawBoard();
            }
        }

        if (checkIfOnePlayerRemains()) {
            System.out.println("il player " +playerList.get(0).getName()+ " ha vinto la partita!");
        }
    }

    public void NOMVCsetgame(){

        //builds the turns
        String godName= NOMVCsetGodName();
        //playerList.get(0).setPersonalTurn(new Turn(playerList.get(0), Color.GREEN, godName));
        turnMap.put(playerList.get(0),new Turn(playerList.get(0), Color.GREEN, godName) );
        godName = NOMVCsetGodName();
        //playerList.get(1).setPersonalTurn(new Turn(playerList.get(1), Color.RED, godName));
        turnMap.put(playerList.get(1), new Turn(playerList.get(1), Color.RED, godName));
        //also for the third player, if present
        if (playerList.size()==3) {
            godName = NOMVCsetGodName();
            //playerList.get(2).setPersonalTurn(new Turn(playerList.get(2), Color.YELLOW, godName));
            turnMap.put(playerList.get(2),new Turn(playerList.get(2), Color.YELLOW, godName) );
        }

        //asks to place workers
        System.out.println(playerList.get(0).getName()+ " place your workers.");
        turnMap.get(playerList.get(0)).NOMVCplaceWorkers(gameboard);
        gameboard.drawBoard();
        System.out.println(playerList.get(1).getName()+ " place your workers.");
        turnMap.get(playerList.get(1)).NOMVCplaceWorkers(gameboard);
        gameboard.drawBoard();
        if (playerList.size()==3) {
            System.out.println(playerList.get(2).getName() + " place your workers.");
            turnMap.get(playerList.get(2)).NOMVCplaceWorkers(gameboard);
            gameboard.drawBoard();
        }
    }

    public String NOMVCsetGodName() {
        //va messo un parse basato sulla gooLookUptable
        System.out.println("Give me God's name");
        Scanner scanner = new Scanner (System.in);
        String s = scanner.nextLine();
        return s;
    }

    public void declareWinner(Turn t) {
        if (checkIfOnePlayerRemains()) {
            System.out.println("Player " +playerList.get(0).getName()+ " wins the game!");
        }
    }

    //--------------------------------------------------------------------------------------------------------------

    //The following code is for Controller

    //Inizialing turn with the first player of the list: the youngest
    //It happens inside Controller!
    private Player currentPlayer;

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {currentPlayer = player;}



    public void informView(){
        //tells the view what's happened
        notify(getGameboard().takePhotograph(), getGameboard().getModelMessage());
    }



    public void updateTurn(){
        if(currentPlayer == playerList.get(0)) { currentPlayer = playerList.get(1); }

        else {
            if (playerList.size() == 2) {
                if (currentPlayer == playerList.get(1)) {
                    currentPlayer = playerList.get(0);
                }
            }

            if (playerList.size() == 3) {
                if (currentPlayer == playerList.get(1)) {
                    currentPlayer = playerList.get(2);
                }
                else if (currentPlayer == playerList.get(2)) {
                    currentPlayer = playerList.get(0);
                }
            }
        }
    }

    public void updatePlayersAfterLosing(){
        if(playerList.size() == 2){
            if(currentPlayer == playerList.get(0)){
                playerList.set(0, playerList.get(1));
                playerList.remove(1);
                currentPlayer = playerList.get(0);
            }
            else if(currentPlayer == playerList.get(1)){
                playerList.remove(1);
                currentPlayer = playerList.get(0);
            }
        }

        else if(playerList.size() == 3){
            if(currentPlayer == playerList.get(0)){
                playerList.set(0, playerList.get(1));
                playerList.set(1, playerList.get(2));
                playerList.remove(2);
                currentPlayer = playerList.get(0);
            }
            else if(currentPlayer == playerList.get(1)){
                playerList.set(1, playerList.get(2));
                playerList.remove(2);
                currentPlayer = playerList.get(1);
            }
            else if(currentPlayer == playerList.get(2)){
                playerList.remove(2);
                currentPlayer = playerList.get(0);
            }
        }
    }

    public boolean isPlayerTurn(Player player) { return player == currentPlayer; }



}
