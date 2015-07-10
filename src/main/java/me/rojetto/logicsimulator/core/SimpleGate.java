package me.rojetto.logicsimulator.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstrakte Superklasse für alle Gatter mit einem Ausgang mit Namen "o" und beliebig vielen Eingängen mit Namen "i1",
 * "i2" etc., deren Reihenfolge keine Rolle spielt
 */
public abstract class SimpleGate extends Gate {
    public SimpleGate(int numberOfInputs, int delay, String name) {
        super(numberOfInputs, delay, name);
    }

    /**
     * Konkrete Implementierung, die alle Eingangswerte in ein Array zusammenfasst und zur Berechnung weitergibt,
     * sowie Ergebnis in Ausgang "o" einträgt
     */
    @Override
    protected Map<String, Boolean> calculateOutput(Map<String, Boolean> inputValues) {
        Map<String, Boolean> outputMap = new HashMap<>();
        boolean[] inputArray = new boolean[inputValues.size()];

        int counter = 0;
        for (boolean value : inputValues.values()) {
            inputArray[counter] = value;
            counter++;
        }

        outputMap.put("o", calculateOutput(inputArray));

        return outputMap;
    }

    @Override
    protected String[] inputNames() {
        return new String[]{"i\\d+"};
    }

    @Override
    protected String[] outputNames() {
        return new String[]{"o"};
    }

    /**
     * Berechnet Ausgangswert aus Eingangswerten beliebiger Reihenfolge
     *
     * @param inputValues Eingangswerte
     * @return Ausgangswert
     */
    protected abstract boolean calculateOutput(boolean[] inputValues);
}
