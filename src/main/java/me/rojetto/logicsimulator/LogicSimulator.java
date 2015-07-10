package me.rojetto.logicsimulator;

import me.rojetto.logicsimulator.core.*;
import me.rojetto.logicsimulator.files.*;
import me.rojetto.logicsimulator.ui.SimulatorWindow;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicSimulator {
    private final EventQueue queue;
    private Circuit circuit;

    public LogicSimulator(File circuitFile, File eventFile) throws IOException, ParserConfigurationException, SAXException {
        queue = new EventQueue();
        Event.setEventQueue(queue);

        if (circuitFile.getName().matches(".*\\.xml")) {
            LogiFlashParser parser = new LogiFlashParser(circuitFile);
            circuit = parser.parse();
        } else {
            String circuitString = readFileContent(circuitFile);
            circuit = CirParser.parse(circuitString);
        }
        String eventString = readFileContent(eventFile);
        EventParser.parse(eventString, circuit);
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        if (args.length == 0) {
            new SimulatorWindow();
        } else if (args.length == 2) {
            File xmlFile = new File(args[0]);
            File cirFile = new File(args[1]);

            Circuit c = new LogiFlashParser(xmlFile).parse();
            String cirString = CirCreator.create(c);
            System.out.println(cirString);
        } else if (args.length == 4) {
            File circuitFile = new File(args[0]);
            File eventFile = new File(args[1]);
            File ergFile = new File(args[2]);
            File graphFile = new File(args[3]);

            LogicSimulator simulator = new LogicSimulator(circuitFile, eventFile);
            SimulationResult result = simulator.simulate();

            ergFile.delete();
            ergFile.createNewFile();
            System.setOut(new PrintStream(ergFile)); // TODO: Irgendwie Mist
            System.out.println(ErgCreator.create(result));

            ImageIO.write(DiagramCreator.create(result), "PNG", graphFile);
        } else {
            System.out.println("Unzureichende Parameter. Moeglichkeiten:");
            System.out.println("GUI: Keine Parameter");
            System.out.println("Simulation: [in: schaltung] [in: events] [out: ergebnis] [out: diagramm]");
            System.out.println("LogiFlash Converter: [in: xml] [out: cir]");
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
