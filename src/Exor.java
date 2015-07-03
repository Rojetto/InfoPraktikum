public class Exor extends SimpleGate {

    public Exor(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
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
