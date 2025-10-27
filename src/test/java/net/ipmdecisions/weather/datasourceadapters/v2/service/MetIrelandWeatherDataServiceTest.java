package net.ipmdecisions.weather.datasourceadapters.v2.service;

import net.ipmdecisions.weather.datasourceadapters.v2.client.Client;
import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetIrelandResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.params.MetirelandParamModel;
import net.ipmdecisions.weather.datasourceadapters.v2.params.ParamModel;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class MetIrelandWeatherDataServiceTest {

    @Test
    void test_mapping_of_met_ireland_response() throws Exception {
        // given
        var xml = new String(
                getClass().getResourceAsStream("/fixtures/met_eireann_locationforecast.xml").readAllBytes(),
                StandardCharsets.UTF_8
        );
        var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        var metClient = mock(Client.class);
        var metParams = mock(ParamModel.class);
        var metParamModel = new MetirelandParamModel();
        Map<String, Object> params = Map.of(
                "latitude", 52.597709,
                "longitude", -7.644361,
                "altitude", 0.0
        );
        metParamModel.initialize(params);

        when(metClient.supports("metireland")).thenReturn(true);
        when(metParams.supports("metireland")).thenReturn(true);

        when(metClient.getData(any(ParamModel.class))).thenReturn(new MetIrelandResponse(doc, metParamModel));

        var service = new WeatherDataService(
                List.of(metClient),
                List.of(metParams)
        );

        // when
        var result = service.getWeatherData("metireland", Map.of());

        // then
        var locationWeatherData = result.getLocationWeatherData().get(0);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTimeStart(), Instant.parse("2025-10-21T07:00:00Z")),
                () -> assertEquals(result.getTimeEnd(), Instant.parse("2025-10-30T12:00:00Z")),
                () -> assertEquals(result.getInterval(), Integer.valueOf(3600)),
                () -> assertEquals(locationWeatherData.getLongitude(), Double.valueOf(-7.644361)),
                () -> assertEquals(locationWeatherData.getLatitude(), Double.valueOf(52.597709)),
                () -> assertEquals(locationWeatherData.getAltitude(), Double.valueOf(0.0)),
                () -> assertEquals(locationWeatherData.getData().length, 222)

        );

    }
}