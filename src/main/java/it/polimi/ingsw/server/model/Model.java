package it.polimi.ingsw.server.model;

import it.polimi.ingsw.bothsides.onlinemessages.BoardPhotography;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelError;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessage;
import it.polimi.ingsw.bothsides.onlinemessages.modelmessage.ModelMessageType;
import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.server.observers.Observable;

import java.util.*;

/**
 * This is the class through which we access the different elements of the game.
 * It contains the list of Players in the match, the game board and the turnMap, which is a map
 * used to bind every Turn to its related Player
 */
public class Model extends Observable <BoardPhotography> {

    private ArrayList<Player> playerList;
    private Map <Player, Turn> turnMap;
    private Board gameboard;

    /**
     * The constructor assigns to the Model the given list of Players and arrange them by age.
     * It also creates the turnMap and a new game board.
     *
     * @param listPlayerLobby the given List of Players
     */
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

    /**
     * This method finds the youngest player of a given List of Players
     *
     * @return the youngest player of a given List of Players
     */
    Optional<Player> findYoungest() {
        return playerList.stream().reduce( (player1, player2) -> player1.getBirthDate().younger(player2.getBirthDate()) ? player1 : player2 );
    }

    /**
     * This method sets the order of players based on the date of their birth.
     */
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

    /**
     * This method is used to check if there is just one Player remained in the game.
     * In this case, he will be the winner.
     *
     * @return true if there is just one Player remained in the game, false otherwise
     */
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


    /**
     * This method is used to notify the View about what is changed.
     * For this reason, the method sends a photograph of the game board and a model message
     */
    public void informView(){
        //tells the view what's happened
        notify(getGameboard().takePhotograph(), getGameboard().getModelMessage());
    }


    /**
     * This method is used to perform a turns' rotation between the players in the game.
     */
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

    /**
     * This method is used to rearrange the players after one of them lost.
     */
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
        getGameboard().setModelMessage(new ModelMessage(ModelMessageType.COORDINATES, ModelError.NONE, Global.SELECTTHEWORKERTOMOVE,  currentPlayer.getName()));
    }

    /**
     * This method is used to know if it is a given Player's turn.
     *
     * @param player the given Player
     * @return true if it is the given Player's turn, false otherwise
     */
    public boolean isPlayerTurn(Player player) { return player == currentPlayer; }



}
