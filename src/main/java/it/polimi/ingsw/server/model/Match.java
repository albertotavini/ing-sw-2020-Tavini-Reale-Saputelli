package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.observers.Observable;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Match extends Observable <Board> {

    private ArrayList<Player> playerList;
    private Board gameboard;

    //DA CAMBIARE DEVE ESSERE IL TURNO A ESSERE RIMOSSO NON IL GIOCATORE
    public void deletePlayer(Player loser){
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
        System.out.println("Benvenuti nel gioco! inserisci un comando qualsiasi per sapere cosa fare");

    }


    public void NOMVCsetgame(){

        //builds the turns
        String godName= NOMVCsetGodName();
        playerList.get(0).setPersonalTurn(new Turn(playerList.get(0), Color.GREEN, godName));
        godName = NOMVCsetGodName();
        playerList.get(1).setPersonalTurn(new Turn(playerList.get(1), Color.RED, godName));
        //also for the third player, if present
        if (playerList.size()==3) {
            godName = NOMVCsetGodName();
            playerList.get(2).setPersonalTurn(new Turn(playerList.get(2), Color.YELLOW, godName));
        }

        //asks to place workers
        System.out.println(playerList.get(0).getName()+ " place your workers.");
        playerList.get(0).getPersonalTurn().NOMVCplaceWorkers(gameboard);
        gameboard.drawBoard();
        System.out.println(playerList.get(1).getName()+ " place your workers.");
        playerList.get(1).getPersonalTurn().NOMVCplaceWorkers(gameboard);
        gameboard.drawBoard();
        if (playerList.size()==3) {
            System.out.println(playerList.get(2).getName() + " place your workers.");
            playerList.get(2).getPersonalTurn().NOMVCplaceWorkers(gameboard);
            gameboard.drawBoard();
        }
    }

    public String NOMVCsetGodName() {
        //va messo un parse basato sulla gooLookUptable
        System.out.println("Dammi il nome della divinità");
        Scanner scanner = new Scanner (System.in);
        String s = scanner.nextLine();
        return s;
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
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
                System.out.println(p.getName()+" it's your turn, remember " +p.getPersonalTurn().getColor()+ " workers");
                turnCompleted = p.getPersonalTurn().callTurn(gameboard);

                if (!turnCompleted) {
                    //if the player loses removes his workers from board and him from list of players
                    p.getPersonalTurn().clearBoard(gameboard);
                    playerList.removeIf(player -> player.equals(p));
                }

                if (p.getPersonalTurn().isWinner()) {
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

    public void declareWinner(Turn t) {
        if (true == checkIfOnePlayerRemains()) {
            System.out.println("Player " +playerList.get(0).getName()+ " wins the game!");
        }
        //bisogna riflettere su come integrare il caso di vittoria per salita

    }

    //--------------------------------------------------------------------------------------------------------------


    //ROBA PER CONTROLLER
    //inizializzo il turno col primo giocatore della lista, cioé il più giovane
    private Player currentPlayer; //VA INIZIALIZZATO;


    public void informView(){
        //controllo su un cambio di stato del controller
        notify(getGameboard());
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {currentPlayer = player;}

    //ricordarsi che subito dopo la removePlayer va aggioranto il currentPlayer con il giocatore precedente
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

    public boolean isPlayerTurn(Player player) { return player == currentPlayer; }



}
