package me.rojetto.logicsimulator.gate;

import me.rojetto.logicsimulator.core.Gate;

import java.util.HashMap;
import java.util.Map;

public class Latch extends Gate {
    private boolean storedValue;

    public Latch(int numberOfInputs, int delay, String name) {
        super(2, delay, name);
    }

    @Override
    protected String[] inputNames() {
        return new String[]{"d", "e"};
    }

    @Override
    protected String[] outputNames() {
        return new String[]{"q", "nq"};
    }

    @Override
    protected Map<String, Boolean> calculateOutput(Map<String, Boolean> inputs) {
        if (inputs.get("e")) {
            storedValue = inputs.get("d");
        }

        Map<String, Boolean> output = new HashMap<>();
        output.put("q", storedValue);
        output.put("nq", !storedValue);

        return output;
    }
}
