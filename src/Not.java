public class Not extends LogicElement {
    public Not(int delay) {
        super(1, delay);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        return !inputs[0];
    }
}
