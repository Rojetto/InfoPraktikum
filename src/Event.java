public class Event {
    private static EventQueue eventQueue;
    private final Signal signal;
    private final int time;
    private final boolean value;

    public Event(Signal signal, int time, boolean value) {
        this.signal = signal;
        this.time = time;
        this.value = value;
    }

    public static void setEventQueue(EventQueue eventQueue) {
        Event.eventQueue = eventQueue;
    }

    public void propagate() {
        signal.setValue(value);
    }
}
