public class Nand extends LogicElement {
    public Nand(int numberOfInputs, int delay) {
        super(numberOfInputs, delay);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        boolean acc = true;
        for (Boolean bool : inputs) {
            acc = acc && bool;
        }

        return !acc;
    }
}
