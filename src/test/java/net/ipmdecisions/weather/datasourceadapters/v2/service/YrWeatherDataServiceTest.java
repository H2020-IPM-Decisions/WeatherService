package net.ipmdecisions.weather.datasourceadapters.v2.service;

import net.ipmdecisions.weather.datasourceadapters.v2.client.Client;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.YrResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.YrParamModel;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class YrWeatherDataServiceTest {

    @Test
    void test_mapping_of_yr_response() throws Exception {
        var xml = new String(
                getClass().getResourceAsStream("/fixtures/yr_locationforecast.xml").readAllBytes(),
                StandardCharsets.UTF_8
        );
        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        var metClient = mock(Client.class);
        var metParams = mock(ParamModel.class);
        var yrParamModel = new YrParamModel();
        Map<String, Object> params = Map.of(
                "latitude", 52.597709,
                "longitude", -7.644361,
                "altitude", 0.0
        );
        yrParamModel.initialize(params);

        when(metClient.supports("yr")).thenReturn(true);
        when(metParams.supports("yr")).thenReturn(true);

        when(metClient.getData(any(ParamModel.class))).thenReturn(new YrResponse(doc, yrParamModel));

        var service = new WeatherDataService(
                List.of(metClient),
                List.of(metParams)
        );

        // when
        var result = service.getWeatherData("yr", Map.of());

        // then
        var locationWeatherData = result.getLocationWeatherData().get(0);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTimeStart(), Instant.parse("2025-10-22T05:00:00Z")),
                () -> assertEquals(result.getTimeEnd(), Instant.parse("2025-11-01T00:00:00Z")),
                () -> assertEquals(result.getInterval(), Integer.valueOf(3600)),
                () -> assertEquals(locationWeatherData.getLongitude(), Double.valueOf(-7.644361)),
                () -> assertEquals(locationWeatherData.getLatitude(), Double.valueOf(52.597709)),
                () -> assertEquals(locationWeatherData.getAltitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getData().length, 236)

        );

    }
}