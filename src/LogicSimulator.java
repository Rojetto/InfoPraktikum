import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicSimulator {
    private final EventQueue queue;
    private Circuit circuit;

    public LogicSimulator(File circuitFile, File eventFile) throws IOException {
        queue = new EventQueue();
        circuit = null;
        Event.setEventQueue(queue);

        String circuitString = readFileContent(circuitFile);
        circuit = CirParser.parse(circuitString);
        String eventString = readFileContent(eventFile);
        EventParser.parse(eventString, circuit);
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            if (args.length != 4) {
                System.out.println("Unzureichende Parameter.");
                System.out.println("Format: [cirPfad] [eventsPfad] [ergPfad] [pngPfad]");
                return;
            }

            File circuitFile = new File(args[0]);
            File eventFile = new File(args[1]);
            File ergFile = new File(args[2]);
            File graphFile = new File(args[3]);

            LogicSimulator simulator = new LogicSimulator(circuitFile, eventFile);
            SimulationResult result = simulator.simulate();

            ergFile.delete();
            ergFile.createNewFile();
            System.setOut(new PrintStream(ergFile));
            System.out.println(ErgCreator.create(result));

            ImageIO.write(DiagramCreator.create(result), "PNG", graphFile);
        } else {
            new SimulatorWindow();
        }
    }

    private String readFileContent(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String content = "";

        String line = reader.readLine();
        while (line != null) {
            content += line + "\n";
            line = reader.readLine();
        }

        return content;
    }

    public SimulationResult simulate() {
        for (Signal input : circuit.getInputs()) {
            input.setValueAndPropagate(false);
        }

        List<Event> history = new ArrayList<>();
        Map<Signal, Boolean> startValues = new HashMap<>();
        List<Signal> inputsAndOutputs = circuit.getInputsAndOutputs();

        for (Signal s : inputsAndOutputs) {
            startValues.put(s, s.getValue());
        }

        while (queue.hasMore()) {
            Event e = queue.getFirst();
            if (inputsAndOutputs.contains(e.getSignal())) {
                history.add(e);
            }

            e.propagate();
        }

        return new SimulationResult(startValues, history, inputsAndOutputs);
    }
}
