public class Or extends SimpleGate {
    public Or(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        for (boolean input : inputs) {
            if (input) {
                return true;
            }
        }

        return false;
    }
}
