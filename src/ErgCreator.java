import java.util.List;

public class ErgCreator {
    public static String create(SimulationResult result) {
        List<Signal> signals = result.getSignals();
        String headerFormat = "Zeit   ";
        String rowFormat = "%-7d";
        String[] names = new String[signals.size()];

        for (int i = 0; i < signals.size(); i++) {
            String name = signals.get(i).getName();
            if (name.length() > 4) {
                name = name.substring(0, 4);
            }

            names[i] = name;
            headerFormat += "%-5s";
            rowFormat += "%-5d";
        }

        rowFormat += "\n";

        String s = String.format(headerFormat, names);
        s += "\n\n";

        for (Keyframe frame : result.getKeyframes()) {
            Object[] values = new Object[signals.size() + 1];
            values[0] = frame.getTime();

            for (int i = 0; i < signals.size(); i++) {
                values[i + 1] = frame.getValue(signals.get(i)) ? 1 : 0;
            }

            s += String.format(rowFormat, values);
        }

        return s;
    }
}
