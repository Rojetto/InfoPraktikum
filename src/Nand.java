import java.util.Arrays;

public class Nand extends LogicElement {
    public Nand(int numberOfInputs, int delay) {
        super(numberOfInputs, delay);
    }

    @Override
    protected boolean calculateOutput(Signal[] inputs) {
        return Arrays.asList(inputs).stream().map(Signal::getValue).anyMatch(value -> !value);
    }
}
