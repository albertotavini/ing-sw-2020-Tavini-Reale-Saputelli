package it.polimi.ingsw.server.model;

import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.server.observers.Observable;

import java.util.*;

public class Model extends Observable <BoardPhotography> {

    private ArrayList<Player> playerList;
    private Map <Player, Turn> turnMap;
    private Board gameboard;

    public Model(List <Player> listPlayerLobby){
        playerList = new ArrayList<>(listPlayerLobby);
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

    //this method sets the order of players based on the date of their birth
    void arrangeByAge() {
        ArrayList <Player> prov = new ArrayList<>();
        Optional <Player> youngest;
        Optional <Player> middle;

        youngest = findYoungest();
        //sets youngest as first turn taker
        if (youngest.isPresent()) {
            prov.add(youngest.get());
            playerList.removeIf(p -> p.equals(prov.get(0)));
        }
        //now the absolute youngest has been removed, so
        middle = findYoungest();

        if (middle.isPresent()) {
            //sets second youngest as second turn taker
            prov.add(middle.get());
            playerList.removeIf(p -> p.equals(prov.get(1)));
        }
        //if there's a third player adds it as last turn taker
        if (!playerList.isEmpty()) {
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
            if (playerList.size() == 2 && currentPlayer == playerList.get(1)) {
                    currentPlayer = playerList.get(0);
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
        getGameboard().setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ModelError.NONE, "Select the worker to move", false, currentPlayer.getName()));
    }

    public boolean isPlayerTurn(Player player) { return player == currentPlayer; }



}
