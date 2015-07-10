package me.rojetto.logicsimulator.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Speichert Zustand aller Eingangs- und Ausgangssignale zu einer bestimmten Zeit
 */
public class SignalState {
    private final int time;
    private final Map<Signal, Boolean> values;

    /**
     * @param time   Zeit des Zustandes
     * @param values Signalnamen und jeweilige Werte
     */
    public SignalState(int time, Map<Signal, Boolean> values) {
        this.time = time;
        this.values = new HashMap<>(values);
    }

    /**
     * @param lastFrame Letzter Zustand
     * @param event     Event, das Zustandsänderung enthält
     */
    public SignalState(SignalState lastFrame, Event event) {
        this(event.getTime(), lastFrame.values);
        this.values.put(event.getSignal(), event.getValue());
    }

    public int getTime() {
        return time;
    }

    /**
     * @return Wert des Signals in diesem Zustand
     */
    public boolean getValue(Signal signal) {
        return values.get(signal);
    }
}
