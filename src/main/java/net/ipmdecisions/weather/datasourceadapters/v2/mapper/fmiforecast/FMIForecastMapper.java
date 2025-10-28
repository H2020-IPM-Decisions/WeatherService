package net.ipmdecisions.weather.datasourceadapters.v2.mapper.fmiforecast;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.FMIForecastResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.VIPSWeatherObservationMapper;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;

import java.time.Instant;
import java.util.*;

public class FMIForecastMapper {

    private static final int INTERVAL_SECONDS = 3600;
    private static final int DEFAULT_QC = 1;

    private FMIForecastMapper() {}

    public static WeatherData toWeatherData(FMIForecastResponse fmiForecastResponse) {
        FMIForecastParsingResult parsed = FMIForecastXmlExtractor.extract(fmiForecastResponse.data());
        List<VIPSWeatherObservation> observations = buildObservations(parsed);
        return VIPSWeatherObservationMapper.toWeatherData(
                observations,
                fmiForecastResponse.params().getLongitude(),
                fmiForecastResponse.params().getLatitude(),
                INTERVAL_SECONDS,
                DEFAULT_QC,
                FMIForecastParameterMapper::getIPMParameterId,
                true
        );
    }

    private static List<VIPSWeatherObservation> buildObservations(FMIForecastParsingResult parsed) {
        List<VIPSWeatherObservation> list = new ArrayList<>();
        List<String> parameterNames = parsed.getParameterNames();
        List<Long> timestamps = parsed.getTimestamps();
        double[][] values = parsed.getValues();

        List<String> vipsCodes = parameterNames.stream()
                .map(FMIForecastParameterMapper::mapToVipsCode)
                .toList();

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
}
