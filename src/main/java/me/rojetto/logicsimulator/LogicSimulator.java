package me.rojetto.logicsimulator;

import me.rojetto.logicsimulator.core.*;
import me.rojetto.logicsimulator.files.*;
import me.rojetto.logicsimulator.ui.SimulatorWindow;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hauptklasse des Programms. Liest Dateien ein, simuliert, und schreibt Ergebnisse in Dateien.
 */
public class LogicSimulator {
    private final EventQueue queue;
    private Circuit circuit;

    /**
     * Neuer Logik-Simulator aus Schaltungs- und Eventdatei. Erstellt EventQueue und Schaltung und tr�gt Events
     * in Queue ein.
     *
     * @param circuitFile Datei mit Schaltung im .cir oder LogiFlash .xml Format
     * @param eventFile   Datei mit zugeh�rigen Events im .events Format
     * @throws IOException             Wenn eine der Dateien nicht gelesen oder geschrieben werden konnte
     * @throws LogicSimulatorException Wenn XML-Datei nicht lesbar war
     */
    public LogicSimulator(File circuitFile, File eventFile) throws IOException, LogicSimulatorException {
        queue = new EventQueue();
        Event.setEventQueue(queue);

        if (circuitFile.getName().matches(".*\\.xml")) {
            LogiFlashParser parser = new LogiFlashParser(circuitFile);
            circuit = parser.parse();
        } else {
            String circuitString = fileToString(circuitFile);
            circuit = CirParser.parse(circuitString);
        }

        String eventString = fileToString(eventFile);
        EventParser.parse(eventString, circuit);
    }

    /**
     * Main-Methode des Projekts. Hat 3 verschiedene Verhaltensweisen:<br>
     * - 0 Argumente: �ffnet GUI des Logik-Simulators<br>
     * - 2 Argumente: Nimmt LogiFlash .xml und konvertiert Schaltung zu .cir<br>
     * - 4 Argumente: Nimmt .cir oder .xml, .events und erzeugt .erg und .png
     *
     * @param args Kommandozeilenargumente
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                new SimulatorWindow();
            } else if (args.length == 2) {
                File xmlFile = new File(args[0]);
                File cirFile = new File(args[1]);

                Circuit c = new LogiFlashParser(xmlFile).parse();
                String cirString = CirCreator.create(c);

                stringToFile(cirString, cirFile);
            } else if (args.length == 4) {
                File circuitFile = new File(args[0]);
                File eventFile = new File(args[1]);
                File ergFile = new File(args[2]);
                File graphFile = new File(args[3]);

                LogicSimulator simulator = new LogicSimulator(circuitFile, eventFile);
                SimulationResult result = simulator.simulate();

                stringToFile(ErgCreator.create(result), ergFile);
                ImageIO.write(DiagramCreator.create(result), "PNG", graphFile);
            } else {
                System.out.println("Unzureichende Parameter. Moeglichkeiten:");
                System.out.println("GUI: Keine Parameter");
                System.out.println("Simulation: [in: schaltung] [in: events] [out: ergebnis] [out: diagramm]");
                System.out.println("LogiFlash Converter: [in: xml] [out: cir]");
            }
        } catch (LogicSimulatorException e1) {
            System.err.println(e1.getMessage());
        } catch (IOException e2) {
            System.err.println(e2);
        }
    }

    /**
     * Hilfsmethode um String aus Textdatei auszulesen
     * @param file Auszulesende Datei
     * @return Generierter String aus allen Zeilen der Datei
     * @throws IOException Wenn Datei nicht gelesen werden konnte
     */
    private static String fileToString(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String content = "";

        String line = reader.readLine();
        while (line != null) {
            content += line + "\n";
            line = reader.readLine();
        }

        return content;
    }

    /**
     * Hilfsmethode um String in Datei zu speichern. L�scht Datei und erstellt sie neu bevor geschrieben wird.
     * @param content String der geschrieben werden soll
     * @param file Zieldatei
     * @throws IOException Wenn ein Fehler beim Schreiben aufgetreten ist
     */
    private static void stringToFile(String content, File file) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        PrintWriter w = new PrintWriter(file);
        w.print(content);
        w.close();
    }

    /**
     * Arbeitet EventQueue ab bis leer, tr�gt Ergebnisse in neues SimulationResult ein.
     * @return Simulationsergebnis
     */
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
