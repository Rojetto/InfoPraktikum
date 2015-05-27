import java.util.Arrays;

public class Nand {
    private final Signal[] inputs;
    private final int delay;
    private Signal output;
    private boolean lastOutput;
    private boolean isFirstCalculation;

    public Nand(int numberOfSlots) {
        this(numberOfSlots, 1);
    }

    public Nand(int numberOfSlots, int delay) {
        this.delay = delay;
        this.inputs = new Signal[numberOfSlots];
        this.isFirstCalculation = true;
    }

    public void setInput(int slot, Signal signal) {
        inputs[slot] = signal;

        signal.addOutput(this);
    }

    public void setOutput(Signal output) {
        this.output = output;
    }

    public void update() {
        boolean newValue = calculateOutput();

        if (output.getValue() != newValue) {
            output.setValue(newValue);
        }
    }

    public void timedUpdate(int time) {
        boolean newValue = calculateOutput();

        if (newValue != lastOutput || isFirstCalculation) {
            new Event(output, time + delay, newValue);
            lastOutput = newValue;
            isFirstCalculation = false;
        }
    }

    private boolean calculateOutput() {
        return Arrays.asList(inputs).stream().map(Signal::getValue).anyMatch(value -> !value);
    }
}
