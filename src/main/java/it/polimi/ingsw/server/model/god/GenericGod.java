package it.polimi.ingsw.server.model.god;

import it.polimi.ingsw.server.model.Board;


public class GenericGod {

    private God specificGod;

    public GenericGod(String godName) {
        godName = godName.toUpperCase();
        this.setGod(godName);
    }

    public void setGod(String godName) {
        specificGod = GodLookUpTable.lookUp(godName);
    }

    public void activateEffect(Board board) {
        specificGod.Effect(board);
    }

    public String getSpecificGodName() {
        return specificGod.godName;
    }

}





