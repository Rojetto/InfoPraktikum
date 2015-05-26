import java.util.Arrays;

public class Nand {
    private final Signal[] inputs;
    private Signal output;

    public Nand(int numberOfSlots) {
        this.inputs = new Signal[numberOfSlots];
    }

    public void setInput(int slot, Signal signal) {
        inputs[slot] = signal;

        signal.addOutput(this);
    }

    public void setOutput(Signal output) {
        this.output = output;
    }

    public void update() {
        output.setValue(Arrays.asList(inputs).stream().map(Signal::getValue).anyMatch(value -> !value));
    }
}
