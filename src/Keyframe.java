import java.util.HashMap;
import java.util.Map;

public class Keyframe {
    private final int time;
    private final Map<Signal, Boolean> values;

    public Keyframe(int time, Map<Signal, Boolean> values) {
        this.time = time;
        this.values = new HashMap<>(values);
    }

    public Keyframe(Keyframe lastFrame, Event event) {
        this(event.getTime(), lastFrame.values);
        this.values.put(event.getSignal(), event.getValue());
    }

    public int getTime() {
        return time;
    }

    public boolean getValue(Signal signal) {
        return values.get(signal);
    }
}
