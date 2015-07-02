import java.util.HashMap;
import java.util.Map;

public class Circuit {
    private final Map<String, Signal> inputs;
    private final Map<String, Signal> outputs;
    private final Map<String, Signal> signals;
    private final Map<String, LogicElement> gates;

    public Circuit() {
        inputs = new HashMap<>();
        outputs = new HashMap<>();
        signals = new HashMap<>();
        gates = new HashMap<>();
    }

    public void addInput(Signal signal) {
        inputs.put(signal.getName(), signal);
        signals.put(signal.getName(), signal);
    }

    public void addOutput(Signal signal) {
        outputs.put(signal.getName(), signal);
        signals.put(signal.getName(), signal);
    }

    public void addSignal(Signal signal) {
        signals.put(signal.getName(), signal);
    }

    public void addGate(LogicElement gate) {
        gates.put(gate.getName(), gate);
    }

    public Signal getInput(String name) {
        return inputs.get(name);
    }

    public Signal getOutput(String name) {
        return outputs.get(name);
    }

    public Signal getSignal(String name) {
        return signals.get(name);
    }

    public LogicElement getGate(String name) {
        return gates.get(name);
    }
}
