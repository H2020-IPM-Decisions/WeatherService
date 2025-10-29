package net.ipmdecisions.weather.datasourceadapters.v2.mapper.metos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetosResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.ParameterMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.VIPSWeatherObservationMapper;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;
import net.ipmdecisions.weather.util.vips.WeatherElements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

public class MetosResponseMapper {

    private static final int INTERVAL_SECONDS = 3600;
    private static final int DEFAULT_QC = 0;

    private static final List<ParamInfo> PARAM_MAP = List.of(
            new ParamInfo(WeatherElements.PRECIPITATION,            new Integer[]{6},               "sum"),
            new ParamInfo(WeatherElements.LEAF_WETNESS_DURATION,    new Integer[]{4},               "time"),
            new ParamInfo(WeatherElements.GLOBAL_RADIATION,         new Integer[]{600},             "avg"),
            new ParamInfo(WeatherElements.WIND_SPEED_2M,            new Integer[]{5},               "avg"),
            new ParamInfo(WeatherElements.TEMPERATURE_MEAN,         new Integer[]{0,16385,506},     "avg"),
            new ParamInfo(WeatherElements.TEMPERATURE_MAXIMUM,      new Integer[]{16385,506},       "max"),
            new ParamInfo(WeatherElements.TEMPERATURE_MINIMUM,      new Integer[]{16385,506},       "min"),
            new ParamInfo(WeatherElements.RELATIVE_HUMIDITY_MEAN,   new Integer[]{1,507,21778},     "avg")
    );

    public static WeatherData toWeatherData(MetosResponse resp) {
        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode station = om.readTree(resp.stationInfoJson());

            int minutesOffset = station.get("config").get("timezone_offset").asInt();
            String timezoneCode = station.get("position").get("timezoneCode").asText();

            List<Double> coords = new ArrayList<>();
            station.get("position").get("geo").get("coordinates").iterator().forEachRemaining(n -> coords.add(n.asDouble()));
            double longitude = coords.get(0);
            double latitude = coords.get(1);

            TimeZone dataTimeZone = minutesOffset / 60 >= 0
                    ? TimeZone.getTimeZone("GMT+" + (minutesOffset / 60))
                    : TimeZone.getTimeZone("GMT" + (minutesOffset / 60));

            ZoneId locationZone = ZoneId.of(timezoneCode);
            Date startDateAtLocation = Date.from(resp.params().getStartDate().atStartOfDay(locationZone).toInstant());

            List<VIPSWeatherObservation> observations = parseObservations(resp.dataJson(), dataTimeZone, startDateAtLocation);

            return VIPSWeatherObservationMapper.toWeatherData(
                    observations,
                    longitude,
                    latitude,
                    INTERVAL_SECONDS,
                    DEFAULT_QC,
                    ParameterMapper::getIPMParameterId,
                    true
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<VIPSWeatherObservation> parseObservations(String jsonTxt, TimeZone timeZone, Date startDate)
            throws ParseException, com.fasterxml.jackson.core.JsonProcessingException {
        ObjectMapper oMapper = new ObjectMapper();
        JsonNode jNode = oMapper.readTree(jsonTxt);

        List<VIPSWeatherObservation> retVal = new ArrayList<>();
        JsonNode dates = jNode.get("dates");
        JsonNode data = jNode.get("data");
        if (data == null || dates == null) {
            return retVal;
        }

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dFormat.setTimeZone(timeZone);

        for (ParamInfo paramInfo : PARAM_MAP) {
            boolean foundParam = false;
            for (Integer code : paramInfo.preferredCodes) {
                for (JsonNode aData : data) {
                    if (aData.get("code").asInt() != code) {
                        continue;
                    }
                    JsonNode aggrNode = aData.get("aggr").get(paramInfo.aggregationType);
                    if (aggrNode == null || !aggrNode.isArray()) {
                        continue;
                    }
                    for (int i = 0; i < dates.size(); i++) {
                        Date timeMeasured = dFormat.parse(dates.get(i).asText());
                        if (timeMeasured.before(startDate)) {
                            continue;
                        }
                        VIPSWeatherObservation obs = new VIPSWeatherObservation();
                        obs.setTimeMeasured(timeMeasured);
                        obs.setElementMeasurementTypeId(paramInfo.VIPSCode);
                        obs.setValue(aggrNode.get(i).asDouble());
                        obs.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                        retVal.add(obs);
                    }
                    foundParam = true;
                    break;
                }
                if (foundParam) break;
            }
        }
        return retVal;
    }

    private static final class ParamInfo {
        final String VIPSCode;
        final Integer[] preferredCodes;
        final String aggregationType;
        ParamInfo(String VIPSCode, Integer[] preferredCodes, String aggregationType) {
            this.VIPSCode = VIPSCode;
            this.preferredCodes = preferredCodes;
            this.aggregationType = aggregationType;
        }
    }
}