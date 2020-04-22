package it.polimi.ingsw.server.observers;

import java.util.ArrayList;
import java.util.List;

public class ObservableVC <T> {
    private final List<ObserverVC<T>> observers = new ArrayList<>();

    public void addObserver(ObserverVC<T> observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(ObserverVC<T> observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }
    protected void notify(T message){
        synchronized (observers) {
            for(ObserverVC<T> observer : observers){
                observer.update(message);
            }
        }
    }
}
