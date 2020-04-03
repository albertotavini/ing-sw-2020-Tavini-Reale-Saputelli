package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.Global;
import it.polimi.ingsw.server.model.Board;

import java.util.HashMap;




//per adesso le divinità vengono istanziate qui, in seguito verranno lette da file


public class GodLookUpTable {

    private static HashMap<String, God> move_list = new HashMap<>();
    private static HashMap<String, God> build_list = new HashMap<>();
    private static HashMap<String, God> opponent_list = new HashMap<>();
    private static HashMap<String, God> setup_list = new HashMap<>();

    private static boolean alreadyInitialized = false;


    private static SpecificEffect athenaEffect = new SpecificEffect() {
        @Override
        public void SpecificEffect(Board board) {
            //effetto athena
        }
    };
    private static SpecificEffect minotaurEffect = new SpecificEffect() {
        @Override
        public void SpecificEffect(Board board) {
            //effetto Minotaur
        }
    };


    private static final God atena = new God(Global.athena, Global.athenaDescription, athenaEffect);
    private static final God minotaur = new God(Global.minotaur, Global.minotaurDescription, minotaurEffect);


    public static God lookUp(String godname) {

        godname = godname.toUpperCase();

        //inizializzazione delle carte e della lista o liste a cui appartengono
        if( !alreadyInitialized ) {

            move_list.put("ATENA", atena);
            atena.addEffectTypes(Global.on_move);

            move_list.put("MINOTAUR", minotaur);
            minotaur.addEffectTypes(Global.on_move);


            alreadyInitialized = true;

        }



        if(move_list.containsKey(godname)) return move_list.get(godname);
        if(build_list.containsKey(godname)) return build_list.get(godname);
        if(opponent_list.containsKey(godname)) return opponent_list.get(godname);

        else return null;


    }


    public static boolean isEffectMove(String godname) {
        godname = godname.toUpperCase();
        return move_list.containsKey(godname);
    }

    public static boolean isEffectBuild(String godname) {
        godname = godname.toUpperCase();
        return build_list.containsKey(godname);
    }

    public static boolean isEffectOnOpponent(String godname) {
        godname = godname.toUpperCase();
        return opponent_list.containsKey(godname);
    }

    public static boolean isEffectSetup(String godname) {
        godname = godname.toUpperCase();
        return setup_list.containsKey(godname);
    }

}