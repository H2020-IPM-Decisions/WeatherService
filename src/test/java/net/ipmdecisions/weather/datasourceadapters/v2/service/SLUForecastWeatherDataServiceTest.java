package net.ipmdecisions.weather.datasourceadapters.v2.service;

import net.ipmdecisions.weather.datasourceadapters.v2.client.Client;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.SLUResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.SLUParamModel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class SLUForecastWeatherDataServiceTest {

    @Test
    void test_mapping_of_slu_response() throws Exception {
        var json = new String(
                getClass().getResourceAsStream("/fixtures/sluforecast.json").readAllBytes(),
                StandardCharsets.UTF_8
        );

        var metClient = mock(Client.class);
        var metParams = mock(ParamModel.class);
        var sluParamModel = new SLUParamModel();
        Map<String, Object> params = Map.of(
                "latitude", 60.1695,
                "longitude", 24.9354
        );
        sluParamModel.initialize(params);

        when(metClient.supports("SLU")).thenReturn(true);
        when(metParams.supports("SLU")).thenReturn(true);

        when(metClient.getData(any(ParamModel.class))).thenReturn(new SLUResponse(json, sluParamModel));

        var service = new WeatherDataService(
                List.of(metClient),
                List.of(metParams)
        );

        // when
        var result = service.getWeatherData("SLU", Map.of());

        // then
        var locationWeatherData = result.getLocationWeatherData().get(0);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTimeStart(), Instant.parse("2025-10-21T22:00:00Z")),
                () -> assertEquals(result.getTimeEnd(), Instant.parse("2025-10-27T22:00:00Z")),
                () -> assertEquals(result.getInterval(), Integer.valueOf(3600)),
                () -> assertEquals(locationWeatherData.getLongitude(), Double.valueOf(24.9354)),
                () -> assertEquals(locationWeatherData.getLatitude(), Double.valueOf(60.1695)),
                () -> assertEquals(locationWeatherData.getAltitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getData().length, 145)

        );

    }
}