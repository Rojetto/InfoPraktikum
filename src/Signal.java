import java.util.HashSet;
import java.util.Set;

public class Signal {
    private final String name;
    private final Set<LogicElement> outputs;
    private boolean value;
    private int updateCounter;

    public Signal(String name) {
        this.name = name;
        this.outputs = new HashSet<>();
    }

    public void setValue(boolean value) {
        this.value = value;

        if (updateCounter < 10) {
            updateCounter++;
            for (LogicElement output : outputs) {
                output.update();
            }
        }
    }

    public void handleEvent(Event event) {
        if (event.getSignal() == this) {
            value = event.getValue();

            for (LogicElement output : outputs) {
                output.timedUpdate(event.getTime());
            }
        }
    }

    public void addOutput(LogicElement nand) {
        outputs.add(nand);
    }

    public boolean hasOutputs() {
        return outputs.size() > 0;
    }

    public boolean getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
