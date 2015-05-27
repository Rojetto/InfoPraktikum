import java.util.Arrays;

public class Exor extends LogicElement {
    protected Exor(int numberOfInputs, int delay) {
        super(numberOfInputs, delay);
    }

    @Override
    protected boolean calculateOutput(Signal[] inputs) {
        return Arrays.asList(inputs).stream().map(Signal::getValue).reduce(false, (acc, value) -> acc != value);
    }
}
