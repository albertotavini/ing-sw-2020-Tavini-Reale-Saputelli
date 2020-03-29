package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;
import java.util.ArrayList;

//Dubbio sulla visibilità di god


@FunctionalInterface
interface SpecificEffect {
    public void SpecificEffect(Board board);
}



class God {
    //self-explaining

    public final String godName;
    public final String godDescription;
    private ArrayList<String> effectTypes;

    public God(String godName, String godDescription, SpecificEffect effect) {
        this.godName = godName;
        this.godDescription = godDescription;
        this.effect = effect;
    }

    private SpecificEffect effect;

    public void Effect(Board board) {
        //mossa move classica + eventuale effetto athena
        effect.SpecificEffect(board);
    }

    public void addEffectTypes(String type){
        effectTypes.add(type);
    }

}








