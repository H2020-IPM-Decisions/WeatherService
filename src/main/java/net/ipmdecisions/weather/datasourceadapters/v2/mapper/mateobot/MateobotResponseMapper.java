package net.ipmdecisions.weather.datasourceadapters.v2.mapper.mateobot;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MateobotResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.ParameterMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.VIPSWeatherObservationMapper;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MateobotResponseMapper {

    private static final String[][] ELEMENT_MEASUREMENT_TYPES = {
            {"airTemperature", "TM", "AVG"},
            {"airPressure", "POM", "AVG"},
            {"airHumidity", "UM", "AVG"},
            {"dewPoint", "DUMMY", "AVG"},
            {"earthHumidity1", "EH1", "AVG"},
            {"earthHumidity2", "EH2", "AVG"},
            {"earthHumidity3", "EH3", "AVG"},
            {"earthTemperature1", "ET1", "AVG"},
            {"earthTemperature2", "ET2", "AVG"},
            {"earthTemperature3", "ET3", "AVG"},
            {"leafWetness1", "BT1", "SUM"},
            {"leafWetness2", "BT2", "SUM"},
            {"precipitation", "RR", "SUM"},
            {"windDirection", "DM2", "AVG"},
            {"windSpeed", "FM2", "AVG"},
            {"solarRadiation", "Q0", "AVG"},
            {"batteryVoltage", "BATTERY", "AVG"},
            {"solarPanelVoltage", "DUMMY", "AVG"},
            {"evapotranspiration", "DUMMY", "AVG"}
    };

    public static WeatherData toWeatherData(MateobotResponse mateobotResponse) {
        String raw = mateobotResponse.data();
        var params = mateobotResponse.params();

        List<VIPSWeatherObservation> observations = parseIndexData(raw);

        return VIPSWeatherObservationMapper.toWeatherData(
                observations,
                params.getLongitude(),
                params.getLatitude(),
                3600,
                0,
                ParameterMapper::getIPMParameterId,
                false
        );
    }

    private static List<VIPSWeatherObservation> parseIndexData(String raw) {
        if (raw == null || raw.isEmpty()) return Collections.emptyList();

        final SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Map<Integer, Integer> elementOrdering = new HashMap<>();

        List<String[]> dataRows = new ArrayList<>();

        String[] lines = raw.split("\\r?\\n");
        for (String line : lines) {
            if (line == null || line.isBlank()) continue;

            String[] lineData = line.split(";");
            if (lineData.length <= 1) continue;

            if ("time".equals(lineData[1])) {
                for (int i = 2; i < lineData.length; i++) {
                    String header = lineData[i];
                    for (int j = 0; j < ELEMENT_MEASUREMENT_TYPES.length; j++) {
                        if (ELEMENT_MEASUREMENT_TYPES[j][0].equals(header)) {
                            elementOrdering.put(i, j);
                            break;
                        }
                    }
                }
                continue;
            }

            try {
                dFormat.parse(lineData[1]);
                dataRows.add(lineData);
            } catch (ParseException ignore) {
            }
        }

        List<VIPSWeatherObservation> ret = new ArrayList<>();

        for (String[] row : dataRows) {
            Date ts;
            try {
                ts = dFormat.parse(row[1]);
            } catch (ParseException e) {
                throw new RuntimeException("Error with time stamp in weather data from Meteobot station: " + e.getMessage(), e);
            }

            String[] tsParts = row[1].split(":");
            if (tsParts.length < 2 || !"00".equals(tsParts[1])) {
                continue;
            }

            for (int i = 2; i < row.length; i++) {
                Integer typeIdx = elementOrdering.get(i);
                if (typeIdx == null) continue;
                String v = row[i];
                if (v == null || v.isEmpty()) continue;

                Double value;
                try {
                    value = Double.valueOf(v);
                } catch (NumberFormatException ignore) {
                    continue;
                }

                VIPSWeatherObservation obs = new VIPSWeatherObservation();
                obs.setTimeMeasured(ts);
                obs.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                obs.setElementMeasurementTypeId(ELEMENT_MEASUREMENT_TYPES[typeIdx][1]);
                obs.setValue(value);
                ret.add(obs);
            }
        }
        return ret;
    }
}