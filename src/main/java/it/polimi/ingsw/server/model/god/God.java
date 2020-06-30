package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.bothsides.utils.Global;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;

import java.io.Serializable;
import java.util.ArrayList;

//Dubbio sulla visibilità di god


/**
 * this functional interface represents the effect of a divinty to be called in various situations depending on info in
 * GodLookUpTable
 */
@FunctionalInterface
interface SpecificEffect extends Serializable {
    public boolean activateSpecificEffect(Board board, Turn turn, PlayerMove p);
}


/**
 * this class represents a GodCard entity
 * contains name and description
 * and the effect itself inerithed from the functional interface
 */
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

    /**
     * this method will activate the effect through the functional interface
     *
     * @param board of the game
     * @param turn of the player with this god
     * @param p playermove from input
     * @return true when effect is completed
     */
    public boolean effect(Board board, Turn turn, PlayerMove p) {
        return effect.activateSpecificEffect(board, turn, p);
    }

    /**
     * adds to the array that tells the types of effect that this god's power relates to
     *
     * @param type to be added
     */
    public void addEffectTypes(GodTypeEffect type){

        boolean isPresent = false;

        for(GodTypeEffect e : effectTypes){

            if(e == type){

                isPresent = true;
            }

        }

       if( !isPresent ) effectTypes.add(type);


    }

    boolean hasEffectType(GodTypeEffect type){

        return effectTypes.contains(type);

    }

    @Override
    public String toString() {
        return Global.DIVINITYNAME +godName + " " +Global.DIVINITYDESCRIPTION +godDescription;
    }
}










