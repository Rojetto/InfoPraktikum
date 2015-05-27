import java.util.Arrays;

public class And extends LogicElement {
    public And(int numberOfInputs, int delay) {
        super(numberOfInputs, delay);
    }

    @Override
    protected boolean calculateOutput(Signal[] inputs) {
        return Arrays.asList(inputs).stream().map(Signal::getValue).allMatch(value -> true);
    }
}
