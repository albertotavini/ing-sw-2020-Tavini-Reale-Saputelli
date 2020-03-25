package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;
import java.util.HashMap;


abstract class God {
    //self-explaining

    public final String godName;
    public final String godDescription;


    public God(String godName, String godDescription){
        this.godName = godName;
        this.godDescription = godDescription;
    }





}



abstract class GodOnMove extends God {

    public abstract void EffectOnMove(Board board);
    public GodOnMove(String godName, String godDescription) {
        super(godName, godDescription);
    }

}

abstract class GodOnBuild extends God {

    public GodOnBuild(String godName, String godDescription) {
        super(godName, godDescription);
    }
    public abstract void EffectOnBuild(Board board);

}

abstract class GodOnOpponent extends God {

    public GodOnOpponent(String godName, String godDescription) {
        super(godName, godDescription);
    }
    public abstract void EffectDuringOpponentTurn(Board board);
}



class LookUpTable {

    //in seguito si può implementare un metodo che legge le carte divinità da
    //un file di configurazione (molto interessante) creando una nuova classe god
    //p.s si possono usare anche file di configurazione veri e propri, o potremmo usare dei tagging comments (tipo simulatore
    //di testbench) e inserire dopo i metodi (un po' hardcodato)

    private final Athena atena = Athena.instance();
    private final Minotaur minotaur = Minotaur.instance();
    private HashMap<String, God> lista_dei_move = new HashMap<>();
    private HashMap<String, God> lista_dei_build = new HashMap<>();
    private HashMap<String, God> lista_dei_opponent = new HashMap<>();

    public LookUpTable(){

        lista_dei_move.put("ATENA", atena);
        lista_dei_move.put("MINOTAUR", minotaur);

    }



    public boolean isEffectMove(String godname){
        return lista_dei_move.containsKey(godname);
    }

    public boolean isEffectBuild(String godname){
        return lista_dei_build.containsKey(godname);
    }

    public boolean isEffectOnOpponent(String godname){
        return lista_dei_opponent.containsKey(godname);
    }

    public God lookUp(String godname) {
        if(lista_dei_move.containsKey(godname)) return lista_dei_move.get(godname);
        if(lista_dei_build.containsKey(godname)) return lista_dei_build.get(godname);
        if(lista_dei_opponent.containsKey(godname)) return lista_dei_opponent.get(godname);
        else return null;


    }



}