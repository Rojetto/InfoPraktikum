import java.io.*;

public class DateiSimulator {
    private final EventQueue queue;
    private Circuit circuit;

    public DateiSimulator(File circuitFile, File eventFile) {
        queue = new EventQueue();
        circuit = null;
        Event.setEventQueue(queue);

        try {
            String circuitString = readFileContent(circuitFile);
            circuit = CirFileParser.parse(circuitString);
            String eventString = readFileContent(eventFile);
            EventFileParser.parse(eventString, circuit);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Signal input : circuit.getInputs()) {
            input.setValueAndPropagate(false);
        }

        while (queue.hasMore()) {
            queue.getFirst().propagate();
        }
    }

    public static void main(String[] args) {
        File circuitFile = new File(args[0]);
        File eventFile = new File(args[1]);

        new DateiSimulator(circuitFile, eventFile);
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
