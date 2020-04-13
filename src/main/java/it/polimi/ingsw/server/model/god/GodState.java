package it.polimi.ingsw.server.model.god;

public interface GodState {
}

class GodStateOne implements GodState {
    private static GodStateOne instance = new GodStateOne();
    public static GodStateOne getInstance() {return instance;}
}
class GodStateTwo implements GodState {
    private static GodStateTwo instance = new GodStateTwo();
    public static GodStateTwo getInstance() {return instance;}
}

class GodStateThree implements GodState {
    private static GodStateThree instance = new GodStateThree();
    public static GodStateThree getInstance() {return instance;}
}

class GodStateFour implements GodState {
    private static GodStateFour instance = new GodStateFour();
    public static GodStateFour getInstance() {return instance;}
}
