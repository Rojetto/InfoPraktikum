public class Exor extends LogicElement {
    protected Exor(int numberOfInputs, int delay) {
        super(numberOfInputs, delay);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        int trueInputs = 0;

        for (Boolean value : inputs) {
            if (value) {
                trueInputs++;
            }
        }

        return trueInputs % 2 != 0;
    }
}
