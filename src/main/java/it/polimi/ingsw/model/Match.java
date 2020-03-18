package it.polimi.ingsw.model;

import java.util.ArrayList;

public class Match {

    //manca l'array list di turni
    //ovviamente non inserisco il metodo addPlayer visto che i player sono aggiunti solo all'inizio, attraverso la add non lascio
    //riferimenti fuori vietati gli assegnamenti, anche perchè l'attributo è private!!!!

    private ArrayList<Player> playerList;

    public void deletePlayer(Player loser){
        //sono sicuro che questa cosa darà problemi, non so perchè
        playerList.remove(loser);
    }

    //l'arrayList la costruisce la lobby e la passa attraverso la rete
    //ovviamente il costruttore non è completo...
    public Match(ArrayList<Player> listPlayerLobby){
        //si potrebbe usare addall ma questo dà più il senso
        for(Player n : listPlayerLobby) playerList.add(n);
    }


}
