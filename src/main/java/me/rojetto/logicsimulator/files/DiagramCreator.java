package me.rojetto.logicsimulator.files;

import me.rojetto.logicsimulator.core.Signal;
import me.rojetto.logicsimulator.core.SignalState;
import me.rojetto.logicsimulator.core.SimulationResult;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Erstellt Diagramme aus Simulationsergebnissen.
 */
public class DiagramCreator {
    private static int hOffset = 100;
    private static int vOffset = 20;
    private static int lineHeight = 20;
    private static int textInterval = 50;
    private static int lineInterval = 10;

    /**
     * Erzeugt ein neues Diagramm aus einem Simulationsergebnis
     *
     * @param result Simulationsergebnis, das dargestellt werden soll
     * @return Diagramm als Bild
     */
    public static BufferedImage create(SimulationResult result) {
        int timeLineLength = result.lastState().getTime() + 10;
        List<Signal> signals = result.getSignals();
        List<SignalState> frames = result.getSignalStates();
        BufferedImage image = new BufferedImage(hOffset + timeLineLength + 20, signals.size() * lineHeight + vOffset, BufferedImage.TYPE_INT_RGB);
        Graphics2D graph = image.createGraphics();
        graph.setColor(Color.white);
        graph.fillRect(0, 0, image.getWidth(), image.getHeight());

        graph.setColor(Color.BLACK);
        int y = vOffset + lineHeight - 2;
        for (Signal name : signals) {
            graph.drawString(name.getName(), 0, y);
            y += lineHeight;
        }
        graph.setColor(Color.lightGray);
        for (int i = 0; i < timeLineLength; i++) {
            if (i % lineInterval == 0) {
                graph.drawLine(i + hOffset, vOffset, i + hOffset, image.getHeight());
            }
        }
        graph.setColor(Color.BLACK);
        for (int i = 0; i < timeLineLength; i++) {
            if (i % textInterval == 0) {
                graph.drawString(i + "", i + hOffset, vOffset - 2);
            }
        }
        SignalState lastFrame = result.firstState();

        frames.remove(0);
        int lastX = hOffset;
        graph.setColor(Color.BLACK);

        while (frames.size() > 0) {
            SignalState currentFrame = frames.remove(0);
            int time = currentFrame.getTime();
            int currentX = time + hOffset;
            y = vOffset;
            for (Signal s : signals) {
                int zero = y + 16;
                int one = y + 4;
                boolean lastValue = lastFrame.getValue(s);
                boolean currentValue = currentFrame.getValue(s);

                if (currentValue && !lastValue) {
                    graph.drawLine(lastX, zero, currentX, zero);
                    graph.drawLine(currentX, zero, currentX, one);
                }
                if (!currentValue && lastValue) {
                    graph.drawLine(lastX, one, currentX, one);
                    graph.drawLine(currentX, one, currentX, zero);
                }
                if (currentValue == lastValue) {
                    int holdline = zero;
                    if (lastValue) {
                        holdline = one;
                    }
                    graph.drawLine(lastX, holdline, currentX, holdline);
                }
                y = y + lineHeight;
            }

            lastFrame = currentFrame;
            lastX = currentX;
            // Ende des Graphen weiterzeichnen
            if (frames.size() == 0) {
                currentX = timeLineLength + hOffset;
                y = vOffset;
                for (Signal s : signals) {
                    int zero = y + 16;
                    int one = y + 4;
                    boolean lastValue = lastFrame.getValue(s);
                    int holdline = zero;
                    if (lastValue) {
                        holdline = one;
                    }
                    graph.drawLine(lastX, holdline, currentX, holdline);
                    y = y + lineHeight;
                }
            }
        }

        return image;
    }
}
