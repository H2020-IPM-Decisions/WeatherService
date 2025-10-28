package net.ipmdecisions.weather.datasourceadapters.v2.mapper.slu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.SLUResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.ParameterMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.VIPSWeatherObservationMapper;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;

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
        List<VIPSWeatherObservation> observations;
        try {
            observations = OBJECT_MAPPER.readValue(
                    sluResponse.data(),
                    new TypeReference<List<VIPSWeatherObservation>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (observations == null || observations.isEmpty()) {
            return null;
        }

        return VIPSWeatherObservationMapper.toWeatherData(
                observations,
                longitude,
                latitude,
                INTERVAL_SECONDS,
                0,
                ParameterMapper::getIPMParameterId,
                true
        );
    }


}
