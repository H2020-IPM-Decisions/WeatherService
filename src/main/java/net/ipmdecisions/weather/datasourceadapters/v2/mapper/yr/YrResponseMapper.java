package net.ipmdecisions.weather.datasourceadapters.v2.mapper.yr;

import net.ipmdecisions.weather.datasourceadapters.v2.client.responsemodel.YrResponse;
import net.ipmdecisions.weather.datasourceadapters.v2.mapper.DocumentElementExtractor;
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
            1001, // Instantaneous temperature at 2m (Celcius)
            3001, // Instantaneous RH at 2m (%)
            2001, // Precipitation (mm)
            4002 // Instantaneous wind speed at 2m
    };
    // Make sure QC is just as long as parameters
    // This indicates that each parameter has been controlled by the supplier, and that everything's OK
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
            Instant fromTime = Instant.parse(timeNode.getAttributes().getNamedItem("from").getNodeValue());
            Instant toTime   = Instant.parse(timeNode.getAttributes().getNamedItem("to").getNodeValue());
            Node locationNode = DOMUtils.getNode("location", timeNode.getChildNodes());

            DocumentElementExtractor.extract(timeNode, locationNode, timeStart, yrValues, interval);
            if (fromTime.compareTo(toTime) != 0
                    && locationNode != null
                    && DOMUtils.getNode("precipitation", locationNode.getChildNodes()) != null) {
                // Precip is aggregated for the from-to period and timestamped
                // With the toTime
                Long row = timeStart.until(toTime, ChronoUnit.SECONDS)/interval;
                Long currentPeriodDuration = (RRMap.get(row) == null) ? null : Long.valueOf(RRMap.get(row).split("_")[0]);
                Long candidatePeriodDuration = fromTime.until(toTime,ChronoUnit.SECONDS);
                if(currentPeriodDuration == null || currentPeriodDuration > candidatePeriodDuration)
                {
                    RRMap.put(row,candidatePeriodDuration + "_" + DOMUtils.getNodeAttr("precipitation","value",locationNode.getChildNodes()));
                }
            }
        }
        // Adding all the precipitation values
        for(Long row:RRMap.keySet())
        {
            yrValues.setValue(row.intValue(), 2, Double.valueOf(RRMap.get(row).split("_")[1]));
        }
        yrValues = createHourlyDataFromYr(yrValues);
        yrValues.setLongitude(longitude);
        yrValues.setLatitude(latitude);
        yrValues.setAltitude(altitude);
        yrValues.setQC(QC);
        WeatherData retVal = new WeatherData();
        retVal.setInterval(3600);
        retVal.setWeatherParameters(parameters);
        retVal.setTimeStart(timeStart);
        retVal.setTimeEnd(timeEnd);
        retVal.addLocationWeatherData(yrValues);
        return retVal;
    }

    private static LocationWeatherData createHourlyDataFromYr(LocationWeatherData yrValues)
    {
        // We interpolate temp, RH and wind speed
        Integer[] columnsToInterpolate = {0,1,3};
        for(Integer i=0;i<columnsToInterpolate.length; i++)
        {
            yrValues = getInterpolatedData(yrValues, columnsToInterpolate[i]);
        }
        // We set Null RR to zero
        for(Integer i=0;i<yrValues.getLength();i++)
        {
            if(yrValues.getValue(i, 2) == null)
            {
                yrValues.setValue(i, 2, 0.0);
            }
        }
        return yrValues;
    }

    private static LocationWeatherData getInterpolatedData (LocationWeatherData yrValues, Integer column)
    {
        for(Integer i = 0; i< yrValues.getLength();i++)
        {
            if(yrValues.getValue(i, column) == null)
            {
                // Find last value before hole and first value after hole
                Integer lastRowWithValueBeforeHole = null;
                Integer firstRowWithValueAfterHole = null;
                Double lastValueBeforeHole = null;
                Double firstValueAfterHole = null;
                while(lastValueBeforeHole == null && i >= 0)
                {
                    i--;
                    if(yrValues.getValue(i, column) != null)
                    {
                        lastRowWithValueBeforeHole = i;
                        lastValueBeforeHole = yrValues.getValue(i, column);
                    }
                }

                while(firstValueAfterHole == null && i <= yrValues.getLength())
                {
                    i++;
                    if(yrValues.getValue(i, column) != null)
                    {
                        firstRowWithValueAfterHole = i;
                        firstValueAfterHole = yrValues.getValue(i, column);
                    }
                }
                // Interpolate.
                Double step = (firstValueAfterHole - lastValueBeforeHole) / (firstRowWithValueAfterHole - lastRowWithValueBeforeHole);
                for(i = lastRowWithValueBeforeHole + 1; i < firstRowWithValueAfterHole ; i++)
                {
                    yrValues.setValue(i, column, lastValueBeforeHole + ((i-lastRowWithValueBeforeHole) * step));
                }
            }
        }
        return yrValues;
    }
}
