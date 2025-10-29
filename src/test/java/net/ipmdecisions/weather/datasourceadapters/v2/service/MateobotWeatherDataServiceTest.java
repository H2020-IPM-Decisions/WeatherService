package net.ipmdecisions.weather.datasourceadapters.v2.service;

import net.ipmdecisions.weather.datasourceadapters.v2.client.Client;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MateobotResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.MateobotParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class MateobotWeatherDataServiceTest {

    @Test
    void test_mapping_of_mateobot_response() throws Exception {
        //given
        var txt = new String(
                getClass().getResourceAsStream("/fixtures/mateobot.txt").readAllBytes(),
                StandardCharsets.UTF_8
        );

        var metClient = mock(Client.class);
        var metParams = mock(ParamModel.class);
        var mateobotParamModel = new MateobotParamModel();
        Map<String, Object> params = Map.of(
                "latitude", 60.1695,
                "longitude", 24.9354
        );
        mateobotParamModel.initialize(params);
        mateobotParamModel.setLatitude(60.1695);
        mateobotParamModel.setLongitude(24.9354);

        when(metClient.supports("mateobot")).thenReturn(true);
        when(metParams.supports("mateobot")).thenReturn(true);

        when(metClient.getData(any(ParamModel.class))).thenReturn(new MateobotResponse(txt, mateobotParamModel));

        var service = new WeatherDataService(
                List.of(metClient),
                List.of(metParams)
        );

        // when
        var result = service.getWeatherData("mateobot", Map.of());

        // then
        var locationWeatherData = result.getLocationWeatherData().get(0);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTimeStart(), Instant.parse("2025-10-24T22:00:00Z")),
                () -> assertEquals(result.getTimeEnd(), Instant.parse("2025-10-27T23:00:00Z")),
                () -> assertEquals(result.getInterval(), Integer.valueOf(3600)),
                () -> assertEquals(locationWeatherData.getLongitude(), Double.valueOf(24.9354)),
                () -> assertEquals(locationWeatherData.getLatitude(), Double.valueOf(60.1695)),
                () -> assertEquals(locationWeatherData.getAltitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getData().length, 74)

        );

    }
}