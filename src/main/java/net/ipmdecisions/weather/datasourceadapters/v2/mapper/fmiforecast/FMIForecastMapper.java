package net.ipmdecisions.weather.datasourceadapters.v2.mapper.fmiforecast;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.FMIForecastResponse;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class FMIForecastMapper {

    private static final int INTERVAL_SECONDS = 3600;
    private static final int DEFAULT_QC = 1;

    private FMIForecastMapper() {}

    public static WeatherData toWeatherData(FMIForecastResponse fmiForecastResponse) {

        FMIForecastParsingResult parsed = FMIForecastXmlExtractor.extract(fmiForecastResponse.data());
        List<VIPSWeatherObservation> observations = buildObservations(parsed);

        return buildWeatherData(observations,
                fmiForecastResponse.params().getLongitude(),
                fmiForecastResponse.params().getLatitude(),
                DEFAULT_QC);
    }

    private static List<VIPSWeatherObservation> buildObservations(FMIForecastParsingResult parsed) {
        List<VIPSWeatherObservation> list = new ArrayList<>();
        List<String> parameterNames = parsed.getParameterNames();
        List<Long> timestamps = parsed.getTimestamps();
        double[][] values = parsed.getValues();

        List<String> vipsCodes = parameterNames.stream()
                .map(FMIForecastParameterMapper::mapToVipsCode)
                .collect(Collectors.toList());

        for (int t = 0; t < timestamps.size(); t++) {
            long epoch = timestamps.get(t);
            Date measured = Date.from(Instant.ofEpochSecond(epoch));
            double[] row = values[t];
            for (int p = 0; p < parameterNames.size(); p++) {
                String vipsCode = vipsCodes.get(p);
                VIPSWeatherObservation o = new VIPSWeatherObservation();
                o.setElementMeasurementTypeId(vipsCode);
                o.setValue(row[p]);
                o.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                o.setTimeMeasured(measured);
                list.add(o);
            }
        }
        Collections.sort(list);
        return list;
    }

    private static WeatherData buildWeatherData(List<VIPSWeatherObservation> observations,
                                                Double longitude,
                                                Double latitude,
                                                Integer defaultQC) {

        Integer[] parameters = observations.stream()
                .map(VIPSWeatherObservation::getElementMeasurementTypeId)
                .map(FMIForecastParameterMapper::getIPMParameterId)
                .filter(Objects::nonNull)
                .distinct()
                .toArray(Integer[]::new);

        Map<Integer, Integer> paramIndex = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            paramIndex.put(parameters[i], i);
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

        Integer[] qcPerParam = new Integer[parameters.length];
        Arrays.fill(qcPerParam, defaultQC);

        for (VIPSWeatherObservation obs : observations) {
            Integer ipmId = FMIForecastParameterMapper.getIPMParameterId(obs.getElementMeasurementTypeId());
            if (ipmId == null) continue;
            long row = timeStart.until(obs.getTimeMeasured().toInstant(), ChronoUnit.SECONDS) / INTERVAL_SECONDS;
            Integer col = paramIndex.get(ipmId);
            if (col != null && row >= 0 && row < rows) {
                locationData.setValue((int) row, col, obs.getValue());
            }
        }
        locationData.setQC(qcPerParam);

        WeatherData wd = new WeatherData();
        wd.setInterval(INTERVAL_SECONDS);
        wd.setTimeStart(timeStart);
        wd.setTimeEnd(timeEnd);
        wd.setWeatherParameters(parameters);
        wd.addLocationWeatherData(locationData);
        return wd;
    }
}
