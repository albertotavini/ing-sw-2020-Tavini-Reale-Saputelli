package it.polimi.ingsw.model.god;


import it.polimi.ingsw.model.Board;




abstract class GenericGod {

    protected final GodLookUpTable lookup = new GodLookUpTable();


    //l'implementazione ovviamente è diversa per GenericGodWithEffectOnMove/Build
    //dunque non implemento il metodo e uso una classe astratta (ma non solo per questo)
    //uso il metodo setGod e non il costruttore per non rendere impossibile un cambio di dio successivo

    public abstract void setGod(String godname);
    public abstract void attivaEffetto(Board board);

}

class ChooseGodInTurn {

    //ha un solo metodo statico che restituisce il generic god adatto;

    public static GenericGod chooseTypeOfGod(String godname){
        godname = godname.toUpperCase();
        GodLookUpTable lookUpTable = new GodLookUpTable();

            if(lookUpTable.isEffectMove(godname)) {
                GenericGod godTemp = new GenericGodWithEffectOnMove();
                godTemp.setGod(godname);
                return godTemp;}


            if(lookUpTable.isEffectBuild(godname)) {
                GenericGod godTemp = new GenericGodWithEffectOnBuild();
                godTemp.setGod(godname);
                return godTemp;}


            if(lookUpTable.isEffectOnOpponent(godname)) {
                GenericGod godTemp = new GenericGodWithEffectDuringOpponentTurn();
                godTemp.setGod(godname);
                return godTemp;}

            else return null;

    }
}



class GenericGodWithEffectOnMove extends GenericGod {

    private GodOnMove godEffectOnMove;

    @Override
    public void setGod(String godname) {
        if(lookup.isEffectMove(godname)) this.godEffectOnMove = (GodOnMove) lookup.lookUp(godname);
        else godEffectOnMove = null;
    }

    @Override
    public void attivaEffetto(Board board) {
        godEffectOnMove.EffectOnMove(board);
    }


}

class GenericGodWithEffectOnBuild extends GenericGod {

    private GodOnBuild godEffectOnBuild;


    @Override
    public void setGod(String godname) {
        if(lookup.isEffectBuild(godname)) this.godEffectOnBuild = (GodOnBuild) lookup.lookUp(godname);
        else godEffectOnBuild = null;
    }

    @Override
    public void attivaEffetto(Board board) {

    }
}

class GenericGodWithEffectDuringOpponentTurn extends GenericGod {

    private GodOnOpponent godEffectDuringOpponentTurn;

    public void setGod(String godname) {
        if(lookup.isEffectOnOpponent(godname)) this.godEffectDuringOpponentTurn = (GodOnOpponent) lookup.lookUp(godname);
        else godEffectDuringOpponentTurn = null;
    }

    @Override
    public void attivaEffetto(Board board) {

    }
}

