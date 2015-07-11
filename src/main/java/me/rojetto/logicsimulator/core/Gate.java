package me.rojetto.logicsimulator.core;

import me.rojetto.logicsimulator.LogicSimulatorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstrakte Superklasse f�r alle Logikgatter der Schaltung.
 */
public abstract class Gate {
    public static final int MAX_RECURSION_DEPTH = 1000; // TODO: Konfigurierbar machen

    private final Map<String, Signal> inputs;
    private final int numberOfInputs;
    private final int delay;
    private final String name;
    private final Map<String, Signal> outputs;
    private Map<String, Boolean> lastOutputs;
    private boolean isFirstCalculation;
    private int nonTimedUpdateCounter;
    private int timedUpdateCounter;

    /**
     * @param numberOfInputs Anzahl der Eing�nge
     * @param delay          Berechnungsdauer des Gatters
     * @param name           Name des Gatters
     */
    public Gate(int numberOfInputs, int delay, String name) {
        this.inputs = new HashMap<>();
        this.numberOfInputs = numberOfInputs;
        this.outputs = new HashMap<>();
        this.lastOutputs = new HashMap<>();
        this.delay = delay;
        this.name = name;
        this.isFirstCalculation = true;
    }

    public String getName() {
        return name;
    }

    /**
     * @param slot Name des Eingangsanschlusses
     * @param signal Anliegendes Signal
     */
    public void setInput(String slot, Signal signal) {
        inputs.put(slot, signal);

        signal.addOutput(this);
    }

    /**
     * @param slot   Name des Ausgangsanschlusses
     * @param signal Anliegendes Signal
     */
    public void setOutput(String slot, Signal signal) {
        outputs.put(slot, signal);
    }

    /**
     * Propagiert Ergebnis bei �nderung zeitunabh�ngig direkt an anliegende Signale
     */
    public void update() {
        propagateCalculations(calculateOutput(getInputValues()), false, 0);
    }

    /**
     * Propagiert Ergebnis bei �nderung zeitabh�ngig an anliegende Signale
     * @param time Zeit, zu der sich Eing�nge ge�ndert haben
     */
    public void timedUpdate(int time) {
        propagateCalculations(calculateOutput(getInputValues()), true, time);
    }

    /**
     * Gibt berechnete Werte an Ausgangssignale weiter
     * @param values Ausgangsanschlussnamen und jeweilige Werte
     * @param timed Events erzeugen oder direkt propagieren
     * @param time Zeit, zu der sich Eing�nge ge�ndert haben
     */
    private void propagateCalculations(Map<String, Boolean> values, boolean timed, int time) {
        List<Signal> signalsToUpdate = new ArrayList<>();

        for (String output : outputs.keySet()) {
            if ((values.get(output) != lastOutputs.get(output) || isFirstCalculation) && outputs.get(output) != null
                    && !(!timed && nonTimedUpdateCounter > MAX_RECURSION_DEPTH)
                    && !(timed && timedUpdateCounter > MAX_RECURSION_DEPTH)) {
                signalsToUpdate.add(outputs.get(output));
            }
        }

        isFirstCalculation = false;
        if (!timed) {
            nonTimedUpdateCounter++;
        } else {
            timedUpdateCounter++;
        }
        lastOutputs = values;

        for (Signal s : signalsToUpdate) {
            if (timed) {
                new Event(s, time + delay, values.get(getOutputSlot(s)));
            } else {
                s.setValueAndPropagate(values.get(getOutputSlot(s)));
            }
        }
    }

    /**
     * @return Eingangsanschlussnamen und jeweilige aktuelle Werte
     */
    private Map<String, Boolean> getInputValues() {
        Map<String, Boolean> inputValues = new HashMap<>();

        for (String input : inputs.keySet()) {
            inputValues.put(input, inputs.get(input).getValue());
        }

        return inputValues;
    }

    /**
     * Verbindet Signal mit jeweiligem Eingang oder Ausgang
     *
     * @param slot   Name des Anschlusses
     * @param signal Signal, das angeschlossen werden soll
     * @throws LogicSimulatorException Wenn Anschluss nicht existiert
     */
    public void connectSignal(String slot, Signal signal) throws LogicSimulatorException {
        boolean connected = false;

        if (stringMatchesRegexArray(inputNames(), slot)) {
            setInput(slot, signal);
            connected = true;
        } else if (stringMatchesRegexArray(outputNames(), slot)) {
            setOutput(slot, signal);
            connected = true;
        }

        if (!connected) {
            throw new LogicSimulatorException("Der Slot " + slot + " existiert bei Gattern vom Typ " +
                    getClass().getName() + " nicht");
        }
    }

    /**
     * Hilfsmethode um zu �berpr�fen ob String auf mindestens eine aus einer Liste von Regex's passt
     * @param array Array von Regular Expressions
     * @param s String, der �berpr�ft werden soll
     */
    private boolean stringMatchesRegexArray(String[] array, String s) {
        for (int i = 0; i < array.length; i++) {
            if (s.matches(array[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return Name des Anschlusses, an den Signal angeschlossen ist, <code>null</code>, wenn nicht angeschlossen
     */
    private String getOutputSlot(Signal signal) {
        for (String slot : outputs.keySet()) {
            if (outputs.get(slot) == signal) {
                return slot;
            }
        }

        return null;
    }

    /**
     * @return Anschlussnamen und jeweilige angeschlossene Signale
     */
    public Map<String, Signal> getSlots() {
        Map<String, Signal> slots = new HashMap<>();
        slots.putAll(inputs);
        slots.putAll(outputs);

        return slots;
    }

    public int getNumberOfInputs() {
        return numberOfInputs;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + name + ": Inputs=" + numberOfInputs + " Delay=" + delay;
    }

    /**
     * @return Array von Regular Expressions auf die Eingangsnamen des Gatters passen
     */
    protected abstract String[] inputNames();

    /**
     * @return Array von Regular Expressions auf die Ausgangsnamen des Gatters passen
     */
    protected abstract String[] outputNames();

    /**
     * @param inputValues Anschlussnamen und jeweilige anliegende Werte
     * @return Ausgangsanschlussnamen und jeweilige berechnete Werte
     */
    protected abstract Map<String, Boolean> calculateOutput(Map<String, Boolean> inputValues);
}
