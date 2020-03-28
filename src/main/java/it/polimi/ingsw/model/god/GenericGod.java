package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;

import java.sql.ClientInfoStatus;

//Per aggiungere un nuovo dio basta creare una classe concreta qui che estende la
//classe giusta a seconda che l'effetto si attivo durante il movimento, durante il turno dell'avversario o
//durante la costruzione.In seguito va aggiunta ed istanziata la divinità nella lookupTable

//Inizio lista dei move


@FunctionalInterface
interface SpecificEffect {

    public void SpecificEffect(Board board);
}


public class GenericGod {

    private God specificGod;


    public GenericGod(String godname){
        godname = godname.toUpperCase();
        this.setGod(godname);

    }

    public void setGod(String godname) {
        specificGod = GodLookUpTable.lookUp(godname);
    }

    public void attivaEffetto(Board board) {
        specificGod.Effect(board);
    }

}








class Atena extends God {

    //roba del singleton
    private static Atena instance;
    private Atena() {
        super("ATENA", "Descrizione Athena");
        }
    public static Atena instance() {
        if(instance == null) instance = new Atena();
        return instance;
    }
    //roba del singleton


    SpecificEffect effect = new SpecificEffect() {
        @Override
        public void SpecificEffect(Board board) {
            //effetto atena
        }
    };

    @Override
    public void Effect(Board board) {
        //mossa move classica + eventuale effetto athena
        effect.SpecificEffect(board);
    }

}

class Minotaur extends God {

    //roba del singleton
    private static Minotaur instance;
    private Minotaur() {
        super("MINOTAUR", "Descrizione Minotaur");
        addTipiEffetto("ON_MOVE");
    }
    public static Minotaur instance(){
        if(instance == null) instance = new Minotaur();
        return instance;
    }
    //fine roba del singleton


    SpecificEffect effect = new SpecificEffect() {
        @Override
        public void SpecificEffect(Board board) {
            //effetto minotauro
        }
    };

    @Override
    public void Effect(Board board) {
        //mossa move classica + eventuale effetto athena
        effect.SpecificEffect(board);
    }
}

//fine lista dei move


//Inizio lista dei build

//altri dei

//fine lista dei build


//Inizio lista dei on opponent

//altri dei

//fine lista dei build



