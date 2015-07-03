import java.util.HashMap;
import java.util.Map;

public abstract class SimpleGate extends LogicElement {
    public SimpleGate(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    @Override
    protected Map<String, Boolean> calculateOutput(Map<String, Boolean> inputValues) {
        Map<String, Boolean> outputMap = new HashMap<>();
        boolean[] inputArray = new boolean[inputValues.size()];
        String[] keys = (String[]) inputValues.keySet().toArray();

        for (int i = 0; i < keys.length; i++) {
            inputArray[i] = inputValues.get(keys[i]);
        }

        outputMap.put("o", calculateOutput(inputArray));

        return outputMap;
    }

    @Override
    protected String[] inputNames() {
        return new String[]{"i\\d+"};
    }

    @Override
    protected String[] outputNames() {
        return new String[]{"o"};
    }

    protected abstract boolean calculateOutput(boolean[] inputValues);
}
