public class Exor extends LogicElement {
    protected Exor(int numberOfInputs, int delay) {
        super(numberOfInputs, delay);
    }

    @Override
    protected boolean calculateOutput(Signal[] inputs) {
        int trueInputs = 0;

        for (Signal input : inputs) {
            if (input.getValue()) {
                trueInputs++;
            }
        }

        return trueInputs % 2 != 0;
    }
}
