package it.polimi.ingsw.model.god;


import it.polimi.ingsw.model.Board;


/*abstract class GenericGod {

    protected final GodLookUpTable lookup = new GodLookUpTable();


    //l'implementazione ovviamente è diversa per GenericGodWithEffectOnMove/Build
    //dunque non implemento il metodo e uso una classe astratta (ma non solo per questo)
    //uso il metodo setGod e non il costruttore per non rendere impossibile un cambio di dio successivo

    public abstract void setGod(String godname);
    public abstract void attivaEffetto(Board board);

}*/

public class ChooseGod {

    //ha un solo metodo statico che restituisce il dio specifico

    public static GenericGod setSpecificGod(String godname) {
        godname = godname.toUpperCase();

        GenericGod god = new GenericGod();
        god.setGod(godname);

        return god;

    }

}

class GenericGod {

    private God specificGod;

    public void setGod(String godname) {
        specificGod = GodLookUpTable.lookUp(godname);
    }

    public void attivaEffetto(Board board) {
        specificGod.Effect(board);
    }

}