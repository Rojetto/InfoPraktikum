package me.rojetto.logicsimulator.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enthält alle Informationen über eine Schaltung
 */
public class Circuit {
    private final List<Signal> inputs;
    private final List<Signal> outputs;
    private final Map<String, Signal> signals;
    private final Map<String, Gate> gates;

    public Circuit() {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        signals = new HashMap<>();
        gates = new HashMap<>();
    }

    /**
     * Fügt neues Eingangssignal zu Schaltung hinzu
     *
     * @param signal
     */
    public void addInput(Signal signal) {
        inputs.add(signal);
        signals.put(signal.getName(), signal);
    }

    /**
     * Fügt neues Ausgangssignal zu Schaltung hinzu
     * @param signal
     */
    public void addOutput(Signal signal) {
        outputs.add(signal);
        signals.put(signal.getName(), signal);
    }

    /**
     * Fügt neues internes Signal zu Schaltung hinzu
     * @param signal
     */
    public void addSignal(Signal signal) {
        signals.put(signal.getName(), signal);
    }

    /**
     * Fügt neues Gater zu Schaltung hinzu
     * @param gate
     */
    public void addGate(Gate gate) {
        gates.put(gate.getName(), gate);
    }

    /**
     * Gibt gespeichertes internes, Eingangs-, oder Ausgangssignal nach Name zurück
     * @param name Name des gesuchten Signals
     * @return Gefundenes Signal, <code>null</code>, wenn kein Signal gefunden
     */
    public Signal getSignal(String name) {
        return signals.get(name);
    }

    /**
     * Gibt gespeichertes Gatter nach Name zurück
     * @param name Name des gesuchten Gatters
     * @return Gefundenes Gatter, <code>null</code>, wenn kein Gatter gefunden
     */
    public Gate getGate(String name) {
        return gates.get(name);
    }

    /**
     * @return Alle gespeicherten Eingangssignale in Reihenfolge der Deklaration
     */
    public List<Signal> getInputs() {
        return new ArrayList<>(inputs);
    }

    /**
     * @return Alle gespeicherten Ausgangssignale in Reihenfolge der Deklaration
     */
    public List<Signal> getOutputs() {
        return new ArrayList<>(outputs);
    }

    /**
     * @return Alle gespeicherten Signale in beliebiger Reihenfolge
     */
    public List<Signal> getSignals() {
        return new ArrayList<>(signals.values());
    }

    /**
     * @return Liste mit Eingangssignalen und Ausgangssignalen in Deklarationsreihenfolge
     */
    public List<Signal> getInputsAndOutputs() {
        List<Signal> inputsAndOutputs = new ArrayList<>();
        inputsAndOutputs.addAll(inputs);
        inputsAndOutputs.addAll(outputs);

        return inputsAndOutputs;
    }

    /**
     * @return Alle gespeicherten Gatter in beliebiger Reihenfolge
     */
    public List<Gate> getGates() {
        return new ArrayList<>(gates.values());
    }
}
