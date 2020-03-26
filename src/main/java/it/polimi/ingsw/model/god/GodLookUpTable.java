package it.polimi.ingsw.model.god;

import java.util.HashMap;


//Per aggiungere un nuovo dio basta creare una classe concreta qui che estende la
//classe giusta a seconda che l'effetto si attivo durante il movimento, durante il turno dell'avversario o
//durante la costruzione.In seguito va aggiunta ed istanziata la divinità nella lookupTable
//Come ho già detto questa operazione di caricamento possiamo farla anche da file.





public class GodLookUpTable {

    //in seguito si può implementare un metodo che legge le carte divinità da
    //un file di configurazione (molto interessante) creando una nuova classe god
    //p.s si possono usare anche file di configurazione veri e propri, o potremmo usare dei tagging comments (tipo simulatore
    //di testbench) e inserire dopo i metodi (un po' hardcodato)

    private HashMap<String, God> lista_dei_move = new HashMap<>();
    private HashMap<String, God> lista_dei_build = new HashMap<>();
    private HashMap<String, God> lista_dei_opponent = new HashMap<>();

    //Qui vanno aggiunte le classi concrete degli dei
    public GodLookUpTable(){

        //Inizio lista dei move

        lista_dei_move.put("ATENA", Atena.instance());
        lista_dei_move.put("MINOTAUR", Minotaur.instance());

        //fine lista dei move



        //Inizio lista dei build

        //altri dei

        //fine lista dei build



        //Inizio lista dei on opponent

        //altri dei

        //fine lista dei build

    }




    public boolean isEffectMove(String godname){
        godname = godname.toUpperCase();
        return lista_dei_move.containsKey(godname);
    }

    public boolean isEffectBuild(String godname){
        godname = godname.toUpperCase();
        return lista_dei_build.containsKey(godname);
    }

    public boolean isEffectOnOpponent(String godname){
        godname = godname.toUpperCase();
        return lista_dei_opponent.containsKey(godname);
    }

    public God lookUp(String godname) {
        godname = godname.toUpperCase();

        if(lista_dei_move.containsKey(godname)) return lista_dei_move.get(godname);
        if(lista_dei_build.containsKey(godname)) return lista_dei_build.get(godname);
        if(lista_dei_opponent.containsKey(godname)) return lista_dei_opponent.get(godname);

        else return null;


    }


}