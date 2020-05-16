package it.polimi.ingsw.server.observers;

public interface Observer <T> {


    void update (T message, Object o);

}
