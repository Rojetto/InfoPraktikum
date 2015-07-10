package me.rojetto.logicsimulator.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulationResult {
    private final List<SignalState> signalStates;
    private final List<Signal> signals;

    public SimulationResult(Map<Signal, Boolean> startValues, List<Event> history, List<Signal> signals) {
        this.signals = signals;
        signalStates = new ArrayList<>();
        signalStates.add(new SignalState(0, startValues));

        for (Event event : history) {
            appendFrame(new SignalState(signalStates.get(signalStates.size() - 1), event));
        }
    }

    private void appendFrame(SignalState frame) {
        if (signalStates.get(signalStates.size() - 1).getTime() == frame.getTime()) {
            signalStates.remove(signalStates.size() - 1);
        }

        signalStates.add(frame);
    }

    public SignalState lastFrame() {
        return signalStates.get(signalStates.size() - 1);
    }

    public List<SignalState> getSignalStates() {
        return new ArrayList<>(signalStates);
    }

    public List<Signal> getSignals() {
        return new ArrayList<>(signals);
    }

    public SignalState firstFrame() {
        return signalStates.get(0);
    }
}
