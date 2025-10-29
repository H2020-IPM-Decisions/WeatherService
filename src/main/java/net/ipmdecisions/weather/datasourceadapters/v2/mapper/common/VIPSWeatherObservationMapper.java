package net.ipmdecisions.weather.datasourceadapters.v2.mapper.common;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

/**
 * Shared utility for constructing WeatherData objects from a list of VIPSWeatherObservation.
 * Keeps logic for: parameter extraction/mapping, matrix sizing, QC assignment and value placement.
 */
public final class VIPSWeatherObservationMapper {

    private VIPSWeatherObservationMapper() {}

    /**
     * Assemble WeatherData from observations.
     *
     * @param observations      list of source observations (may be unsorted)
     * @param longitude         location longitude
     * @param latitude          location latitude
     * @param intervalSeconds   interval resolution in seconds (e.g. 3600)
     * @param defaultQC         QC value to assign to all parameters
     * @param paramIdMapper     function mapping external parameter code to internal integer id (returns null if unmapped)
     * @param sortParameters    if true, sort resulting parameter ids ascending for deterministic order
     * @return WeatherData (empty instance if observations list empty or no mapped params)
     */
    public static WeatherData toWeatherData(List<VIPSWeatherObservation> observations,
                                            Double longitude,
                                            Double latitude,
                                            int intervalSeconds,
                                            int defaultQC,
                                            Function<String, Integer> paramIdMapper,
                                            boolean sortParameters) {
        WeatherData wd = new WeatherData();
        if (observations == null || observations.isEmpty()) {
            return wd; // return empty data structure
        }

        observations.sort(Comparator.comparing(o -> o.getTimeMeasured().toInstant()));

        LinkedHashSet<Integer> paramSet = new LinkedHashSet<>();
        for (VIPSWeatherObservation obs : observations) {
            Integer id = paramIdMapper.apply(obs.getElementMeasurementTypeId());
            if (id != null) {
                paramSet.add(id);
            }
        }
        if (paramSet.isEmpty()) {
            return wd;
        }

        Integer[] parameters = paramSet.toArray(new Integer[0]);
        if (sortParameters) {
            Arrays.sort(parameters);
        }

        Map<Integer, Integer> paramIndex = new HashMap<>(parameters.length * 2);
        for (int i = 0; i < parameters.length; i++) {
            paramIndex.put(parameters[i], i);
        }

        Instant timeStart = observations.get(0).getTimeMeasured().toInstant();
        Instant timeEnd = observations.get(observations.size() - 1).getTimeMeasured().toInstant();
        long rows = 1 + Math.max(0, timeStart.until(timeEnd, ChronoUnit.SECONDS)) / intervalSeconds;

        LocationWeatherData locationData = new LocationWeatherData(
                longitude,
                latitude,
                0.0,
                (int) rows,
                parameters.length
        );

        Integer[] qcPerParam = new Integer[parameters.length];
        Arrays.fill(qcPerParam, defaultQC);
        locationData.setQC(qcPerParam);

        long baseEpoch = timeStart.getEpochSecond();
        for (VIPSWeatherObservation obs : observations) {
            Integer ipmId = paramIdMapper.apply(obs.getElementMeasurementTypeId());
            if (ipmId == null) continue;
            long secondsDelta = obs.getTimeMeasured().toInstant().getEpochSecond() - baseEpoch;
            long row = secondsDelta / intervalSeconds;
            Integer col = paramIndex.get(ipmId);
            if (col != null && row >= 0 && row < rows) {
                locationData.setValue((int) row, col, obs.getValue());
            }
        }

        wd.setInterval(intervalSeconds);
        wd.setTimeStart(timeStart);
        wd.setTimeEnd(timeEnd);
        wd.setWeatherParameters(parameters);
        wd.addLocationWeatherData(locationData);
        return wd;
    }
}
