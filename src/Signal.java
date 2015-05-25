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
        if (outputs.size() == 0 && value != this.value) {
            System.out.println(name + " -> " + value);
        }

        this.value = value;

        for (Nand output : outputs) {
            output.update();
        }
    }

    public void addOutput(Nand nand) {
        outputs.add(nand);
    }

    public boolean getValue() {
        return value;
    }
}
