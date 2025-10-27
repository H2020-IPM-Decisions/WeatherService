package net.ipmdecisions.weather.datasourceadapters.v2.mapper.fmi;

import java.util.List;

/**
 * Holds parsed XML data: parameter names, timestamps, and aligned values.
 */
public class FMIParsingResult {
    private final List<String> parameterNames;
    private final List<Long> timestamps;
    private final double[][] values;

    public FMIParsingResult(List<String> parameterNames, List<Long> timestamps, double[][] values) {
        this.parameterNames = parameterNames;
        this.timestamps = timestamps;
        this.values = values;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public double[][] getValues() {
        return values;
    }
}
