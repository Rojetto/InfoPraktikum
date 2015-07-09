package me.rojetto.logicsimulator.files;

public class LogiVector {
    private final int x;
    private final int y;

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

    public LogiVector add(LogiVector other) {
        return new LogiVector(x + other.x, y + other.y);
    }

    public LogiVector mul(double scalar) {
        return new LogiVector((int) Math.round(x * scalar), (int) Math.round(y * scalar));
    }

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
}
