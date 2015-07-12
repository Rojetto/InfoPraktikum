package me.rojetto.logicsimulator.files;

/**
 * Zeidimensionaler Vektor mit ganzzahligen Koordinaten. Hilfsklasse für den
 * {@link me.rojetto.logicsimulator.files.LogiFlashParser}.
 */
public class LogiVector {
    private final int x;
    private final int y;

    /**
     * @param x X Koordinate
     * @param y Y Koordinate
     */
    public LogiVector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Addiert zwei Vektoren
     *
     * @param other Zweiter Summand
     * @return Ergebnis der Addition
     */
    public LogiVector add(LogiVector other) {
        return new LogiVector(x + other.x, y + other.y);
    }

    /**
     * Multipliziert den Vektor mit einem Skalar
     *
     * @param scalar Skalarer Faktor
     * @return Ergebnis der Multiplikation
     */
    public LogiVector mul(double scalar) {
        return new LogiVector((int) Math.round(x * scalar), (int) Math.round(y * scalar));
    }

    /**
     * Rotiert den Vektor
     * @param radians Winkel in rad
     * @return Ergebnis der Rotation
     */
    public LogiVector rotate(double radians) {
        return new LogiVector((int) Math.round(x * Math.cos(radians) - y * Math.sin(radians)),
                (int) Math.round(x * Math.sin(radians) + y * Math.cos(radians)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogiVector logiVector = (LogiVector) o;

        if (x != logiVector.x) return false;
        return y == logiVector.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
