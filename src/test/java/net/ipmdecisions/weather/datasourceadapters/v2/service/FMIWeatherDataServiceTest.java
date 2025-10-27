package net.ipmdecisions.weather.datasourceadapters.v2.service;

import net.ipmdecisions.weather.datasourceadapters.v2.client.Client;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.FMIResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.FMIParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class FMIWeatherDataServiceTest {

    @Test
    void test_mapping_of_yr_response() throws Exception {
        var xml = new String(
                getClass().getResourceAsStream("/fixtures/fmiforecast.xml").readAllBytes(),
                StandardCharsets.UTF_8
        );

        var metClient = mock(Client.class);
        var metParams = mock(ParamModel.class);
        var fmiParamModel = new FMIParamModel();
        Map<String, Object> params = Map.of(
                "latitude", 60.1695,
                "longitude", 24.9354
        );
        fmiParamModel.initialize(params);

        when(metClient.supports("FMI")).thenReturn(true);
        when(metParams.supports("FMI")).thenReturn(true);

        when(metClient.getData(any(ParamModel.class))).thenReturn(new FMIResponse(xml, fmiParamModel));

        var service = new WeatherDataService(
                List.of(metClient),
                List.of(metParams)
        );

        // when
        var result = service.getWeatherData("FMI", Map.of());

        // then
        var locationWeatherData = result.getLocationWeatherData().get(0);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTimeStart(), Instant.parse("2025-10-27T07:00:00Z")),
                () -> assertEquals(result.getTimeEnd(), Instant.parse("2025-10-29T11:00:00Z")),
                () -> assertEquals(result.getInterval(), Integer.valueOf(3600)),
                () -> assertEquals(locationWeatherData.getLongitude(), Double.valueOf(-7.644361)),
                () -> assertEquals(locationWeatherData.getLatitude(), Double.valueOf(52.597709)),
                () -> assertEquals(locationWeatherData.getAltitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getData().length, 53)

        );

    }
}