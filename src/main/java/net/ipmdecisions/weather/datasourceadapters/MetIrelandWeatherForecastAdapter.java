/*
 * Copyright (c) 2021 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of IPMDecisionsWeatherService.
 * IPMDecisionsWeatherService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IPMDecisionsWeatherService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IPMDecisionsWeatherService.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.ipmdecisions.weather.datasourceadapters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Using the open data API of https://data.gov.ie/dataset/met-eireann-weather-forecast-api
 * This is a direct rip-off of Met Norway's locationforecast
 * 
 * @copyright 2021 <a href="http://www.nibio.no/">NIBIO</a>
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class MetIrelandWeatherForecastAdapter {
    Integer[] parameters = {
        1001, // Instantaneous temperature at 2m (Celcius)
        3001, // Instantaneous RH at 2m (%)
        2001, // Precipitation (mm)
        4002 // Instantaneous wind speed at 2m
    };
    // Make sure QC is just as long as parameters
    // This indicates that each parameter has been controlled by the supplier, and that everything's OK
    Integer[] QC = {1,1,1,1};
    

    private final static String IRELAND_API_URL = "http://metwdb-openaccess.ichec.ie/metno-wdb2ts/locationforecast?lat=%f&long=%f";
    

    
    public WeatherData getWeatherForecasts(Double longitude, Double latitude, Double altitude) throws ParseWeatherDataException 
    {
        URL irelandURL;
        LocationWeatherData irelandValues;
        try {
            irelandURL = new URL(String.format(Locale.US,
                    MetIrelandWeatherForecastAdapter.IRELAND_API_URL,
                    latitude,
                    longitude,
                    altitude.intValue()) // Need to do this in order to avoid formatting the int 2000 to "2,000"
            );
            URLConnection connection = irelandURL.openConnection();
            // LocationForecast >= 2.0 requires a unique User-Agent
            connection.setRequestProperty("User-Agent", "net.ipmdecisions.weatherapi/BETA-07 IPMDecisions@adas.co.uk");
            connection.connect();
            // Find earliest and latest forecast time stamp
            
            
            //System.out.println("yrURL=" + yrURL.toString());
            
            
            // Parse with DOM parser
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(connection.getInputStream());
            NodeList nodes = doc.getElementsByTagName("time");
            Map<Long, String> RRMap = new HashMap<>();
            Instant timeStart = Instant.parse(nodes.item(0).getAttributes().getNamedItem("from").getNodeValue());
            Instant timeEnd = Instant.parse(nodes.item(nodes.getLength()-1).getAttributes().getNamedItem("to").getNodeValue());
            Integer interval = 3600; // Hourly intervals
            Long rows = 1 + timeStart.until(timeEnd,ChronoUnit.SECONDS)/interval;
            irelandValues = new LocationWeatherData(longitude,latitude, altitude, rows.intValue(), parameters.length);
            for(int i=0;i<nodes.getLength();i++)
            {
                Node node = nodes.item(i);
                Instant fromTime = Instant.parse(node.getAttributes().getNamedItem("from").getNodeValue());
                Instant toTime = Instant.parse(node.getAttributes().getNamedItem("to").getNodeValue());
                Node node2 = DOMUtils.getNode("location", node.getChildNodes());
                
                // TODO: Handle different kinds of elements and durations?
                // The instantaneous measured values
                if(fromTime.compareTo(toTime) == 0 && DOMUtils.getNode("temperature", node2.getChildNodes()) != null)
                {
                    irelandValues.setValue(
                            Long.valueOf(timeStart.until(fromTime, ChronoUnit.SECONDS)/interval).intValue(),
                            0,
                            Double.valueOf(DOMUtils.getNodeAttr("temperature","value",node2.getChildNodes()))
                    );
                }
                if(fromTime.compareTo(toTime) == 0 && DOMUtils.getNode("humidity", node2.getChildNodes()) != null)
                {

                    irelandValues.setValue(
                            Long.valueOf(timeStart.until(fromTime, ChronoUnit.SECONDS)/interval).intValue(),
                            1,
                            Double.valueOf(DOMUtils.getNodeAttr("humidity","value",node2.getChildNodes()))
                    );
                }
                if(fromTime.compareTo(toTime) == 0 && DOMUtils.getNode("windSpeed", node2.getChildNodes()) != null)
                {
                    irelandValues.setValue(
                            Long.valueOf(timeStart.until(fromTime, ChronoUnit.SECONDS)/interval).intValue(),
                            3,
                            Double.valueOf(DOMUtils.getNodeAttr("windSpeed","mps",node2.getChildNodes()))
                    );
                }
                // The aggregated values (duration known, but comes in parallel)
                // We store the values with the highest resolution (least time between)
                if(fromTime.compareTo(toTime) != 0 && DOMUtils.getNode("precipitation", node2.getChildNodes()) != null)
                {
                    // Precip is aggregated for the from-to period and timestamped
                    // With the toTime
                    Long row = timeStart.until(toTime, ChronoUnit.SECONDS)/interval; 
                    Long currentPeriodDuration = (RRMap.get(row) == null) ? null : Long.valueOf(RRMap.get(row).split("_")[0]);
                    Long candidatePeriodDuration = fromTime.until(toTime,ChronoUnit.SECONDS);
                    if(currentPeriodDuration == null || currentPeriodDuration > candidatePeriodDuration)
                    {
                        RRMap.put(row,candidatePeriodDuration + "_" + DOMUtils.getNodeAttr("precipitation","value",node2.getChildNodes()));
                    }
                }
            }
            // Adding all the precipitation values
            for(Long row:RRMap.keySet())
            {
                irelandValues.setValue(row.intValue(), 2, Double.valueOf(RRMap.get(row).split("_")[1]));
            }
            irelandValues = this.createHourlyDataFromYr(irelandValues);
            irelandValues.setLongitude(longitude);
            irelandValues.setLatitude(latitude);
            irelandValues.setAltitude(altitude);
            irelandValues.setQC(QC);
            WeatherData retVal = new WeatherData();
            retVal.setInterval(3600);
            retVal.setWeatherParameters(this.parameters);
            retVal.setTimeStart(timeStart);
            retVal.setTimeEnd(timeEnd);
            retVal.addLocationWeatherData(irelandValues);
            return retVal;
        }
        catch(IOException | ParserConfigurationException | SAXException | NullPointerException ex)
        {
            //ex.printStackTrace();
            throw new ParseWeatherDataException(ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    private LocationWeatherData createHourlyDataFromYr(LocationWeatherData yrValues)
    {
        // We interpolate temp, RH and wind speed
        Integer[] columnsToInterpolate = {0,1,3};
        for(Integer i=0;i<columnsToInterpolate.length; i++)
        {
            yrValues = this.getInterpolatedData(yrValues, columnsToInterpolate[i]);
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
    
    private LocationWeatherData getInterpolatedData (LocationWeatherData yrValues, Integer column)
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
    
    /**
    private List<WeatherObservation> getInterpolatedObservations(WeatherObservation start, WeatherObservation end, String elementMeasurementTypeId)
    {
        List<WeatherObservation> retVal = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        Double difference = end.getValue() - start.getValue();
        Long steps = (end.getTimeMeasured().getTime() - start.getTimeMeasured().getTime()) / 3600000;
        Double delta = difference/steps;
        cal.setTime(start.getTimeMeasured());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        int counter = 1;
        while(cal.getTime().compareTo(end.getTimeMeasured()) < 0)
        {
            WeatherObservation interpolated = new WeatherObservation();
            interpolated.setElementMeasurementTypeId(elementMeasurementTypeId);
            interpolated.setLogIntervalId(WeatherObservation.LOG_INTERVAL_ID_1H);
            interpolated.setTimeMeasured(cal.getTime());
            interpolated.setValue(start.getValue() + (delta * counter++));
            retVal.add(interpolated);
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }
        return retVal;
    }
    * */

    private String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
    }
    
}
