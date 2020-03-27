package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;

import java.util.ArrayList;
import java.util.HashMap;


interface Effect {

    public void Effect(Board board);
}


abstract class God implements Effect {
    //self-explaining

    public final String godName;
    public final String godDescription;
    public ArrayList<String> tipiEffetto;


    public God(String godName, String godDescription){
        this.godName = godName;
        this.godDescription = godDescription;
    }





}






/*if(god instance of GodOnMove)*/