package it.polimi.ingsw.server.observers;

import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;

public interface ObserverMV <T> {


    void update (T message, Object o);

}
