public class Nor extends SimpleGate {
    public Nor(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        for (boolean input : inputs) {
            if (input) {
                return false;
            }
        }

        return true;
    }
}
