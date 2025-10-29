package net.ipmdecisions.weather.datasourceadapters.v2.service;

import net.ipmdecisions.weather.datasourceadapters.v2.client.Client;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetosResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.MetosParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class MetosWeatherDataServiceTest {

    @Test
    void test_mapping_of_metos_response() throws Exception {
        var metosStationInfo = new String(
                getClass().getResourceAsStream("/fixtures/metosStationInfo.json").readAllBytes(),
                StandardCharsets.UTF_8
        );
        var metosData = new String(
                getClass().getResourceAsStream("/fixtures/metosData.json").readAllBytes(),
                StandardCharsets.UTF_8
        );

        var metClient = mock(Client.class);
        var metParams = mock(ParamModel.class);
        var metosParamModel = new MetosParamModel();
        Map<String, Object> params = Map.of(
                "latitude", 60.1695,
                "longitude", 24.9354,
                "startDate", LocalDate.parse("2025-09-27")
        );
        metosParamModel.initialize(params);

        when(metClient.supports("metos")).thenReturn(true);
        when(metParams.supports("metos")).thenReturn(true);

        when(metClient.getData(any(ParamModel.class))).thenReturn(new MetosResponse(metosStationInfo, metosData, metosParamModel));

        var service = new WeatherDataService(
                List.of(metClient),
                List.of(metParams)
        );

        // when
        var result = service.getWeatherData("metos", Map.of());

        // then
        var locationWeatherData = result.getLocationWeatherData().get(0);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTimeStart(), Instant.parse("2025-09-26T21:00:00Z")),
                () -> assertEquals(result.getTimeEnd(), Instant.parse("2025-10-26T12:00:00Z")),
                () -> assertEquals(result.getInterval(), Integer.valueOf(3600)),
                () -> assertEquals(locationWeatherData.getLongitude(), Double.valueOf(23.8627337)),
                () -> assertEquals(locationWeatherData.getLatitude(), Double.valueOf(55.3960648)),
                () -> assertEquals(locationWeatherData.getAltitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getData().length, 712)

        );

    }
}