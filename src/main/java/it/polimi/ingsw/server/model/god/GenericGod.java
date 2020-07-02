package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;


/**
 * wrapper class used to link the GodObject and allow it to be used by model and controller
 */
public class GenericGod {

    private God specificGod;

    /**
     * basic constructor
     *
     * @param godName to be associated
     */
    public GenericGod(String godName) {
        godName = godName.toUpperCase();
        this.setGod(godName);
    }

    /**
     * sets the corresponding god object
     *
     * @param godName given by user
     */
    public void setGod(String godName) {
        specificGod = GodLookUpTable.lookUp(godName);
    }

    /**
     * calls the SpecificEffect of the assigned God
     *
     * @param board of the game
     * @param turn of the player using the effect
     * @param message playermove received as input
     * @return true when effect has been completed
     */
    public boolean activateEffect(Board board, Turn turn, PlayerMove message) {
        return specificGod.effect(board, turn, message);
    }

    public String getSpecificGodName() {
        return specificGod.godName;
    }

}





