package me.rojetto.logicsimulator.core;

/**
 * Die Änderung eines Signalswertes zu einer bestimmten Zeit
 */
public class Event {
    private static EventQueue eventQueue;
    private final Signal signal;
    private final int time;
    private final boolean value;

    /**
     * Erzeugt neues Event und trägt es in EventQueue ein
     *
     * @param signal Signal, das sich ändert
     * @param time   Zeit, zu der Änderung eintritt
     * @param value  Neuer Wert des Signals
     */
    public Event(Signal signal, int time, boolean value) {
        this.signal = signal;
        this.time = time;
        this.value = value;

        Event.eventQueue.addEvent(this);
    }

    /**
     * @param eventQueue EventQueue in die neue Events eingetragen werden sollen
     */
    public static void setEventQueue(EventQueue eventQueue) {
        Event.eventQueue = eventQueue;
    }

    /**
     * Propagiert Wertänderung durch Schaltung
     */
    public void propagate() {
        signal.handleEvent(this);
    }

    /**
     * @return Zeit, zu der Änderung eintritt
     */
    public int getTime() {
        return time;
    }

    /**
     * @return Signal, das sich ändert
     */
    public Signal getSignal() {
        return signal;
    }

    /**
     * @return Neuer Wert des Signals
     */
    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return time + ": " + signal.getName() + " = " + (value ? 1 : 0);
    }
}
