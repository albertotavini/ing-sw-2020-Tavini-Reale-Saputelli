package it.polimi.ingsw.model;

public class Worker {

    //colour lo faccio String per garantire maggiore flessibilità, la visualizzazione e la scelta del
    //colore sono delegati alla view (con tanto di caso di default nel caso in cui il colore non ci fosse
    //workerTag non mi convince granchè, soprattutto perchè è poco efficiente nell'input
    //mi sembra inutile fare gli attributi private in questo caso e avere venti getter e setter

    public final Player player;
    public final String colour;
    public final char workerTag;


    public Worker(Player player, String colour, char workerTag){

        //tutto in upperCase per uniformità
        this.colour = colour.toUpperCase();
        this.workerTag = workerTag;
        this.player = player;
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Worker)) return false;
        if( ((Worker) obj).player.equals(this.player) && ((Worker) obj).colour.equals(this.colour) && ((Worker) obj).workerTag == this.workerTag)
            return true;
        else return false;
    }
}
