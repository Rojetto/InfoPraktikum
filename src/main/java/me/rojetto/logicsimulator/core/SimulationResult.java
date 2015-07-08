package me.rojetto.logicsimulator.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulationResult {
    private final List<Keyframe> keyframes;
    private final List<Signal> signals;

    public SimulationResult(Map<Signal, Boolean> startValues, List<Event> history, List<Signal> signals) {
        this.signals = signals;
        keyframes = new ArrayList<>();
        keyframes.add(new Keyframe(0, startValues));

        for (Event event : history) {
            appendFrame(new Keyframe(keyframes.get(keyframes.size() - 1), event));
        }
    }

    private void appendFrame(Keyframe frame) {
        if (keyframes.get(keyframes.size() - 1).getTime() == frame.getTime()) {
            keyframes.remove(keyframes.size() - 1);
        }

        keyframes.add(frame);
    }

    public Keyframe lastFrame() {
        return keyframes.get(keyframes.size() - 1);
    }

    public List<Keyframe> getKeyframes() {
        return new ArrayList<>(keyframes);
    }

    public List<Signal> getSignals() {
        return new ArrayList<>(signals);
    }

    public Keyframe firstFrame() {
        return keyframes.get(0);
    }
}
