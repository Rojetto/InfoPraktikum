package me.rojetto.logicsimulator.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Verwaltet Events, die noch nicht eingetreten sind
 */
public class EventQueue {
    private final List<Event> queue;

    public EventQueue() {
        queue = new LinkedList<>();
    }

    /**
     * @return <code>true</code>, wenn die Queue noch nicht leer ist
     */
    public boolean hasMore() {
        return queue.size() > 0;
    }

    /**
     * Gibt nächstes anstehendes Event aus Queue und entfernt es
     * @return Nächstes Event
     */
    public Event getFirst() {
        return queue.remove(0);
    }

    /**
     * Sortiert Event an höchstmögliche Position in Queue ein. Wenn ein Event mit widersprüchlichen Eigenschaften
     * bereits existiert, wird Queue dementsprechend aufgeräumt.
     * @param event Hinzuzufügendes Event
     */
    public void addEvent(Event event) {
        if (removeEventWithTimeAndSignal(event.getTime(), event.getSignal())) {
            return;
        }

        for (int i = queue.size() - 1; i >= 0; i--) {
            if (event.getTime() >= queue.get(i).getTime()) {
                queue.add(i + 1, event);
                return;
            }
        }

        queue.add(0, event);
    }

    /**
     * Entfernt Events aus Queue, die zu bestimmter Zeit bestimmtes Signal verändern
     *
     * @return <code>true</code>, wenn ein Event entfernt werden musste
     */
    private boolean removeEventWithTimeAndSignal(int time, Signal signal) {
        Iterator<Event> iter = queue.iterator();
        boolean didRemoveSomething = false;
        while (iter.hasNext()) {
            Event next = iter.next();
            if (next.getSignal() == signal && next.getTime() == time) {
                iter.remove();
                didRemoveSomething = true;
            }
        }

        return didRemoveSomething;
    }
}
