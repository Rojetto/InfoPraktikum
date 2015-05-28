import java.util.HashSet;
import java.util.Set;

public class Signal {
    private final String name;
    private final Set<Nand> outputs;
    private boolean value;

    public Signal(String name) {
        this.name = name;
        this.outputs = new HashSet<>();
    }

    public void setValue(boolean value) {
        this.value = value;

        for (Nand output : outputs) {
            output.update();
        }
    }

    public void handleEvent(Event event) {
        if (event.getSignal() == this) {
            value = event.getValue();

            for (Nand output : outputs) {
                output.timedUpdate(event.getTime());
            }
        }
    }

    public void addOutput(Nand nand) {
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
