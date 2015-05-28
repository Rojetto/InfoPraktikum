public class FF extends LogicElement {
    private boolean storedValue;
    private boolean lastClockValue;

    public FF(int delay) {
        super(2, delay);
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
