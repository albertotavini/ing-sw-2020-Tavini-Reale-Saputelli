package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.GlobalConstants;

import java.util.HashMap;


//Inserire le descrizioni delle divinità con le stringhe in global constants!!!!!!!!



public class GodLookUpTable {

    private static HashMap<String, God> move_list = new HashMap<>();
    private static HashMap<String, God> build_list = new HashMap<>();
    private static HashMap<String, God> opponent_list = new HashMap<>();


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


    private static final God atena = new God(GlobalConstants.athena, "descizione athena", athenaEffect);
    private static final God minotaur = new God(GlobalConstants.minotaur, "descrizione minotaur", minotaurEffect);


    public static God lookUp(String godname) {

        godname = godname.toUpperCase();

        //inizializzazione delle carte e della lista a cui appartengono
        if( !alreadyInitialized ) {

            //Inizio lista dei move

            move_list.put("ATENA", atena);
            atena.addEffectTypes(GlobalConstants.on_move);

            move_list.put("MINOTAUR", minotaur);
            minotaur.addEffectTypes(GlobalConstants.on_move);

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






}