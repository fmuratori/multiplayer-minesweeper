package multiplayer.minesweeper.observer;

import multiplayer.minesweeper.observer.events.ConnectionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Observers {

    private static Observers instance = new Observers();
    private List<Observer> observers = new ArrayList<>();

    private Observers() {}

    public void subscribeObserver(Observer obs) {
        observers.add(obs);
    }
    public void unsubscribeObserver(Observer obs) {
        observers.remove(obs);
    }

    public void notifyAll(Object event) {
        observers.forEach(obs -> obs.notifyEvent(event));
    }

    public static Observers get() {
        return instance;
    }

}
