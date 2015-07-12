package me.rojetto.logicsimulator.files;

import me.rojetto.logicsimulator.LogicSimulatorException;
import me.rojetto.logicsimulator.core.Circuit;
import me.rojetto.logicsimulator.core.Event;
import me.rojetto.logicsimulator.core.Signal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Liest Strings im EVENTS-Format ein, generiert die entsprechenden Events und ordnet sie in die EventQueue ein.
 */
public class EventParser {
    private static final Pattern linePattern = Pattern.compile("\\s*(\\d+)\\s+([a-zA-Z0-9]+)\\s+([01])\\s*");

    /**
     * Erzeugt neue Events aus einem String im EVENTS-Format
     *
     * @param content Einzulesender String im EVENTS-Format
     * @param circuit Schaltung, für die Events erzeugt werden sollen
     * @throws LogicSimulatorException Wenn ein im String erwähntes Signal nicht in der Schaltung existiert
     */
    public static void parse(String content, Circuit circuit) throws LogicSimulatorException {
        content = content.replaceAll("#.*", "");

        while (firstLine(content) != null) {
            String line = firstLine(content);
            Matcher m = linePattern.matcher(line);
            if (m.matches()) {
                int time = Integer.parseInt(m.group(1));
                String signalName = m.group(2);
                boolean value = Integer.parseInt(m.group(3)) == 1;

                Signal signal = circuit.getSignal(signalName);

                if (signal == null) {
                    throw new LogicSimulatorException("Signal " + signalName + " existiert nicht");
                }

                new Event(signal, time, value);
            }

            content = content.replaceFirst(firstLine(content), "");
        }
    }

    private static String firstLine(String s) {
        Pattern p = Pattern.compile(".*\\n");
        Matcher m = p.matcher(s);

        if (m.find()) {
            return m.group();
        }

        return null;
    }
}
