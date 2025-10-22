package net.ipmdecisions.weather.datasourceadapters.v2.mapper.metireland;

import net.ipmdecisions.weather.entity.LocationWeatherData;

public class MetIrelandTimeSeriesMapper {
    static LocationWeatherData finalizeHourly(LocationWeatherData values) {
        Integer[] columnsToInterpolate = {0, 1, 3};
        for (Integer col : columnsToInterpolate) {
            values = interpolateColumn(values, col);
        }
        for (int i = 0; i < values.getLength(); i++) {
            if (values.getValue(i, 2) == null) {
                values.setValue(i, 2, 0.0);
            }
        }
        return values;
    }

    private static LocationWeatherData interpolateColumn(LocationWeatherData values, Integer column) {
        for (Integer i = 0; i < values.getLength(); i++) {
            if (values.getValue(i, column) == null) {
                Integer lastRowWithValueBeforeHole = null;
                Integer firstRowWithValueAfterHole = null;
                Double lastValueBeforeHole = null;
                Double firstValueAfterHole = null;

                while (lastValueBeforeHole == null && i >= 0) {
                    i--;
                    if (values.getValue(i, column) != null) {
                        lastRowWithValueBeforeHole = i;
                        lastValueBeforeHole = values.getValue(i, column);
                    }
                }
                while (firstValueAfterHole == null && i <= values.getLength()) {
                    i++;
                    if (values.getValue(i, column) != null) {
                        firstRowWithValueAfterHole = i;
                        firstValueAfterHole = values.getValue(i, column);
                    }
                }
                Double step = (firstValueAfterHole - lastValueBeforeHole) /
                        (firstRowWithValueAfterHole - lastRowWithValueBeforeHole);
                for (i = lastRowWithValueBeforeHole + 1; i < firstRowWithValueAfterHole; i++) {
                    values.setValue(i, column,
                            lastValueBeforeHole + ((i - lastRowWithValueBeforeHole) * step));
                }
            }
        }
        return values;
    }
}
