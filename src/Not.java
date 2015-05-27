public class Not extends LogicElement {
    public Not(int delay) {
        super(1, delay);
    }

    @Override
    protected boolean calculateOutput(Signal[] inputs) {
        return !inputs[0].getValue();
    }
}
