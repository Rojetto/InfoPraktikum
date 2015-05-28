import java.util.Arrays;

public class And extends LogicElement {
    public And(int numberOfInputs, int delay) {
        super(numberOfInputs, delay);
    }

    @Override
    protected boolean calculateOutput(boolean[] inputs) {
        return Arrays.asList(inputs).stream().allMatch(value -> true);
    }
}
