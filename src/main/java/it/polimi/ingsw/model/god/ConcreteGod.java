package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;



class Athena extends GodOnMove /*implements GodEffectOnMove*/{

    private static Athena instance;
    private Athena(){
        super("Athena", "Descrizione Athena");
    }

    public static Athena instance(){
        if(instance == null) instance = new Athena();
        return instance;
    }

    private void AthenaEffect(Board board){}

    @Override
    public void EffectOnMove(Board board) {
        //mossa move classica + eventuale effetto athena
        AthenaEffect(board);
    }
}

class Minotaur extends GodOnMove /*implements GodEffectOnMove*/{

    private static Minotaur instance;
    private Minotaur(){
        super("Athena", "Descrizione Athena");
    }

    public static Minotaur instance(){
        if(instance == null) instance = new Minotaur();
        return instance;
    }

    private void MinotaurEffect(Board board){}

    @Override
    public void EffectOnMove(Board board) {
        //mossa move classica + eventuale effetto athena
        MinotaurEffect(board);
    }
}



