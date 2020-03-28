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

    private static HashMap<String, God> lista_dei_move = new HashMap<>();
    private static HashMap<String, God> lista_dei_build = new HashMap<>();
    private static HashMap<String, God> lista_dei_opponent = new HashMap<>();
    private static boolean alreadyUsed = false;

    private static final God atena = Atena.instance();
    private static final God minotaur = Minotaur.instance();


    public static God lookUp(String godname) {

        godname = godname.toUpperCase();

        //inizializzazione delle carte e della lista a cui appartengono
        if( !alreadyUsed ) {

            //Inizio lista dei move

            lista_dei_move.put("ATENA", atena);
            atena.addTipiEffetto("ON_MOVE");

            lista_dei_move.put("MINOTAUR", minotaur);
            minotaur.addTipiEffetto("ON_MOVE");

            //fine lista dei move


            //Inizio lista dei build

            //altri dei

            //fine lista dei build


            //Inizio lista dei on opponent

            //altri dei

            //fine lista dei build

            alreadyUsed = true;

        }



        if(lista_dei_move.containsKey(godname)) return lista_dei_move.get(godname);
        if(lista_dei_build.containsKey(godname)) return lista_dei_build.get(godname);
        if(lista_dei_opponent.containsKey(godname)) return lista_dei_opponent.get(godname);

        else return null;


    }



    static boolean isEffectMove(String godname) {
        godname = godname.toUpperCase();
        return lista_dei_move.containsKey(godname);
    }

    static boolean isEffectBuild(String godname) {
        godname = godname.toUpperCase();
        return lista_dei_build.containsKey(godname);
    }

    static boolean isEffectOnOpponent(String godname) {
        godname = godname.toUpperCase();
        return lista_dei_opponent.containsKey(godname);
    }






}