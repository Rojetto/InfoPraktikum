public class FF extends LogicElement {
    private boolean storedValue;
    private boolean lastClockValue;

    public FF(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        boolean clock = inputs[0];
        boolean data = inputs[1];

        if (!lastClockValue && clock) {
            storedValue = data;
        }

        lastClockValue = clock;
        return storedValue;
    }
}
