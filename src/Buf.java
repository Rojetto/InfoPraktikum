public class Buf extends LogicElement {
    public Buf(int delay) {
        super(1, delay);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        return inputs[0];
    }
}
