package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;


public class GenericGod {

    private God specificGod;

    public GenericGod(String godname){
        godname = godname.toUpperCase();
        this.setGod(godname);
    }

    public void setGod(String godname) {
        specificGod = GodLookUpTable.lookUp(godname);
    }

    public void activateEffect(Board board) {
        specificGod.Effect(board);
    }

    public String getSpecificGodName() {

        return specificGod.godName;
    }

}





