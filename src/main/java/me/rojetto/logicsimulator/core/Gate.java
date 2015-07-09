package me.rojetto.logicsimulator.core;

import me.rojetto.logicsimulator.exception.InvalidConnectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Gate {
    private final Map<String, Signal> inputs;
    private final int numberOfInputs;
    private final int delay;
    private final String name;
    private final Map<String, Signal> outputs;
    private Map<String, Boolean> lastOutputs;
    private boolean isFirstCalculation;

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

    public void setInput(String slot, Signal signal) {
        inputs.put(slot, signal);

        signal.addOutput(this);
    }

    public void setOutput(String slot, Signal output) {
        outputs.put(slot, output);
    }

    public void update() {
        propagateCalculations(calculateOutput(getInputValues()), false, 0);
    }

    public void timedUpdate(int time) {
        propagateCalculations(calculateOutput(getInputValues()), true, time);
    }

    private void propagateCalculations(Map<String, Boolean> values, boolean timed, int time) {
        List<Signal> signalsToUpdate = new ArrayList<>();

        for (String output : outputs.keySet()) {
            if ((values.get(output) != lastOutputs.get(output) || isFirstCalculation) && outputs.get(output) != null) {
                signalsToUpdate.add(outputs.get(output));
            }
        }

        isFirstCalculation = false;
        lastOutputs = values;

        for (Signal s : signalsToUpdate) {
            if (timed) {
                new Event(s, time + delay, values.get(getSignalSlot(s)));
            } else {
                // TODO: Schmiert bei instabilen Rückkopplungen ab
                s.setValueAndPropagate(values.get(getSignalSlot(s)));
            }
        }
    }

    private Map<String, Boolean> getInputValues() {
        Map<String, Boolean> inputValues = new HashMap<>();

        for (String input : inputs.keySet()) {
            inputValues.put(input, inputs.get(input).getValue());
        }

        return inputValues;
    }

    public void connectSignal(String slot, Signal signal) throws InvalidConnectionException {
        boolean connected = false;

        if (stringMatchesRegexArray(inputNames(), slot)) {
            setInput(slot, signal);
            connected = true;
        } else if (stringMatchesRegexArray(outputNames(), slot)) {
            setOutput(slot, signal);
            connected = true;
        }

        if (!connected) {
            throw new InvalidConnectionException("Der Slot " + slot + " existiert bei Gattern vom Typ " +
                    getClass().getName() + " nicht");
        }
    }

    private boolean stringMatchesRegexArray(String[] array, String s) {
        for (int i = 0; i < array.length; i++) {
            if (s.matches(array[i])) {
                return true;
            }
        }

        return false;
    }

    private String getSignalSlot(Signal signal) {
        for (String slot : inputs.keySet()) {
            if (inputs.get(slot) == signal) {
                return slot;
            }
        }

        for (String slot : outputs.keySet()) {
            if (outputs.get(slot) == signal) {
                return slot;
            }
        }

        return null;
    }

    public int getNumberOfInputs() {
        return numberOfInputs;
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + name + ": Inputs=" + numberOfInputs + " Delay=" + delay;
    }

    protected abstract String[] inputNames();
    protected abstract String[] outputNames();
    protected abstract Map<String, Boolean> calculateOutput(Map<String, Boolean> inputValues);
}
