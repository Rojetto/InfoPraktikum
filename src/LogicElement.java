public abstract class LogicElement {
    private final Signal[] inputs;
    private final int delay;
    private Signal output;
    private boolean lastOutput;
    private boolean isFirstCalculation;

    protected LogicElement(int numberOfInputs, int delay) {
        this.inputs = new Signal[numberOfInputs];
        this.delay = delay;
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
        boolean newValue = calculateOutput(inputs);

        if (output.getValue() != newValue) {
            output.setValue(newValue);
        }
    }

    public void timedUpdate(int time) {
        boolean newValue = calculateOutput(inputs);

        if (newValue != lastOutput || isFirstCalculation) {
            new Event(output, time + delay, newValue);
            lastOutput = newValue;
            isFirstCalculation = false;
        }
    }

    protected abstract boolean calculateOutput(Signal[] inputs);
}
