public class Not extends LogicElement {
    public Not(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        return !inputs[0];
    }
}
