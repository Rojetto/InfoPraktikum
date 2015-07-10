package me.rojetto.logicsimulator.files;

import me.rojetto.logicsimulator.core.Gate;

public class LogiSlot {
    private final Gate gate;
    private final String slot;
    private final boolean inverted;

    public LogiSlot(Gate gate, String slot, boolean inverted) {
        this.gate = gate;
        this.slot = slot;
        this.inverted = inverted;
    }

    public Gate getGate() {
        return gate;
    }

    public String getSlot() {
        return slot;
    }

    public boolean isInverted() {
        return inverted;
    }

    @Override
    public String toString() {
        return gate.getClass().getSimpleName() + " " + gate.getName() + ": " + (inverted ? "!" : "") + slot;
    }
}
