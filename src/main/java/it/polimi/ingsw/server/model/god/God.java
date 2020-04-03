package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;

import java.io.Serializable;
import java.util.ArrayList;

//Dubbio sulla visibilità di god


@FunctionalInterface
interface SpecificEffect extends Serializable {
    public void SpecificEffect(Board board);
}


class God implements Serializable{

    //self-explaining
    private static final long serialVersionUID = 6518035230479718913L;
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

    @Override
    public String toString() {
        return "Nome dio: " +godName +" " +"\nDescrizione dio: " +godDescription;
    }

}








