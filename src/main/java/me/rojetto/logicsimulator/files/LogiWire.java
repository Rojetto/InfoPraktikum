package me.rojetto.logicsimulator.files;

import java.util.HashSet;
import java.util.Set;

public class LogiWire {
    private final Set<LogiVector> points;

    public LogiWire() {
        points = new HashSet<>();
    }

    public boolean segmentFits(LogiWireSegment segment) {
        return points.contains(segment.getP1()) || points.contains(segment.getP2());
    }

    public void addSegment(LogiWireSegment segment) {
        points.add(segment.getP1());
        points.add(segment.getP2());
    }

    public boolean contains(LogiVector point) {
        return points.contains(point);
    }

    public Set<LogiVector> getPoints() {
        return new HashSet<>(points);
    }
}
