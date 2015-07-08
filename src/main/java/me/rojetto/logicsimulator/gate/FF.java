package me.rojetto.logicsimulator.gate;

import me.rojetto.logicsimulator.core.Gate;

import java.util.HashMap;
import java.util.Map;

public class FF extends Gate {
    private boolean storedValue;
    private boolean lastClockValue;

    public FF(int numberOfInputs, int delay, String name) {
        super(2, delay, name);
    }

    @Override
    protected String[] inputNames() {
        return new String[]{"c", "d"};
    }

    @Override
    protected String[] outputNames() {
        return new String[]{"q", "nq"};
    }

    @Override
    protected Map<String, Boolean> calculateOutput(Map<String, Boolean> inputs) {
        boolean clock = inputs.get("c");
        boolean data = inputs.get("d");

        if (!lastClockValue && clock) {
            storedValue = data;
        }

        lastClockValue = clock;

        Map<String, Boolean> output = new HashMap<>();
        output.put("q", storedValue);
        output.put("nq", !storedValue);

        return output;
    }
}
