package me.rojetto.logicsimulator.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Speichert das Ergebnis einer Simulation in einer geordneten Liste von
 * {@link me.rojetto.logicsimulator.core.SignalState}s.
 */
public class SimulationResult {
    private final List<SignalState> signalStates;
    private final List<Signal> signals;

    /**
     * Neues Simulationsergebnis aus Startwerten, einer Liste von eingetretenen Events und einer Liste von Signalen,
     * die die Reihenfolge im Ergebnistabellenkopf und dem Diagramm festlegt. Nur Signale mit Startwerten werden
     * gespeichert.
     *
     * @param startValues Alle zu speichernden Signale und ihre jeweiligen Startwerte
     * @param history     Zeitlich geordnete Liste mit eingetretenen Events, die zu speichernden Signale betreffen
     * @param signals     Liste mit allen zu speichernden Signalen in der Reihenfolge, in der sie im Tabellenkopf
     *                    und dem Diagramm auftreten sollen
     */
    public SimulationResult(Map<Signal, Boolean> startValues, List<Event> history, List<Signal> signals) {
        this.signals = signals;
        signalStates = new ArrayList<>();
        signalStates.add(new SignalState(0, startValues));

        for (Event event : history) {
            appendState(new SignalState(signalStates.get(signalStates.size() - 1), event));
        }
    }

    /**
     * Hängt einen Signalzustand an das Ende der gespeicherten Zustände an
     *
     * @param frame Anzuhängender Zustand
     */
    private void appendState(SignalState frame) {
        if (signalStates.get(signalStates.size() - 1).getTime() == frame.getTime()) {
            signalStates.remove(signalStates.size() - 1);
        }

        signalStates.add(frame);
    }

    /**
     * @return Letzter gespeicherter Zustand
     */
    public SignalState lastState() {
        return signalStates.get(signalStates.size() - 1);
    }

    /**
     * @return Geordnete Liste von gespeicherten Zuständen
     */
    public List<SignalState> getSignalStates() {
        return new ArrayList<>(signalStates);
    }

    /**
     * @return Alle gespeicherten Signale in oben beschriebener Reihenfolge
     */
    public List<Signal> getSignals() {
        return new ArrayList<>(signals);
    }

    /**
     * @return Erster gespeicherter Zustand
     */
    public SignalState firstState() {
        return signalStates.get(0);
    }
}
