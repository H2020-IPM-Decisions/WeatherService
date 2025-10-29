package net.ipmdecisions.weather.datasourceadapters.v2.service;

import net.ipmdecisions.weather.datasourceadapters.v2.client.Client;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.DavisFruitResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.DavisFruitParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DavisFruitWeatherDataServiceTest {

    @Test
    void test_mapping_of_davisfruit_response() throws Exception {
        var txt = new String(
                getClass().getResourceAsStream("/fixtures/davisfruitforecast.txt").readAllBytes(),
                StandardCharsets.UTF_8
        );

        var metClient = mock(Client.class);
        var metParams = mock(ParamModel.class);
        var davisParamModel = new DavisFruitParamModel();
        Map<String, Object> params = Map.of(
                "latitude", 60.1695,
                "longitude", 24.9354
        );
        davisParamModel.initialize(params);

        when(metClient.supports("DavisFruit")).thenReturn(true);
        when(metParams.supports("DavisFruit")).thenReturn(true);

        when(metClient.getData(any(ParamModel.class))).thenReturn(new DavisFruitResponse(txt, davisParamModel));

        var service = new WeatherDataService(
                List.of(metClient),
                List.of(metParams)
        );

        // when
        var result = service.getWeatherData("DavisFruit", Map.of());

        // then
        var locationWeatherData = result.getLocationWeatherData().get(0);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTimeStart(), Instant.parse("2025-10-25T01:00:00Z")),
                () -> assertEquals(result.getTimeEnd(), Instant.parse("2025-10-28T14:00:00Z")),
                () -> assertEquals(result.getInterval(), Integer.valueOf(3600)),
                () -> assertEquals(locationWeatherData.getLongitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getLatitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getAltitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getData().length, 86)

        );

    }
}