package net.ipmdecisions.weather.datasourceadapters.v2.mapper.metireland;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.MetIrelandResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.DocumentElementExtractor;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.PrecipitationMapper;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.common.TimeSeriesMapper;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.DOMUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class MetIrelandResponseMapper {

    static Integer[] parameters = {
            1001, // Instantaneous temperature at 2m (Celcius)
            3001, // Instantaneous RH at 2m (%)
            2001, // Precipitation (mm)
            4002 // Instantaneous wind speed at 2m
    };

    static Integer[] QC = {1,1,1,1};

    private static final int INTERVAL_SECONDS = 3600;

    public static WeatherData toWeatherData(MetIrelandResponse metIrelandResponse) {
        var doc = metIrelandResponse.doc();
        var params = metIrelandResponse.params();
        Double longitude = params.getLongitude();
        Double latitude = params.getLatitude();
        Double altitude = params.getAltitude();

        NodeList nodes = doc.getElementsByTagName("time");
        Map<Long, String> rrMap = new HashMap<>();

        Instant timeStart = Instant.parse(nodes.item(0).getAttributes().getNamedItem("from").getNodeValue());
        Instant timeEnd = Instant.parse(nodes.item(nodes.getLength() - 1).getAttributes().getNamedItem("to").getNodeValue());

        Long rows = 1 + timeStart.until(timeEnd, ChronoUnit.SECONDS) / INTERVAL_SECONDS;
        LocationWeatherData irelandValues =
                new LocationWeatherData(longitude, latitude, altitude, rows.intValue(), parameters.length);

        for (int i = 0; i < nodes.getLength(); i++) {
            Node timeNode = nodes.item(i);
            Node locationNode = DOMUtils.getNode("location", timeNode.getChildNodes());

            // Instantaneous values
            DocumentElementExtractor.extract(timeNode, locationNode, timeStart, irelandValues, INTERVAL_SECONDS);
            PrecipitationMapper.aggregate(timeNode, locationNode, timeStart, rrMap, INTERVAL_SECONDS);
        }

        PrecipitationMapper.apply(rrMap, 2, irelandValues);
        TimeSeriesMapper.finalizeHourly(irelandValues, 2, 0, 1, 3);

        irelandValues.setLongitude(longitude);
        irelandValues.setLatitude(latitude);
        irelandValues.setAltitude(altitude);
        irelandValues.setQC(QC);

        WeatherData retVal = new WeatherData();
        retVal.setInterval(INTERVAL_SECONDS);
        retVal.setWeatherParameters(parameters);
        retVal.setTimeStart(timeStart);
        retVal.setTimeEnd(timeEnd);
        retVal.addLocationWeatherData(irelandValues);
        return retVal;
    }
}
