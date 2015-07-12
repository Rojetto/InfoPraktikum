package me.rojetto.logicsimulator.files;

import me.rojetto.logicsimulator.core.Gate;

/**
 * Ein Anschluss an einem Gatter. Hilfsklasse für den {@link me.rojetto.logicsimulator.files.LogiFlashParser}.
 */
public class LogiSlot {
    private final Gate gate;
    private final String slot;
    private final boolean inverted;

    /**
     * @param gate     Gatter, zu dem der Anschluss gehört
     * @param slot     Name des Anschlusses
     * @param inverted <code>true</code>, wenn der Anschluss in der Schaltung invertiert ist
     */
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
