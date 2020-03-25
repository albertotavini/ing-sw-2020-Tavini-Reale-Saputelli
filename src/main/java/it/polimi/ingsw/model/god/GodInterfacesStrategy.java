package it.polimi.ingsw.model.god;


import it.polimi.ingsw.model.Board;


/*interface GodEffectOnMove {

    //strategia
    public void EffectOnMove(Board board);
}

interface GodEffectOnBuild{

    //strategia
    public void EffectOnBuild(Board board);
}

interface GodEffectDuringOpponentTurn{

    //strategia
    public void EffectDuringOpponentTurn(Board board);
}*/


abstract class GenericGod {

    protected final LookUpTable lookup = new LookUpTable();


    //l'implementazione ovviamente è diversa per GenericGodWithEffectOnMove/Build
    //dunque non implemento il metodo e uso una classe astratta (ma non solo per questo)
    public abstract void setGod(String godname);
    public abstract void attivaEffetto(Board board);

}


class GenericGodWithEffectOnMove extends GenericGod{

    private GodOnMove godEffectOnMove;
    private final String type = "move";

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

class GenericGodWithEffectOnBuild extends GenericGod{

    private God godEffectOnBuild;
    private final String type = "build";


    @Override
    public void setGod(String godname) {
        if(lookup.isEffectBuild(godname)) this.godEffectOnBuild = (GodOnBuild) lookup.lookUp(godname);
        else godEffectOnBuild = null;
    }

    @Override
    public void attivaEffetto(Board board) {

    }
}

class GenericGodWithEffectDuringOpponentTurn extends GenericGod{

    private God godEffectDuringOpponentTurn;
    private final String type = "onopponent";

    public void setGod(String godname) {
        if(lookup.isEffectOnOpponent(godname)) this.godEffectDuringOpponentTurn = (GodOnOpponent) lookup.lookUp(godname);
        else godEffectDuringOpponentTurn = null;
    }

    @Override
    public void attivaEffetto(Board board) {

    }
}

