package me.rojetto.logicsimulator.files;

import java.util.HashSet;
import java.util.Set;

/**
 * Eine Verbindung zwischen mehreren Element in einer LogiFlash Schaltung. Hilfsklasse für den
 * {@link me.rojetto.logicsimulator.files.LogiFlashParser}.
 */
public class LogiWire {
    private final Set<LogiVector> points;

    public LogiWire() {
        points = new HashSet<>();
    }

    /**
     * Prüft, ob Segment räumlich an Kabel angeschlossen ist (d.h. mindestens einen Punkt gemeinsam hat)
     *
     * @param segment Zu prüfendes Segment
     * @return <code>true</code>, wenn Segment passt
     */
    public boolean segmentFits(LogiWireSegment segment) {
        return points.contains(segment.getP1()) || points.contains(segment.getP2());
    }

    /**
     * Fügt Punkte des Segments zu Kabel hinzu
     *
     * @param segment Hinzuzufügendes Segment
     */
    public void addSegment(LogiWireSegment segment) {
        points.add(segment.getP1());
        points.add(segment.getP2());
    }

    /**
     * Prüft, ob Punkt mit Kabel verbunden ist
     * @param point Zu überprüfender Punkt
     * @return <code>true</code>, wenn Punkt verbunden ist
     */
    public boolean contains(LogiVector point) {
        return points.contains(point);
    }

    /**
     * @return Ungeordnete Menge von Punkten, die von Kabel verbunden werden
     */
    public Set<LogiVector> getPoints() {
        return new HashSet<>(points);
    }
}
