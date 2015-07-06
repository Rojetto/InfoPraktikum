import java.util.*;

public class Circuit {
    private final List<Signal> inputs;
    private final List<Signal> outputs;
    private final Map<String, Signal> signals;
    private final Map<String, LogicElement> gates;

    public Circuit() {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        signals = new HashMap<>();
        gates = new HashMap<>();
    }

    public void addInput(Signal signal) {
        inputs.add(signal);
        signals.put(signal.getName(), signal);
    }

    public void addOutput(Signal signal) {
        outputs.add(signal);
        signals.put(signal.getName(), signal);
    }

    public void addSignal(Signal signal) {
        signals.put(signal.getName(), signal);
    }

    public void addGate(LogicElement gate) {
        gates.put(gate.getName(), gate);
    }

    public Signal getSignal(String name) {
        return signals.get(name);
    }

    public LogicElement getGate(String name) {
        return gates.get(name);
    }

    public List<Signal> getInputs() {
        return new ArrayList<>(inputs);
    }

    public List<Signal> getOutputs() {
        return new ArrayList<>(outputs);
    }

    public List<Signal> getSignals() {
        return new ArrayList<>(signals.values());
    }

    public List<Signal> getInputsAndOutputs() {
        List<Signal> inputsAndOutputs = new ArrayList<>();
        inputsAndOutputs.addAll(inputs);
        inputsAndOutputs.addAll(outputs);

        return inputsAndOutputs;
    }

    public List<LogicElement> getGates() {
        return new ArrayList<>(gates.values());
    }
}
