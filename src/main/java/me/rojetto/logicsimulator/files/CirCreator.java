package me.rojetto.logicsimulator.files;

import me.rojetto.logicsimulator.core.Circuit;
import me.rojetto.logicsimulator.core.Gate;
import me.rojetto.logicsimulator.core.Signal;
import me.rojetto.logicsimulator.gate.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CirCreator {
    private static final Map<Class<? extends Gate>, String> TYPE_NAMES = new HashMap<>();

    static {
        TYPE_NAMES.put(And.class, "AND");
        TYPE_NAMES.put(Nand.class, "NAND");
        TYPE_NAMES.put(Or.class, "OR");
        TYPE_NAMES.put(Nor.class, "NOR");
        TYPE_NAMES.put(Exor.class, "EXOR");
        TYPE_NAMES.put(Not.class, "NOT");
        TYPE_NAMES.put(Buf.class, "BUF");
        TYPE_NAMES.put(Latch.class, "LATCH");
        TYPE_NAMES.put(FF.class, "FF");
    }

    public static String create(Circuit circuit) {
        String result = "";
        List<Signal> internalSignals = circuit.getSignals();

        for (Signal in : circuit.getInputs()) {
            internalSignals.remove(in);
        }

        if (circuit.getInputs().size() > 0) {
            result += "Input " + listWithCommas(circuit.getInputs()) + ";\n";
        }

        for (Signal out : circuit.getOutputs()) {
            internalSignals.remove(out);
        }

        if (circuit.getOutputs().size() > 0) {
            result += "Output " + listWithCommas(circuit.getOutputs()) + ";\n";
        }

        result += "Signal " + listWithCommas(internalSignals) + ";\n";

        for (Gate gate : circuit.getGates()) {
            result += "Gate " + gate.getName() + " " + getGateTypeName(gate) + " Delay " + gate.getDelay() + ";\n";
        }

        for (Gate gate : circuit.getGates()) {
            for (String slot : gate.getSlots().keySet()) {
                result += gate.getName() + "." + slot + " = " + gate.getSlots().get(slot).getName() + ";\n";
            }
        }

        return result;
    }

    private static String getGateTypeName(Gate gate) {
        String name = TYPE_NAMES.get(gate.getClass());
        Class<? extends Gate> c = gate.getClass();

        if (c != Not.class && c != Buf.class && c != Latch.class && c != FF.class) {
            name += gate.getNumberOfInputs();
        }

        return name;
    }

    private static String listWithCommas(List<Signal> signals) {
        if (signals.size() == 0) {
            return "";
        }

        String result = signals.remove(0).getName();
        for (Signal next : signals) {
            result += ", " + next.getName();
        }

        return result;
    }
}
