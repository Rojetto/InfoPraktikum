package me.rojetto.logicsimulator.core;

import java.util.HashSet;
import java.util.Set;

/**
 * Eine Verbindung zwischen mehreren Gattern. Kann an einen Ausgang und an beliebig viele Eingänge angeschlossen werden
 */
public class Signal {
    private final String name;
    private final Set<Gate> outputs;
    private boolean value;

    /**
     * @param name Name des neuen Signals
     */
    public Signal(String name) {
        this.name = name;
        this.outputs = new HashSet<>();
    }

    /**
     * Setzt den aktuellen Wert des Signals und propagiert die Änderung zeitunabhängig durch die Schaltung
     *
     * @param value Neuer Wert des Signals
     */
    public void setValueAndPropagate(boolean value) {
        this.value = value;

        for (Gate output : outputs) {
            output.update();
        }
    }

    /**
     * Übernimmt den neuen Wert aus dem Event und propagiert die Änderung zeitgesteuert durch die Schaltung
     * @param event Zu behandelndes Event
     */
    public void handleEvent(Event event) {
        if (event.getSignal() == this) {
            value = event.getValue();

            for (Gate output : outputs) {
                output.timedUpdate(event.getTime());
            }
        }
    }

    /**
     * Interne Methode, die Gatter auf Signalen aufrufen, die an ihre Eingänge angeschlossen werden.
     * @param gate Gatter, dass über Wertänderungen informiert werden soll
     */
    public void addOutput(Gate gate) {
        outputs.add(gate);
    }

    /**
     * @return Der aktuelle Wert des Signals
     */
    public boolean getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
