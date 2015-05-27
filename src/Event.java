public class Event {
    private static EventQueue eventQueue;
    private final Signal signal;
    private final int time;
    private final boolean value;

    public Event(Signal signal, int time, boolean value) {
        this.signal = signal;
        this.time = time;
        this.value = value;

        Event.eventQueue.addEvent(this);
    }

    public static void setEventQueue(EventQueue eventQueue) {
        Event.eventQueue = eventQueue;
    }

    public void propagate() {
        if (!signal.hasOutputs()) {
            System.out.println(this);
        }
        signal.handleEvent(this);
    }

    public int getTime() {
        return time;
    }

    public Signal getSignal() {
        return signal;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return time + ": " + signal.getName() + " = " + (value ? 1 : 0);
    }
}
