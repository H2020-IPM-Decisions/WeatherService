package net.ipmdecisions.weather.datasourceadapters.v2.mapper.davisfruit;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.DavisFruitResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.VIPSWeatherObservationMapper;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DavisFruitMapper {

    private static final String[][] ELEMENT_MEASUREMENT_TYPES = {
            {"RAIN","RR","SUM"},
            {"LW1","BT","SUM"},
            {"AIRTEMP","TM","AVG"},
            {"AIRHUM","UM","AVG"}
    };

    private static final Map<String,Integer> VIPS_TO_IPM = Map.of(
            "TM",1002,
            "RR",2001,
            "UM",3002,
            "Q0",5001,
            "BT",3101,
            "FF2",4002,
            "FM2",4003,
            "DP",1901
    );

    private static final int INTERVAL_SECONDS = 3600;
    private static final int DEFAULT_QC = 0;

    public static WeatherData toWeatherData(DavisFruitResponse davisFruitResponse) {

        var params = davisFruitResponse.params();
        var timeZone = params.getTimeZone() == null ? TimeZone.getTimeZone("UTC") : params.getTimeZone();
        var startDate = params.getStartDate();
        var endDate = params.getEndDate();

        List<VIPSWeatherObservation> observations = parseToObservations(
                davisFruitResponse.data(),
                timeZone,
                startDate,
                endDate
        );

        if (observations == null || observations.isEmpty()) {
            return null;
        }

        return VIPSWeatherObservationMapper.toWeatherData(
                observations,
                0.0,
                0.0,
                INTERVAL_SECONDS,
                DEFAULT_QC,
                DavisFruitMapper::mapVipsToIpm,
                false
        );
    }

    private static Integer mapVipsToIpm(String vipsId) {
        return VIPS_TO_IPM.get(vipsId);
    }

    private static List<VIPSWeatherObservation> parseToObservations(String raw,
                                                                    TimeZone timeZone,
                                                                    Instant startDate,
                                                                    Instant endDate) {

        SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dFormat.setTimeZone(timeZone);

        Integer logIntervalId = VIPSWeatherObservation.LOG_INTERVAL_ID_1H;
        List<String[]> dataLines = new ArrayList<>();
        Map<Integer, Integer> elementOrdering = new HashMap<>();

        String[] lines = raw.split("\\r?\\n");
        for (String line : lines) {
            if (line == null || line.isBlank()) continue;
            String[] parts = line.split(";");
            if (parts.length <= 1) continue;

            try {
                dFormat.parse(parts[0] + " " + parts[1]);
                dataLines.add(parts);
                if (parts[1].contains(":30")) {
                    logIntervalId = VIPSWeatherObservation.LOG_INTERVAL_ID_30M;
                }
            } catch (ParseException e) {
                if ("DATE".equals(parts[0])) {
                    for (int i = 2; i < parts.length; i++) {
                        for (int j = 0; j < ELEMENT_MEASUREMENT_TYPES.length; j++) {
                            if (ELEMENT_MEASUREMENT_TYPES[j][0].equals(parts[i])) {
                                elementOrdering.put(i, j);
                            }
                        }
                    }
                }
            }
        }

        if (dataLines.isEmpty()) {
            return Collections.emptyList();
        }

        List<VIPSWeatherObservation> retVal = new ArrayList<>();

        if (logIntervalId.equals(VIPSWeatherObservation.LOG_INTERVAL_ID_30M)) {
            boolean expect30 = true;
            String[] data30 = null;
            String[] data00;
            Date timestamp;

            for (String[] lineData : dataLines) {
                String minute = extractMinute(lineData);
                if (minute == null) continue;
                if (!minute.equals("00") && !minute.equals("30")) continue;

                if (minute.equals("30") && expect30) {
                    data30 = lineData;
                    expect30 = false;
                    continue;
                } else if (minute.equals("00") && !expect30) {
                    data00 = lineData;
                    try {
                        timestamp = dFormat.parse(lineData[0] + " " + lineData[1]);
                    } catch (ParseException ex) {
                        throw new RuntimeException("Error with time stamp in weather data from Davis/FruitWeb station: " + ex.getMessage(), ex);
                    }
                    expect30 = true;
                } else {
                    throw new RuntimeException("Doesn't make sense at " + lineData[0] + " " + lineData[1] + "!");
                }

                for (int i = 2; i < data30.length; i++) {
                    Integer elementIdx = elementOrdering.get(i);
                    if (elementIdx == null) continue;

                    Double value00 = parseDoubleOrNullSafe(data00[i]);
                    Double value30 = parseDoubleOrNullSafe(data30[i]);
                    boolean atLeastOneMissing = (value00 == null || value30 == null);

                    Double aggregateValue;
                    if (value00 == null && value30 == null) {
                        aggregateValue = null;
                    } else {
                        double v00 = value00 == null ? 0.0 : value00;
                        double v30 = value30 == null ? 0.0 : value30;
                        if ("AVG".equals(ELEMENT_MEASUREMENT_TYPES[elementIdx][2])) {
                            aggregateValue = (v00 + v30) / (atLeastOneMissing ? 1 : 2);
                        } else {
                            aggregateValue = v00 + v30;
                        }
                    }

                    VIPSWeatherObservation obs = new VIPSWeatherObservation();
                    obs.setTimeMeasured(timestamp);
                    obs.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                    obs.setElementMeasurementTypeId(ELEMENT_MEASUREMENT_TYPES[elementIdx][1]);
                    obs.setValue(aggregateValue);
                    retVal.add(obs);
                }

                data30 = null;
            }
        } else {
            for (String[] lineData : dataLines) {
                String minute = extractMinute(lineData);
                if (minute == null || !minute.equals("00")) continue;

                Date timestamp;
                try {
                    timestamp = dFormat.parse(lineData[0] + " " + lineData[1]);
                } catch (ParseException ex) {
                    throw new RuntimeException("Error with time stamp in weather data from Davis/FruitWeb station: " + ex.getMessage(), ex);
                }

                for (int i = 2; i < lineData.length; i++) {
                    Integer elementIdx = elementOrdering.get(i);
                    if (elementIdx == null) continue;

                    Double value = parseDoubleOrNullSafe(lineData[i]);

                    VIPSWeatherObservation obs = new VIPSWeatherObservation();
                    obs.setTimeMeasured(timestamp);
                    obs.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                    obs.setElementMeasurementTypeId(ELEMENT_MEASUREMENT_TYPES[elementIdx][1]);
                    obs.setValue(value);
                    retVal.add(obs);
                }
            }
        }

        return retVal.stream()
                .filter(obs ->
                        (startDate == null || obs.getTimeMeasured().compareTo(Date.from(startDate)) >= 0) &&
                                (endDate == null || obs.getTimeMeasured().compareTo(Date.from(endDate)) <= 0)
                )
                .collect(Collectors.toList());
    }

    private static String extractMinute(String[] lineData) {
        if (lineData.length < 2) return null;
        String[] timeParts = lineData[1].split(":");
        if (timeParts.length < 2) return null;
        return timeParts[1];
    }

    private static Double parseDoubleOrNullSafe(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Double.valueOf(s.replace(',', '.'));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
