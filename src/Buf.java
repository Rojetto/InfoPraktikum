public class Buf extends LogicElement {
    public Buf(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        return inputs[0];
    }
}
