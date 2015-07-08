package me.rojetto.logicsimulator.gate;

import me.rojetto.logicsimulator.core.SimpleGate;

public class Nand extends SimpleGate {
    public Nand(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        boolean acc = true;
        for (Boolean bool : inputs) {
            acc = acc && bool;
        }

        return !acc;
    }
}
