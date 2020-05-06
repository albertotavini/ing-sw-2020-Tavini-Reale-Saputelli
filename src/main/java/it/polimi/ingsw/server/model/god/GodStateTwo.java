package it.polimi.ingsw.server.model.god;

public class GodStateTwo implements GodState {
    private static GodStateTwo instance = new GodStateTwo();
    public static GodStateTwo getInstance() {return instance;}
}
