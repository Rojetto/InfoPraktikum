public abstract class LogicElement {
    protected final Signal[] inputs;
    protected final int delay;
    protected final String name;
    protected Signal output;
    private boolean lastOutput;
    private boolean isFirstCalculation;

    public LogicElement(int numberOfInputs, int delay, String name) {
        this.inputs = new Signal[numberOfInputs];
        this.delay = delay;
        this.name = name;
        this.isFirstCalculation = true;
    }

    public String getName() {
        return name;
    }

    public void setInput(int slot, Signal signal) {
        inputs[slot] = signal;

        signal.addOutput(this);
    }

    public void setOutput(Signal output) {
        this.output = output;
    }

    public void update() {
        boolean newValue = calculateOutput(getInputValues());

        output.setValue(newValue);
    }

    public void timedUpdate(int time) {
        boolean newValue = calculateOutput(getInputValues());

        if (newValue != lastOutput || isFirstCalculation) {
            new Event(output, time + delay, newValue);
            lastOutput = newValue;
            isFirstCalculation = false;
        }
    }

    private boolean[] getInputValues() {
        boolean[] inputArray = new boolean[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            inputArray[i] = inputs[i].getValue();
        }

        return inputArray;
    }

    protected abstract boolean calculateOutput(boolean[] inputs);
}
