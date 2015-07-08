import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicSimulator {
    private final EventQueue queue;
    private Circuit circuit;

    public LogicSimulator(File circuitFile, File eventFile) {
        queue = new EventQueue();
        circuit = null;
        Event.setEventQueue(queue);

        try {
            String circuitString = readFileContent(circuitFile);
            circuit = CirParser.parse(circuitString);
            String eventString = readFileContent(eventFile);
            EventParser.parse(eventString, circuit);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        SimulationResult result = new SimulationResult(startValues, history, inputsAndOutputs);

        System.out.println(ErgCreator.create(result));
        Image graph = DiagramCreator.create(result);
    }

    public static void main(String[] args) {
        File circuitFile = new File(args[0]);
        File eventFile = new File(args[1]);

        new LogicSimulator(circuitFile, eventFile);
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
}
