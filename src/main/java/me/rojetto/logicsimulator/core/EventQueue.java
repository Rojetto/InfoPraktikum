package me.rojetto.logicsimulator.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EventQueue {
    private final List<Event> queue;

    public EventQueue() {
        queue = new LinkedList<>();
    }

    public boolean hasMore() {
        return queue.size() > 0;
    }

    public Event getFirst() {
        return queue.remove(0);
    }

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
