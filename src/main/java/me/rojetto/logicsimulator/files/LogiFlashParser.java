package me.rojetto.logicsimulator.files;

import me.rojetto.logicsimulator.core.Circuit;
import me.rojetto.logicsimulator.core.Gate;
import me.rojetto.logicsimulator.exception.InvalidStatementException;
import me.rojetto.logicsimulator.gate.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LogiFlashParser {
    private static final LogiVector OUTPUT_NOT_OFFSET = new LogiVector(10, 0);
    private static final LogiVector INPUT_NOT_OFFSET = new LogiVector(-10, 0);

    private static final LogiVector EVEN_INPUT_OFFSET = new LogiVector(0, -40);
    private static final LogiVector ODD_INPUT_OFFSET = new LogiVector(0, -30);
    private static final int STANDARD_DELAY = 1;

    private final File file;
    private final Map<LogiVector, Set<LogiVector>> wires;
    private final Map<LogiVector, LogiSlot> outputs;
    private final Map<LogiVector, LogiSlot> inputs;

    private int gateCounter = 0;

    public LogiFlashParser(File file) {
        this.file = file;
        this.wires = new HashMap<>();
        this.outputs = new HashMap<>();
        this.inputs = new HashMap<>();
    }

    public Circuit parse() throws ParserConfigurationException, IOException, SAXException {
        Circuit circuit = new Circuit();
        Document dom = getDocument();

        NodeList wireNodes = dom.getElementsByTagName("wire");
        for (int i = 0; i < wireNodes.getLength(); i++) {
            Element node = (Element) wireNodes.item(i);
            addWire(Integer.parseInt(node.getAttribute("rightx")), Integer.parseInt(node.getAttribute("righty")),
                    Integer.parseInt(node.getAttribute("leftx")), Integer.parseInt(node.getAttribute("lefty")));
        }

        NodeList gateNodes = dom.getElementsByTagName("gate");
        for (int i = 0; i < gateNodes.getLength(); i++) {
            Element node = (Element) gateNodes.item(i);
            Gate gate = fromNode(node);
        }

        return circuit;
    }

    private Gate fromNode(Element node) {
        int numberOfInputs = node.getAttribute("ins").length();
        Gate gate = null;

        switch (node.getAttribute("type")) {
            case "And":
                gate = new And(numberOfInputs, STANDARD_DELAY, newGateName());
                break;
            case "Or":
                gate = new Or(numberOfInputs, STANDARD_DELAY, newGateName());
                break;
            case "Xor":
                gate = new Exor(numberOfInputs, STANDARD_DELAY, newGateName());
                break;
            case "Delay":
                gate = new Buf(1, Integer.parseInt(node.getAttribute("delaytime")), newGateName());
                break;
            case "One":
                gate = new Buf(1, STANDARD_DELAY, newGateName());
                break;
            case "FF_D":
                if (node.hasAttribute("edge") && node.getAttribute("edge").equals("yes")) {
                    gate = new FF(2, STANDARD_DELAY, newGateName());
                } else {
                    gate = new Latch(2, STANDARD_DELAY, newGateName());
                }
                break;
        }

        return gate;
    }

    private void addSlots(Element node) {
        Gate gate = fromNode(node);
        double rotation = Double.parseDouble(node.getAttribute("rot"));
        LogiVector pos = new LogiVector(Integer.parseInt(node.getAttribute("x")), Integer.parseInt(node.getAttribute("y")));
        List<LogiVector> relativeInputSlotPositions = new ArrayList<>();
        List<LogiVector> relativeOutputSlotPositions = new ArrayList<>();
        List<LogiSlot> inputSlots = new ArrayList<>();
        List<LogiSlot> outputSlots = new ArrayList<>();

        if (gate instanceof And || gate instanceof Or || gate instanceof Exor || gate instanceof Buf) {
            LogiVector firstPosition = new LogiVector(-30, 0);
            LogiVector offset;

            if (gate.getNumberOfInputs() % 2 == 0) {
                offset = EVEN_INPUT_OFFSET;
                firstPosition = firstPosition.add(offset.mul(-0.5 - ((gate.getNumberOfInputs() - 1) / 2)));
            } else {
                offset = ODD_INPUT_OFFSET;
                firstPosition = firstPosition.add(offset.mul(-((gate.getNumberOfInputs() - 1) / 2)));
            }

            for (int i = 0; i < gate.getNumberOfInputs(); i++) {
                relativeInputSlotPositions.add(firstPosition.add(offset.mul(i)));
                LogiSlot slot = new LogiSlot(gate, "i" + (i + 1), node.getAttribute("ins").charAt(i) == '1');
                inputSlots.add(slot);
            }

            relativeOutputSlotPositions.add(new LogiVector(30, 0));
            outputSlots.add(new LogiSlot(gate, "o", node.getAttribute("outs").charAt(0) == '1'));
        } else if (gate instanceof FF || gate instanceof Latch) {
            relativeInputSlotPositions.add(new LogiVector(-80, -30));
            relativeInputSlotPositions.add(new LogiVector(-80, 0));

            inputSlots.add(new LogiSlot(gate, "d", node.getAttribute("ins").charAt(0) == '1'));

            if (gate instanceof FF) {
                inputSlots.add(new LogiSlot(gate, "c", node.getAttribute("ins").charAt(1) == '1'));
            } else {
                inputSlots.add(new LogiSlot(gate, "e", node.getAttribute("ins").charAt(1) == '1'));
            }

            relativeOutputSlotPositions.add(new LogiVector(80, -30));
            relativeOutputSlotPositions.add(new LogiVector(80, 30));

            outputSlots.add(new LogiSlot(gate, "q", node.getAttribute("outs").charAt(0) == '1'));
            outputSlots.add(new LogiSlot(gate, "nq", node.getAttribute("outs").charAt(1) == '1'));
        }

        for (int i = 0; i < relativeInputSlotPositions.size(); i++) {
            if (inputSlots.get(i).isInverted()) {
                relativeInputSlotPositions.set(i, relativeInputSlotPositions.get(i).add(INPUT_NOT_OFFSET));
            }

            relativeInputSlotPositions.set(i, relativeInputSlotPositions.get(i).rotate(rotation / 180.0 * Math.PI));
        }

        for (int i = 0; i < relativeOutputSlotPositions.size(); i++) {
            if (outputSlots.get(i).isInverted()) {
                relativeOutputSlotPositions.set(i, relativeOutputSlotPositions.get(i).add(OUTPUT_NOT_OFFSET));
            }

            relativeOutputSlotPositions.set(i, relativeOutputSlotPositions.get(i).rotate(rotation / 180.0 * Math.PI));
        }

        for (int i = 0; i < relativeInputSlotPositions.size(); i++) {
            inputs.put(pos.add(relativeInputSlotPositions.get(i)), inputSlots.get(i));
        }

        for (int i = 0; i < relativeOutputSlotPositions.size(); i++) {
            outputs.put(pos.add(relativeOutputSlotPositions.get(i)), outputSlots.get(i));
        }
    }

    private void addWire(int x1, int y1, int x2, int y2) {
        LogiVector p1 = new LogiVector(x1, y1);
        LogiVector p2 = new LogiVector(x2, y2);

        if (!wires.containsKey(p1)) {
            wires.put(p1, new HashSet<LogiVector>());
        }

        wires.get(p1).add(p2);
    }

    private Document getDocument() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }

    private Gate newElement(Class<? extends Gate> type, int numberOfInputs, int delay, String name) {
        try {
            return type.getConstructor(int.class, int.class, String.class).newInstance(numberOfInputs, delay, name);
        } catch (Exception e) {
            throw new InvalidStatementException("Konnte kein Element vom Typ " + type + " erstellen");
        }
    }

    private String newGateName() {
        String name = "g" + gateCounter;
        gateCounter++;

        return name;
    }
}
