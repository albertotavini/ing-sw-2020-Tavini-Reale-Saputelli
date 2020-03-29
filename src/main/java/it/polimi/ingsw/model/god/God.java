package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;

import java.util.ArrayList;
import java.util.HashMap;



public abstract class God {
    //self-explaining

    public final String godName;
    public final String godDescription;
    protected ArrayList<String> tipiEffetto;


    public God(String godName, String godDescription){
        this.godName = godName;
        this.godDescription = godDescription;
    }

    public abstract void Effect(Board board);

    public void addTipiEffetto(String tipo){
        tipiEffetto.add(tipo);
    }

}

