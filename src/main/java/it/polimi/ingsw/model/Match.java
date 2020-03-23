package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Match {

    //manca l'array list di turni
    //ovviamente non inserisco il metodo addPlayer visto che i player sono aggiunti solo all'inizio, attraverso la add non lascio
    //riferimenti fuori vietati gli assegnamenti, anche perchè l'attributo è private!!!!

    private ArrayList<Player> playerList;
    private Board gameboard = Board.instance();

    //DA CAMBIARE DEVE ESSERE IL TURNO A ESSERE RIMOSSO NON IL GIOCATORE
    public void deletePlayer(Player loser){
        //sono sicuro che questa cosa darà problemi, non so perchè
        playerList.remove(loser);
    }

    //l'arrayList la costruisce la lobby e la passa attraverso la rete
    //ovviamente il costruttore non è completo...
    public Match(ArrayList<Player> listPlayerLobby){
        //si potrebbe usare addall ma questo dà più il senso
        playerList = listPlayerLobby.stream().collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    //method which sets order of turns based on birthdate while building TurnList
    // trovo il più giovane, lo tolgo dalla lista, trovo il nuovo più giovane, lo tolgo, se c'è il terzo giocatore lo metto
    public void setgame () {
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
    /*
    public void rotateTurns() {
        while (!checkIfOnePlayerRemains()) {
            for (Turn current : turnList) {

                if (!current.checkIfCanMove()) {
                    turnList.removeIf(t -> t.equals(current));
                    System.out.println("Il giocatore " +current.getPlayer().getName()+ " non può muovere alcun lavoratore, ha perso!");
                    continue;
                }
                //l'argomento non è proprio corretto che ci sia, va deciso prima il worker da muovere
                //current.move(Worker w);

                if (!current.checkIfCanBuild()) {
                    turnList.removeIf(t -> t.equals(current));
                    System.out.println("Il giocatore " +current.getPlayer().getName()+ " non può costruire, ha perso!");
                }

                current.build();
                System.out.println("Il giocatore" +current.getPlayer().getName()+ " ha concluso il suo turno");

            }

        }
    }*/

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
