package it.polimi.ingsw.server.observers;

import java.util.ArrayList;
import java.util.List;

/**
 * classic implementation of the observable class for observer pattern
 * @param <T> type of object that will be notified to observer
 */
public class Observable <T> {
    private final List<Observer<T>> observers = new ArrayList<>();

    public void addObserver(Observer<T> observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(Observer<T> observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }
    protected void notify(T message, Object o){
        synchronized (observers) {
            for(Observer<T> observer : observers){
                observer.update(message, o);
            }
        }
    }

}
