package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

import java.io.Serializable;
import java.util.ArrayList;

//Dubbio sulla visibilità di god


@FunctionalInterface
interface SpecificEffect extends Serializable {
    public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p);
}



public class God implements Serializable {

    //self-explaining
    private static final long serialVersionUID = 6518035230479718913L;
    public final String godName;
    public final String godDescription;
    private ArrayList<GodTypeEffect> effectTypes = new ArrayList<>();

    public God(String godName, String godDescription, SpecificEffect effect) {
        this.godName = godName.toUpperCase();
        this.godDescription = godDescription;
        this.effect = effect;
    }

    private SpecificEffect effect;

    public boolean effect(Board board, Turn turn, PlayerMove p) {
        //mossa move classica + eventuale effetto athena
        return effect.activateSpecificEffect(board, turn, p);
    }

    public void addEffectTypes(GodTypeEffect type){

        boolean isPresent = false;

        for(GodTypeEffect e : effectTypes){

            if(e == type){

                isPresent = true;
            }

        }

       if( !isPresent ) effectTypes.add(type);


    }

    public boolean hasEffectType(GodTypeEffect type){

        return effectTypes.contains(type);

    }

    @Override
    public String toString() {
        return "Nome dio: " +godName +" " +"\nDescrizione dio: " +godDescription;
    }

}








