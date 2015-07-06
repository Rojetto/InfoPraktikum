import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CirParser {
    private static final Map<Pattern, Statement> patterns = new HashMap<>();
    private static final Pattern statementPattern = Pattern.compile("\\s*([^;]+?)\\s*;");
    private static final Map<String, Class<? extends LogicElement>> elementTypes = new HashMap<>();

    static {
        patterns.put(Pattern.compile("(?i:(Signal|Input|Output))\\s+([a-zA-Z0-9]+\\s*(?:\\s*,\\s*[a-zA-Z0-9]+)*)"), Statement.SIGNAL);
        patterns.put(Pattern.compile("(?i:Gate)\\s+([a-zA-Z0-9]+)\\s+([A-Z]+)(\\d*)\\s+(?i:Delay)\\s+(\\d+)"), Statement.GATE);
        patterns.put(Pattern.compile("([a-zA-Z0-9]+)\\s*\\.\\s*([a-zA-Z0-9]+)\\s*=\\s*([a-zA-Z0-9]+)"), Statement.CONNECTION);

        elementTypes.put("AND", And.class);
        elementTypes.put("NAND", Nand.class);
        elementTypes.put("OR", Or.class);
        elementTypes.put("NOR", Nor.class);
        elementTypes.put("EXOR", Exor.class);
        elementTypes.put("NOT", Not.class);
        elementTypes.put("BUF", Buf.class);
        elementTypes.put("LATCH", Latch.class);
        elementTypes.put("FF", FF.class);
    }

    private enum Statement {
        SIGNAL, GATE, CONNECTION
    }

    public static Circuit parse(String content) {
        Circuit circuit = new Circuit();

        content = content.replaceAll("#.+", "");

        String statement = firstStatement(content); // TODO: Dinge hinter letztem gültigen Statement werden ignoriert
        while (statement != null) {
            Pattern p = identifyPattern(statement);
            if (p == null) {
                throw new InvalidStatementException("Fehler in Kommando: " + statement);
            }

            Matcher m = p.matcher(statement);
            m.find();

            switch (patterns.get(p)) {
                case SIGNAL:
                    String type = m.group(1);
                    String names = m.group(2);
                    names = names.replaceAll("\\s*", "");
                    for (String name : names.split(",")) {
                        Signal signal = new Signal(name);
                        switch (type.toLowerCase()) {
                            case "input":
                                circuit.addInput(signal);
                                break;
                            case "output":
                                circuit.addOutput(signal);
                                break;
                            case "signal":
                                circuit.addSignal(signal);
                                break;
                        }
                    }

                    break;
                case GATE:
                    String name = m.group(1);
                    String gateType = m.group(2);
                    String inputs = m.group(3);
                    if (inputs.equals("")) {
                        inputs = "0";
                    }
                    String delay = m.group(4);

                    circuit.addGate(newElement(gateType, Integer.parseInt(inputs), Integer.parseInt(delay), name));

                    break;
                case CONNECTION:
                    String gateName = m.group(1);
                    String slot = m.group(2);
                    String signalName = m.group(3);

                    Signal signal = circuit.getSignal(signalName);
                    LogicElement gate = circuit.getGate(gateName);

                    if (signal == null) {
                        throw new InvalidStatementException("Signal " + signalName + " existiert nicht");
                    }

                    if (gate == null) {
                        throw new InvalidStatementException("Gatter " + gateName + " existiert nicht");
                    }

                    gate.connectSignal(slot, signal);

                    break;
            }

            content = content.replaceFirst(statementPattern.pattern(), "");
            statement = firstStatement(content);
        }

        return circuit;
    }

    private static String firstStatement(String s) {
        Matcher m = statementPattern.matcher(s);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    private static Pattern identifyPattern(String s) {
        for (Pattern p : patterns.keySet()) {
            Matcher m = p.matcher(s);
            if (m.matches()) {
                return p;
            }
        }

        return null;
    }

    private static LogicElement newElement(String type, int numberOfInputs, int delay, String name) {
        Class<? extends LogicElement> clazz = elementTypes.get(type);
        try {
            return clazz.getConstructor(int.class, int.class, String.class).newInstance(numberOfInputs, delay, name);
        } catch (Exception e) {
            throw new InvalidStatementException("Konnte kein Element vom Typ " + type + " erstellen");
        }
    }
}
