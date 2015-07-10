package me.rojetto.logicsimulator.files;

import me.rojetto.logicsimulator.core.Circuit;
import me.rojetto.logicsimulator.core.Gate;
import me.rojetto.logicsimulator.core.Signal;
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
    private static final LogiVector OUTPUT_NOT_OFFSET = new LogiVector(20, 0);
    private static final LogiVector INPUT_NOT_OFFSET = new LogiVector(-20, 0);

    private static final LogiVector EVEN_INPUT_OFFSET = new LogiVector(0, -40);
    private static final LogiVector ODD_INPUT_OFFSET = new LogiVector(0, -30);
    private static final LogiVector IN_OUT_OFFSET = new LogiVector(20, 0);
    private static final int STANDARD_DELAY = 1;

    private final File file;
    private final List<LogiWire> wires;
    private final Map<LogiVector, LogiSlot> outputSlots;
    private final Map<LogiVector, LogiSlot> inputSlots;

    private int gateCounter = 0;
    private int signalCounter = 0;

    public LogiFlashParser(File file) {
        this.file = file;
        this.wires = new ArrayList<>();
        this.outputSlots = new HashMap<>();
        this.inputSlots = new HashMap<>();
    }

    public Circuit parse() throws ParserConfigurationException, IOException, SAXException {
        Circuit circuit = new Circuit();
        Document dom = getDocument();

        NodeList wireNodes = dom.getElementsByTagName("wire");
        List<LogiWireSegment> segments = new ArrayList<>();
        for (int i = 0; i < wireNodes.getLength(); i++) {
            Element node = (Element) wireNodes.item(i);
            LogiVector p1 = new LogiVector(Integer.parseInt(node.getAttribute("rightx")),
                    Integer.parseInt(node.getAttribute("righty")));
            LogiVector p2 = new LogiVector(Integer.parseInt(node.getAttribute("leftx")),
                    Integer.parseInt(node.getAttribute("lefty")));

            segments.add(new LogiWireSegment(p1, p2));
        }

        while (segments.size() > 0) {
            Iterator<LogiWireSegment> it = segments.iterator();
            int howManyRemoved = 0;
            while (it.hasNext()) {
                LogiWireSegment s = it.next();

                for (LogiWire wire : wires) {
                    if (wire.segmentFits(s)) {
                        wire.addSegment(s);
                        it.remove();
                        howManyRemoved++;
                    }
                }
            }

            if (segments.size() > 0 && howManyRemoved == 0) {
                LogiWire newWire = new LogiWire();
                newWire.addSegment(segments.remove(0));
                wires.add(newWire);
            }
        }

        NodeList gateNodes = dom.getElementsByTagName("gate");
        for (int i = 0; i < gateNodes.getLength(); i++) {
            Element node = (Element) gateNodes.item(i);
            Gate gate = fromNode(node);
            circuit.addGate(gate);
            addSlots(node, gate);
        }

        NodeList sourceNodes = dom.getElementsByTagName("source");
        for (int i = 0; i < sourceNodes.getLength(); i++) {
            Element node = (Element) sourceNodes.item(i);
            Gate inDelay = new Buf(1, STANDARD_DELAY, newGateName());
            circuit.addGate(inDelay);
            Signal inSignal = new Signal("in" + (i + 1));
            inDelay.connectSignal("i1", inSignal);
            circuit.addInput(inSignal);

            LogiVector pos = new LogiVector(Integer.parseInt(node.getAttribute("x")), Integer.parseInt(node.getAttribute("y")));
            for (int j = 0; j < 4; j++) {
                LogiSlot outSlot = new LogiSlot(inDelay, "o", false);
                LogiVector outPos = pos.add(IN_OUT_OFFSET.rotate(j * Math.PI / 2));
                outputSlots.put(outPos, outSlot);
            }
        }

        NodeList drainNodes = dom.getElementsByTagName("drain");
        for (int i = 0; i < drainNodes.getLength(); i++) {
            Element node = (Element) drainNodes.item(i);
            Gate outDelay = new Buf(1, STANDARD_DELAY, newGateName());
            circuit.addGate(outDelay);
            Signal outSignal = new Signal("o" + (i + 1));
            outDelay.connectSignal("o", outSignal);
            circuit.addOutput(outSignal);

            LogiVector pos = new LogiVector(Integer.parseInt(node.getAttribute("x")), Integer.parseInt(node.getAttribute("y")));
            for (int j = 0; j < 4; j++) {
                LogiSlot inSlot = new LogiSlot(outDelay, "i1", false);
                LogiVector inPos = pos.add(IN_OUT_OFFSET.rotate(j * Math.PI / 2));
                inputSlots.put(inPos, inSlot);
            }
        }

        for (LogiVector outputPos : outputSlots.keySet()) {
            LogiSlot slot = outputSlots.get(outputPos);
            List<LogiSlot> connectedInputSlots = getConnectedInputSlots(outputPos);
            if (connectedInputSlots.size() == 0) {
                continue;
            }

            Gate gate = slot.getGate();
            Signal newSignal = new Signal(newSignalName());
            circuit.addSignal(newSignal);
            if (!outputSlots.get(outputPos).isInverted()) {
                gate.connectSignal(slot.getSlot(), newSignal);
            } else {
                Gate notOutput = new Not(1, STANDARD_DELAY, newGateName());
                circuit.addGate(notOutput);
                Signal signalBetween = new Signal(newSignalName());
                circuit.addSignal(signalBetween);
                gate.connectSignal(slot.getSlot(), signalBetween);
                notOutput.connectSignal("i1", signalBetween);
                notOutput.connectSignal("o", newSignal);
            }

            for (LogiSlot connectedInputSlot : connectedInputSlots) {
                if (!connectedInputSlot.isInverted()) {
                    connectedInputSlot.getGate().connectSignal(connectedInputSlot.getSlot(), newSignal);
                } else {
                    Gate notInput = new Not(1, STANDARD_DELAY, newGateName());
                    circuit.addGate(notInput);
                    Signal signalBetween = new Signal(newSignalName());
                    circuit.addSignal(signalBetween);
                    notInput.connectSignal("i1", newSignal);
                    notInput.connectSignal("o", signalBetween);
                    connectedInputSlot.getGate().connectSignal(connectedInputSlot.getSlot(), signalBetween);
                }
            }
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

    private void addSlots(Element node, Gate gate) {
        double rotation = Double.parseDouble(node.getAttribute("rot"));
        LogiVector pos = new LogiVector(Integer.parseInt(node.getAttribute("x")), Integer.parseInt(node.getAttribute("y")));
        List<LogiVector> relativeInputSlotPositions = new ArrayList<>();
        List<LogiVector> relativeOutputSlotPositions = new ArrayList<>();
        List<LogiSlot> newInputSlots = new ArrayList<>();
        List<LogiSlot> newOutputSlots = new ArrayList<>();

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
                newInputSlots.add(slot);
            }

            relativeOutputSlotPositions.add(new LogiVector(30, 0));
            newOutputSlots.add(new LogiSlot(gate, "o", node.getAttribute("outs").charAt(0) == '1'));
        } else if (gate instanceof FF || gate instanceof Latch) {
            relativeInputSlotPositions.add(new LogiVector(-80, -30));
            relativeInputSlotPositions.add(new LogiVector(-80, 0));

            newInputSlots.add(new LogiSlot(gate, "d", node.getAttribute("ins").charAt(0) == '1'));

            if (gate instanceof FF) {
                newInputSlots.add(new LogiSlot(gate, "c", node.getAttribute("ins").charAt(1) == '1'));
            } else {
                newInputSlots.add(new LogiSlot(gate, "e", node.getAttribute("ins").charAt(1) == '1'));
            }

            relativeOutputSlotPositions.add(new LogiVector(80, -30));
            relativeOutputSlotPositions.add(new LogiVector(80, 30));

            newOutputSlots.add(new LogiSlot(gate, "q", node.getAttribute("outs").charAt(0) == '1'));
            newOutputSlots.add(new LogiSlot(gate, "nq", node.getAttribute("outs").charAt(1) == '1'));
        }

        for (int i = 0; i < relativeInputSlotPositions.size(); i++) {
            if (newInputSlots.get(i).isInverted()) {
                relativeInputSlotPositions.set(i, relativeInputSlotPositions.get(i).add(INPUT_NOT_OFFSET));
            }

            relativeInputSlotPositions.set(i, relativeInputSlotPositions.get(i).rotate(rotation / 180.0 * Math.PI));
        }

        for (int i = 0; i < relativeOutputSlotPositions.size(); i++) {
            if (newOutputSlots.get(i).isInverted()) {
                relativeOutputSlotPositions.set(i, relativeOutputSlotPositions.get(i).add(OUTPUT_NOT_OFFSET));
            } else {
                relativeOutputSlotPositions.set(i, relativeOutputSlotPositions.get(i).rotate(rotation / 180.0 * Math.PI));
            }
        }

        for (int i = 0; i < relativeInputSlotPositions.size(); i++) {
            this.inputSlots.put(pos.add(relativeInputSlotPositions.get(i)), newInputSlots.get(i));
        }

        for (int i = 0; i < relativeOutputSlotPositions.size(); i++) {
            this.outputSlots.put(pos.add(relativeOutputSlotPositions.get(i)), newOutputSlots.get(i));
        }
    }

    private List<LogiSlot> getConnectedInputSlots(LogiVector pos) {
        List<LogiSlot> connectedInputSlots = new ArrayList<>();

        for (LogiWire wire : wires) {
            if (wire.contains(pos)) {
                for (LogiVector point : wire.getPoints()) {
                    if (inputSlots.containsKey(point)) {
                        connectedInputSlots.add(inputSlots.get(point));
                    }
                }
            }
        }

        return connectedInputSlots;
    }

    private Document getDocument() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }

    private String newGateName() {
        String name = "g" + gateCounter;
        gateCounter++;

        return name;
    }

    private String newSignalName() {
        String name = "i" + signalCounter;
        signalCounter++;

        return name;
    }
}
