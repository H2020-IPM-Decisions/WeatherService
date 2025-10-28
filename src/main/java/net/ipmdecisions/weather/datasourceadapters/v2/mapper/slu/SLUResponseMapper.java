package net.ipmdecisions.weather.datasourceadapters.v2.mapper.slu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.SLUResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.ParameterMapper;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class SLUResponseMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int INTERVAL_SECONDS = 3600;

    public static WeatherData toWeatherData(SLUResponse sluResponse) {
        if (sluResponse == null || sluResponse.data() == null || sluResponse.params() == null) {
            return null;
        }

        double longitude = sluResponse.params().getLongitude();
        double latitude = sluResponse.params().getLatitude();
        List<VIPSWeatherObservation> observations = null;
        try {
            observations = OBJECT_MAPPER.readValue(
                    sluResponse.data(),
                    new TypeReference<List<VIPSWeatherObservation>>() {}
            );
            return getWeatherDataFromVIPSWeatherObservations(observations, longitude, latitude, 0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static WeatherData getWeatherDataFromVIPSWeatherObservations(List<VIPSWeatherObservation> observations,
                                                                         Double longitude,
                                                                         Double latitude,
                                                                         Integer defaultQC) {
        if (observations == null || observations.isEmpty()) {
            return null;
        }

        observations.sort(Comparator.comparing(o -> o.getTimeMeasured().toInstant()));

        Integer[] parameters = observations.stream()
                .map(obs -> ParameterMapper.getIPMParameterId(obs.getElementMeasurementTypeId()))
                .filter(Objects::nonNull)
                .distinct()
                .sorted() // deterministic order
                .toArray(Integer[]::new);

        Map<Integer,Integer> paramIndexById = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            paramIndexById.put(parameters[i], i);
        }

        Instant timeStart = observations.get(0).getTimeMeasured().toInstant();
        Instant timeEnd = observations.get(observations.size() - 1).getTimeMeasured().toInstant();
        long rows = 1 + timeStart.until(timeEnd, ChronoUnit.SECONDS) / INTERVAL_SECONDS;

        LocationWeatherData locationData = new LocationWeatherData(
                longitude,
                latitude,
                0.0,
                (int) rows,
                parameters.length
        );

        WeatherData weatherData = new WeatherData();
        weatherData.setInterval(INTERVAL_SECONDS);
        weatherData.setTimeStart(timeStart);
        weatherData.setTimeEnd(timeEnd);
        weatherData.setWeatherParameters(parameters);

        Integer[] qcPerParam = new Integer[parameters.length];
        Arrays.fill(qcPerParam, defaultQC);
        locationData.setQC(qcPerParam);

        observations.stream()
                .map(obs -> new AbstractMap.SimpleEntry<>(ParameterMapper.getIPMParameterId(obs.getElementMeasurementTypeId()), obs))
                .filter(e -> e.getKey() != null)
                .forEach(e -> {
                    long row = timeStart.until(e.getValue().getTimeMeasured().toInstant(), ChronoUnit.SECONDS) / INTERVAL_SECONDS;
                    Integer col = paramIndexById.get(e.getKey());
                    if (col != null) {
                        locationData.setValue((int) row, col, e.getValue().getValue());
                    }
                });

        weatherData.addLocationWeatherData(locationData);
        return weatherData;
    }


}
