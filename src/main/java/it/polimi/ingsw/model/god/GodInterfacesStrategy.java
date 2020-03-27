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

class ChooseGodInTurn {

    //ha un solo metodo statico che restituisce il generic god adatto;

    public static God chooseTypeOfGod(String godname) {
        godname = godname.toUpperCase();
        GodLookUpTable lookUpTable = new GodLookUpTable();

        God godTemp = lookUpTable.lookUp(godname);

        if(lookUpTable.isEffectMove(godname)) {
            godTemp.tipiEffetto.add("ON_MOVE");
                }


        if(lookUpTable.isEffectBuild(godname)) {
            godTemp.tipiEffetto.add("ON_BUILD");
                }

        if(lookUpTable.isEffectOnOpponent(godname)) {
            godTemp.tipiEffetto.add("DURING_OPPONENT_TURN");
                }

        return godTemp;

    }
}


class GenericGod {

    private God specificGod;

    public void setGod(String godname) {
        specificGod = ChooseGodInTurn.chooseTypeOfGod(godname);
    }

    public void attivaEffetto(Board board) {
        specificGod.Effect(board);
    }

}