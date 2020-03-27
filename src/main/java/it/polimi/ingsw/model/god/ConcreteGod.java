package it.polimi.ingsw.model.god;

import it.polimi.ingsw.model.Board;

//Per aggiungere un nuovo dio basta creare una classe concreta qui che estende la
//classe giusta a seconda che l'effetto si attivo durante il movimento, durante il turno dell'avversario o
//durante la costruzione.In seguito va aggiunta ed istanziata la divinità nella lookupTable



//Inizio lista dei move

@FunctionalInterface
interface SpecificEffect {

    public void SpecificEffect(Board board);
}



class Atena extends God {

    private static Atena instance;
    EffettoAtena effettoAtena;
    class EffettoAtena implements SpecificEffect {

        @Override
        public void SpecificEffect(Board board) {
            //effetto atena
        }
    }



    private Atena(){
        super("ATENA", "Descrizione Athena");
        this.effettoAtena = new EffettoAtena();
    }
    public static Atena instance(){
        if(instance == null) instance = new Atena();
        return instance;
    }



    @Override
    public void Effect(Board board) {
        //mossa move classica + eventuale effetto athena
        effettoAtena.SpecificEffect(board);
    }
}

class Minotaur extends God implements SpecificEffect {

    private static Minotaur instance;
    private Minotaur(){
        super("MINOTAUR", "Descrizione Minotaur");
    }

    public static Minotaur instance(){
        if(instance == null) instance = new Minotaur();
        return instance;
    }

    public void SpecificEffect(Board board){}

    @Override
    public void Effect(Board board) {
        //mossa move classica + eventuale effetto athena
        SpecificEffect(board);
    }
}

//fine lista dei move


//Inizio lista dei build

//altri dei

//fine lista dei build


//Inizio lista dei on opponent

//altri dei

//fine lista dei build


/*class TemplateGod extends GodOnMove {

    private static TemplateGod instance;
    Eff
}*/

