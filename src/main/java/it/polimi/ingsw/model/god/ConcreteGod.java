package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;

//Per aggiungere un nuovo dio basta creare una classe concreta qui che estende la
//classe giusta a seconda che l'effetto si attivo durante il movimento, durante il turno dell'avversario o
//durante la costruzione.In seguito va aggiunta ed istanziata la divinità nella lookupTable



//Inizio lista dei move

class Atena extends GodOnMove {

    private static Atena instance;
    private Atena(){
        super("ATENA", "Descrizione Athena");
    }

    public static Atena instance(){
        if(instance == null) instance = new Atena();
        return instance;
    }

    private void AthenaEffect(Board board){}

    @Override
    public void EffectOnMove(Board board) {
        //mossa move classica + eventuale effetto athena
        AthenaEffect(board);
    }
}

class Minotaur extends GodOnMove {

    private static Minotaur instance;
    private Minotaur(){
        super("MINOTAUR", "Descrizione Minotaur");
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

//fine lista dei move


//Inizio lista dei build

//altri dei

//fine lista dei build


//Inizio lista dei on opponent

//altri dei

//fine lista dei build

