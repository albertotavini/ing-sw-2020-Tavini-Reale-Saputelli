package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Turn;


public class GenericGod {

    private God specificGod;

    public GenericGod(String godName) {
        godName = godName.toUpperCase();
        this.setGod(godName);
    }

    public void setGod(String godName) {
        specificGod = GodLookUpTable.lookUp(godName);
    }

    public boolean activateEffect(Board board, Turn turn, int row, int column) {
        return specificGod.Effect(board, turn, row, column);
    }

    public String getSpecificGodName() {
        return specificGod.godName;
    }

}





