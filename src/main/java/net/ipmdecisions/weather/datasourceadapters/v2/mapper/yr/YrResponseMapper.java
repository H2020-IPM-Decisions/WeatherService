package net.ipmdecisions.weather.datasourceadapters.v2.mapper.yr;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.YrResponse;
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

public class YrResponseMapper {

    static Integer[] parameters = {
            1001,
            3001,
            2001,
            4002
    };
    static Integer[] QC = {1,1,1,1};

    public static WeatherData toWeatherData(YrResponse data) {
        var doc = data.doc();
        var params = data.paramModel();
        LocationWeatherData yrValues;
        Double longitude = params.getLongitude();
        Double latitude = params.getLatitude();
        Double altitude = params.getAltitude();
        NodeList nodes = doc.getElementsByTagName("time");
        Map<Long, String> RRMap = new HashMap<>();
        Instant timeStart = Instant.parse(nodes.item(0).getAttributes().getNamedItem("from").getNodeValue());
        Instant timeEnd = Instant.parse(nodes.item(nodes.getLength()-1).getAttributes().getNamedItem("to").getNodeValue());
        Integer interval = 3600; // Hourly intervals
        Long rows = 1 + timeStart.until(timeEnd, ChronoUnit.SECONDS)/interval;
        yrValues = new LocationWeatherData(longitude,latitude, altitude, rows.intValue(), parameters.length);
        for(int i=0;i<nodes.getLength();i++)
        {
            Node timeNode = nodes.item(i);
            Node locationNode = DOMUtils.getNode("location", timeNode.getChildNodes());
            DocumentElementExtractor.extract(timeNode, locationNode, timeStart, yrValues, interval);
            PrecipitationMapper.aggregate(timeNode, locationNode, timeStart, RRMap, interval);
        }

        PrecipitationMapper.apply(RRMap, 2, yrValues);
        TimeSeriesMapper.finalizeHourly(yrValues, 2, 0, 1, 3);

        yrValues.setLongitude(longitude);
        yrValues.setLatitude(latitude);
        yrValues.setAltitude(altitude);
        yrValues.setQC(QC);
        WeatherData retVal = new WeatherData();
        retVal.setInterval(interval);
        retVal.setWeatherParameters(parameters);
        retVal.setTimeStart(timeStart);
        retVal.setTimeEnd(timeEnd);
        retVal.addLocationWeatherData(yrValues);
        return retVal;
    }
}
