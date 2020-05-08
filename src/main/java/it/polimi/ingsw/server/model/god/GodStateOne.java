package it.polimi.ingsw.server.model.god;

public class GodStateOne implements GodState {
    private static GodStateOne instance = new GodStateOne();
    public static GodStateOne getInstance() {return instance;}
}
