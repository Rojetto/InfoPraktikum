import java.util.LinkedList;
import java.util.List;

public class EventQueue {
    private final List<Event> queue;

    public EventQueue() {
        queue = new LinkedList<>();
    }

    public boolean hasMore() {
        return queue.size() > 0;
    }

    public Event getFirst() {
        return queue.get(0);
    }
}
