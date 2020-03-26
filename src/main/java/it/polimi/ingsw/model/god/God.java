package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;
import java.util.HashMap;


abstract class God {
    //self-explaining

    public final String godName;
    public final String godDescription;


    public God(String godName, String godDescription){
        this.godName = godName;
        this.godDescription = godDescription;
    }





}



abstract class GodOnMove extends God {

    public abstract void EffectOnMove(Board board);
    public GodOnMove(String godName, String godDescription) {
        super(godName, godDescription);
    }

}

abstract class GodOnBuild extends God {

    public GodOnBuild(String godName, String godDescription) {
        super(godName, godDescription);
    }
    public abstract void EffectOnBuild(Board board);

}

abstract class GodOnOpponent extends God {

    public GodOnOpponent(String godName, String godDescription) {
        super(godName, godDescription);
    }
    public abstract void EffectDuringOpponentTurn(Board board);
}




/*if(god instance of GodOnMove)*/