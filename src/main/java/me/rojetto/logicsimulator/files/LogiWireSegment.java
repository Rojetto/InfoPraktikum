package me.rojetto.logicsimulator.files;

/**
 * Eine direkte Verbindung zwischen zwei Punkten in einer LogiFlash Schaltung. Hilfsklasse für den
 * {@link me.rojetto.logicsimulator.files.LogiFlashParser}.
 */
public class LogiWireSegment {
    private final LogiVector p1;
    private final LogiVector p2;

    /**
     * Neue Verbindung zwischen zwei Punkten
     *
     * @param p1 Punkt 1
     * @param p2 Punkt 2
     */
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
