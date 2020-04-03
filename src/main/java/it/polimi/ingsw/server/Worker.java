package it.polimi.ingsw.server;

public class Worker {

    //colour lo faccio String per garantire maggiore flessibilità, la visualizzazione e la scelta del
    //colore sono delegati alla view (con tanto di caso di default nel caso in cui il colore non ci fosse
    //workerTag non mi convince granchè, soprattutto perchè è poco efficiente nell'input
    //mi sembra inutile fare gli attributi private in questo caso e avere venti getter e setter

    private final Player player;
    private final String colour;
    private final String workerTag;

    private Box occupiedBox;

    public String getColour() {
        return colour;
    }

    public Player getPlayer() {
        return player;
    }

    public String getWorkerTag() {
        return workerTag;
    }

    public Worker(Player player, String colour, String workerTag){

        //tutto in upperCase per uniformità
        this.colour = colour ;
        this.workerTag = workerTag;
        this.player = player;
    }

    public Box getOccupiedBox(){ return this.occupiedBox; }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Worker)) return false;
        if( ((Worker) obj).player.equals(this.player) && ((Worker) obj).colour.equals(this.colour) && ((Worker) obj).workerTag.equals(this.workerTag))
            return true;
        else return false;
    }
}
