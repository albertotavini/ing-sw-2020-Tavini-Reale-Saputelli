package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.observers.Observable;

import java.util.*;
import java.util.stream.Collectors;

public class Model extends Observable <BoardPhotography> {

    private ArrayList<Player> playerList;
    private Map <Player, Turn> turnMap;
    private Board gameboard;

    public Model(ArrayList<Player> listPlayerLobby){
        playerList = listPlayerLobby.stream().collect(Collectors.toCollection(ArrayList::new));
        turnMap = new HashMap<>();
        arrangeByAge();
        gameboard = new Board();
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

    void deletePlayer(Player loser){
        playerList.remove(loser);
    }

    Optional<Player> findYoungest() {
        return playerList.stream().reduce( (player1, player2) -> player1.getBirthDate().younger(player2.getBirthDate()) ? player1 : player2 );
    }

    //this method sets the order of turns based on birthdate
    void arrangeByAge() {
        ArrayList <Player> prov = new ArrayList<>();

        //sets youngest as first turn taker
        if (findYoungest().isPresent()) {
            prov.add(findYoungest().get());
            playerList.removeIf(p -> p.equals(prov.get(0)));
        }

        if (findYoungest().isPresent()) {
            //sets second youngest as second turn taker
            prov.add(findYoungest().get());
            playerList.removeIf(p -> p.equals(prov.get(1)));
        }
        //if there's a third player adds it as last turn taker
        if (playerList.size() != 0) {
            prov.add(playerList.get(0));
        }
        playerList = prov;
    }

    public boolean checkIfOnePlayerRemains() {
        return playerList.size() == 1;
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
