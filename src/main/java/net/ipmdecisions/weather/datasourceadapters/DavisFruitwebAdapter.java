/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPM Decisions Weather Service.
 * IPM Decisions Weather Service is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPM Decisions Weather Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPM Decisions Weather Service.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.datasourceadapters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.ws.rs.NotAuthorizedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.vips.VIPSWeatherObservation;
import net.ipmdecisions.weather.util.vips.WeatherUtils;


/**
 * Reads/parses data from a Davis weather station using the fruitweb service: http://www.fruitweb.info/ 
 * @copyright 2016-2024 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class DavisFruitwebAdapter {

    private Logger LOGGER = LoggerFactory.getLogger(DavisFruitwebAdapter.class);
    
    private WeatherUtils wUtils;
    
    public DavisFruitwebAdapter(){
        this.wUtils = new WeatherUtils();
    }
    
    

    public final static String FRUITWEB_URL_TEMPLATE = "https://www.fruitweb.info/sc/getFile.php?id={0}&pw={1}&date={2}";
    // Davis/Fruitweb parameters, including name and aggregation type
    private final static String[][] ELEMENT_MEASUREMENT_TYPES = {
        {"RAIN","RR","SUM"},
        {"LW1","BT","SUM"},
        {"AIRTEMP","TM","AVG"},
        {"AIRHUM","UM","AVG"}
    };
    
    public WeatherData getWeatherData(String stationID, String password, LocalDate startDate, LocalDate endDate, TimeZone timeZone) throws ParseWeatherDataException 
    {
        // Time zone is not obtainable from station
        if(timeZone == null)
        {
            timeZone = TimeZone.getTimeZone("UTC");
        }
        
        return this.wUtils.getWeatherDataFromVIPSWeatherObservations(
                this.getWeatherObservations(stationID, password, timeZone, Date.from(startDate.atStartOfDay(ZoneId.of(timeZone.getID())).toInstant()), Date.from(endDate.atStartOfDay(ZoneId.of(timeZone.getID())).toInstant())),  
                0.0, // TODO Get location
                0.0, // TODO Get location
                0
        );
    }
    
    /**
     * @param stationID the METOS station ID
     * @param timeZone
     * @param startDate
     * @return 
     */
    public List<VIPSWeatherObservation> getWeatherObservations(String stationID, String password, TimeZone timeZone, Date startDate, Date endDate) throws ParseWeatherDataException 
    {
        List<VIPSWeatherObservation> retVal = new ArrayList<>();
        SimpleDateFormat urlDFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        urlDFormat.setTimeZone(timeZone);
        dFormat.setTimeZone(timeZone);
        // Assuming 1 hour resolution until we find a timestamp that says :30
        Integer logIntervalId = VIPSWeatherObservation.LOG_INTERVAL_ID_1H;
        List<String[]> data = new ArrayList<>();
        String[] headers;
        Map<Integer, Integer> elementOrdering = new HashMap<>();
        try {
            URL fruitwebDavisURL = new URL(MessageFormat.format(DavisFruitwebAdapter.FRUITWEB_URL_TEMPLATE, stationID,password,urlDFormat.format(startDate)));
            LOGGER.debug(fruitwebDavisURL.toString());
            HttpURLConnection connection = (HttpURLConnection) fruitwebDavisURL.openConnection();
            
            int responseCode = connection.getResponseCode();
            if(responseCode == 401 || responseCode == 403) // Unauthorized or Forbidden
            {
                throw new NotAuthorizedException("Access denied by FruitWeb. Please check your credentials");
            }

            BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));

            String inputLine;
            Date testTimestamp;
            // We need to collect all the lines first, because we need to analyze
            // If the resolution is 30 minutes or 1 hour
            while ((inputLine = in.readLine()) != null)
            {
                //System.out.println(inputLine);
                String[] lineData = inputLine.split(";");
                // Skip empty lines
                if(lineData.length <= 1)
                {
                    continue;
                }
                // Check for valid start of line
                try {
                    testTimestamp = dFormat.parse(lineData[0] + " " + lineData[1]);
                    data.add(lineData);
                    if(lineData[1].split(":")[1].equals("30"))
                    {
                        logIntervalId = VIPSWeatherObservation.LOG_INTERVAL_ID_30M;
                    }
                } catch (ParseException ex) {
                    
                    // Is this the heading line?
                    // Then we parse it to set the ordering of elements
                    if(lineData[0].equals("DATE"))
                    {
                        headers = lineData;
                        // DATE and TIME should always be the two first ones
                        for(int i=2;i<lineData.length;i++)
                        {
                            for(int j=0;j<ELEMENT_MEASUREMENT_TYPES.length;j++)
                            {
                                if(ELEMENT_MEASUREMENT_TYPES[j][0].equals(lineData[i]))
                                {
                                    elementOrdering.put(i,j);
                                }
                            }
                        }
                    }
                }
            }
            in.close();
        } catch (IOException ex) {
            throw new ParseWeatherDataException(ex.getMessage());
        }
        

        // Data comes in half-hour chunks (resolution = 30 minutes)
        // Unlike Metos, the hour starts at :30
        if(logIntervalId.equals(VIPSWeatherObservation.LOG_INTERVAL_ID_30M))
        {
            Boolean shouldBe30Now = true;
            String[] data30 = null; 
            String[] data00 = null;
            Date timestamp = null;
            for(String[] lineData: data)
            {
                //System.out.println(String.join(",",lineData));
                // Skip lines that are not exactly :00 or :30
                if(
                        !lineData[1].split(":")[1].equals("00")
                        && ! lineData[1].split(":")[1].equals("30")
                        )
                {
                    continue;
                }
                else if(lineData[1].split(":")[1].equals("30") && shouldBe30Now)
                {
                    data30 = lineData;
                    
                    shouldBe30Now = false;
                    continue; // So that we summarize only after :00 data has been set too
                }
                else if(lineData[1].split(":")[1].equals("00") && !shouldBe30Now)
                {
                    data00 = lineData;
                    try
                    {
                        timestamp = dFormat.parse(lineData[0] + " " + lineData[1]);
                    }
                    catch(ParseException ex)
                    {
                        throw new ParseWeatherDataException("Error with time stamp in weather data from Davis/FruitWeb station: " + ex.getMessage());
                    }
                    shouldBe30Now = true;
                }
                else
                {
                    throw new ParseWeatherDataException("Doesn't make sense at " + lineData[0] + " " + lineData[1] + "!");
                }

                for(Integer i=2;i<data30.length;i++)
                {
                    
                    Integer elementMeasurementTypeIndex = elementOrdering.get(i);
                    // This means there is an element type we don't collect
                    if(elementMeasurementTypeIndex == null)
                    {
                        continue;
                    }
                    Double aggregateValue = null;
                    Double value00 = null;
                    Double value30 = null;
                    Boolean atLeastOneValueMissing = false;
                    try{value00 = Double.valueOf(data00[i].replaceAll(",","."));}catch(NumberFormatException ex){atLeastOneValueMissing = Boolean.TRUE;}
                    try{value30 = Double.valueOf(data30[i].replaceAll(",","."));}catch(NumberFormatException ex){atLeastOneValueMissing = Boolean.TRUE;}
                    if(value00 == null && value30 == null)
                    {
                        aggregateValue = null;
                    }
                    else
                    {
                        value00 = value00 == null ? 0.0 : value00;
                        value30 = value30 == null ? 0.0 : value30;
                        //System.out.println("element " + i + "=" + MetosDataParser.elementMeasurementTypes[elementMeasurementTypeIndex][1]);
                        if(DavisFruitwebAdapter.ELEMENT_MEASUREMENT_TYPES[elementMeasurementTypeIndex][2].equals("AVG"))
                        {
                            aggregateValue = (value00 + value30) / (atLeastOneValueMissing ? 1 : 2);
                        }
                        else
                        {
                            aggregateValue = (value00 + value30);
                        }
                    }
                    VIPSWeatherObservation obs = new VIPSWeatherObservation();
                    obs.setTimeMeasured(timestamp);
                    obs.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                    obs.setElementMeasurementTypeId(DavisFruitwebAdapter.ELEMENT_MEASUREMENT_TYPES[elementMeasurementTypeIndex][1]);
                    obs.setValue(aggregateValue);
                    retVal.add(obs);
                }
            }
        }
        // Data is hourly, easy to add
        else
        {
            Date timestamp = null;
            for(String[] lineData: data)
            {
                // If minute != 00 we skip the line
                if(!lineData[1].split(":")[1].equals("00"))
                {
                    continue;
                }
                try
                {
                    timestamp = dFormat.parse(lineData[0] + " " + lineData[1]);
                }
                catch(ParseException ex)
                {
                    throw new ParseWeatherDataException("Error with time stamp in weather data from Metos station: " + ex.getMessage());
                }
                for(Integer i=2;i<lineData.length;i++)
                {
                    Double value = Double.valueOf(lineData[i].replaceAll(",","."));
                    Integer elementMeasurementTypeIndex = elementOrdering.get(i);
                    // This means there is an element type we don't collect
                    if(elementMeasurementTypeIndex == null)
                    {
                        continue;
                    }
                    VIPSWeatherObservation obs = new VIPSWeatherObservation();
                    obs.setTimeMeasured(timestamp);
                    obs.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
                    obs.setElementMeasurementTypeId(DavisFruitwebAdapter.ELEMENT_MEASUREMENT_TYPES[elementMeasurementTypeIndex][1]);
                    obs.setValue(value);
                    retVal.add(obs);
                }
            }
        }
        return retVal.stream().filter(obs -> 
                (obs.getTimeMeasured().compareTo(startDate) >= 0 && (endDate == null || obs.getTimeMeasured().compareTo(endDate) <= 0))
        ).collect(Collectors.toList());
    }
}
