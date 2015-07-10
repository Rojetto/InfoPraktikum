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
            result += "Input " + in.getName() + ";\n";
            internalSignals.remove(in);
        }

        for (Signal out : circuit.getOutputs()) {
            result += "Output " + out.getName() + ";\n";
            internalSignals.remove(out);
        }

        for (Signal internal : internalSignals) {
            result += "Signal " + internal.getName() + ";\n";
        }

        for (Gate gate : circuit.getGates()) {
            result += "Gate " + gate.getName() + " " + getGateTypeName(gate) + " Delay " + gate.getDelay() + ";\n";
        }

        // TODO: Verbindungen schreiben

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
}
