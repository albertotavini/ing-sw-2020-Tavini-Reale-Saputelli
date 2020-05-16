package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.bothsides.onlinemessages.playermove.PlayerMove;


public class GenericGod {

    private God specificGod;

    public GenericGod(String godName) {
        godName = godName.toUpperCase();
        this.setGod(godName);
    }

    public void setGod(String godName) {
        specificGod = GodLookUpTable.lookUp(godName);
    }

    public boolean activateEffect(Board board, Turn turn, PlayerMove message) {
        return specificGod.Effect(board, turn, message);
    }

    public String getSpecificGodName() {
        return specificGod.godName;
    }

}





