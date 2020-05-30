package it.polimi.ingsw.server.model;

/**
 * This class represents the worker, which will be player's piece during the game.
 * Every player has two Workers on the game board, so there will be two different Worker objects
 * bound to the same Player object during the game.
 */
public class Worker {

    private final Player player;
    private final Color colour;
    private final String workerTag;

    /**
     * @param player the worker's related player
     * @param color worker's own color
     * @param workerTag worker's tag: will be "A" or "B"
     */
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

    /**
     * This method is present in this class because it was asked by Sonar.
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
