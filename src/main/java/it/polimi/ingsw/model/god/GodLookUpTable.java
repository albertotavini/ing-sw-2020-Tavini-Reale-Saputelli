package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.GlobalConstants;

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

    private static HashMap<String, God> move_list = new HashMap<>();
    private static HashMap<String, God> build_list = new HashMap<>();
    private static HashMap<String, God> opponent_list = new HashMap<>();
    private static boolean alreadyInitialized = false;

    private static final God atena = Atena.instance();
    private static final God minotaur = Minotaur.instance();


    public static God lookUp(String godname) {

        godname = godname.toUpperCase();

        //inizializzazione delle carte e della lista a cui appartengono
        if( !alreadyInitialized ) {

            //Inizio lista dei move

            move_list.put("ATENA", atena);
            atena.addTipiEffetto(GlobalConstants.on_move);

            move_list.put("MINOTAUR", minotaur);
            minotaur.addTipiEffetto(GlobalConstants.on_move);

            //fine lista dei move


            //Inizio lista dei build

            //altri dei

            //fine lista dei build


            //Inizio lista dei on opponent

            //altri dei

            //fine lista dei build

            alreadyInitialized = true;

        }



        if(move_list.containsKey(godname)) return move_list.get(godname);
        if(build_list.containsKey(godname)) return build_list.get(godname);
        if(opponent_list.containsKey(godname)) return opponent_list.get(godname);

        else return null;


    }



    static boolean isEffectMove(String godname) {
        godname = godname.toUpperCase();
        return move_list.containsKey(godname);
    }

    static boolean isEffectBuild(String godname) {
        godname = godname.toUpperCase();
        return build_list.containsKey(godname);
    }

    static boolean isEffectOnOpponent(String godname) {
        godname = godname.toUpperCase();
        return opponent_list.containsKey(godname);
    }






}