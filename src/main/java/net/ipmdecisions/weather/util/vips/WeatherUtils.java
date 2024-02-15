/*
 * Copyright (c) 2020 NIBIO <http://www.nibio.no/>. 
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

package net.ipmdecisions.weather.util.vips;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.ipmdecisions.weather.entity.LocationWeatherData;
import net.ipmdecisions.weather.entity.WeatherData;
import net.ipmdecisions.weather.entity.WeatherDataSourceException;


/**
 * Weather related utility methods
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class WeatherUtils {
    
    private final boolean DEBUG = false;
    
    public final static int AGGREGATION_TYPE_AVERAGE = 1;
    public final static int AGGREGATION_TYPE_SUM = 2;
    public final static int AGGREGATION_TYPE_MINIMUM = 3;
    public final static int AGGREGATION_TYPE_MAXIMUM = 4;
    public final static int AGGREGATION_TYPE_SUM_GLOBAL_RADIATION = 5;
    
    private final Map<String, Integer> VIPSToIPM = Map.of(
            "TM",1002, // TM
            "RR", 2001, // RR
            "UM", 3002, // UM
            "Q0", 5001, // Q0
            "BT",3101, // BT
            "FF2", 4002,
            "FM2", 4003,
            "DP", 1901 // Dew point
            
    );
    
    private final Map<Integer, String> IPMToVIPS = Map.of(
            1002,"TM", // TM
            2001, "RR", // RR
            3002, "UM", // UM
            5001,"Q0", // Q0
            3101, "BT",
            4002, "FF2",
            4003, "FM2",
            1901, "DP" // Dew Point
    );
    
    public Integer getIPMParameterId(String VIPSParameterId)
    {
        if(DEBUG)
        {
            if(this.VIPSToIPM.get(VIPSParameterId) == null)
            {
                System.out.println("Could not find IPM parameter for VIPS parameter " + VIPSParameterId);
            }
        }
        return this.VIPSToIPM.get(VIPSParameterId);
    }
    
    public String getVIPSParameterId(Integer IPMParameterId)
    {
        return this.IPMToVIPS.get(IPMParameterId);
    }

    
    /**
     * 
     * @param observations
     * @param timeZone
     * @param logintervalId
     * @param typeOfAggregation
     * @return
     * @throws WeatherObservationListException
     * @throws InvalidAggregationTypeException 
     */
    public List<VIPSWeatherObservation> getAggregateHourlyValues(List<VIPSWeatherObservation> observations, TimeZone timeZone, Integer logintervalId, Integer typeOfAggregation) throws WeatherObservationListException, InvalidAggregationTypeException{
        if(observations == null || observations.isEmpty())
        {
            return null;
        }
        // First we organize the less-than-hourly values into one bucket per hour
        Map<Date,Map> hourBucket = new HashMap<>();
        String expectedParameter = observations.get(0).getElementMeasurementTypeId();
        Date lastDate = null;
        for(VIPSWeatherObservation observation:observations)
        {
            if(!observation.getElementMeasurementTypeId().equals(expectedParameter))
            {
                throw new WeatherObservationListException("Found multiple parameters: " + observation.getElementMeasurementTypeId() + " and " + expectedParameter);
            }
            Date theDate = normalizeToExactHour(observation.getTimeMeasured(), timeZone);
            lastDate = lastDate == null ? theDate : (lastDate.compareTo(theDate) < 0 ? theDate : lastDate);
            Map<Date, Double> hourValuesForDate = hourBucket.get(theDate);
            if(hourValuesForDate == null)
            {
                hourValuesForDate = new HashMap<>();
                hourBucket.put(theDate, hourValuesForDate);
            }
            
            // Check for double entries
            // TODO: Handle DST change with double entries at 03:00
            Double possibleDuplicate = hourValuesForDate.get(observation.getTimeMeasured());
            if(possibleDuplicate != null)
            {
                throw new WeatherObservationListException(
                        "Found duplicate weatherObservations for parameter " +
                        observation.getElementMeasurementTypeId() + " at time " +
                        observation.getTimeMeasured()
                );
            }
            hourValuesForDate.put(observation.getTimeMeasured(), observation.getValue());
        }
        
        // Then we iterate the buckets, do the aggregation and create return values
        List<VIPSWeatherObservation> aggregatedObservations = new ArrayList<>();
        VIPSWeatherObservation templateObservation = observations.get(0);
        Double aggregateValue;
        for(Date anHour:hourBucket.keySet())
        {
            //System.out.println("date=" + aDay);
            Map valuesForAnHour = hourBucket.get(anHour);
            
            switch(typeOfAggregation){
                case WeatherUtils.AGGREGATION_TYPE_AVERAGE:
                    aggregateValue = getAverage(valuesForAnHour.values()); break;
                case WeatherUtils.AGGREGATION_TYPE_SUM:
                    aggregateValue = getSum(valuesForAnHour.values()); break;
                case WeatherUtils.AGGREGATION_TYPE_MINIMUM:
                    aggregateValue = getMinimum(valuesForAnHour.values()); break;
                case WeatherUtils.AGGREGATION_TYPE_MAXIMUM:
                    aggregateValue = getMaximum(valuesForAnHour.values()); break;
                default:
                    throw new InvalidAggregationTypeException(
                            "No aggregation method with id= " + typeOfAggregation  + " exists."
                            );
            }
            VIPSWeatherObservation aggregatedObservation = new VIPSWeatherObservation();
            aggregatedObservation.setElementMeasurementTypeId(templateObservation.getElementMeasurementTypeId());
            aggregatedObservation.setLogIntervalId(VIPSWeatherObservation.LOG_INTERVAL_ID_1H);
            aggregatedObservation.setTimeMeasured(anHour);
            aggregatedObservation.setValue(aggregateValue);
            aggregatedObservations.add(aggregatedObservation);
        }
        return aggregatedObservations;
    }
    
    /**
     * Slicing off minutes, seconds and milliseconds
     * @param timeStamp
     * @param timeZone
     * @return 
     */
     public Date normalizeToExactHour(Date timeStamp, TimeZone timeZone)
    {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(timeStamp);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }
  
     public Double getAverage(Collection<Double> values)
    {
        return this.getSum(values)/values.size();
    }

    public Double getSum(Collection<Double> values)
    {
       Double sum = 0d;
        for(Double value:values)
        {
            sum += value;
        }
        return sum;
    }
    
    public Double getMinimum(Collection<Double> values)
    {
        Double minimum = null;
        for(Double value:values)
        {
            minimum = minimum == null ? value : Math.min(minimum, value);
        }
        return minimum;
    }
    
    public Double getMaximum(Collection<Double> values)
    {
        Double maximum = null;
        for(Double value:values)
        {
            maximum = maximum == null ? value : Math.max(maximum, value);
        }
        return maximum;
    }
    
    public WeatherData getWeatherDataFromVIPSWeatherObservations(URL sourceURL, Double longitude, Double latitude, Integer defaultQC) throws IOException, WeatherDataSourceException
    {
    	HttpURLConnection conn = (HttpURLConnection) sourceURL.openConnection();
    	int resultCode = conn.getResponseCode();
		// Follow redirects, also https
		if(resultCode == HttpURLConnection.HTTP_MOVED_PERM || resultCode == HttpURLConnection.HTTP_MOVED_TEMP)
		{
			String location = conn.getHeaderField("Location");
			conn.disconnect();
			conn = (HttpURLConnection)  new URL(location).openConnection();
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		String response = "";

		while ((inputLine = in.readLine()) != null) {
			response += inputLine;
		}
		in.close();
		//System.out.println("response = " + response);
		// Are we getting anything else but 200? Raise error
		if(conn.getResponseCode() != HttpURLConnection.HTTP_OK)
		{
			throw new WeatherDataSourceException("ERROR: Got Http response code " + conn.getResponseCode() + " from data source. Message from server was: " + response.toString());
		}
		ObjectMapper objectMapper = new ObjectMapper();
                //System.out.println(response);
		List<VIPSWeatherObservation> observations = objectMapper.readValue(response, new TypeReference<List<VIPSWeatherObservation>>(){});
		return this.getWeatherDataFromVIPSWeatherObservations(observations, longitude, latitude, defaultQC);
    }
    
    /**
     * 
     * @param observations
     * @param longitude
     * @param latitude
     * @return 
     */
    public WeatherData getWeatherDataFromVIPSWeatherObservations(List<VIPSWeatherObservation> observations, Double longitude, Double latitude, Integer defaultQC)
    {
    	if(observations == null || observations.size() == 0)
    	{
    		return null;
    	}
        // TODO: Ensure that both VIPS named parameters and IPM parameter codes are mapped
        // Some adapters have included the IPM parameter codes already
        Integer[] parameters = observations.stream()
                    .filter(obs->this.getIPMParameterId(obs.getElementMeasurementTypeId())!=null)
                    .map(obs->this.getIPMParameterId(obs.getElementMeasurementTypeId()))
                    .collect(Collectors.toSet())
                    .toArray(Integer[]::new);
                    
        Map<Integer,Integer> paramCol = new HashMap<>();
        for(int i=0;i<parameters.length;i++)
        {
            paramCol.put(parameters[i], i);
        }
        Collections.sort(observations);
        Instant timeStart = observations.get(0).getTimeMeasured().toInstant();
        Instant timeEnd = observations.get(observations.size()-1).getTimeMeasured().toInstant();
        // Convert to IPM Decisions format
        Integer interval = 3600; // Hourly data
        Long rows = 1 + timeStart.until(timeEnd, ChronoUnit.SECONDS) / interval;
        LocationWeatherData ipmData = new LocationWeatherData(
                longitude,
                latitude,
                0.0,
                rows.intValue(),
                parameters.length
        );
        WeatherData weatherData = new WeatherData();
        weatherData.setInterval(interval);
        weatherData.setTimeStart(timeStart);
        weatherData.setTimeEnd(timeEnd);
        weatherData.setWeatherParameters(parameters);

        // Set the QC to defaultQC from the method's client
        Integer[] QCPerParam = new Integer[weatherData.getWeatherParameters().length];
        for(int i=0;i<QCPerParam.length;i++) {
            QCPerParam[i] = defaultQC;
        }
        
        observations.stream()
            .filter(obs->this.getIPMParameterId(obs.getElementMeasurementTypeId())!=null)
            .forEach(obse -> {
                Long row = timeStart.until(obse.getTimeMeasured().toInstant(), ChronoUnit.SECONDS) / interval;
                Integer col = paramCol.get(this.getIPMParameterId(obse.getElementMeasurementTypeId()));
                ipmData.setValue(row.intValue(), col, obse.getValue());
                ipmData.setQC(QCPerParam);
        });
        weatherData.addLocationWeatherData(ipmData);

        
        
        return weatherData;
    }
}
