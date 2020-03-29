package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Match {

    private ArrayList<Player> playerList;
    private Board gameboard;

    //DA CAMBIARE DEVE ESSERE IL TURNO A ESSERE RIMOSSO NON IL GIOCATORE
    public void deletePlayer(Player loser){
        //sono sicuro che questa cosa darà problemi, non so perchè
        playerList.remove(loser);
    }

    public Board getGameboard() {
        return gameboard;
    }

    //l'arrayList la costruisce la lobby e la passa attraverso la rete
    //ovviamente il costruttore non è completo...
    public Match(ArrayList<Player> listPlayerLobby){
        //si potrebbe usare addall ma questo dà più il senso
        playerList = listPlayerLobby.stream().collect(Collectors.toCollection(ArrayList::new));
        arrangeByAge();
        gameboard = Board.instance();

        //builds the turns
        playerList.get(0).setPersonalTurn(new Turn(playerList.get(0), "G"));
        playerList.get(1).setPersonalTurn(new Turn(playerList.get(1), "R"));
        //also for the third player, if present
        if (playerList.get(2)!=null) {
            playerList.get(2).setPersonalTurn(new Turn(playerList.get(2), "Y"));
        }

        //finally asks to place workers
        System.out.println(playerList.get(0).getName()+ " piazza i tuoi worker");
        playerList.get(0).getPersonalTurn().placeWorkers(gameboard);
        gameboard.drawBoard();
        System.out.println(playerList.get(1).getName()+ " piazza i tuoi worker");
        playerList.get(1).getPersonalTurn().placeWorkers(gameboard);
        gameboard.drawBoard();
        if (playerList.get(2)!=null) {
            System.out.println(playerList.get(2).getName()+ " piazza i tuoi worker");
            playerList.get(2).getPersonalTurn().placeWorkers(gameboard);
            gameboard.drawBoard();
        }
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    //method which sets order of turns based on birthdate
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

    //iterates on the Turns and if necessary removes players who lost, should be part of Controller
    public void rotate() {
        boolean turnCompleted;

        while (!checkIfOnePlayerRemains()) {
            for (Player p: playerList) {
                turnCompleted = p.getPersonalTurn().callTurn(gameboard);
                if (!turnCompleted) {
                    //if the player loses removes his workers from board and him from list of players
                    p.getPersonalTurn().clearBoard(gameboard);
                    playerList.removeIf(player -> player.equals(p));

                }
                if (p.getPersonalTurn().isWinner()) {
                    System.out.println(p+" ha vinto la partita");
                    break;
                }
            }
        }
    }

    public Optional<Player> findYoungest () {
       return playerList.stream().reduce( (player1, player2) -> player1.getBirthDate().younger(player2.getBirthDate()) ? player1 : player2 );
    }

    public boolean checkIfOnePlayerRemains() {
        if (playerList.size() == 1) return true;
        else return false;
    }

    public void declareWinner(Turn t) {
        if (true == checkIfOnePlayerRemains()) {
            System.out.println("Il giocatore " +playerList.get(0).getName()+ " ha vinto la partita");
        }
        //bisogna riflettere su come integrare il caso di vittoria per salita

    }

}
