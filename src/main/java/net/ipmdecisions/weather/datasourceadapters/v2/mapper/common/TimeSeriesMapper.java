package net.ipmdecisions.weather.datasourceadapters.v2.mapper.common;

import net.ipmdecisions.weather.entity.LocationWeatherData;

/**
 * Interpolates specified columns and fills null precipitation with 0.0.
 */
public final class TimeSeriesMapper {

    private TimeSeriesMapper() {}

    public static LocationWeatherData finalizeHourly(LocationWeatherData data,
                                                     int precipitationColumn,
                                                     int... interpolateCols) {
        for (int col : interpolateCols) {
            interpolateColumn(data, col);
        }
        fillNullPrecipitation(data, precipitationColumn);
        return data;
    }

    private static void fillNullPrecipitation(LocationWeatherData data, int col) {
        for (int r = 0; r < data.getLength(); r++) {
            if (data.getValue(r, col) == null) {
                data.setValue(r, col, 0.0);
            }
        }
    }

    private static void interpolateColumn(LocationWeatherData data, int col) {
        int n = data.getLength();
        int i = 0;
        while (i < n) {
            while (i < n && data.getValue(i, col) != null) {
                i++;
            }
            if (i >= n) break;

            int startHole = i - 1;
            while (i < n && data.getValue(i, col) == null) {
                i++;
            }
            int endHole = i;

            Double before = (startHole >= 0) ? data.getValue(startHole, col) : null;
            Double after = (endHole < n) ? data.getValue(endHole, col) : null;

            if (before == null || after == null) {
                continue;
            }
            int span = endHole - startHole;
            double step = (after - before) / span;
            for (int r = startHole + 1; r < endHole; r++) {
                data.setValue(r, col, before + (r - startHole) * step);
            }
        }
    }
}
