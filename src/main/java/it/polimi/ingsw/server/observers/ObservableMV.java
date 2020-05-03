package it.polimi.ingsw.server.observers;

import it.polimi.ingsw.server.observers.ModelMessage.ModelMessage;

import java.util.ArrayList;
import java.util.List;

public class ObservableMV <T> {
    private final List<ObserverMV<T>> observers = new ArrayList<>();

    public void addObserver(ObserverMV<T> observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(ObserverMV<T> observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }
    protected void notify(T message, Object o){
        synchronized (observers) {
            for(ObserverMV<T> observer : observers){
                observer.update(message, o);
            }
        }
    }

}
