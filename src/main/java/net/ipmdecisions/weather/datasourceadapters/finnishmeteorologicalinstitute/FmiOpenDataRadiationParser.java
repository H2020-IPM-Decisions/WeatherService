/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of VIPSWeatherProxy.
 * VIPSLogic is free software: you can redistribute it and/or modify
 * it under the terms of the NIBIO Open Source License as published by 
 * NIBIO, either version 1 of the License, or (at your option) any
 * later version.
 * 
 * VIPSWeatherProxy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NIBIO Open Source License for more details.
 * 
 * You should have received a copy of the NIBIO Open Source License
 * along with VIPSWeatherProxy.  If not, see <http://www.nibio.no/licenses/>.
 * 
 */

package net.ipmdecisions.weather.datasourceadapters.finnishmeteorologicalinstitute;

import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;
import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.ipmdecisions.weather.util.vips.InvalidAggregationTypeException;
import net.ipmdecisions.weather.util.vips.WeatherObservationListException;
import net.ipmdecisions.weather.util.vips.WeatherUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Get weather data from the Finnish Meteorological Institute's open data API
 * Read the docs here: https://en.ilmatieteenlaitos.fi/open-data 
 * 
 * 
 * Global radiation comes from a separate endpoint and in 1 minutes intervals
 * This needs special parsing and aggregation
 * 
 * @copyright 2020 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class FmiOpenDataRadiationParser {

    /**
     * Convert FMI XML 1-minute radiation data into hourly VIPS type radiation weather observations
     * @param FMIXMLString
     * @return 
     */
    public List<VIPSWeatherObservation> getVIPSWeatherObservations(String FMIXMLString){
        try
        {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(FMIXMLString));
            Document doc = dBuilder.parse(is);
            
            

            // Which item in the data series is the GLOB_1MIN
            NodeList parameterSet = doc.getElementsByTagName("swe:field");
            int radiationArrayIndex = 0;
            for(;radiationArrayIndex < parameterSet.getLength(); radiationArrayIndex++)
            {
                Element e = (Element) parameterSet.item(radiationArrayIndex );
                if(e.getAttribute("name").equals("GLOB_1MIN"))
                    break;
            }
            //System.out.println("GLOB_1MIN is element #" + radiationArrayIndex );
            // Creating array of timestamps and values
            String[] positionsAndTimeStamps = doc.getElementsByTagName("gmlcov:positions").item(0).getTextContent().split("\\r?\\n");
            String[] values = doc.getElementsByTagName("gml:doubleOrNilReasonTupleList").item(0).getTextContent().split("\\r?\\n");
            //System.out.println("length=" + positionsAndTimeStamps.length);

            List<VIPSWeatherObservation> minuteRadiationValues = new ArrayList<>();
            for(int i=0;i<positionsAndTimeStamps.length;i++)
            {
                if(positionsAndTimeStamps[i].trim().isBlank())
                {
                    continue;
                }
                VIPSWeatherObservation o = new VIPSWeatherObservation();
                o.setElementMeasurementTypeId("Q0");
                o.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1M);
                //System.out.println("||" + positionsAndTimeStamps[i].trim());
                o.setTimeMeasured(Date.from(Instant.ofEpochSecond(Long.valueOf(positionsAndTimeStamps[i].trim().split("\\s+")[2]))));
                o.setValue(Double.valueOf(values[i].trim().split("\\s+")[radiationArrayIndex]));
                minuteRadiationValues.add(o);
            }
            Collections.sort(minuteRadiationValues);
            // Since the last minute value is on the hour, it will be aggregated as the single 
            // object in the forthcoming hour. This creates confusion. We remove it.
            minuteRadiationValues.remove(minuteRadiationValues.size()-1);
            
            WeatherUtils wUtil = new WeatherUtils();
            return wUtil.getAggregateHourlyValues(minuteRadiationValues, TimeZone.getTimeZone("Europe/Helsinki"), VIPSWeatherObservation.LOG_INTERVAL_ID_1M, WeatherUtils.AGGREGATION_TYPE_AVERAGE);
        }catch(NullPointerException | IOException | ParserConfigurationException | SAXException | WeatherObservationListException | InvalidAggregationTypeException ex)
        {
            //ex.printStackTrace();
            return null;
        }
    }
}
