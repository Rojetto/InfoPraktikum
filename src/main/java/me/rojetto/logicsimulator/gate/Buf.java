package me.rojetto.logicsimulator.gate;

import me.rojetto.logicsimulator.core.SimpleGate;

public class Buf extends SimpleGate {
    public Buf(int numberOfInputs, int delay, String name) {
        super(1, delay, name);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        return inputs[0];
    }
}
