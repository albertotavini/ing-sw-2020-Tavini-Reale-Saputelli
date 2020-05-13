package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.view.PlayerMove.PlayerMove;

import java.io.Serializable;
import java.util.ArrayList;

//Dubbio sulla visibilità di god


@FunctionalInterface
interface SpecificEffect extends Serializable {
    public boolean SpecificEffect(Board board, Turn turn, PlayerMove p);
}



class God implements Serializable {

    //self-explaining
    private static final long serialVersionUID = 6518035230479718913L;
    public final String godName;
    public final String godDescription;
    private ArrayList<GodTypeEffect> effectTypes = new ArrayList<>();

    public God(String godName, String godDescription, SpecificEffect effect) {
        this.godName = godName;
        this.godDescription = godDescription;
        this.effect = effect;
    }

    private SpecificEffect effect;

    public boolean Effect(Board board, Turn turn, PlayerMove p) {
        //mossa move classica + eventuale effetto athena
        return effect.SpecificEffect(board, turn, p);
    }

    public void addEffectTypes(GodTypeEffect type){
        effectTypes.add(type);
    }

    @Override
    public String toString() {
        return "Nome dio: " +godName +" " +"\nDescrizione dio: " +godDescription;
    }

}








