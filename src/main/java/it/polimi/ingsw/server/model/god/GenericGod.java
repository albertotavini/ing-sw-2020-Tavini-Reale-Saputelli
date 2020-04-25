package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;
import it.polimi.ingsw.server.view.playerMove.playerMove;


public class GenericGod {

    private God specificGod;

    public GenericGod(String godName) {
        godName = godName.toUpperCase();
        this.setGod(godName);
    }

    public void setGod(String godName) {
        specificGod = GodLookUpTable.lookUp(godName);
    }

    public boolean activateEffect(Board board, Turn turn, playerMove message) {
        return specificGod.Effect(board, turn, message);
    }

    public String getSpecificGodName() {
        return specificGod.godName;
    }

}





