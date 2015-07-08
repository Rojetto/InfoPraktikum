package me.rojetto.logicsimulator.core;

import java.util.HashSet;
import java.util.Set;

public class Signal {
    private final String name;
    private final Set<Gate> outputs;
    private boolean value;

    public Signal(String name) {
        this.name = name;
        this.outputs = new HashSet<>();
    }

    public void setValueAndPropagate(boolean value) {
        this.value = value;

        for (Gate output : outputs) {
            output.update();
        }
    }

    public void handleEvent(Event event) {
        if (event.getSignal() == this) {
            value = event.getValue();

            for (Gate output : outputs) {
                output.timedUpdate(event.getTime());
            }
        }
    }

    public void addOutput(Gate nand) {
        outputs.add(nand);
    }

    public boolean hasOutputs() {
        return outputs.size() > 0;
    }

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
