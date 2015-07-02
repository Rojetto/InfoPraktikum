public class Latch extends LogicElement {
    private boolean storedValue;

    public Latch(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        if (inputs[0]) {
            storedValue = inputs[1];
        }

        return storedValue;
    }
}
