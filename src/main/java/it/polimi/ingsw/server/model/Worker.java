package it.polimi.ingsw.server.model;

public class Worker {

    private final Player player;
    private final Color colour;
    private final String workerTag;

    public Worker(Player player, Color color, String workerTag){

        //tutto in upperCase per uniformità
        this.colour = color ;
        this.workerTag = workerTag;
        this.player = player;
    }

    public Color getColour() {
        return colour;
    }

    public Player getPlayer() {
        return player;
    }

    String getWorkerTag() {
        return workerTag;
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Worker)) return false;
        return ((Worker) obj).player.equals(this.player) && ((Worker) obj).colour.equals(this.colour) && ((Worker) obj).workerTag.equals(this.workerTag);
    }
}
