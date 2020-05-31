package it.polimi.ingsw.server.observers;

/**
 * standard implementation of the observer class of the patter
 * @param <T> object the observed class will send
 */
public interface Observer <T> {


    void update (T message, Object o);

}
