public class Latch extends LogicElement {
    private boolean storedValue;

    public Latch(int delay) {
        super(2, delay);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        if (inputs[0]) {
            storedValue = inputs[1];
        }

        return storedValue;
    }
}
