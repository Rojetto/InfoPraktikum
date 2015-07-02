public class Nand extends LogicElement {
    public Nand(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
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
