package me.rojetto.logicsimulator.files;

public class LogiWireSegment {
    private final LogiVector p1;
    private final LogiVector p2;

    public LogiWireSegment(LogiVector p1, LogiVector p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public LogiVector getP1() {
        return p1;
    }

    public LogiVector getP2() {
        return p2;
    }
}
